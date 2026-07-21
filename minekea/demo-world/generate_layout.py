#!/usr/bin/env python3
"""
Minekea demo-world layout generator  (by-material, tiered edition)

Groups every Minekea block by MATERIAL into flexible regions, and lays every block on
a rising staircase so nothing is hidden directly behind another. The core rule is:

    every display block sits at  y = 56 + depth   (depth = how many rows back it is),
    with a solid support column underneath, so each row back is one block higher.

Region kinds:
  * material regions (woods, stones, metals, ...): flat blocks fill staircase rows at the
    front, then each material's compressed blocks form small stepped 3x3 podiums behind.
  * colour regions: organised BY BLOCK TYPE, each type a column showing the full 16-colour
    gradient receding back (and rising); a block type with an un-dyed variant (wax, votive
    candles) puts that "plain" one in spot 0 ahead of white.

Colour order follows Minecraft's current creative order:
  white, light_gray, gray, black, brown, red, orange, yellow, lime, green, cyan,
  light_blue, blue, purple, magenta, pink.

Regions flow west->east (+X); rows wrap north (-Z) with a 3-block aisle so per-region
teleport vantages never land inside the next row. Arena front-left corner = (-192,55,191)
(the smooth-stone west border; the display blocks are framed just inside, from x=-191).

Outputs: demo_layout_manifest.csv, layout_regions.txt, layout_stats.json, demo_build.mcfunction
Regenerate with:  python generate_layout.py
"""
import csv, json, math, os
from collections import OrderedDict, defaultdict

HERE = os.path.dirname(os.path.abspath(__file__))

# ---- arena geometry -------------------------------------------------------
ORIGIN_X = -191           # first region's left (west) edge; the 1-block west border sits at -192
ORIGIN_Z =  190           # first region's front row; the +1 front margin makes the front edge 191
FLOOR_Y  =   55           # floor pads; display blocks start at y=56 (depth 0)
ROW_WIDTH = 124           # wrap to a new row once a row grows past this many blocks
AISLE     =    2          # gap between regions within a row
ROW_AISLE =    3          # gap between rows (so teleport vantages land in the aisle, not in blocks)
VANTAGE   =    2          # teleport this many blocks in front (south) of a region's front row
CLEAR_SIZE   = 128        # wipe a CLEAR_SIZE x CLEAR_SIZE footprint (from the front-left corner)
CLEAR_HEIGHT =  15        # ...up this many blocks, before building

FLOOR_BLOCK  = "minecraft:polished_andesite"   # region pads + staircase supports
BORDER_BLOCK = "minecraft:smooth_stone"        # arena base / aisles
SIGN_BLOCK   = "minecraft:oak_sign"

FLAT_COLS = 12            # flat display blocks per staircase row
POD_COLS  = 3             # stepped 3x3 podiums per podium-row
POD_SIZE  = 3
POD_GAP   = 1
FRONT_ROWS = 1            # region rows kept clear at the front (sign / cmd / standing)

# ---- material grouping ----------------------------------------------------
COLORS = ["white", "light_gray", "gray", "black", "brown", "red", "orange", "yellow",
          "lime", "green", "cyan", "light_blue", "blue", "purple", "magenta", "pink"]
COLOR_SET = set(COLORS)
WOOD_BASES = ["dark_oak", "pale_oak", "acacia", "bamboo", "birch", "cherry", "crimson",
              "jungle", "mangrove", "oak", "spruce", "warped"]

# ---- consistent block-family layout (bands) -------------------------------
# Within a wood set (and within the stone-like sets) each block family sits on the same
# tier/band in every region, so the armoire, stairs, table, ... are always in the same
# relative spot. Bands are ordered front->back; the 2-tall families (armoires, doors) get their
# own dedicated back band, and the staircase steps up a full 2 blocks over any 2-tall tier (see
# the cumulative-elevation pass in the resolve loop), so a double-height block never hides what's
# behind it. A "slot" is a block's family (path minus material).
def slot_of(path):
    return "/".join(path.split("/")[:-1])

WOOD_BANDS = [
    ["building/beams", "building/covers", "building/general/framed_planks"],
    ["building/slabs/vertical", "building/slabs/bookshelves", "building/slabs/bookshelves/vertical"],
    ["building/stairs/vertical", "building/stairs/bookshelves", "building/stairs/bookshelves/vertical"],
    ["furniture/bookshelves", "furniture/shelves/floating", "furniture/shelves/supported"],
    ["furniture/seating/chairs", "furniture/seating/stools", "furniture/tables"],
    ["containers/crates", "containers/crates/trapped", "containers/barrels",
     "furniture/display_cases", "furniture/display_cases/stripped",
     "furniture/shutters", "furniture/trapdoors/bookshelves", "crops/warped_wart"],
    ["furniture/armoires", "furniture/doors/bookshelves"],          # 2-tall: dedicated back band
]
STONE_BANDS = [
    ["building/beams", "building/covers", "building/general"],
    ["building/slabs", "building/slabs/vertical", "building/slabs/bookshelves", "building/slabs/bookshelves/vertical"],
    ["building/stairs", "building/stairs/vertical", "building/stairs/bookshelves", "building/stairs/bookshelves/vertical"],
    ["building/walls", "furniture/bookshelves"],
]
STONE_BAND_SLOTS = {s for band in STONE_BANDS for s in band}

STONE_RULES = [
    ("red_sandstone", ["red_sandstone"]), ("sandstone", ["sandstone"]),
    ("nether_brick", ["nether_brick"]), ("deepslate", ["deepslate"]),
    ("blackstone", ["blackstone"]), ("basalt", ["basalt"]), ("end_stone", ["end_stone"]),
    ("prismarine", ["prismarine"]), ("purpur", ["purpur"]), ("quartz", ["quartz"]),
    ("tuff", ["tuff"]), ("granite", ["granite"]), ("diorite", ["diorite"]),
    ("andesite", ["andesite"]), ("cobblestone", ["cobblestone"]),
    ("iron", ["iron_block"]), ("gold", ["gold_block"]), ("copper", ["copper"]),
    ("diamond", ["diamond"]), ("netherite", ["netherite"]), ("lapis", ["lapis"]),
    ("redstone", ["redstone"]), ("emerald", ["emerald"]), ("coal", ["coal", "charcoal"]),
    ("amethyst", ["amethyst"]), ("obsidian", ["obsidian"]), ("calcite", ["calcite"]),
    ("netherrack", ["netherrack"]), ("bone", ["bone"]),
    ("stone", ["stone_brick", "smooth_stone"]), ("stone", ["stone"]),
    ("mud", ["mud"]), ("earth", ["dirt", "sand", "gravel", "clay", "soul_sand"]),
    ("brick", ["brick"]),
]

# material region display order (colour regions get spliced in after the woods)
ORDER_WOODS = ["oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove",
               "cherry", "pale_oak", "bamboo", "crimson", "warped"]
ORDER_STONES = ["stone", "cobblestone", "granite", "diorite", "andesite", "deepslate",
                "tuff", "blackstone", "basalt", "sandstone", "red_sandstone", "prismarine",
                "quartz", "purpur", "nether_brick", "end_stone", "netherrack", "obsidian",
                "calcite", "mud", "bone", "brick", "amethyst"]
ORDER_METALS = ["iron", "gold", "copper", "diamond", "netherite", "lapis", "redstone"]
ORDER_TAIL = ["earth", "food", "lighting", "misc"]

LABELS = {
    "dark_oak": "Dark Oak", "pale_oak": "Pale Oak", "red_sandstone": "Red Sandstone",
    "nether_brick": "Nether Brick", "end_stone": "End Stone",
    "food": "Compressed Food", "earth": "Soil & Sand", "misc": "Misc",
}
def label_for(key):
    return LABELS.get(key, key.replace("_", " ").title())

# ---- helpers --------------------------------------------------------------
def is_comp(p):
    pt = p.split("/")
    return pt[:2] == ["building", "compressed"] and pt[-1].endswith("x")

def norm(seg):
    return "stripped_" + seg[:-9] if seg.endswith("_stripped") else seg

def material_name(p):
    pt = p.split("/")
    if is_comp(p):
        return norm(pt[2])
    return norm(pt[-1])

COLOR_SUFFIXES = ("concrete", "terracotta", "glazed_terracotta")

def color_of(token):
    """If `token` is '<colour>_<suffix>' or '<colour>', return (colour, suffix|'')."""
    if token in COLOR_SET:
        return token, ""
    for c in sorted(COLORS, key=len, reverse=True):
        if token.startswith(c + "_"):
            return c, token[len(c) + 1:]
    return None, None

def is_colored(p):
    """Route colour blocks to gradient-by-type regions. Returns (region, column, colour|'plain')
    or None if the block isn't a colour variant."""
    pt = p.split("/")
    if pt[:2] == ["building", "dyed"]:                       # dyed <base> in each colour
        return ("Dyed Blocks", pt[2], pt[3])
    if pt[:2] == ["furniture", "pillows"]:
        return ("Colour Accents", "pillows", pt[2])
    if pt[:2] == ["storage", "dyes"]:
        return ("Colour Accents", "dyes", pt[2])
    if pt[:2] == ["decorations", "candles"]:
        name = pt[2]                                         # <colour>_votive_candle / plain_votive_candle
        col = name[:-len("_votive_candle")]
        return ("Colour Accents", "votive candles", col)
    if pt[:3] == ["building", "general", "wax"]:
        return ("Colour Accents", "wax", pt[3])
    if pt[0] == "building" and pt[1] in ("beams", "covers"):
        col, suf = color_of(pt[-1])
        if col and suf in COLOR_SUFFIXES:
            return ("Colored Building", f"{pt[1]} {suf}", col)
    if is_comp(p):
        col, suf = color_of(pt[2])
        if col and suf in COLOR_SUFFIXES:
            tier = pt[-1]
            title = {"concrete": "Concrete", "terracotta": "Terracotta",
                     "glazed_terracotta": "Glazed Terracotta"}[suf]
            return (f"Compressed {title}", tier, col)
    return None

def canonical(p):
    """Bucket a non-colour block into its material region key."""
    mn = material_name(p)
    pt = p.split("/")
    if mn.endswith("_open"):
        return ""
    if pt[:2] == ["storage", "compressed"]:
        return "food"
    if pt[:2] == ["decorations", "lighting"]:
        return "lighting"
    if pt[:2] == ["decorations", "misc"]:
        return "misc"
    if pt[0] == "crops":
        return "warped"
    if len(pt) > 1 and pt[1] == "cauldrons":
        return "misc"
    if "egg_crate" in mn:
        return "misc"
    if mn == "plain":
        return "misc"
    for w in WOOD_BASES:
        if mn == w or mn.startswith("stripped_" + w):
            return w
        if mn.startswith(w + "_") and mn[len(w) + 1:] in ("log", "planks", "stem", "mosaic", "wood", "hyphae"):
            return w
    for bucket, tests in STONE_RULES:
        if any(t in mn for t in tests):
            return bucket
    raise SystemExit(f"unmapped material for {p!r} (material_name={mn!r}); extend canonical()")

# ---- facing / state -------------------------------------------------------
def block_state(path):
    if path.startswith("decorations/candles/"):
        return ("candles=4,lit=false", None)                # show 4 candles per block
    if path.startswith("building/stairs/") and "/vertical/" in path:
        return ("facing=south", None)                       # vertical stairs face the viewer
    if path.startswith("building/stairs/"):
        return ("facing=north", None)                       # regular stairs rotated 180
    if path.startswith("building/slabs/") and "/vertical/" in path:
        return ("facing=east", None)
    if path.startswith("building/covers/"):
        return ("facing=south", None)
    if path.startswith("furniture/seating/chairs/"):
        return ("facing=south", None)
    if path.startswith("containers/barrels/") or path == "containers/glass_jar":
        return ("facing=south", None)
    if path.startswith("furniture/trapdoors/"):
        return ("facing=south,half=bottom,open=true", None)
    if path.startswith("furniture/armoires/"):
        return ("facing=north,half=lower", "facing=north,half=upper")
    if path.startswith("furniture/doors/"):
        return ("facing=south,half=lower,hinge=left,open=false",
                "facing=south,half=upper,hinge=left,open=false")
    return ("", None)

# ---- glass jar contents ---------------------------------------------------
GLASS_JAR = "minekea:containers/glass_jar"
JAR_FLUID_FILL = 8.0
MOB_NBT = {
    "minecraft:allay": "NoGravity:1b,Health:20.0f", "minecraft:bat": "BatFlags:0b,Health:6.0f",
    "minecraft:bee": "Health:10.0f,Age:0", "minecraft:endermite": "Health:8.0f,Lifetime:0",
    "minecraft:silverfish": "Health:8.0f", "minecraft:slime": "Size:0,Health:1.0f",
    "minecraft:vex": "NoGravity:1b,Health:14.0f",
}
jar_kind_by_id = {}
def jar_nbt(contents_id):
    kind = jar_kind_by_id.get(contents_id, "")
    if kind == "item":
        return '{StoredItem:"%s",StoredItemQty:64,FullItemStacks:7}' % contents_id
    if kind == "fluid":
        return '{StoredFluid:"%s",StoredFluidAmount:%rd}' % (contents_id, JAR_FLUID_FILL)
    if kind == "mob":
        return '{"minecraft:entity_data":{id:"%s",%s}}' % (contents_id, MOB_NBT.get(contents_id, "NoGravity:1b"))
    return ""

# ---- load inventory -------------------------------------------------------
paths = []
with open(os.path.join(HERE, "block_inventory.txt"), encoding="utf-8") as fh:
    for line in fh:
        p = line.strip()
        if p:
            paths.append(p)
for p in ["containers/cauldrons/honey", "containers/cauldrons/milk",
          "decorations/lighting/endless_rod",
          "storage/egg_crate", "storage/brown_egg_crate", "storage/blue_egg_crate"]:
    if p not in paths:
        paths.append(p)

# Compressed storage items that also have a "bagged" (burlap-sack) model — the demo shows both
# forms (is_bagged=false + is_bagged=true). Source of truth: the blockstate JSONs carrying an
# `is_bagged=true` variant (grep 'is_bagged=true' .../blockstates/storage/compressed/*.json).
BAGGABLE_STORAGE = {
    "beetroot", "beetroot_seeds", "carrot", "chorus_fruit", "gold_nugget", "iron_nugget",
    "melon_seeds", "phantom_membrane", "potato", "pumpkin_seeds", "sugar", "wheat_seeds",
}

# ---- classify -------------------------------------------------------------
mat_flats = defaultdict(list)                    # region key -> [(path, state, upper)]
mat_comp  = defaultdict(lambda: defaultdict(dict))   # region -> submaterial -> {tier:path}
col_cols  = defaultdict(lambda: defaultdict(dict))   # colour region -> column -> {colour: path}

for p in sorted(paths):
    if material_name(p).endswith("_open"):
        continue
    col = is_colored(p)
    if col:
        region, column, colour = col
        col_cols[region][column][colour] = "minekea:" + p
        continue
    key = canonical(p)
    if key == "":
        continue
    if is_comp(p):
        sub = material_name(p)
        mat_comp[key][sub][int(p.split("/")[-1][:-1])] = "minekea:" + p
    elif p.split("/")[:2] == ["storage", "compressed"] and p.split("/")[-1] in BAGGABLE_STORAGE:
        # baggable compressed item: show both its plain block and its burlap-sack (bagged) form
        mat_flats[key].append(("minekea:" + p, "is_bagged=false", None))
        mat_flats[key].append(("minekea:" + p, "is_bagged=true", None))
    else:
        state, upper = block_state(p)
        mat_flats[key].append(("minekea:" + p, state, upper))

# In the compressed-food region, group the baggable items (each an unbagged+bagged pair, kept
# adjacent) first, then the rest. With an even FLAT_COLS this keeps every pair on one row.
if "food" in mat_flats:
    bagged = [t for t in mat_flats["food"] if "is_bagged" in t[1]]
    plain = [t for t in mat_flats["food"] if "is_bagged" not in t[1]]
    mat_flats["food"] = bagged + plain

# glass jars
jar_items, jar_misc = [], []
jar_csv = os.path.join(HERE, "glass_jar_contents.csv")
if os.path.exists(jar_csv):
    with open(jar_csv, encoding="utf-8") as fh:
        for row in csv.DictReader(fh):
            jar_kind_by_id[row["id"]] = row["kind"]
            (jar_items if row["kind"] == "item" else jar_misc).append((row["id"], row["label"]))

# ---- build region item lists ---------------------------------------------
# each region -> list of items: (dx, depth, block_id, state, upper, contents, clabel)
regions = OrderedDict()

# which regions get the consistent-band treatment, + wood per-slot reserved widths
WOOD_KEYS = set(ORDER_WOODS)
def _variant_key(bid):
    """Sort a family's variants so an un-stripped block always precedes its stripped form,
    keyed by base material. A plain path sort breaks this for 'warped' (stripped_warped < warped),
    which is why only that set showed stripped-first."""
    seg = bid.rsplit("/", 1)[-1]
    if seg.startswith("stripped_"):
        return (seg[len("stripped_"):], 1, seg)
    if seg.endswith("_stripped"):
        return (seg[:-len("_stripped")], 1, seg)
    return (seg, 0, seg)
def _slots_of(key):
    d = defaultdict(list)
    for (bid, state, upper) in mat_flats.get(key, []):
        d[slot_of(bid.split(":", 1)[1])].append((bid, state, upper))
    for s in d:
        d[s].sort(key=lambda t: _variant_key(t[0]))
    return d
WOOD_RESERVE = defaultdict(int)                     # slot -> max count across the wood sets
for _k in WOOD_KEYS:
    for _s, _blk in _slots_of(_k).items():
        WOOD_RESERVE[_s] = max(WOOD_RESERVE[_s], len(_blk))
STONE_LIKE = {k for k in ORDER_STONES
              if _slots_of(k).keys() & STONE_BAND_SLOTS}   # stones w/ stairs/slabs/bookshelves

def banded_flat_items(key, bands, reserve):
    """Place flats so each family lands on its fixed band/tier (same relative spot in every
    region of the group). `reserve` (wood) pads each slot to its group max for exact column
    alignment; None (stone) just packs. Returns (items, tiers_used)."""
    bs = _slots_of(key)
    used, items = set(), []
    for depth, band in enumerate(bands):
        dx = 0
        for slot in band:
            used.add(slot)
            blocks = bs.get(slot, [])
            for i, (bid, state, upper) in enumerate(blocks):
                items.append((dx + i, depth, bid, state, upper, "", ""))
            dx += reserve[slot] if reserve else len(blocks)
    depth_used = len(bands)
    for slot in sorted(s for s in bs if s not in used):      # safety: any unbanded family
        for i, (bid, state, upper) in enumerate(bs[slot]):
            items.append((i, depth_used, bid, state, upper, "", ""))
        depth_used += 1
    return items, depth_used

def material_items(key):
    if key in WOOD_KEYS:
        items, flat_rows = banded_flat_items(key, WOOD_BANDS, WOOD_RESERVE)
    elif key in STONE_LIKE:
        items, flat_rows = banded_flat_items(key, STONE_BANDS, None)
    else:
        items = []
        flats = mat_flats.get(key, [])
        for i, (bid, state, upper) in enumerate(flats):
            items.append((i % FLAT_COLS, i // FLAT_COLS, bid, state, upper, "", ""))
        flat_rows = math.ceil(len(flats) / FLAT_COLS) if flats else 0
    subs = sorted(mat_comp.get(key, {}))
    for pi, sub in enumerate(subs):
        tiers = mat_comp[key][sub]
        pc, pr = pi % POD_COLS, pi // POD_COLS
        base_dx = pc * (POD_SIZE + POD_GAP)
        base_depth = flat_rows + pr * POD_SIZE          # each podium-row sits 3 higher
        for t in range(3):
            for j in range(3):
                items.append((base_dx + j, base_depth + t, tiers[t * 3 + j + 1], "", None, "", ""))
    return items

def color_items(region):
    cols = col_cols[region]
    col_order = sorted(cols)
    if region == "Colour Accents":
        col_order = [c for c in ["pillows", "dyes", "votive candles", "wax"] if c in cols]
    elif region == "Colored Building":
        col_order = [c for c in ["beams concrete", "covers concrete", "beams terracotta",
                                  "covers terracotta", "beams glazed_terracotta",
                                  "covers glazed_terracotta"] if c in cols]
    elif region.startswith("Compressed "):
        col_order = sorted(cols, key=lambda t: int(t[:-1]))     # 1x..9x
    has_plain = any("plain" in cols[c] for c in col_order)
    plain_off = 1 if has_plain else 0
    items = []
    for ci, column in enumerate(col_order):
        dx = ci * 2                                     # 1-block gap between type columns
        variants = cols[column]
        if "plain" in variants:
            bid = variants["plain"]
            st, up = block_state(bid.split(":", 1)[1])
            items.append((dx, 0, bid, st, up, "", ""))
        for k, colour in enumerate(COLORS):
            if colour not in variants:
                continue
            bid = variants[colour]
            st, up = block_state(bid.split(":", 1)[1])
            items.append((dx, plain_off + k, bid, st, up, "", ""))
    return items

def jar_items_list(cells):
    items = []
    for i, (cid, clabel) in enumerate(cells):
        dx = i % FLAT_COLS
        depth = i // FLAT_COLS
        items.append((dx, depth, GLASS_JAR, "facing=south", None, cid, clabel))
    return items

def register(key, label, items, flat=False):
    if not items:
        return
    w = max(dx for (dx, *_ ) in items) + 1
    maxdepth = max(depth for (_, depth, *_) in items)
    regions[key] = {"label": f"{label} ({len(items)})", "items": items,
                    "w": w, "maxdepth": maxdepth, "flat": flat}

# assemble in display order
for k in ORDER_WOODS:
    register(k, label_for(k), material_items(k))
for region in ["Dyed Blocks", "Colored Building", "Colour Accents",
               "Compressed Concrete", "Compressed Terracotta", "Compressed Glazed Terracotta"]:
    if region in col_cols:
        register(region, region, color_items(region), flat=True)   # colours stay flat
for k in ORDER_STONES + ORDER_METALS + ORDER_TAIL:
    if k in mat_flats or k in mat_comp:
        register(k, label_for(k), material_items(k))
register("jars_items", "Glass Jars - Items", jar_items_list(jar_items))
register("jars_misc", "Glass Jars - Fluids & Mobs", jar_items_list(jar_misc))

# ---- flow regions into rows ----------------------------------------------
cursor_x, cursor_z, row_depth = ORIGIN_X, ORIGIN_Z, 0
for rg in regions.values():
    d = FRONT_ROWS + rg["maxdepth"] + 1
    if cursor_x != ORIGIN_X and (cursor_x - ORIGIN_X) + rg["w"] > ROW_WIDTH:
        cursor_z -= row_depth + ROW_AISLE
        cursor_x = ORIGIN_X
        row_depth = 0
    rg["ox"], rg["oz"], rg["d"] = cursor_x, cursor_z, d
    cursor_x += rg["w"] + AISLE
    row_depth = max(row_depth, d)

# ---- resolve to absolute placements ---------------------------------------
placements, platforms = [], []              # platforms = (x1,y1,z1,x2,y2,z2) andesite fill boxes
for key, rg in regions.items():
    ox, oz = rg["ox"], rg["oz"]
    flat = rg["flat"]
    W = rg["w"]
    # Cumulative elevation: each tier rises by the *height* of the tier in front of it (2 if that
    # tier holds a double-tall block, else 1), so whatever sits behind a 2-tall tier clears it.
    elev = None
    if not flat:
        md = rg["maxdepth"]
        tallness = [1] * (md + 1)
        used = set()
        for (_dx, depth, _b, _s, upper, _c, _cl) in rg["items"]:
            used.add(depth)
            if upper:
                tallness[depth] = 2
        elev, acc = [0] * (md + 1), 0
        for d in range(md + 1):
            elev[d] = acc
            acc += tallness[d]
        # Fill each tier's floor to the FULL region width (a solid step), so the empty spots
        # between the real blocks read as one continuous stairstep instead of ragged columns.
        for d in sorted(used):
            if elev[d] > 0:
                wz = oz - (FRONT_ROWS + d)
                platforms.append((ox, FLOOR_Y + 1, wz, ox + W - 1, FLOOR_Y + elev[d], wz))
    for (dx, depth, bid, state, upper, contents, clabel) in rg["items"]:
        wx = ox + dx
        wz = oz - (FRONT_ROWS + depth)
        wy = FLOOR_Y + 1 if flat else FLOOR_Y + 1 + elev[depth]
        placements.append({"block_id": bid, "x": wx, "y": wy, "z": wz,
                           "region": key, "material": rg["label"], "depth": depth,
                           "state": state or "", "upper": upper or "",
                           "contents": contents, "contents_label": clabel})

# ---- arena bounds (front-left corner = -192,55,191) -----------------------
all_x = [p["x"] for p in placements] + [b[0] for b in platforms] + [b[3] for b in platforms]
all_z = [p["z"] for p in placements] + [b[2] for b in platforms]
X0, X1 = ORIGIN_X - 1, max(all_x) + 1                   # X0 = west border at -192 (blocks framed inside)
Z0, Z1 = ORIGIN_Z + 1, min(all_z) - 1                   # Z0 = south/front (191), Z1 = north/back

# ---- manifest CSV ---------------------------------------------------------
fields = ["block_id", "x", "y", "z", "region", "material", "depth", "state", "upper",
          "contents", "contents_label"]
with open(os.path.join(HERE, "demo_layout_manifest.csv"), "w", newline="", encoding="utf-8") as fh:
    w = csv.DictWriter(fh, fieldnames=fields)
    w.writeheader()
    w.writerows(placements)

# ---- mcfunction -----------------------------------------------------------
def fill(x1, y1, z1, x2, y2, z2, b):
    return f"fill {x1} {y1} {z1} {x2} {y2} {z2} {b}"

lines = ["# Minekea demo world - by-material tiered layout (generated by generate_layout.py)",
         f"# arena X[{X0}..{X1}] Z[{Z1}..{Z0}] floor y={FLOOR_Y}; front-left corner ({X0},{FLOOR_Y},{Z0})", ""]
# 0) wipe the build area to air (128x128 x CLEAR_HEIGHT), anchored at the front-left corner,
#    then remove any items knocked loose - so a re-run starts from a clean slate.
#    /fill caps at 32768 blocks; 128*128*2 = 32768, so clear two y-layers per command.
cx0, cx1 = X0, X0 + CLEAR_SIZE - 1
cz0, cz1 = Z0, Z0 - (CLEAR_SIZE - 1)          # cz0 = front/south, cz1 = back/north
top = FLOOR_Y + CLEAR_HEIGHT
lines.append(f"# clear {CLEAR_SIZE}x{CLEAR_SIZE} area to air (y {FLOOR_Y}..{top}) then kill dropped items")
for y in range(FLOOR_Y, top + 1, 2):
    lines.append(fill(cx0, y, cz1, cx1, min(y + 1, top), cz0, "air"))
lines.append("kill @e[type=item]")
lines.append("")
lines.append(f"# lay the whole cleared floor as smooth sandstone (the build sits on top)")
lines.append(fill(cx0, FLOOR_Y, cz1, cx1, FLOOR_Y, cz0, "minecraft:smooth_sandstone"))
lines.append("")
lines.append("# floor base (smooth stone) covering arena + border")
lines.append(fill(X0, FLOOR_Y, Z1, X1, FLOOR_Y, Z0, BORDER_BLOCK))
lines.append("")
lines.append("# region floor pads (polished andesite)")
for rg in regions.values():
    ox, oz = rg["ox"], rg["oz"]
    lines.append(fill(ox, FLOOR_Y, oz - (rg["d"] - 1), ox + rg["w"] - 1, FLOOR_Y, oz, FLOOR_BLOCK))
lines.append("")
lines.append(f"# staircase tier platforms ({len(platforms)})")
for (x1, y1, z1, x2, y2, z2) in platforms:
    lines.append(fill(x1, y1, z1, x2, y2, z2, FLOOR_BLOCK))
lines.append("")
disp, upper_halves, jars_filled = [], 0, 0
for p in placements:
    suffix = f"[{p['state']}]" if p["state"] else ""
    nbt = jar_nbt(p["contents"]) if p["block_id"] == GLASS_JAR else ""
    if nbt:
        jars_filled += 1
    disp.append(f"setblock {p['x']} {p['y']} {p['z']} {p['block_id']}{suffix}{nbt}")
    if p["upper"]:
        disp.append(f"setblock {p['x']} {p['y'] + 1} {p['z']} {p['block_id']}[{p['upper']}]")
        upper_halves += 1
lines.append(f"# display blocks ({len(placements)} + {upper_halves} upper halves)")
lines.extend(disp)
lines.append("")
lines.append(f"# region label signs + teleport command blocks + plates ({len(regions)})")
for rg in regions.values():
    ox, oz = rg["ox"], rg["oz"]
    msg = rg["label"].replace('"', "'")
    lines.append(f'setblock {ox} {FLOOR_Y + 1} {oz} {SIGN_BLOCK}[rotation=0]'
                 f'{{front_text:{{messages:[\'"{msg}"\',\'""\',\'""\',\'""\']}}}}')
    cx = ox + 1
    vx = ox + rg["w"] / 2.0
    vz = min(oz + VANTAGE, Z0)                           # stay on the arena floor
    lines.append(f'setblock {cx} {FLOOR_Y} {oz} minecraft:command_block[facing=up]'
                 f'{{Command:"tp @p {vx:.1f} {FLOOR_Y + 1} {vz} 180 0",auto:0b}}')
    lines.append(f"setblock {cx} {FLOOR_Y + 1} {oz} minecraft:stone_pressure_plate")
with open(os.path.join(HERE, "demo_build.mcfunction"), "w", encoding="utf-8") as fh:
    fh.write("\n".join(lines) + "\n")

# ---- region list ----------------------------------------------------------
with open(os.path.join(HERE, "layout_regions.txt"), "w", encoding="utf-8") as fh:
    fh.write("Minekea demo world - by-material tiered regions (walking order)\n")
    fh.write(f"arena X[{X0}..{X1}] Z[{Z1}..{Z0}] floor y={FLOOR_Y}, front-left ({X0},{FLOOR_Y},{Z0})\n\n")
    fh.write(f"{'region':30s} {'w':>3s} {'d':>3s} {'tall':>4s}  front-left\n")
    for key, rg in regions.items():
        tall = 1 if rg["flat"] else rg["maxdepth"] + 1
        fh.write(f"{rg['label']:30s} {rg['w']:3d} {rg['d']:3d} {tall:4d}  ({rg['ox']},{FLOOR_Y},{rg['oz']})\n")

# ---- stats ----------------------------------------------------------------
stats = {"summary": {"arena_x": [X0, X1], "arena_z": [Z1, Z0], "floor_y": FLOOR_Y,
                     "front_left": [X0, FLOOR_Y, Z0],
                     "width": X1 - X0 + 1, "depth": Z0 - Z1 + 1, "regions": len(regions),
                     "display_blocks": len(placements), "tier_platforms": len(platforms),
                     "jars_filled": jars_filled, "command_blocks": len(regions)},
         "regions": [{"region": k, "label": rg["label"], "front_left": [rg["ox"], FLOOR_Y, rg["oz"]],
                      "w": rg["w"], "d": rg["d"],
                      "tall": 1 if rg["flat"] else rg["maxdepth"] + 1} for k, rg in regions.items()]}
with open(os.path.join(HERE, "layout_stats.json"), "w", encoding="utf-8") as fh:
    json.dump(stats, fh, indent=2)

print(f"regions         : {len(regions)}")
print(f"display blocks  : {len(placements)}  (+{upper_halves} upper halves)")
print(f"tier platforms  : {len(platforms)}")
print(f"jars filled     : {jars_filled}")
print(f"tallest region  : {max((1 if rg['flat'] else rg['maxdepth'] + 1) for rg in regions.values())} blocks")
print(f"arena           : X[{X0}..{X1}] ({X1-X0+1} wide)  Z[{Z1}..{Z0}] ({Z0-Z1+1} deep)")
print(f"front-left      : ({X0},{FLOOR_Y},{Z0})")
print(f"mcfunction lines: {len(lines)}")

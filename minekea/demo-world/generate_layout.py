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
teleport vantages never land inside the next row. Arena front-left corner = (-191,55,191).

Outputs: demo_layout_manifest.csv, layout_regions.txt, layout_stats.json, demo_build.mcfunction
Regenerate with:  python generate_layout.py
"""
import csv, json, math, os
from collections import OrderedDict, defaultdict

HERE = os.path.dirname(os.path.abspath(__file__))

# ---- arena geometry -------------------------------------------------------
ORIGIN_X = -190           # first region's left (west) edge; +1 border makes the corner -191
ORIGIN_Z =  190           # first region's front row; +1 border makes the front edge 191
FLOOR_Y  =   55           # floor pads; display blocks start at y=56 (depth 0)
ROW_WIDTH = 124           # wrap to a new row once a row grows past this many blocks
AISLE     =    2          # gap between regions within a row
ROW_AISLE =    3          # gap between rows (so teleport vantages land in the aisle, not in blocks)
VANTAGE   =    2          # teleport this many blocks in front (south) of a region's front row

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
    if path.startswith("building/stairs/"):
        return ("facing=north", None)
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
    else:
        state, upper = block_state(p)
        mat_flats[key].append(("minekea:" + p, state, upper))

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

def material_items(key):
    items = []
    flats = mat_flats.get(key, [])
    for i, (bid, state, upper) in enumerate(flats):
        dx = i % FLAT_COLS
        depth = i // FLAT_COLS
        items.append((dx, depth, bid, state, upper, "", ""))
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
placements, supports = [], []
for key, rg in regions.items():
    ox, oz = rg["ox"], rg["oz"]
    flat = rg["flat"]
    for (dx, depth, bid, state, upper, contents, clabel) in rg["items"]:
        wx = ox + dx
        wz = oz - (FRONT_ROWS + depth)
        wy = FLOOR_Y + 1 if flat else FLOOR_Y + 1 + depth
        if not flat:
            for yy in range(FLOOR_Y + 1, wy):           # support column beneath the step
                supports.append((wx, yy, wz))
        placements.append({"block_id": bid, "x": wx, "y": wy, "z": wz,
                           "region": key, "material": rg["label"], "depth": depth,
                           "state": state or "", "upper": upper or "",
                           "contents": contents, "contents_label": clabel})

# ---- arena bounds (front-left corner = -191,55,191) -----------------------
all_x = [p["x"] for p in placements] + [s[0] for s in supports]
all_z = [p["z"] for p in placements] + [s[2] for s in supports]
X0, X1 = ORIGIN_X - 1, max(all_x) + 1
Z0, Z1 = ORIGIN_Z + 1, min(all_z) - 1                    # Z0 = south/front (191), Z1 = north/back

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
lines.append("# floor base (smooth stone) covering arena + border")
lines.append(fill(X0, FLOOR_Y, Z1, X1, FLOOR_Y, Z0, BORDER_BLOCK))
lines.append("")
lines.append("# region floor pads (polished andesite)")
for rg in regions.values():
    ox, oz = rg["ox"], rg["oz"]
    lines.append(fill(ox, FLOOR_Y, oz - (rg["d"] - 1), ox + rg["w"] - 1, FLOOR_Y, oz, FLOOR_BLOCK))
lines.append("")
lines.append(f"# staircase supports ({len(supports)})")
for (x, y, z) in supports:
    lines.append(f"setblock {x} {y} {z} {FLOOR_BLOCK}")
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
                     "display_blocks": len(placements), "supports": len(supports),
                     "jars_filled": jars_filled, "command_blocks": len(regions)},
         "regions": [{"region": k, "label": rg["label"], "front_left": [rg["ox"], FLOOR_Y, rg["oz"]],
                      "w": rg["w"], "d": rg["d"],
                      "tall": 1 if rg["flat"] else rg["maxdepth"] + 1} for k, rg in regions.items()]}
with open(os.path.join(HERE, "layout_stats.json"), "w", encoding="utf-8") as fh:
    json.dump(stats, fh, indent=2)

print(f"regions         : {len(regions)}")
print(f"display blocks  : {len(placements)}  (+{upper_halves} upper halves)")
print(f"supports        : {len(supports)}")
print(f"jars filled     : {jars_filled}")
print(f"tallest region  : {max((1 if rg['flat'] else rg['maxdepth'] + 1) for rg in regions.values())} blocks")
print(f"arena           : X[{X0}..{X1}] ({X1-X0+1} wide)  Z[{Z1}..{Z0}] ({Z0-Z1+1} deep)")
print(f"front-left      : ({X0},{FLOOR_Y},{Z0})")
print(f"mcfunction lines: {len(lines)}")

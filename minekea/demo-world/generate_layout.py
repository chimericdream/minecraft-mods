#!/usr/bin/env python3
"""
Minekea demo-world layout generator  (by-material edition)

Groups every Minekea block by MATERIAL rather than by block family: all the acacia
things sit together, all the granite things together, all the black things together,
and so on. Each material gets its own flexible-size region (sized to its contents):

    * flat blocks (stairs, slabs, furniture, ...) fill a grid at the front of the region
    * compressed blocks (1x-9x) are shown as small stepped 3x3 podiums behind them
      (tier 0 = 1x/2x/3x on the ground, tier +1 = 4x/5x/6x, tier +2 = 7x/8x/9x)
    * a label sign + a teleport command block (with a stone pressure plate) sit at the
      region's front so you can jump straight to a screenshot vantage

Materials that only exist in compressed form and don't belong to any real material
(metals/gems, plus soil-type blocks) get their own small regions too. Glass jars keep
their own dedicated section (built from glass_jar_contents.csv).

Regions flow left->right (west->east, +X) in the given display order; when a row fills
past ROW_WIDTH they wrap to a new row further back (north, -Z). The whole arena is sized
to fit and its bounds are printed / written to layout_stats.json.

Outputs (all in this folder):
    demo_layout_manifest.csv   one row per placed block, absolute coords
    layout_regions.txt         region list: material -> footprint + command block
    layout_stats.json          machine-readable arena/region summary
    demo_build.mcfunction      floor + every block + podium supports + signs + cmd blocks
Regenerate with:  python generate_layout.py
(refresh block_inventory.txt / glass_jar_contents.csv first if the mod changed)
"""
import csv, json, math, os
from collections import OrderedDict, defaultdict

HERE = os.path.dirname(os.path.abspath(__file__))

# ---- arena geometry -------------------------------------------------------
ORIGIN_X = -191            # west edge (left), region flow starts here
ORIGIN_Z =  190            # front/south edge; rows march north (-Z) as they wrap
FLOOR_Y  =   55            # floor blocks; displays sit at FLOOR_Y+1
ROW_WIDTH = 124            # wrap regions to a new row once a row grows past this many blocks
AISLE     =    2           # blocks of gap between regions and between rows
VIEW_APRON =   5           # floored strip in front (south) so front-row teleport vantages land safely

FLOOR_BLOCK   = "minecraft:polished_andesite"   # region floor pads
BORDER_BLOCK  = "minecraft:smooth_stone"        # aisles + podium step supports
SIGN_BLOCK    = "minecraft:oak_sign"

# region interior packing
FLAT_COLS = 12             # flat display blocks per row inside a region
POD_COLS  = 3             # stepped podiums per row inside a region
POD_SIZE  = 3             # podium footprint is POD_SIZE x POD_SIZE (3 for the 3 tiers)
POD_GAP   = 1             # empty blocks between podiums
FRONT_ROWS = 1            # region rows kept clear at the front (sign / cmd / standing)

# ---- material grouping ----------------------------------------------------
COLORS = ["white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
          "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"]
WOOD_BASES = ["dark_oak", "pale_oak", "acacia", "bamboo", "birch", "cherry", "crimson",
              "jungle", "mangrove", "oak", "spruce", "warped"]

# ordered stone/mineral/metal base rules (metals BEFORE the generic "stone" catch-all,
# since e.g. "redstone_block" contains the substring "stone"); first hit wins.
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

# region display order (walking order); glass jars appended at the end
ORDER = (["oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry",
          "pale_oak", "bamboo", "crimson", "warped"]
         + COLORS
         + ["stone", "cobblestone", "granite", "diorite", "andesite", "deepslate", "tuff",
            "blackstone", "basalt", "sandstone", "red_sandstone", "prismarine", "quartz",
            "purpur", "nether_brick", "end_stone", "netherrack", "obsidian", "calcite",
            "mud", "bone", "brick", "amethyst"]
         + ["iron", "gold", "copper", "diamond", "netherite", "lapis", "redstone"]
         + ["earth", "food", "lighting", "misc"])

LABELS = {
    "dark_oak": "Dark Oak", "pale_oak": "Pale Oak", "light_blue": "Light Blue",
    "light_gray": "Light Gray", "red_sandstone": "Red Sandstone",
    "nether_brick": "Nether Brick", "end_stone": "End Stone",
    "food": "Compressed Food", "earth": "Soil & Sand", "misc": "Misc",
}
def label_for(key):
    return LABELS.get(key, key.replace("_", " ").title())

def is_comp(p):
    pt = p.split("/")
    return pt[:2] == ["building", "compressed"] and pt[-1].endswith("x")

def norm(seg):
    return "stripped_" + seg[:-9] if seg.endswith("_stripped") else seg

def material_name(p):
    pt = p.split("/")
    if is_comp(p):
        return norm(pt[2])
    if pt[:2] == ["building", "dyed"]:
        return pt[-1]                       # dyed blocks group by their colour
    return norm(pt[-1])

def canonical(p):
    """Bucket a block path into its material region key ('' -> drop)."""
    mn = material_name(p)
    pt = p.split("/")
    if mn.endswith("_open"):
        return ""                           # shutter open-halves: internal, never placed
    if pt[:2] == ["storage", "compressed"]:
        return "food"
    if pt[:2] == ["decorations", "lighting"]:
        return "lighting"
    if pt[:2] == ["decorations", "misc"]:
        return "misc"
    if pt[0] == "crops":
        return "warped"                     # warped wart -> the warped region
    if len(pt) > 1 and pt[1] == "cauldrons":
        return "misc"                       # honey/milk cauldrons
    if "egg_crate" in mn:
        return "misc"                       # egg crates
    if pt[-1] == "glass_jar":
        return "jars"                       # handled separately from block_inventory
    if mn == "plain":                       # plain (uncoloured) wax / votive candle
        return "misc"
    if mn.endswith("_votive_candle"):
        c = mn[:-len("_votive_candle")]
        return c if c in COLORS else "misc"
    if mn in COLORS:
        return mn
    for c in sorted(COLORS, key=len, reverse=True):
        if mn.startswith(c + "_") and mn[len(c) + 1:] in ("concrete", "terracotta", "glazed_terracotta"):
            return c
    for w in WOOD_BASES:
        if mn == w or mn.startswith("stripped_" + w):
            return w
        if mn.startswith(w + "_") and mn[len(w) + 1:] in ("log", "planks", "stem", "mosaic", "wood", "hyphae"):
            return w
    for bucket, tests in STONE_RULES:
        if any(t in mn for t in tests):
            return bucket
    raise SystemExit(f"unmapped material for {p!r} (material_name={mn!r}); extend canonical()")

# ---- facing / state (unchanged from the tiled generator) ------------------
def block_state(path):
    """Orient a block's front toward the viewer (standing at the front looking north)."""
    if path.startswith("building/stairs/"):
        return ("facing=north", None)                       # rotated 180 from south
    if path.startswith("building/slabs/") and "/vertical/" in path:
        return ("facing=east", None)                        # vertical slabs, 90 CCW
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

# ---- glass jar contents (unchanged) ---------------------------------------
GLASS_JAR = "minekea:containers/glass_jar"
JAR_FLUID_FILL = 8.0
MOB_NBT = {
    "minecraft:allay":      "NoGravity:1b,Health:20.0f",
    "minecraft:bat":        "BatFlags:0b,Health:6.0f",
    "minecraft:bee":        "Health:10.0f,Age:0",
    "minecraft:endermite":  "Health:8.0f,Lifetime:0",
    "minecraft:silverfish": "Health:8.0f",
    "minecraft:slime":      "Size:0,Health:1.0f",
    "minecraft:vex":        "NoGravity:1b,Health:14.0f",
}
jar_kind_by_id = {}
def jar_nbt(contents_id):
    kind = jar_kind_by_id.get(contents_id, "")
    if kind == "item":
        return '{StoredItem:"%s",StoredItemQty:64,FullItemStacks:7}' % contents_id
    if kind == "fluid":
        return '{StoredFluid:"%s",StoredFluidAmount:%rd}' % (contents_id, JAR_FLUID_FILL)
    if kind == "mob":
        fields = MOB_NBT.get(contents_id, "NoGravity:1b")
        return '{"minecraft:entity_data":{id:"%s",%s}}' % (contents_id, fields)
    return ""

# ---- load inventory -------------------------------------------------------
paths = []
with open(os.path.join(HERE, "block_inventory.txt"), encoding="utf-8") as fh:
    for line in fh:
        p = line.strip()
        if p:
            paths.append(p)

HAND_AUTHORED = [
    "containers/cauldrons/honey", "containers/cauldrons/milk",
    "decorations/lighting/endless_rod",
    "storage/egg_crate", "storage/brown_egg_crate", "storage/blue_egg_crate",
]
for p in HAND_AUTHORED:
    if p not in paths:
        paths.append(p)

# ---- assemble regions -----------------------------------------------------
# region -> {"flats":[cell...], "podiums":[(sub, [9 paths 1x..9x])...]}
flats = defaultdict(list)          # cell = (block_id, state, upper, contents, clabel)
comp_tiers = defaultdict(lambda: defaultdict(dict))   # region -> sub -> {tier:path}

for p in sorted(paths):
    key = canonical(p)
    if key in ("", "jars"):
        continue
    if is_comp(p):
        sub = material_name(p)
        tier = int(p.split("/")[-1][:-1])
        comp_tiers[key][sub][tier] = "minekea:" + p
    else:
        state, upper = block_state(p)
        flats[key].append(("minekea:" + p, state, upper, "", ""))

# glass jar contents -> two jar regions
jar_items, jar_misc = [], []
jar_csv = os.path.join(HERE, "glass_jar_contents.csv")
if os.path.exists(jar_csv):
    with open(jar_csv, encoding="utf-8") as fh:
        for row in csv.DictReader(fh):
            jar_kind_by_id[row["id"]] = row["kind"]
            cell = (GLASS_JAR, "facing=south", None, row["id"], row["label"])
            (jar_items if row["kind"] == "item" else jar_misc).append(cell)

regions = OrderedDict()
def add_region(key, label, flat_cells, podiums):
    regions[key] = {"label": f"{label} ({len(flat_cells) + sum(9 for _ in podiums)})",
                    "flats": flat_cells, "podiums": podiums}

for key in ORDER:
    if key not in flats and key not in comp_tiers:
        continue
    pods = []
    for sub in sorted(comp_tiers.get(key, {})):
        tiers = comp_tiers[key][sub]
        pods.append((sub, [tiers[t] for t in range(1, 10)]))
    add_region(key, label_for(key), flats.get(key, []), pods)

if jar_items:
    add_region("jars_items", "Glass Jars - Items", jar_items, [])
if jar_misc:
    add_region("jars_misc", "Glass Jars - Fluids & Mobs", jar_misc, [])

# ---- lay out each region's interior ---------------------------------------
# returns (width, depth, placements) with placements relative to the region front-left
# corner: (block_id, dx, dy, dz, state, upper, contents, clabel, floor_kind)
# floor_kind: 'andesite' cells get an andesite pad; podium supports handle their own fill.
def layout_region(rg):
    flat_cells = rg["flats"]
    pods = rg["podiums"]
    placements = []

    nflat = len(flat_cells)
    flat_cols = min(nflat, FLAT_COLS) if nflat else 0
    flat_rows = math.ceil(nflat / flat_cols) if nflat else 0

    npod = len(pods)
    pod_cols = min(npod, POD_COLS) if npod else 0
    pod_rows = math.ceil(npod / pod_cols) if npod else 0
    pod_pitch = POD_SIZE + POD_GAP
    pod_strip_w = pod_cols * pod_pitch - POD_GAP if npod else 0
    pod_strip_d = pod_rows * pod_pitch - POD_GAP if npod else 0

    width = max(flat_cols, pod_strip_w, 3)
    depth = FRONT_ROWS + flat_rows + (1 if (flat_rows and pod_rows) else 0) + pod_strip_d

    # flat blocks: grid at the front, centred horizontally
    x_off = (width - flat_cols) // 2
    for i, (bid, state, upper, contents, clabel) in enumerate(flat_cells):
        dx = x_off + (i % flat_cols)
        dz = FRONT_ROWS + (i // flat_cols)
        placements.append((bid, dx, 1, dz, state, upper, contents, clabel, "andesite"))

    # podiums behind the flats
    pod_base_dz = FRONT_ROWS + flat_rows + (1 if (flat_rows and pod_rows) else 0)
    pod_x_off = (width - pod_strip_w) // 2 if npod else 0
    for pi, (sub, tier_paths) in enumerate(pods):
        pc, pr = pi % pod_cols, pi // pod_cols
        base_dx = pod_x_off + pc * pod_pitch
        base_dz = pod_base_dz + pr * pod_pitch          # front row of this podium
        for t in range(3):                              # tier 0,1,2 -> steps back + up
            for i in range(3):
                path = tier_paths[t * 3 + i]
                dx = base_dx + i
                dz = base_dz + t                        # each higher tier steps one back
                dy = 1 + t
                # solid support column under the step so it reads as a stepped podium
                for yy in range(1, dy):
                    placements.append((BORDER_BLOCK, dx, yy, dz, "", None, "", "", "support"))
                placements.append((path, dx, dy, dz, "", None, "", "", "andesite"))

    return width, depth, placements

for rg in regions.values():
    rg["w"], rg["d"], rg["cells"] = layout_region(rg)

# ---- flow regions into rows ----------------------------------------------
cursor_x = ORIGIN_X
cursor_z = ORIGIN_Z
row_depth = 0
row_start_x = ORIGIN_X
for key, rg in regions.items():
    if cursor_x != row_start_x and (cursor_x - row_start_x) + rg["w"] > ROW_WIDTH:
        # wrap to next row (further north / -Z)
        cursor_z -= row_depth + AISLE
        cursor_x = row_start_x
        row_depth = 0
    rg["ox"] = cursor_x                         # front-left corner (min X)
    rg["oz"] = cursor_z                         # front edge (max Z)
    cursor_x += rg["w"] + AISLE
    row_depth = max(row_depth, rg["d"])

# ---- resolve to absolute placements + command blocks ----------------------
placements = []           # dicts for CSV / mcfunction
supports = []             # (x,y,z,block) podium step supports
signs = []                # (x,y,z,text)
cmd_blocks = []           # (x,y,z) -> command teleports player to view the region

for key, rg in regions.items():
    ox, oz = rg["ox"], rg["oz"]
    for (bid, dx, dy, dz, state, upper, contents, clabel, floor_kind) in rg["cells"]:
        wx = ox + dx
        wz = oz - dz
        wy = FLOOR_Y + dy
        if floor_kind == "support":
            supports.append((wx, wy, wz, BORDER_BLOCK))
            continue
        placements.append({
            "block_id": bid, "x": wx, "y": wy, "z": wz,
            "region": key, "material": rg["label"],
            "state": state, "upper": upper or "",
            "contents": contents, "contents_label": clabel,
        })
    # label sign + command block at the region front-left
    sx, sz = ox, oz
    signs.append((sx, FLOOR_Y + 1, sz, rg["label"]))
    # command block one column right of the sign, teleport to a vantage in front, facing north
    cx = ox + min(rg["w"] - 1, 1)
    view_x = ox + rg["w"] / 2.0
    view_z = oz + 4                       # stand a few blocks in front (south) of the region
    cmd_blocks.append((cx, FLOOR_Y, sz, view_x, FLOOR_Y + 1, view_z))

# ---- arena bounds ---------------------------------------------------------
all_x = [p["x"] for p in placements] + [s[0] for s in supports]
all_z = [p["z"] for p in placements] + [s[2] for s in supports]
min_x, max_x = min(all_x), max(all_x)
min_z, max_z = min(all_z), max(all_z)
# pad by 1 for a border ring
X0, X1 = min_x - 1, max_x + 1
Z1 = min_z - 1                             # north/back edge
Z0 = max(max_z, ORIGIN_Z) + VIEW_APRON    # south/front edge, incl. the front viewing apron

# ---- write manifest CSV ---------------------------------------------------
fields = ["block_id", "x", "y", "z", "region", "material", "state", "upper",
          "contents", "contents_label"]
with open(os.path.join(HERE, "demo_layout_manifest.csv"), "w", newline="", encoding="utf-8") as fh:
    w = csv.DictWriter(fh, fieldnames=fields)
    w.writeheader()
    w.writerows(placements)

# ---- build mcfunction -----------------------------------------------------
def fill(x1, y1, z1, x2, y2, z2, block):
    return f"fill {x1} {y1} {z1} {x2} {y2} {z2} {block}"

lines = ["# Minekea demo world - by-material layout (generated by generate_layout.py)",
         f"# arena X[{X0}..{X1}] Z[{Z1}..{Z0}] floor y={FLOOR_Y}", ""]

# 1) smooth-stone base over the whole arena (aisles + border), andesite pads on top
lines.append("# floor base (smooth stone) covering arena + border ring")
lines.append(fill(X0, FLOOR_Y, Z1, X1, FLOOR_Y, Z0, BORDER_BLOCK))

# 2) andesite region pads
lines.append("")
lines.append("# region floor pads (polished andesite)")
for rg in regions.values():
    ox, oz = rg["ox"], rg["oz"]
    lines.append(fill(ox, FLOOR_Y, oz - (rg["d"] - 1), ox + rg["w"] - 1, FLOOR_Y, oz, FLOOR_BLOCK))

# 3) podium step supports
lines.append("")
lines.append(f"# podium step supports ({len(supports)})")
for (x, y, z, b) in supports:
    lines.append(f"setblock {x} {y} {z} {b}")

# 4) display blocks (with facing state, jar NBT, and 2-tall upper halves)
lines.append("")
disp = []
jars_filled = upper_halves = 0
for p in placements:
    state = p["state"]
    suffix = f"[{state}]" if state else ""
    nbt = jar_nbt(p["contents"]) if p["block_id"] == GLASS_JAR else ""
    if nbt:
        jars_filled += 1
    disp.append(f"setblock {p['x']} {p['y']} {p['z']} {p['block_id']}{suffix}{nbt}")
    if p["upper"]:
        disp.append(f"setblock {p['x']} {p['y'] + 1} {p['z']} {p['block_id']}[{p['upper']}]")
        upper_halves += 1
lines.append(f"# display blocks ({len(placements)} + {upper_halves} upper halves)")
lines.extend(disp)

# 5) label signs
lines.append("")
lines.append(f"# region label signs ({len(signs)})")
for (x, y, z, text) in signs:
    msg = text.replace('"', "'")
    lines.append(f'setblock {x} {y} {z} {SIGN_BLOCK}[rotation=0]'
                 f'{{front_text:{{messages:[\'"{msg}"\',\'""\',\'""\',\'""\']}}}}')

# 6) teleport command blocks (+ stone pressure plates) per region
lines.append("")
lines.append(f"# teleport command blocks + stone pressure plates ({len(cmd_blocks)})")
for (cx, cy, cz, vx, vy, vz) in cmd_blocks:
    cmd = f"tp @p {vx:.1f} {vy} {vz} 180 0"
    lines.append(f'setblock {cx} {cy} {cz} minecraft:command_block[facing=up]'
                 f'{{Command:"{cmd}",auto:0b}}')
    lines.append(f"setblock {cx} {cy + 1} {cz} minecraft:stone_pressure_plate")

with open(os.path.join(HERE, "demo_build.mcfunction"), "w", encoding="utf-8") as fh:
    fh.write("\n".join(lines) + "\n")

# ---- region list ----------------------------------------------------------
with open(os.path.join(HERE, "layout_regions.txt"), "w", encoding="utf-8") as fh:
    fh.write("Minekea demo world - by-material regions (display / walking order)\n")
    fh.write(f"arena X[{X0}..{X1}] Z[{Z1}..{Z0}] floor y={FLOOR_Y}\n\n")
    fh.write(f"{'material':28s} {'w':>3s} {'d':>3s} {'flat':>4s} {'pod':>3s}  front-left(cmd)\n")
    for key, rg in regions.items():
        fh.write(f"{rg['label']:28s} {rg['w']:3d} {rg['d']:3d} "
                 f"{len(rg['flats']):4d} {len(rg['podiums']):3d}  ({rg['ox']},{FLOOR_Y},{rg['oz']})\n")

# ---- stats ----------------------------------------------------------------
stats = {
    "summary": {
        "arena_x": [X0, X1], "arena_z": [Z1, Z0], "floor_y": FLOOR_Y,
        "width": X1 - X0 + 1, "depth": Z0 - Z1 + 1,
        "regions": len(regions),
        "display_blocks": len(placements),
        "podium_supports": len(supports),
        "command_blocks": len(cmd_blocks),
        "jars_filled": jars_filled,
    },
    "regions": [{
        "region": key, "label": rg["label"],
        "front_left": [rg["ox"], FLOOR_Y, rg["oz"]],
        "w": rg["w"], "d": rg["d"],
        "flat": len(rg["flats"]), "podiums": len(rg["podiums"]),
    } for key, rg in regions.items()],
}
with open(os.path.join(HERE, "layout_stats.json"), "w", encoding="utf-8") as fh:
    json.dump(stats, fh, indent=2)

# ---- console summary ------------------------------------------------------
print(f"regions         : {len(regions)}")
print(f"display blocks  : {len(placements)}  (+{upper_halves} upper halves)")
print(f"podium supports : {len(supports)}")
print(f"jars filled     : {jars_filled}")
print(f"command blocks  : {len(cmd_blocks)}")
print(f"arena           : X[{X0}..{X1}] ({X1-X0+1} wide)  Z[{Z1}..{Z0}] ({Z0-Z1+1} deep)")
print(f"mcfunction lines: {len(lines)}")

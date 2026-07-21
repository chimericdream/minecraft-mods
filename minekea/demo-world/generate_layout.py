#!/usr/bin/env python3
"""
Minekea demo-world layout generator  (tiled 126x126 edition)

Places every Minekea block into an 8x8 grid of 16x16 tiles inside a pre-built
126x126 arena. Each tile is a 14x14 polished-andesite pad framed by smooth-stone
borders; the touching borders of neighbouring tiles form the 2-block aisles.
One family per tile (big families get a merged 2-tile room); the glass jar gets
its own item/misc tiles; compressed blocks are spread across 6 tiles by material
type and shown as 9-tall stacks.

Arena (absolute world coords, all floor at y=55):
    front-left  corner  = (-191, 55, 190)      (west, near/front)
    back-right  corner  = ( -66, 55,  65)      (east, far/back)
    X: -191 (west) .. -66 (east)   126 wide, +X = east = "right"
    Z:  190 (front) ..  65 (back)  126 deep,  -Z = back = north
Tile (col c=0..7 west->east, row r=0..7 front->back):
    interior X = -191+16c .. -178+16c        (14 wide, lx 0..13)
    interior Z = 190-16r  .. 177-16r         (14 deep, lz 0..13; lz 0 = front row)
    the front row (lz 0) is reserved for standing / label sign / command block.
Command block: front-right corner of every tile = (-178+16c, 55, 190-16r),
    facing up, command  tp @p ~-6.5 ~1 ~ 180 0  (lands player front-centre,
    facing north into the tile), with a stone pressure plate on top.

Outputs (all in this folder):
    demo_layout_manifest.csv   one row per placed block, absolute coords + tile
    layout_tilemap.txt         8x8 tile map (which family sits where)
    layout_stats.json          machine-readable room/tile summary
    demo_build.mcfunction      floor + borders + every block + signs + cmd blocks
Regenerate with:  python generate_layout.py
(refresh block_inventory.txt / glass_jar_contents.csv first if the mod changed)
"""
import csv, json, math, os
from collections import OrderedDict

HERE = os.path.dirname(os.path.abspath(__file__))

# ---- arena / tile geometry -----------------------------------------------
ORIGIN_X   = -191     # west edge  (col 0, lx 0)
ORIGIN_Z   =  190     # front edge (row 0, lz 0)
FLOOR_Y    =   55     # floor blocks live here; displays sit at FLOOR_Y+1
TILES      =    8     # 8x8 grid
TILE_PITCH =   16
TILE_USABLE=   14     # interior is 14x14
FRONT_ROWS =    1     # rows of the interior kept clear at the front (standing/sign/cmd)

FLOOR_BLOCK  = "minecraft:polished_andesite"
BORDER_BLOCK = "minecraft:smooth_stone"
SIGN_BLOCK   = "minecraft:oak_sign"
CMD_TP       = "tp @p ~-6.5 ~1 ~ 180 0"

# big families get a merged 2-tile room (breathing room + label margin)
WIDE_FAMILIES = {"beams", "covers", "dyed", "slabs", "stairs"}

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

ZONE_DISPLAY = {
    "building": "BUILDING", "furniture": "FURNITURE", "storage": "STORAGE",
    "containers": "CONTAINERS", "decorations": "DECORATIONS", "crops": "DECORATIONS",
}

def pretty(s):
    return s.replace("_", " ").title()

def block_state(path):
    """Orient a block's front toward the viewer, who stands at the front (high Z)
    looking north (-Z), so 'front toward viewer' == facing south.
    Returns (state, upper_state); state is the blockstate string without brackets
    ('' = leave default). upper_state != None marks a 2-tall block whose upper half
    must be placed one block above. Classified per block id, because families are
    heterogeneous (e.g. slabs mix horizontal `type` slabs with vertical `facing`)."""
    if path.startswith("building/stairs/"):                 # all stairs have facing
        return ("facing=north", None)                       # rotated 180 from south
    if path.startswith("building/slabs/") and "/vertical/" in path:
        return ("facing=east", None)                        # vertical slabs, 90 CCW from south
    if path.startswith("building/covers/"):
        return ("facing=south", None)
    if path.startswith("furniture/seating/chairs/"):        # stools are symmetric
        return ("facing=south", None)
    if path.startswith("containers/barrels/") or path == "containers/glass_jar":
        return ("facing=south", None)
    if path.startswith("furniture/trapdoors/"):             # stand them up to be seen
        return ("facing=south,half=bottom,open=true", None)
    if path.startswith("furniture/armoires/"):              # 2 tall, rotated 180 from south
        return ("facing=north,half=lower", "facing=north,half=upper")
    if path.startswith("furniture/doors/"):                 # 2 tall, closed
        return ("facing=south,half=lower,hinge=left,open=false",
                "facing=south,half=upper,hinge=left,open=false")
    return ("", None)

# split compressed (tiered) / glass jar / plain
comp_by_mat = OrderedDict()
plain = OrderedDict()          # (zone, family) -> list of cells; cell = [(id,y,contents,clabel)]
for p in sorted(paths):
    parts = p.split("/")
    if parts[:2] == ["building", "compressed"] and len(parts) == 4 and parts[3].endswith("x"):
        comp_by_mat.setdefault(parts[2], []).append((p, int(parts[3][:-1])))
    elif p == "containers/glass_jar":
        continue                                   # its own tiles, built from the jar csv
    else:
        zone = ZONE_DISPLAY.get(parts[0], parts[0].upper())
        fam = parts[1] if len(parts) > 1 else parts[0]
        plain.setdefault((zone, fam), []).append([("minekea:" + p, 0, "", "")])

def stack_cell(mat):
    tiers = sorted(comp_by_mat[mat], key=lambda t: t[1])
    return [("minekea:" + path, tier - 1, "", "") for (path, tier) in tiers]  # y 0..8

# classify the 146 compressed materials into 6 groups
COMP_ORE = {"amethyst_block", "copper_block", "diamond_block", "gold_block",
            "iron_block", "lapis_block", "netherite_block", "redstone_block"}
def comp_group(mat):
    if mat.endswith("glazed_terracotta"):
        return "glazed"
    if mat.endswith("terracotta"):
        return "terracotta"
    if mat.endswith("concrete"):
        return "concrete"
    if mat.endswith(("_planks", "_log", "_stem")):
        return "wood"
    if mat in COMP_ORE:
        return "ore"
    return "stone"

comp_groups = OrderedDict((g, []) for g in ["stone", "wood", "ore", "terracotta", "concrete", "glazed"])
for mat in comp_by_mat:
    comp_groups[comp_group(mat)].append(mat)

# ---- glass jars -----------------------------------------------------------
jar_items, jar_misc = [], []      # cells
jar_kind_by_id = {}               # contents id -> kind (item/fluid/mob/empty)
GLASS_JAR = "minekea:containers/glass_jar"
JAR_FLUID_FILL = 8.0              # MAX_BUCKETS in GlassJarBlockEntity
jar_csv = os.path.join(HERE, "glass_jar_contents.csv")
if os.path.exists(jar_csv):
    with open(jar_csv, encoding="utf-8") as fh:
        for row in csv.DictReader(fh):
            cell = [(GLASS_JAR, 0, row["id"], row["label"])]
            (jar_items if row["kind"] == "item" else jar_misc).append(cell)
            jar_kind_by_id[row["id"]] = row["kind"]     # "" (empty jar) -> "empty"

# A captured mob is stored under "minecraft:entity_data" as the mob's FULL entity NBT.
# GlassJarBlockEntity wraps it in a TypedEntityData whose constructor strips "id" from
# the tag, and hasMob() reports the mob present only when that id-less tag is NON-empty
# (a real capture carries Health/Brain/Motion/... so it always is). A bare {id:"..."}
# strips down to {} -> hasMob() false -> the jar renders empty, which is why the earlier
# minimal NBT showed nothing. So each mob needs at least one real field here; these are a
# clean, capture-faithful subset (the renderer fakes Pos, normalises facing, drops
# equipment, so UUID/Pos/Motion/Rotation are pointless to include).
MOB_NBT = {
    "minecraft:allay":      "NoGravity:1b,Health:20.0f",
    "minecraft:bat":        "BatFlags:0b,Health:6.0f",       # BatFlags:0 = flying, not hanging
    "minecraft:bee":        "Health:10.0f,Age:0",            # Age:0 = adult
    "minecraft:endermite":  "Health:8.0f,Lifetime:0",
    "minecraft:silverfish": "Health:8.0f",
    "minecraft:slime":      "Size:0,Health:1.0f",            # jar only holds tiny slimes
    "minecraft:vex":        "NoGravity:1b,Health:14.0f",
}

def jar_nbt(contents_id):
    """Block-entity SNBT that fills a glass jar with its item / fluid / mob,
    matching GlassJarBlockEntity's persisted keys. '' = leave empty."""
    kind = jar_kind_by_id.get(contents_id, "")
    if kind == "item":
        # A full jar: 7 stored stacks + a full 64 top slot (getStoredStacks()+1 -> full fill bar)
        return '{StoredItem:"%s",StoredItemQty:64,FullItemStacks:7}' % contents_id
    if kind == "fluid":
        return '{StoredFluid:"%s",StoredFluidAmount:%rd}' % (contents_id, JAR_FLUID_FILL)
    if kind == "mob":
        fields = MOB_NBT.get(contents_id, "NoGravity:1b")   # any real field -> hasMob() true
        return '{"minecraft:entity_data":{id:"%s",%s}}' % (contents_id, fields)
    return ""

# ---- assemble rooms (ordered) --------------------------------------------
rooms = []      # dict: zone, rid, label, tw, cells
def room(zone, rid, label, cells, tw=1):
    rooms.append({"zone": zone, "rid": rid, "label": label, "tw": tw, "cells": cells})

def P(zone, fam):
    return plain.get((zone, fam), [])

# BUILDING
for fam in ["beams", "covers", "dyed", "slabs", "stairs"]:
    room("BUILDING", fam, f"{pretty(fam)} ({len(P('BUILDING', fam))})",
         P("BUILDING", fam), tw=2)
room("BUILDING", "general", f"General ({len(P('BUILDING','general'))})", P("BUILDING", "general"))
room("BUILDING", "walls", f"Walls ({len(P('BUILDING','walls'))})", P("BUILDING", "walls"))
for g, mats in comp_groups.items():
    cells = [stack_cell(m) for m in mats]
    room("BUILDING", f"compressed_{g}", f"Compressed: {g.title()} ({len(mats)})", cells)

# FURNITURE
for fam in ["armoires", "bookshelves", "display_cases", "doors", "pillows",
            "seating", "shelves", "shutters", "tables", "trapdoors"]:
    room("FURNITURE", fam, f"{pretty(fam)} ({len(P('FURNITURE', fam))})", P("FURNITURE", fam))

# STORAGE
eggs = P("STORAGE", "egg_crate") + P("STORAGE", "brown_egg_crate") + P("STORAGE", "blue_egg_crate")
room("STORAGE", "egg_crates", f"Egg Crates ({len(eggs)})", eggs)
room("STORAGE", "compressed_food", f"Compressed Food ({len(P('STORAGE','compressed'))})", P("STORAGE", "compressed"))
room("STORAGE", "dyes", f"Dye Blocks ({len(P('STORAGE','dyes'))})", P("STORAGE", "dyes"))

# CONTAINERS
for fam in ["barrels", "cauldrons", "crates"]:
    room("CONTAINERS", fam, f"{pretty(fam)} ({len(P('CONTAINERS', fam))})", P("CONTAINERS", fam))

# GLASS JARS
room("CONTAINERS", "jars_items", f"Glass Jars - Items ({len(jar_items)})", jar_items)
room("CONTAINERS", "jars_misc", f"Glass Jars - Fluids & Mobs ({len(jar_misc)})", jar_misc)

# DECORATIONS
room("DECORATIONS", "candles", f"Candles ({len(P('DECORATIONS','candles'))})", P("DECORATIONS", "candles"))
room("DECORATIONS", "lighting", f"Lighting ({len(P('DECORATIONS','lighting'))})", P("DECORATIONS", "lighting"))
deco_misc = P("DECORATIONS", "warped_wart") + P("DECORATIONS", "misc")
room("DECORATIONS", "deco_misc", f"Warped Wart & Fake Cake ({len(deco_misc)})", deco_misc)

# tag wide families
for rm in rooms:
    if rm["rid"] in WIDE_FAMILIES:
        rm["tw"] = 2

# ---- flow rooms into the 8x8 tile grid (front rows first) -----------------
grid = [[None] * TILES for _ in range(TILES)]     # grid[r][c] = rid
c = r = 0
for rm in rooms:
    tw = rm["tw"]
    if c + tw > TILES:
        c, r = 0, r + 1
    if r >= TILES:
        raise SystemExit("ran out of tiles - too many rooms for an 8x8 grid")
    rm["col"], rm["row"] = c, r
    for k in range(tw):
        grid[r][c + k] = rm["rid"]
    c += tw

# ---- place blocks within each room ---------------------------------------
placements = []
INTERIOR = TILE_USABLE                      # 14
def room_rect(rm):
    tw = rm["tw"]
    width = tw * TILE_PITCH - 2              # 14 (tw1) or 30 (tw2)
    depth = INTERIOR - FRONT_ROWS           # 13 usable rows behind the front row
    return width, depth

for rm in rooms:
    cells = rm["cells"]
    width, depth = room_rect(rm)
    n = len(cells)
    gw = min(n, width) if n else 1
    rows_needed = math.ceil(n / gw) if n else 0
    if rows_needed > depth:
        raise SystemExit(f"room {rm['rid']} overflows its tile ({n} cells, cap {width*depth})")
    x_off = (width - gw) // 2               # centre the grid horizontally
    base_x = ORIGIN_X + TILE_PITCH * rm["col"]
    base_z = ORIGIN_Z - TILE_PITCH * rm["row"]
    for i, cell in enumerate(cells):
        gx, gz = i % gw, i // gw
        lx = x_off + gx
        lz = FRONT_ROWS + gz                # skip reserved front row(s)
        wx = base_x + lx
        wz = base_z - lz
        first = (i == 0)
        for (bid, yoff, contents, clabel) in cell:
            state, _upper = block_state(bid.split(":", 1)[1])
            placements.append({
                "block_id": bid,
                "x": wx, "y": FLOOR_Y + 1 + yoff, "z": wz,
                "tile_col": rm["col"], "tile_row": rm["row"],
                "room": rm["rid"], "zone": rm["zone"],
                "label": rm["label"] if first else "",
                "tier": (str(yoff + 1) + "x") if rm["rid"].startswith("compressed") else "",
                "state": state,
                "contents": contents, "contents_label": clabel,
            })

# ---- command blocks (all 64 tiles) + sign coords --------------------------
def cmd_block_pos(c, r):
    return (ORIGIN_X + TILE_PITCH * c + (TILE_USABLE - 1),   # -178+16c  (east/right col)
            FLOOR_Y,
            ORIGIN_Z - TILE_PITCH * r)                        # 190-16r   (front row)

# ---- write manifest CSV ---------------------------------------------------
fields = ["block_id", "x", "y", "z", "tile_col", "tile_row", "room", "zone",
          "tier", "state", "label", "contents", "contents_label"]
with open(os.path.join(HERE, "demo_layout_manifest.csv"), "w", newline="", encoding="utf-8") as fh:
    w = csv.DictWriter(fh, fieldnames=fields)
    w.writeheader()
    w.writerows(placements)

# ---- tile map -------------------------------------------------------------
SHORT = {rm["rid"]: rm["rid"] for rm in rooms}
with open(os.path.join(HERE, "layout_tilemap.txt"), "w", encoding="utf-8") as fh:
    fh.write("Minekea demo world - 8x8 tiles (front row = top, west = left)\n")
    fh.write("each cell = one 14x14 tile; '..' = empty (reserved for growth)\n\n")
    fh.write("      " + "".join(f"c{c:<11}" for c in range(TILES)) + "\n")
    for r in range(TILES):
        cells = []
        for c in range(TILES):
            rid = grid[r][c]
            cells.append(f"{(rid or '..')[:12]:<12}")
        fh.write(f"r{r}  " + " ".join(cells) + "\n")
    fh.write("\nrooms:\n")
    for rm in rooms:
        cx, _, cz = cmd_block_pos(rm["col"], rm["row"])
        fh.write(f"  ({rm['col']},{rm['row']}) tw{rm['tw']}  {rm['label']:36}"
                 f"  cmd@({cx},{FLOOR_Y},{cz})\n")

# ---- build mcfunction -----------------------------------------------------
def fill(x1, y1, z1, x2, y2, z2, block):
    return f"fill {x1} {y1} {z1} {x2} {y2} {z2} {block}"

lines = ["# Minekea demo world - generated by generate_layout.py",
         "# run inside the pre-built 126x126 arena; floor y=55.", ""]

# 1) smooth-stone base over the whole floor + a 1-block outer border ring
#    (one solid fill of the 128x128 covers the arena aisles/borders AND the
#     surrounding ring; andesite interiors are painted on top next)
X0, X1 = ORIGIN_X, ORIGIN_X + 125
Z1, Z0 = ORIGIN_Z - 125, ORIGIN_Z         # Z1=back(65) Z0=front(190)
lines.append("# floor base (smooth stone) + outer border ring")
lines.append(fill(X0 - 1, FLOOR_Y, Z1 - 1, X1 + 1, FLOOR_Y, Z0 + 1, BORDER_BLOCK))

# 2) andesite tile interiors (+ merged seams)
lines.append("")
lines.append("# polished andesite tile interiors")
merged_second = set()   # (c,r) tiles that are the 2nd half of a wide room (seam filled)
for rm in rooms:
    for k in range(rm["tw"]):
        cc = rm["col"] + k
        bx = ORIGIN_X + TILE_PITCH * cc
        bz = ORIGIN_Z - TILE_PITCH * rm["row"]
        lines.append(fill(bx, FLOOR_Y, bz - (TILE_USABLE - 1), bx + (TILE_USABLE - 1), FLOOR_Y, bz, FLOOR_BLOCK))
    if rm["tw"] == 2:      # fill the 2-block seam between the two tiles
        sx = ORIGIN_X + TILE_PITCH * rm["col"] + TILE_USABLE
        bz = ORIGIN_Z - TILE_PITCH * rm["row"]
        lines.append(fill(sx, FLOOR_Y, bz - (TILE_USABLE - 1), sx + 1, FLOOR_Y, bz, FLOOR_BLOCK))

# 3) every display block (oriented to face the viewer; 2-tall blocks get an upper half)
lines.append("")
upper_halves = 0
disp_lines = []
jars_filled = 0
for p in placements:
    state, upper = block_state(p["block_id"].split(":", 1)[1])
    suffix = f"[{state}]" if state else ""
    nbt = jar_nbt(p["contents"]) if p["block_id"] == GLASS_JAR else ""
    if nbt:
        jars_filled += 1
    disp_lines.append(f"setblock {p['x']} {p['y']} {p['z']} {p['block_id']}{suffix}{nbt}")
    if upper:
        disp_lines.append(f"setblock {p['x']} {p['y'] + 1} {p['z']} {p['block_id']}[{upper}]")
        upper_halves += 1
lines.append(f"# display blocks ({len(placements)} + {upper_halves} upper halves)")
lines.extend(disp_lines)

# 4) a label sign at the front-left of each room
lines.append("")
lines.append("# room label signs")
for rm in rooms:
    sx = ORIGIN_X + TILE_PITCH * rm["col"]                 # lx 0 (west corner)
    sz = ORIGIN_Z - TILE_PITCH * rm["row"]                 # lz 0 (front row)
    msg = rm["label"].replace('"', "'")
    lines.append(f'setblock {sx} {FLOOR_Y + 1} {sz} {SIGN_BLOCK}[rotation=0]'
                 f'{{front_text:{{messages:[\'"{msg}"\',\'""\',\'""\',\'""\']}}}}')

# 5) command blocks + pressure plates on all 64 tiles
lines.append("")
lines.append("# teleport command blocks + stone pressure plates (all 64 tiles)")
for r in range(TILES):
    for c in range(TILES):
        cx, cy, cz = cmd_block_pos(c, r)
        lines.append(f'setblock {cx} {cy} {cz} minecraft:command_block[facing=up]'
                     f'{{Command:"{CMD_TP}",auto:0b}}')
        lines.append(f"setblock {cx} {cy + 1} {cz} minecraft:stone_pressure_plate")

with open(os.path.join(HERE, "demo_build.mcfunction"), "w", encoding="utf-8") as fh:
    fh.write("\n".join(lines) + "\n")

# ---- stats ----------------------------------------------------------------
used_tiles = sum(1 for r in range(TILES) for c in range(TILES) if grid[r][c])
stats = {
    "summary": {
        "arena": f"126x126 @ y={FLOOR_Y}",
        "front_left": [ORIGIN_X, FLOOR_Y, ORIGIN_Z],
        "back_right": [ORIGIN_X + 125, FLOOR_Y, ORIGIN_Z - 125],
        "tiles_total": TILES * TILES,
        "tiles_used": used_tiles,
        "tiles_free_for_growth": TILES * TILES - used_tiles,
        "rooms": len(rooms),
        "blocks_placed": len(placements),
        "command_blocks": TILES * TILES,
    },
    "rooms": [{
        "room": rm["rid"], "zone": rm["zone"], "label": rm["label"],
        "col": rm["col"], "row": rm["row"], "tw": rm["tw"],
        "cells": len(rm["cells"]),
        "blocks": sum(len(cell) for cell in rm["cells"]),
        "cmd_block": list(cmd_block_pos(rm["col"], rm["row"])),
    } for rm in rooms],
    "compressed_groups": {g: mats for g, mats in comp_groups.items()},
}
with open(os.path.join(HERE, "layout_stats.json"), "w", encoding="utf-8") as fh:
    json.dump(stats, fh, indent=2)

# ---- console summary ------------------------------------------------------
print(f"blocks placed   : {len(placements)}")
print(f"rooms           : {len(rooms)}")
print(f"tiles used      : {used_tiles} / {TILES*TILES}  ({TILES*TILES-used_tiles} free for growth)")
print(f"grid rows used  : {max(rm['row'] for rm in rooms)+1} / {TILES}")
print(f"compressed groups: " + ", ".join(f"{g}={len(m)}" for g, m in comp_groups.items()))
print(f"command blocks  : {TILES*TILES}")
print(f"mcfunction lines: {len(lines)}")

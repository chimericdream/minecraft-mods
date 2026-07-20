#!/usr/bin/env python3
"""
Minekea demo-world layout generator.

Reads the authoritative block-id list (all_blocks.txt = generated blockstate
paths) plus a few hand-authored specials, groups them into zones -> families,
and flows them into a fixed-width (126) walkable gallery whose depth grows in
16-block increments (30, 46, 62, ...).

Output:
  demo_layout_manifest.csv   one row per placed block (block_id,x,y,z,zone,family,tier,label)
  layout_stats.json          computed per-family footprints + chosen plot size
"""
import csv, json, math, os
from collections import OrderedDict

SCRATCH = os.path.dirname(os.path.abspath(__file__))
ALL = os.path.join(SCRATCH, "block_inventory.txt")

# ---- tunables -------------------------------------------------------------
PLOT_W        = 126     # fixed width (X), west->east
DEPTH_BASE    = 30      # smallest plot depth (Z)
DEPTH_STEP    = 16      # depth grows by one chunk at a time
FAMILY_MAXCOL = 24      # max cells wide a single family grid may be
# a few large families pack better (shallower overall gallery) when allowed to run wider
FAMILY_MAXCOL_OVERRIDE = {
    "dyed":       48,   # 192 cells -> 48x4 instead of 24x8
    "jars_items": 48,   # 95 cells  -> 48x2 instead of 24x4
}
FAMILY_AISLE  = 2       # empty columns between two families on the same shelf
BLOCK_AISLE_EVERY = 0   # (0 = pack family cells solid; walking happens in family/zone aisles)
LABEL_ROWS    = 1       # depth reserved in front (north) of each family for its sign
SHELF_AISLE   = 2       # empty rows between shelves (walkable lane running E-W)
ZONE_HEADER   = 2       # rows at the top of a zone (sign wall + 1 walkable row)
ZONE_TRAIL    = 1       # walkable row after a zone before the next begins
MARGIN        = 1       # perimeter walkway on all four sides
COMPRESS_STACK = True   # render building/compressed materials as 9-tall vertical stacks

# reserved empty depth (rows) appended to each zone so new families / new wood
# types / new compressed materials can slot in without reflowing the gallery
GROWTH_RESERVE = {
    "BUILDING":    6,   # grows most: new woods hit slabs/stairs/beams/covers + new compressed mats
    "FURNITURE":   5,   # new woods hit seating/shutters/tables/etc.
    "STORAGE":     3,
    "CONTAINERS":  3,
    "GLASS JARS":  3,   # new storable items/fluids/mobs
    "DECORATIONS": 2,
}

# zone order + display names
ZONE_ORDER = ["building", "furniture", "storage", "containers", "glass_jars", "decorations", "crops"]
ZONE_DISPLAY = {
    "building":   "BUILDING",
    "furniture":  "FURNITURE",
    "storage":    "STORAGE",
    "containers": "CONTAINERS",
    "glass_jars": "GLASS JARS",
    "decorations":"DECORATIONS",
    "crops":      "DECORATIONS",   # fold crops into decorations
}

# The glass jar is one registered block whose contents vary by NBT, so it gets its
# own zone: one jar per storable item / fluid / mob. Contents come from
# glass_jar_contents.csv (regenerate with extract_jar_contents.py).
GLASS_JAR_BLOCK = "minekea:containers/glass_jar"
JAR_FAMILY = {"empty": "jars_empty", "item": "jars_items",
              "fluid": "jars_fluids", "mob": "jars_mobs"}

# ---- load inventory -------------------------------------------------------
paths = []
with open(ALL, encoding="utf-8") as fh:
    for line in fh:
        p = line.strip()
        if p:
            paths.append(p)

# hand-authored blocks that live in src/main/resources (not the generated tree)
HAND_AUTHORED = [
    "containers/glass_jar",
    "containers/cauldrons/honey",
    "containers/cauldrons/milk",
    "decorations/lighting/endless_rod",
    "storage/egg_crate",
    "storage/brown_egg_crate",
    "storage/blue_egg_crate",
]
for p in HAND_AUTHORED:
    if p not in paths:
        paths.append(p)

def zone_of(path):
    top = path.split("/")[0]
    return ZONE_DISPLAY.get(top, top.upper())

def family_of(path):
    parts = path.split("/")
    # family = 2nd path component when present, else the top
    return parts[1] if len(parts) > 1 else parts[0]

FAMILY_LABELS = {
    "jars_empty":  "Empty Jar",
    "jars_items":  "Jars - Items",
    "jars_fluids": "Jars - Fluids",
    "jars_mobs":   "Jars - Mobs",
}

def pretty(name):
    return FAMILY_LABELS.get(name, name.replace("_", " ").title())

# ---- build cells ----------------------------------------------------------
# A "cell" occupies one (x,z) footprint and holds >=1 block (id, y-offset).
# zones -> families -> list of cells; each cell = list of (block_id, y)
zones = OrderedDict()
for top in ZONE_ORDER:
    zones[ZONE_DISPLAY[top]] = OrderedDict()

# split compressed (tiered) out for stacking
comp_by_mat = OrderedDict()          # material -> [(path, tier)]
plain = []                           # everything else
for p in sorted(paths):
    parts = p.split("/")
    if parts[:2] == ["building", "compressed"] and len(parts) == 4 and parts[3].endswith("x"):
        mat = parts[2]
        tier = int(parts[3][:-1])
        comp_by_mat.setdefault(mat, []).append((p, tier))
    elif p == "containers/glass_jar":
        continue          # handled by its own zone below
    else:
        plain.append(p)

def add_cell(zone, family, cell):
    """cell = list of (block_id, y_offset, contents_id, contents_label)"""
    zones.setdefault(zone, OrderedDict()).setdefault(family, []).append(cell)

# plain families: one block per cell
for p in plain:
    z = zone_of(p); f = family_of(p)
    add_cell(z, f, [("minekea:" + p, 0, "", "")])

# compressed: one cell per material, tiers stacked bottom(1x)->top(9x)
for mat, tiers in comp_by_mat.items():
    tiers.sort(key=lambda t: t[1])
    if COMPRESS_STACK:
        cell = [("minekea:" + path, tier - 1, "", "") for (path, tier) in tiers]  # y 0..8
        add_cell("BUILDING", "compressed", cell)
    else:
        for (path, tier) in tiers:
            add_cell("BUILDING", "compressed", [("minekea:" + path, 0, "", "")])

# glass jars: one jar per storable item / fluid / mob, grouped by kind
jar_csv = os.path.join(SCRATCH, "glass_jar_contents.csv")
jar_counts = OrderedDict()
if os.path.exists(jar_csv):
    with open(jar_csv, encoding="utf-8") as fh:
        for row in csv.DictReader(fh):
            fam = JAR_FAMILY.get(row["kind"])
            if not fam:
                continue
            add_cell("GLASS JARS", fam,
                     [(GLASS_JAR_BLOCK, 0, row["id"], row["label"])])
            jar_counts[fam] = jar_counts.get(fam, 0) + 1
else:
    # fall back to a single empty jar so the layout still generates
    add_cell("GLASS JARS", "jars_empty", [(GLASS_JAR_BLOCK, 0, "", "Empty Jar")])

# ---- pack into the plot ---------------------------------------------------
# Flow families shelf by shelf inside each zone. Coordinates:
#   x in [MARGIN, PLOT_W-1-MARGIN], grows east; wraps to next shelf.
#   z grows south as shelves/zones stack.
placements = []   # dict rows for csv
stats = {"zones": OrderedDict(), "families": []}

x0 = MARGIN
z = MARGIN
usable_w = PLOT_W - 2 * MARGIN

def family_grid(n, family=None):
    cols = min(n, FAMILY_MAXCOL_OVERRIDE.get(family, FAMILY_MAXCOL))
    rows = math.ceil(n / cols)
    return cols, rows

for zone, fams in zones.items():
    if not fams:
        continue
    zone_start_z = z
    zone_cell_count = 0
    # zone header band
    z += ZONE_HEADER
    cx = x0
    shelf_depth = 0
    zfam_stats = []
    for family, cells in fams.items():
        n = len(cells)
        cols, rows = family_grid(n, family)
        fam_w = cols
        fam_d = rows + LABEL_ROWS
        # wrap to a new shelf if this family won't fit on the current one
        if cx + fam_w > x0 + usable_w:
            z += shelf_depth + SHELF_AISLE
            cx = x0
            shelf_depth = 0
        fx, fz = cx, z
        # place cells: label row is fz (front/north), grid starts fz+LABEL_ROWS
        for i, cell in enumerate(cells):
            col = i % cols
            row = i // cols
            bx = fx + col
            bz = fz + LABEL_ROWS + row
            label = pretty(family) if i == 0 else ""
            for (bid, yoff, contents, clabel) in cell:
                tier = ""
                if COMPRESS_STACK and family == "compressed":
                    tier = str(yoff + 1) + "x"
                placements.append({
                    "block_id": bid,
                    "x": bx, "y": yoff, "z": bz,
                    "zone": zone, "family": family,
                    "tier": tier, "label": label,
                    "contents": contents, "contents_label": clabel,
                })
        zone_cell_count += n
        zfam_stats.append({
            "family": family, "cells": n,
            "blocks": sum(len(c) for c in cells),
            "grid": f"{cols}x{rows}", "w": fam_w, "d": fam_d,
            "x": fx, "z": fz,
        })
        cx += fam_w + FAMILY_AISLE
        shelf_depth = max(shelf_depth, fam_d)
    reserve = GROWTH_RESERVE.get(zone, 0)
    z += shelf_depth + reserve + ZONE_TRAIL
    stats["zones"][zone] = {
        "z_start": zone_start_z, "z_end": z, "cells": zone_cell_count,
        "growth_reserve_rows": reserve,
    }
    stats["families"].extend([dict(f, zone=zone) for f in zfam_stats])

used_depth = z + MARGIN
# choose smallest allowed plot depth that fits
plot_depth = DEPTH_BASE
while plot_depth < used_depth:
    plot_depth += DEPTH_STEP

stats["summary"] = {
    "total_blocks_placed": len(placements),
    "footprint_cells": sum(len(cs) for fams in zones.values() for cs in fams.values()),
    "plot_width": PLOT_W,
    "used_depth": used_depth,
    "recommended_plot": f"{PLOT_W}x{plot_depth}",
    "plot_depth": plot_depth,
    "compressed_stacked": COMPRESS_STACK,
}

# ---- write outputs --------------------------------------------------------
csv_path = os.path.join(SCRATCH, "demo_layout_manifest.csv")
with open(csv_path, "w", newline="", encoding="utf-8") as fh:
    w = csv.DictWriter(fh, fieldnames=["block_id","x","y","z","zone","family","tier","label",
                                       "contents","contents_label"])
    w.writeheader()
    for row in placements:
        w.writerow(row)

with open(os.path.join(SCRATCH, "layout_stats.json"), "w", encoding="utf-8") as fh:
    json.dump(stats, fh, indent=2)

# ---- to-scale ASCII map (1 char = 1 block footprint) ----------------------
# symbol per family (falls back to first letter); aisles blank, border '.'
SYMBOLS = {}
_alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
for i, f in enumerate(stats["families"]):
    SYMBOLS.setdefault(f["family"], _alpha[i % len(_alpha)])
grid = [[" " for _ in range(PLOT_W)] for _ in range(plot_depth)]
# perimeter
for x in range(PLOT_W):
    grid[0][x] = grid[plot_depth-1][x] = "."
for zz in range(plot_depth):
    grid[zz][0] = grid[zz][PLOT_W-1] = "."
seen = set()
for row in placements:
    key = (row["x"], row["z"])
    if key in seen:
        continue
    seen.add(key)
    if 0 <= row["z"] < plot_depth and 0 <= row["x"] < PLOT_W:
        grid[row["z"]][row["x"]] = SYMBOLS[row["family"]]
map_path = os.path.join(SCRATCH, "layout_map.txt")
with open(map_path, "w", encoding="utf-8") as fh:
    fh.write(f"Minekea demo world - {PLOT_W} x {plot_depth}  (1 char = 1 block; N=top)\n")
    fh.write("legend: " + "  ".join(f"{SYMBOLS[f]}={f}" for f in
             OrderedDict((s['family'], None) for s in stats['families'])) + "\n\n")
    for zz in range(plot_depth):
        fh.write("".join(grid[zz]) + "\n")
print("\nwrote layout_map.txt")

# ---- console summary ------------------------------------------------------
print(f"blocks placed        : {len(placements)}")
print(f"footprint cells      : {sum(len(cs) for fams in zones.values() for cs in fams.values())}")
print(f"used depth (Z)       : {used_depth}")
print(f"recommended plot     : {PLOT_W} x {plot_depth}")
print(f"max X used           : {max(p['x'] for p in placements)} (limit {PLOT_W-1})")
print()
print(f"{'ZONE':12} {'z0':>4} {'z1':>4} {'cells':>6}")
for zn, zs in stats["zones"].items():
    print(f"{zn:12} {zs['z_start']:>4} {zs['z_end']:>4} {zs['cells']:>6}")
print()
print(f"{'ZONE':11} {'FAMILY':16} {'cells':>5} {'blocks':>6} {'grid':>7}  at(x,z)")
for f in stats["families"]:
    print(f"{f['zone']:11} {f['family']:16} {f['cells']:>5} {f['blocks']:>6} {f['grid']:>7}  ({f['x']},{f['z']})")

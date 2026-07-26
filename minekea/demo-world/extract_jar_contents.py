#!/usr/bin/env python3
"""
Extract everything a Minekea glass jar can hold, straight from the source of truth:

  items  <- GlassJarBlock.ALLOWED_ITEM_IDS keys
  mobs   <- GlassJarItem.canCaptureMob()
  fluids <- GlassJarBlock bucket handling + ModFluids

Writes glass_jar_contents.csv:  kind,id,label
Run this after changing what the jar accepts, then rerun generate_layout.py.
"""
import csv, os, re

HERE = os.path.dirname(os.path.abspath(__file__))
SRC = os.path.abspath(os.path.join(
    HERE, "..", "common", "src", "main", "java", "com", "chimericdream", "minekea"))
JAR_BLOCK = os.path.join(SRC, "block", "containers", "GlassJarBlock.java")

src = open(JAR_BLOCK, encoding="utf-8").read()

items = []
# ALLOWED_ITEM_IDS.put(<key>, <storage block>) — key is either a literal or a computed Identifier
for m in re.finditer(r'ALLOWED_ITEM_IDS\.put\(\s*(.+?)\s*,', src):
    key = m.group(1).strip()
    lit = re.fullmatch(r'"([^"]+)"', key)
    if lit:
        items.append(lit.group(1))
        continue
    wax = re.fullmatch(r'WaxItem\.makeId\("([a-z_]+)"\)\.toString\(\)', key)
    if wax:
        items.append(f"minekea:ingredients/wax/{wax.group(1)}")
        continue
    if key.startswith("WarpedWartItem.ITEM_ID"):
        items.append("minekea:crops/warped_wart")
        continue
    raise SystemExit(f"unrecognised ALLOWED_ITEM_IDS key, update the extractor: {key}")

# canCaptureMob(): bee / vex / allay / silverfish / endermite / bat, plus tiny slime
mobs = [
    ("minecraft:allay",      "Allay"),
    ("minecraft:bat",        "Bat"),
    ("minecraft:bee",        "Bee"),
    ("minecraft:endermite",  "Endermite"),
    ("minecraft:silverfish", "Silverfish"),
    ("minecraft:slime",      "Slime (tiny only)"),
    ("minecraft:vex",        "Vex"),
]

# The four fluids the jar renderer explicitly supports (water, lava, milk, honey);
# any other stored fluid falls back to rendering as water. See the renderer fix in
# {Fabric,NeoForge}GlassJarBlockEntityRenderer -- getFluidColor/getFluidTexture no
# longer throw, so no jar fluid can crash the client anymore.
fluids = [
    ("minecraft:water",       "Water"),
    ("minecraft:lava",        "Lava"),
    ("minekea:fluids/milk",   "Milk"),
    ("minekea:fluids/honey",  "Honey"),
]

def label_for(item_id):
    ns, path = item_id.split(":", 1)
    if path.startswith("ingredients/wax/"):
        return path.rsplit("/", 1)[1].replace("_", " ").title() + " Wax"
    if path == "crops/warped_wart":
        return "Warped Wart"
    return path.split("/")[-1].replace("_", " ").title()

rows = [("empty", "", "Empty Jar")]
rows += [("item", i, label_for(i)) for i in items]
rows += [("fluid", i, l) for (i, l) in fluids]
rows += [("mob", i, l) for (i, l) in mobs]

out = os.path.join(HERE, "glass_jar_contents.csv")
with open(out, "w", newline="", encoding="utf-8") as fh:
    w = csv.writer(fh, lineterminator="\n")   # force LF line endings
    w.writerow(["kind", "id", "label"])
    w.writerows(rows)

print(f"items : {len(items)}")
print(f"fluids: {len(fluids)}")
print(f"mobs  : {len(mobs)}")
print(f"empty : 1")
print(f"total jars: {len(rows)}  -> {out}")

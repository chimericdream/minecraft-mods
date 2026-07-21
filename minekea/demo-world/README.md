# Minekea Demo World — tooling guide

How the demo-world build is generated and how to change it. This is the **how-to**;
[`LAYOUT.md`](./LAYOUT.md) is the **what/why** (the design of the layout itself). Read this
when you want to tweak the build; read `LAYOUT.md` when you want to understand a design choice.

Everything is produced by one deterministic script — **`generate_layout.py`** — from two
input files. You edit the script (or the inputs), rerun it, and copy the resulting
`demo_build.mcfunction` into a datapack.

---

## 1. The pipeline

```
  block_inventory.txt ──┐
                        ├──►  generate_layout.py  ──►  demo_build.mcfunction   (run in-game)
  glass_jar_contents.csv┘                          ├─►  demo_layout_manifest.csv (one row/block)
        ▲                                          ├─►  layout_regions.txt   (region map)
        │ (regenerated from the mod, see §3)       └─►  layout_stats.json    (summary)
  extract_jar_contents.py ──► glass_jar_contents.csv
```

- **`block_inventory.txt`** — the master list of every block id path (one per line, no
  `minekea:` prefix), e.g. `building/stairs/vertical/oak`. Generated from the mod's committed
  blockstate JSONs (§3). This is *the* source of what exists.
- **`glass_jar_contents.csv`** — what the glass jar can hold (`kind,id,label`), produced by
  `extract_jar_contents.py` from the mod source.
- **`generate_layout.py`** — reads those two, decides where every block goes, and writes the
  four outputs. **All layout logic lives here.**
- **Outputs** are generated — never hand-edit them; they're overwritten on every run.

### Files at a glance

| File | Kind | Notes |
|------|------|-------|
| `generate_layout.py` | script | the generator — edit this |
| `extract_jar_contents.py` | script | regenerates `glass_jar_contents.csv` from mod source |
| `block_inventory.txt` | **input** | block id list; regenerate from blockstates (§3) |
| `glass_jar_contents.csv` | input | jar contents; regenerate via `extract_jar_contents.py` |
| `demo_build.mcfunction` | output | the runnable build (fills + setblocks) |
| `demo_layout_manifest.csv` | output | one row per placed block (debug/inspection) |
| `layout_regions.txt` | output | region walking-order + footprints + coords |
| `layout_stats.json` | output | machine-readable summary |
| `LAYOUT.md` | doc | design/rationale |
| `README.md` | doc | this file |

---

## 2. Running it

```bash
cd minekea/demo-world
python generate_layout.py        # (Windows: 'python'; some setups 'python3')
```
No dependencies beyond the Python standard library. It prints a summary (region count,
block count, arena size, tallest region, front-left corner) and rewrites the four outputs.
If a block can't be classified it **raises** with the offending path — that's intentional, so
a new block never silently disappears (see `canonical()` in §5).

### Installing / running the build in Minecraft
`demo_build.mcfunction` is a plain list of `/fill` + `/setblock` (+ a `kill`) commands.

1. Put it in a datapack:
   ```
   <world>/datapacks/minekea_demo/
     pack.mcmeta                              {"pack":{"pack_format":<your version>,"description":"Minekea demo"}}
     data/minekea/function/build.mcfunction   ← copy demo_build.mcfunction here
   ```
   (`functions` with an `s` on MC < 1.21.)
2. In-game: `/reload` then `/function minekea:build`.

It is **idempotent**: it first clears its 128×128 footprint to air, kills dropped items, lays
a smooth-sandstone floor, then builds — so re-running from scratch is safe. It assumes flat
ground at the build coordinates (front-left corner **-192, 55, 191**; see §4 geometry).

---

## 3. Refreshing the inputs after the mod changes

**When you add/rename/remove blocks in the mod, regenerate `block_inventory.txt`** (the script
only knows about blocks listed there):

```bash
# after a mod build/datagen has produced the blockstate JSONs:
cd minekea/common/src/main/generated/assets/minekea/blockstates   # (or build/resources/main/...)
find . -name '*.json' | sed 's|^\./||;s|\.json$||' | sort > /path/to/minekea/demo-world/block_inventory.txt
```
The generator re-adds a few hand-authored specials itself (cauldrons, egg crates, endless rod —
see the `HAND_AUTHORED`-style list near the top of the classify section).

**When the glass jar's accepted contents change**, regenerate the jar list:
```bash
python extract_jar_contents.py     # reads GlassJarBlock.java, writes glass_jar_contents.csv
```
`extract_jar_contents.py` parses `GlassJarBlock.ALLOWED_ITEM_IDS` for items and has hard-coded
lists for the supported **fluids** (water, lava, milk, honey) and **mobs** (allay, bat, bee,
endermite, silverfish, tiny slime, vex) — edit those lists in that file if the mod changes.

Then rerun `generate_layout.py`.

---

## 4. Tunables (top of `generate_layout.py`)

All geometry/appearance knobs are constants at the top:

| Constant | Meaning |
|----------|---------|
| `ORIGIN_X`, `ORIGIN_Z` | front-left of the content; `FLOOR_Y` = floor level (55) |
| `ROW_WIDTH` | wrap to a new row once a row exceeds this many blocks wide |
| `AISLE` / `ROW_AISLE` | gap between regions in a row / between rows |
| `VANTAGE` | how many blocks in front the teleport pads drop you |
| `CLEAR_SIZE` / `CLEAR_HEIGHT` | the air-wipe footprint (128) and height (15) at reset |
| `FLOOR_BLOCK` / `BORDER_BLOCK` / `SIGN_BLOCK` | region pads+platforms / arena base / label sign |
| `FLAT_COLS` | display blocks per staircase row (keep **even** — see §6 bagged pairs) |
| `POD_COLS`, `POD_SIZE`, `POD_GAP` | compressed-podium grid (3×3 podiums, 3 per row) |
| `FRONT_ROWS` | rows kept clear at a region's front (sign/command block/standing) |

Data lists just below the constants: `COLORS` (creative order), `WOOD_BASES`, `WOOD_BANDS` /
`STONE_BANDS` (family→tier), `STONE_RULES` (material bucketing), `ORDER_WOODS` / `ORDER_STONES`
/ `ORDER_METALS` / `ORDER_TAIL` (region walking order), `LABELS` (pretty names), and
`BAGGABLE_STORAGE` (compressed items with a burlap-sack model).

---

## 5. Change cookbook

### Change a block's facing / placed state
Edit **`block_state(path)`**. It returns `(state, upper)` where `state` is the blockstate
string (no brackets, `""` = default) and `upper` is a second state placed one block above for
2-tall blocks (or `None`). It's matched per path prefix, e.g. vertical stairs → `facing=south`,
candles → `candles=4,lit=false`. Add/adjust a branch.

### Move a block family to a different tier, or reorder families
Edit **`WOOD_BANDS`** / **`STONE_BANDS`**. Each inner list is one tier (band), ordered
front→back. A "slot" is the family path (block path minus its material segment, e.g.
`furniture/armoires`). Put 2-tall families (armoires, doors) in the **last** band — the
staircase auto-steps up 2 over any tier holding a 2-tall block, so nothing behind is hidden.
Every family a set can contain must appear in some band, or it falls to a catch-all tier at the
back (fine, just not aligned). Woods reserve a fixed column width per slot (`WOOD_RESERVE`,
computed automatically) so missing families leave a gap and everything stays column-aligned;
stones just pack.

### Change which regions exist / their order
Edit **`ORDER_WOODS` / `ORDER_STONES` / `ORDER_METALS` / `ORDER_TAIL`** (walking order) and, if
adding a material, **`STONE_RULES`** so `canonical()` buckets its blocks. `canonical(path)` is
the router: it maps a non-colour block path to a region key. Order of its checks matters
(special families → colours peeled off earlier by `is_colored` → woods → `STONE_RULES`;
metals are listed before the generic `stone` rule so `redstone_block` isn't swallowed).

### Change the colour order, or add a colour block type
`COLORS` is the gradient order (front→back within a colour region). Colour blocks are pulled
out first by **`is_colored(path)`**, which returns `(region, column, colour)` — add a branch
there to route a new colour block type into a colour region (`Dyed Blocks`, `Colored Building`,
`Colour Accents`, `Compressed {Concrete,Terracotta,Glazed Terracotta}`). A block type with an
un-dyed variant puts it in "spot 0" ahead of white (handled by the `plain` check in
`color_items`). Colour regions are **flat** (no tiering).

### Change the compressed-podium look
Compressed blocks (the `1x`–`9x` towers) render as stepped 3×3 podiums behind each material's
flat blocks — see the podium loop in `material_items()` (`POD_COLS`/`POD_SIZE`/`POD_GAP`).

### Bagged vs unbagged storage blocks
`BAGGABLE_STORAGE` lists the compressed-food items that have a burlap-sack model. Each gets
placed twice (`is_bagged=false` + `is_bagged=true`, kept adjacent); the classify step groups
these ahead of the non-baggable items so a pair never straddles a row (needs `FLAT_COLS` even).
Source of truth for the set: blockstate JSONs with an `is_bagged=true` variant
(`grep is_bagged=true .../blockstates/storage/compressed/*.json`).

### Change the arena floor / reset behaviour
The reset + floor are emitted in the "mcfunction" section: clear fills (`CLEAR_SIZE`/
`CLEAR_HEIGHT`) → `kill @e[type=item]` → smooth-sandstone floor → smooth-stone arena base →
andesite region pads → tier platforms → display blocks → signs+command blocks. Edit those
`lines.append(...)` blocks.

### Change per-region teleport / signs
Emitted in the "region label signs + teleport command blocks" block. Each region gets an
`oak_sign` and a `command_block` running `tp @p <centre> 56 <front+VANTAGE> 180 0` (yaw 180 =
facing north) on a stone pressure plate.

---

## 6. Key concepts

- **depth & elevation.** Each region is a staircase. A block's *depth* = how many rows back it
  is; its `y = 56 + elevation(depth)`. Elevation is cumulative: each tier rises by the height
  of the tier in front of it (**2** over a tier that holds a double-tall block, else **1**), so
  anything behind a 2-tall block clears it. Computed in the resolve loop.
- **tier platforms.** Instead of a support column under each block, every tier is filled to the
  full region width (`polished_andesite`), so a region reads as one solid stairstep; display
  blocks sit on top and empty columns just show the step surface.
- **slots / bands / reserve.** A *slot* is a family (path minus material). *Bands* map slots to
  fixed tiers so a family is always in the same relative spot across a set. *Reserve* (woods)
  pads each slot to the set-wide max width for exact column alignment.
- **colour regions** are the exception to almost all of the above: organised by block type as
  flat 16-colour gradients (see `color_items`).
- **glass jars** are one block whose look comes from block-entity NBT; `jar_nbt()` builds the
  NBT for item/fluid/mob (see the mob-NBT and renderer notes in `LAYOUT.md`).

---

## 7. Gotchas

- **`/fill` caps at 32768 blocks.** The clear splits into two-layer fills for that reason. If
  you enlarge `CLEAR_SIZE`/`CLEAR_HEIGHT`, keep each fill ≤ 32768.
- **Keep `FLAT_COLS` even** or bagged storage pairs can split across rows.
- **Sort of stripped wood variants**: `_variant_key` sorts so an un-stripped block precedes its
  `stripped_` form (a plain path sort breaks this for `warped`). Keep using it for within-slot
  ordering.
- **Shutter `*_open` blocks are skipped** (internal open-halves; standalone they crash on
  click). Handled in the classify step by the `_open` check.
- **Never edit the generated files** (`demo_build.mcfunction`, the CSVs, `layout_*`) — rerun the
  generator instead.

### Sanity-checking a change
After running the generator, a quick self-check (all should be clean):
```python
# from minekea/demo-world:
import re, collections, json
occ = collections.Counter()
for ln in open("demo_build.mcfunction", encoding="utf-8"):
    m = re.match(r'^setblock (-?\d+) (-?\d+) (-?\d+)', ln.strip())
    if m: occ[(int(m[1]), int(m[2]), int(m[3]))] += 1
print("display-block collisions:", sum(1 for c in occ.values() if c > 1))   # want 0
s = json.load(open("layout_stats.json"))["summary"]
print("front-left corner:", s["front_left"])                                 # want [-192, 55, 191]
```
`layout_regions.txt` is the human-readable map of where each region landed; the manifest CSV
has every block's exact coords/state if you need to check one.
```

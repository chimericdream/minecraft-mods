# Test Plan — Sponj

Sponj adds four blocks: **Sponj** / **Wet Sponj** (water) and **Lava Sponj** / **Wet Lava Sponj**
(lava). The interesting logic lives in `SponjBlock.absorbWater` (and its lava counterpart):
absorption radius and capacity scale with the number of *connected* sponjes —
`radius = 6 + 3·(count−1)`, `max = 64·count`, discovered via
`BlockUtils.getConnectedBlocksByType(world, pos, ..., 32)` — and every connected dry sponj turns wet
when any absorption happens. Wet variants dry out in specific dimensions (Nether for sponj, End for
lava sponj) or via smelting, and the wet lava sponj is a 128-item furnace fuel that leaves a dry
lava sponj behind.

## Test conventions

Follow the Hopper X-Treme pattern: GameTest classes in
`fabric/src/main/java/com/chimericdream/sponj/fabric/test/`, registered under the `fabric-gametest`
entrypoint in `fabric.mod.json`, `.snbt` structures under
`common/src/main/resources/data/sponj/gametest/structure/`. Water/lava pools are best baked into the
structures; sponjes are placed by the test so the absorption trigger is deterministic.

## Manual test plan

Setup: creative world with a large water pool (≥ 20×20×6) and a lava pool. Run on Fabric, then a
NeoForge smoke pass.

1. **Single sponj baseline** — place one sponj in the pool center; verify a roughly 6-block-radius
   dry pocket, the sponj turns wet, and absorption pops the vanilla particle/sound effect.
2. **Connected scaling** — place a 2×1, then 3×3×1, then a large 32+ cluster touching the water:
   * radius visibly grows with each added sponj;
   * *all* connected sponjes turn wet together, even ones not touching water;
   * the 32-block search cap: clusters larger than 32 don't grow the radius further (place 40 in a
     line and compare against 32).
3. **Capacity cap** — in an effectively infinite ocean, one sponj should stop after ~64 removed
   water blocks rather than draining forever.
4. **Waterlogged & replaceable blocks** — absorb through kelp, seagrass, and waterlogged stairs:
   kelp/seagrass should break and drop (the code calls `dropResources`), waterlogged `BucketPickup`
   blocks should be drained without being destroyed.
5. **Lava sponj parity** — repeat 1–3 with lava sponjes in lava (Nether lava lakes are the fun
   case); regular sponj must NOT absorb lava and vice versa.
6. **Drying**
   * Wet sponj in the Nether: dries to sponj (vanilla-style, with the hiss/steam).
   * Wet lava sponj in the End: dries to lava sponj.
   * Wrong dimension: neither dries.
   * Smelting: wet sponj smelts back to sponj.
7. **Fuel behavior** — put ONE wet lava sponj in a furnace fuel slot: it should smelt 128 items and
   leave a **dry lava sponj** in the fuel slot when consumed. Then repeat with a stack of 2+ and
   confirm the README's warning (stacked wet lava sponjes don't return dry ones) — decide whether
   that's intended or a bug worth fixing.
8. **Recipes & tab placement** — craft both dry sponjes per the documented recipes; confirm creative
   tab/JEI-style lookup entries exist.
9. **Neighbor-triggered absorption** — place a dry sponj next to air, then flood water toward it;
   the `neighborChanged` path should trigger absorption when water arrives, same as vanilla sponge.

## Recommended automated tests

### GameTests — absorption

Structure family `sponj:absorption/*`: a glass-walled tank of water sized per test.

* **`singleSponjAbsorbs`** — 13×13×5 tank; set sponj at center; after ~5 ticks assert:
  water gone within radius 6 (sample a handful of deterministic positions), water present just
  outside the radius, sponj block is now `WET_SPONJ`.
* **`twoConnectedSponjesExtendRadius`** — same tank; two adjacent sponjes; assert a position that is
  dry only if radius ≥ 9, and that **both** blocks are wet.
* **`disconnectedSponjesDoNotCombine`** — two sponjes with a 1-block gap; assert radius stays 6
  around the triggered one and the far sponj only wets if it independently touched water.
* **`capacityCap`** — a long 1-wide water channel with > 64 water blocks in range; count remaining
  water and assert ≈ the cap (pin the exact expected count — the loop breaks at `i > max`, so it can
  overshoot by a few; the test should encode the actual contract).
* **`searchCapAt32`** — 40-sponj line; trigger one end; assert radius corresponds to 32, not 40.
* **`waterloggedDrained` / `kelpDropsResources`** — tank with kelp and waterlogged stairs; assert the
  stairs survive un-waterlogged, kelp is gone, and kelp item entities exist
  (`assertItemEntityCountIs`, as in Hopper X-Treme's conversion test).
* **Lava mirror suite** — `lavaSponjAbsorbsLava`, `sponjIgnoresLava`, `lavaSponjIgnoresWater`
  (assert the wrong-fluid sponj stays dry and the fluid stays put).

### GameTests — drying and fuel

* **`wetLavaSponjFuelLeavesDrySponj`** — structure with a furnace preloaded (or filled by the test)
  with 1 wet lava sponj + smeltable items; fast-forward with `context.runAfterDelay` past one smelt
  cycle... furnace burn is 100 real ticks minimum, so instead set the furnace's `litTimeRemaining`
  via its block entity or use a short-cook item; assert fuel slot now holds a dry lava sponj.
  (`maxTicks` will need raising — Hopper X-Treme's `TransferSpeedTest` shows the pattern for
  slow tests.)
* **`smeltWetSponj`** — recipe-level check: assert the smelting recipe (input wet sponj → sponj)
  resolves via the server's `RecipeManager` rather than actually running a furnace.
* **Dimension drying** is awkward in GameTest (structures run in the overworld test world). Options,
  in order of preference: (a) if drying is implemented via `randomTick` checking
  `level.dimensionTypeRegistration()`, factor the check into a static method and unit-test it;
  (b) keep it manual. Document which was chosen in the test class.

### Registry/data sanity

* **`allBlocksHaveItemsAndLoot`** — iterate the four blocks; assert each has a registered item and a
  loot table that drops itself (mirrors what datagen should already guarantee, but pins it).

## ChimericLib helper opportunities

* **Fluid-region helpers** — `fillRegionWithWater(helper, from, to)` and
  `assertNoFluidWithin(helper, center, radius)` / `countFluidBlocks(helper, box)`. Needed by every
  absorption test here and by any future fluid-touching mod.
* **Furnace fixture** — "preload furnace, set remaining burn time, run N smelts, assert slots"
  helper; also useful for Minekea (compressed-block fuels, if any) and future fuel items.
* **`assertItemEntityCountIs`-style inventory/world diffing** already exists in vanilla GameTest;
  a ChimericLib wrapper that diffs an entire region's block+item-entity state before/after an action
  would simplify the kelp/waterlogged assertions.
* `BlockUtils.getConnectedBlocksByType` is generic flood-fill — consider moving it into ChimericLib
  outright and unit-testing it there (connectivity, cap behavior, diagonals excluded).

## Open questions

* Is the stacked-wet-lava-sponj fuel behavior (losing the dry sponjes) accepted vanilla-furnace
  jank or a bug to fix? The fuel GameTest should encode whichever answer you pick.
* The replaceable-block branch re-queues only while `j < 6` (hardcoded) rather than `< absorptionRadius`
  — likely a leftover from vanilla's constants. Worth a code review before pinning tests to it.

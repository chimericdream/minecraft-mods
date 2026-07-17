# Test Plan — Artificial Heart

Artificial Heart adds decorative, behavior-free versions of pale garden blocks: the **Detached
Creaking Heart** (no creaking spawns, orientable glowing faces, resin faces strippable with an axe)
and **Clipped Eyeblossoms** (open/closed variants frozen in state, plus potted forms). Conversion
happens via tool-use mixins: `ArtificialHeartAxeItemMixin` (creaking heart → artificial, and
stripping individual faces off the artificial block), `ArtificialHeartShearsItemMixin` (eyeblossom →
clipped), and `ArtificialHeartBlockItemMixin`.

## Test conventions

Follow the Hopper X-Treme pattern: GameTest classes in
`fabric/src/main/java/com/chimericdream/artificialheart/fabric/test/`, registered under the
`fabric-gametest` entrypoint in `fabric.mod.json`, `.snbt` structures under
`common/src/main/resources/data/artificialheart/gametest/structure/`. Conversions are
`useOn`-injected at RETURN, so tests can either drive a mock player's item use or call
`Item#useOn` with a constructed `UseOnContext`.

## Manual test plan

Setup: creative world with pale garden blocks (creaking heart, pale oak logs, eyeblossoms), axe,
shears, flower pots. Fabric full pass, NeoForge smoke pass.

1. **Axe conversion** — place a vanilla creaking heart (between pale oak logs so it's "active");
   right-click with an axe: converts to Detached Creaking Heart, plays the strip sound, damages the
   axe by 1, and — critically — **no creaking spawns from the artificial block, ever** (wait through
   a full night).
2. **Orientation preserved** — vanilla creaking hearts have an axis; convert hearts placed on all
   three axes and confirm `withPropertiesOf` carried the axis over (glowing faces line up as
   before).
3. **Face stripping** — on the artificial heart, right-click each of the 4 side faces with an axe:
   only the clicked face's resin/glow is removed (property per face), sound plays, axe takes 1
   damage. Clicking an already-stripped face does nothing (no durability loss — the mixin returns
   early *before* damaging; verify).
4. **Shears conversion** — shears on an open eyeblossom → Clipped Open Eyeblossom; on a closed one →
   Clipped Eyeblossom. Wait through a day/night cycle: clipped flowers must NOT open/close or emit
   the pollen/particle+sound effects; adjacent *vanilla* eyeblossoms must still cycle (no
   suppression bleed).
5. **Potted variants** — pot both clipped flowers; verify render and that removing returns the
   right item.
6. **Decorative inertness** — artificial heart: no creaking spawn attempts, no resin dripping/
   regeneration, comparator reads (vanilla creaking heart emits comparator signal — decide/verify
   what the artificial one does), no interaction with pale oak log placement.
7. **Survival acquisition** — axe/shears durability consumed; conversions trigger the
   ITEM_USED_ON_BLOCK criteria (relevant for advancements); blocks drop themselves when mined
   (loot tables).
8. **Tool edge cases** — shears on the artificial heart / axe on clipped flowers: normal vanilla
   behavior, no conversion; conversion via dispenser (dispensers use items differently — document
   whether dispenser-axe/shears convert or not).

## Recommended automated tests

### GameTests — conversions

Structure `artificialheart:conversion/platform` (simple 3×3 platform).

* **`axeConvertsCreakingHeart`** — set a vanilla creaking heart with a known `AXIS`; invoke
  `AxeItem#useOn` with a mock-player context targeting it; assert block is now
  `ARTIFICIAL_CREAKING_HEART_BLOCK`, `AXIS` matches, axe durability −1, and return value SUCCESS.
* **`conversionPreservesEachAxis`** — parameterized over X/Y/Z.
* **`stripFaceOnlyAffectsClickedFace`** — artificial heart with all faces on; strip NORTH; assert
  NORTH face property false and the other three still true; assert second strip on same face is a
  no-op with no durability change.
* **`shearsConvertOpenAndClosedEyeblossom`** — both variants; assert resulting block and that shears
  took damage.
* **`noCreakingSpawns`** — artificial heart surrounded by pale oak logs in darkness (GameTest can
  set night via the test world or spoof light); run ~1200 ticks with `maxTicks` raised; assert zero
  creaking entities in the test bounds (`helper.assertEntityNotPresent`). This is the mod's core
  promise — worth the slow test.
* **`clippedFlowersDontCycle`** — clipped open + clipped closed blocks; force time from day to
  night (`helper.getLevel().setDayTime(...)` — needs care in shared test worlds; alternatively
  assert the blocks lack the vanilla `open`-cycling block tick by running 600 ticks and asserting
  state unchanged).

### GameTests — data completeness

* **`allBlocksDropThemselves`** — loop `ModBlocks`; break each with the proper tool via
  `context.destroyBlock` + loot assertion, or resolve loot tables directly.
* **`pottedVariantsRegistered`** — assert flower pot content registrations exist for both flowers.

### Unit tests

* `DirectionUtils.getHitFace(axis, clickedFace)` (from ChimericLib, used in the strip logic) —
  table-test all axis × face combinations. This test belongs in **chimeric-lib**, but this mod is
  its consumer; reference it from here to avoid double coverage.

## ChimericLib helper opportunities

* **Tool-use conversion fixture** — "use item I (as mock player) on block at pos, assert block
  became B with properties P, tool damaged by N." Identical shape needed here (axe, shears), in
  Villager Tweaks (bundle on villager — entity flavor), Minekea (wrench), and Hopper X-Treme
  (wrench). Suggest `ToolConversionTestHelper.assertUseConverts(...)`.
* **Entity-absence-over-time assertion** — `assertNoEntitySpawns(helper, EntityType, ticks)`
  wrapping the runAfterDelay + assertEntityNotPresent pattern; also useful for Villager Tweaks
  (zombie conversion) and future spawn-suppression features.
* **Time-of-day fixture** — safe day/night forcing + restoration inside a GameTest batch, for any
  time-driven behavior (eyeblossoms here; villager schedules in Villager Tweaks).

## Open questions

* Should the artificial heart emit a comparator signal / light level like the vanilla active heart?
  Manual test 6 needs the intended answer before automating.
* Dispenser behavior for conversions: in or out of scope?

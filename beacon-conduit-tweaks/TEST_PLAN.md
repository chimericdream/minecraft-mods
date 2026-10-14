# Test Plan — Beacon & Conduit Tweaks

This server-side mod makes beacon and conduit effect ranges configurable via two mixins
(`BCTweaksBeaconMixin`, `BCTweaksConduitMixin`) driven by `BCTweaksConfig` (YACL,
`config/beacon-conduit-tweaks.json5`): `beaconBaseRange` (default 10), `beaconRangePerLevel`
(default 10), a per-block bonus map `beaconRangePerBlock` (defaults: diamond/emerald 0.5,
netherite 2.0, iron/gold 0), `conduitBaseRange` (0), `conduitFrameBlocksPerStep` (7), `conduitRangePerStep` (16),
`conduitRangePerBlock`, and the deprecated `conduitAddVanillaRange` (default true), which only supplies values for the
three new conduit settings when they are `null` (unset) in the config file.
Both per-block maps are saved in the config file and editable on the config screen as lists of
`block_id=range` lines (invalid lines are dropped on save).

## Test conventions

Follow the Hopper X-Treme pattern: GameTest classes in
`fabric/src/main/java/com/chimericdream/bctweaks/fabric/test/`, registered under the
`fabric-gametest` entrypoint, structures under
`common/src/main/resources/data/bctweaks/gametest/structure/`. Beacon pyramids are large — bake them
into `.snbt` structures rather than building them block-by-block in test code. Config-dependent
tests must **set config values programmatically in the test and restore defaults afterward**, since
config state is global.

## Manual test plan

Setup: creative world; iron blocks for pyramids; conduit + prismarine + water. Fabric first, then a
NeoForge smoke pass. Range checks are easiest with a second account/alt or by walking away while
watching the effect icon.

1. **Beacon base range (default config)** — 1-level pyramid, Speed selected: effect present out to
   ~(10 + 10) blocks horizontally per the formula (base + 1 level), gone a few blocks past it.
   Compare against vanilla's 20 for a 1-level beacon — defaults intentionally match vanilla; verify
   no accidental change.
2. **Range per level** — 4-level pyramid: expect base 10 + 4×10 = 50 (matches vanilla). Then set
   `beaconRangePerLevel` to 50 in the config screen, rejoin the world, and verify the effect reaches
   ~210 blocks (base 10 + 4×50... confirm the exact formula the mixin implements and document it in
   the test plan when implementing).
3. **Per-block bonus** — build one 1-level pyramid from iron (bonus 0) and one from netherite
   (bonus 2.0/block): the netherite beacon's range should exceed the iron one by 2×9 = 18 blocks.
   Mixed pyramids (half iron, half diamond) should scale proportionally.
4. **Vertical range** — vanilla extends the effect vertically much farther than horizontally; verify
   the mod's range math treats vertical distance the way you intend (test above and below the
   beacon).
5. **Conduit: additive mode (default)** — full 42-frame conduit: Conduit Power range should be
   vanilla (96) plus the configured per-block contribution. With `conduitRangePerBlock` empty
   (default), range should equal vanilla exactly.
5a. **Conduit: new settings** — set `conduitRangePerStep = 8`, `conduitFrameBlocksPerStep = 7`,
   `conduitBaseRange = 10`: a 42-frame conduit reaches 10 + 6×8 = 58. Setting `conduitFrameBlocksPerStep = 3` gives
   10 + 14×8. The screen shows 0 / 7 / 16 on a fresh config and rejects a step size below 1.
5b. **Conduit: legacy fallback** — use a config with `conduitBaseRange`, `conduitFrameBlocksPerStep` and
   `conduitRangePerStep` all `null` (or missing, as in a config from the previous version): `conduitAddVanillaRange =
   true` behaves as vanilla (96), `false` as the replace case below. An explicitly set new value wins over the legacy flag.
6. **Conduit: replace mode** — set `conduitAddVanillaRange = false` with the new settings unset, or
   `conduitRangePerStep = 0`; with an empty per-block map the
   range should be 0/minimal — verify the degenerate case doesn't crash and document expected
   behavior.
7. **Config screen** — via Mod Menu (Fabric): change values, save, confirm they persist to
   `beacon-conduit-tweaks.json5` and take effect without a full game restart (or document that a
   world reload is needed). Also open the **Beacon Range / Block** and **Conduit Range / Block**
   lists: the beacon list shows the five default lines; add, edit, and remove lines, add a bad line
   (`nonsense`), a fake vanilla block (`minecraft:nonsense=1`), and a fake modded block
   (`othermod:thing=1`), save, and confirm only the first two are dropped, the modded line stays in the file, and the
   rest persist. Start a world and confirm the log warns that `othermod:thing` is ignored (and that no conduit
   or beacon calculation breaks because of it). Then, in a world with a conduit, add and remove a
   valid block (for example `minecraft:gold_block=4`) on the conduit list: the block counts as a conduit frame
   block as soon as you save, with no restart.
8. **Beacon UI & activation regressions** — beam renders, payment item consumed, effects selectable;
   pyramid detection unchanged (the mixin should only touch range, not validity).

## Recommended automated tests

### GameTests — beacon range

Structure family `bctweaks:beacon/*` with a pre-built pyramid + active beacon (structure blocks can
include the beacon with its effect pre-selected via block entity NBT).

* **`beaconRangeMatchesFormula_default`** — 1-level pyramid structure; spawn a mock player
  (`GameTestHelper` player helpers / `makeMockPlayer`) at distance `base + perLevel − 2` (inside)
  and assert the status effect is applied within one beacon tick cycle (beacons pulse every 80
  ticks — set `maxTicks` ≥ 100); then a second player just outside and assert no effect.
* **`beaconRangeScalesWithConfig`** — same structure; set `beaconBaseRange = 30` in-test; assert a
  player at 35 blocks gets the effect. Restore config in a `finally`/cleanup step.
* **`perBlockBonusApplies`** — netherite-pyramid structure vs iron-pyramid structure, players at a
  distance only reachable with the bonus; one asserts effect, the other asserts none.
* **Implementation note:** effect application happens on the beacon's 80-tick pulse; tests should
  force a pulse (e.g. `runAfterDelay(85, ...)`) rather than polling.

### GameTests — conduit range

* **`conduitAdditiveDefaultEqualsVanilla`** — full conduit structure under water; player at 90
  blocks (inside vanilla 96) gets Conduit Power; player at 120 does not.
* **`conduitPerBlockExtendsRange`** — set `conduitRangePerBlock` for prismarine to a large value
  in-test; assert the 120-block player now gets the effect.
* Conduits require water columns around the player for the effect — bake water into the structure or
  place the mock player in a small water pocket.

### Unit tests

* If the range formulas live inline in the mixins, extract them to static methods
  (`computeBeaconRange(levels, blockCounts, config)`, `computeConduitRange(frameBlocks, config)`)
  and table-test: 0–4 levels × per-level values × per-block maps, including empty maps, unknown
  block IDs in the map, and negative values (decide: clamp or allow range-reducing blocks?).

### Config serialization test

* Done: `ConduitRangeTest` (flat-range formula and the per-setting legacy fallback).
* Done: `RangeEntriesTest` (fabric `test` source set) covers `RangeEntries.toLines` / `fromLines` (sorted output,
  bad and unknown lines skipped) and checks that all five config fields are `@SerialEntry` and not `transient`.
* Still open: a full GSON round-trip of `BCTweaksConfig`.

## ChimericLib helper opportunities

* **Config override fixture** — `withConfig(handler, mutator, () -> testBody)` that snapshots a YACL
  config, applies changes, and guarantees restoration. Needed here, in Banner Tweaks, Villager
  Tweaks, Shulker Stuff, and Hopper X-Treme — every YACL mod in the suite.
* **Mock-player-at-distance helper** — "spawn survival mock player N blocks from pos, assert status
  effect X present/absent after M ticks." Reused by any effect-radius feature.
* **Large-structure conventions** — beacons/conduits need big `.snbt` files; a documented workflow
  (build in-game → `/test export`-style capture) belongs in the shared testing docs ChimericLib
  ships.

## Open questions

* Exact beacon range formula (does per-level replace or add to vanilla's `10 + level*10`?) —
  document it in the mixin when implementing tests so manual and automated expectations agree.

## Guide book

9. **Guide book**
   * Craft a book and a glass block: you get the Beacon & Conduit Tweaks Guide. `/opus give beaconconduittweaks:guide` gives the same book.
   * Open it: the Beacons, Conduits, and Configuration chapters open, links between pages work, and the recipes show on crafting panels.
   * Without Opus installed, the game starts normally, the mod works, and there is no guide recipe (nothing logs a recipe parse error about `opus:book`).
   * With Opus older than 1.2.0 installed, the game refuses to start.

# Test Plan — Beacon & Conduit Tweaks

This server-side mod makes beacon and conduit effect ranges configurable via two mixins
(`BCTweaksBeaconMixin`, `BCTweaksConduitMixin`) driven by `BCTweaksConfig` (YACL,
`config/beacon-conduit-tweaks.json5`): `beaconBaseRange` (default 10), `beaconRangePerLevel`
(default 10), a per-block bonus map `beaconRangePerBlock` (defaults: diamond/emerald 0.5,
netherite 2.0, iron/gold 0), `conduitAddVanillaRange` (default true), and `conduitRangePerBlock`.
Note the per-block maps are marked `transient` in the config class — they currently are **not
user-editable via the config file**, which the tests should make visible (see Open questions).

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
6. **Conduit: replace mode** — set `conduitAddVanillaRange = false`; with an empty per-block map the
   range should be 0/minimal — verify the degenerate case doesn't crash and document expected
   behavior.
7. **Config screen** — via Mod Menu (Fabric): change values, save, confirm they persist to
   `beacon-conduit-tweaks.json5` and take effect without a full game restart (or document that a
   world reload is needed).
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

* Round-trip `BCTweaksConfig` through its GSON serializer and assert which fields survive. This will
  immediately flag the `transient` per-block maps issue and prevent regressions once fixed.

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

* The `transient` keyword on `beaconRangePerBlock` / `conduitRangePerBlock` means those maps never
  serialize — is per-block tuning meant to be user-facing yet, or code-only defaults? README's table
  implies configurable. The serialization test will fail until this is resolved; write it to encode
  the intended behavior.
* Exact beacon range formula (does per-level replace or add to vanilla's `10 + level*10`?) —
  document it in the mixin when implementing tests so manual and automated expectations agree.

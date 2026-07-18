# Test Plan — ChimericLib

ChimericLib is developer-facing plumbing: registration (`ModRegistryHelper`,
`RegisterableBlock`/`BlockConfig`), datagen (`BlockDataGenerator`, `ItemDataGenerator` + Fabric
implementations), inventories (`ImplementedInventory`, `InventoryUtils`), screens
(`SimpleInventoryScreen(Handler)`, `DoubleWideInventoryScreen(Handler)`, `ScreenHelpers`), loot
(`LootTableModifier`), entities (`SimpleSeatEntity`), a `ShearsItemMixin`, tags, and utility classes
(`ColorHelpers`, `FluidHelpers`, `TextHelpers`, `TextureUtils`, `DirectionUtils`, `Tool`,
`ModConfigurable`). It has no player-facing content, so its test strategy differs from every other
mod: **unit tests for the pure helpers, GameTests against test-only fixture content for the
world-coupled pieces, and consumer-mod tests as the integration layer.**

A key decision baked into this plan: chimeric-lib is also the planned home of the suite's **GameTest
harness helpers** (see `POTENTIAL_FEATURES.md`, "Developer & testing tools" — now expanded with the
specific helpers the other mods' test plans call for). The helpers themselves need tests here, using
throwaway fixture blocks registered only in the test environment (or in the `playgrounds` sandbox
project during prototyping).

## Test conventions

* **Unit tests** — add a `test` source set to `common` (JUnit; none of the repo's projects have one
  yet — chimeric-lib should pioneer the Gradle wiring the other mods will copy). Helpers that touch
  registries need the Fabric loader JUnit bootstrap (`fabric-loader-junit`) so `Identifier`,
  `ItemStack`, etc. work off-thread; document that wiring in the test class once.
* **GameTests** — classes in `fabric/src/main/java/com/chimericdream/lib/fabric/test/` (match the
  actual package root, `com.chimericdream.lib`), registered under the `fabric-gametest` entrypoint,
  structures under `common/src/main/resources/data/chimericlib/gametest/structure/`. Fixture
  content (a test block using `RegisterableBlock`, a test container using `ImplementedInventory`, a
  sittable test block) should be registered from the test entrypoint only, never in the shipped jar.

## Manual test plan

The library has almost no direct manual surface; manual verification happens through consumer mods.
Keep this short list:

1. **Suite boot** — full modpack (all active mods) boots on Fabric and NeoForge with no
   registration errors; this is the library's real smoke test.
2. **Seat behavior** — via Minekea chairs/stools: sit, dismount, break-while-seated (details in
   `minekea/TEST_PLAN.md`; root fixes land here).
3. **Shears mixin** — whatever `ShearsItemMixin` enables (verify against consumers — Artificial
   Heart's shears conversion path): confirm vanilla shears behavior is otherwise unchanged
   (sheep, vines, tripwire).
4. **Datagen run** — run each consumer mod's datagen and diff generated resources for unexpected
   changes after touching the generators.

## Recommended automated tests

### Unit tests — pure utilities (highest value, cheapest)

**Status: implemented.** All pure-utility suites below are live in the fabric `test` source set
(`fabric/src/test/java/com/chimericdream/chimericlib/test/`), 30 tests across 7 classes, run with
`./gradlew :chimeric-lib:fabric:test`. Registry-touching tests extend the `BootstrapMinecraft` base
class, which runs the vanilla bootstrap and bakes data components so headless `ItemStack`
construction works on MC 26.2 (plain `Bootstrap.bootStrap()` leaves them unbound — see that class's
javadoc). This is the canonical JUnit wiring the other mods copy (enchantment-numbers-fix next).

* **`DirectionUtils`** — ✅ done. Full table tests: `getHitFace(axis, clickedFace)` across all 3
  axes × 6 faces (Artificial Heart depends on this exact matrix). No rotation/opposite helpers
  exist on the class today.
* **`ColorHelpers`** — ✅ done. `RGB` round-trip and boundary values (0, 255, alpha handling),
  `getTint` in/out-of-range, `getName`/`getColors`, `getDye`/`getWool` resolution + rejection,
  `mixColors` null/identity/blend paths.
* **`TextHelpers`** — ✅ done. Component building/formatting: literal/translatable output, aqua+italic
  style application; asserts flattened strings.
* **`ItemHelpers`** — ✅ done (not originally listed): `getIdentifier` for a populated stack and
  `ItemStack.EMPTY` → `minecraft:air`.
* **`RomanNumeral`-adjacent note** — lives in enchantment-numbers-fix, not here; if it migrates to
  the lib, its unit suite (see that mod's plan) moves with it.
* **`TextureUtils` / `Tool` / `ModConfigurable`** — ✅ done for `TextureUtils` (path prefix/suffix
  building, namespace preservation, registered-block id lookup) and `Tool` (full `getItemTag` /
  `getMineableTag` enum mapping incl. SHEARS/NONE nulls). `ModConfigurable` is a single-method
  interface with no logic — nothing to unit-test.
* **`InventoryUtils`** — ✅ done for the current surface (`convertListToInventory`: size, per-index
  contents, empty case). The stack merging/insertion math described here (component-sensitive
  stacks, max-stack-size edges, Shulker Stuff's builder) is aspirational — that logic does not yet
  live in this class; expand the suite when it lands.

### GameTests — world-coupled pieces (against fixture content)

* **`registerableBlockRegistersEverything`** — register a fixture block through
  `ModRegistryHelper`/`BlockConfig` in the test entrypoint; assert block, item, and (if configured)
  block entity all resolve under the expected ids.
* **`implementedInventoryPersists`** — fixture container block: fill via code, save/load BE NBT
  round-trip, assert contents; hopper insert/extract against it (reuse the Hopper X-Treme chest
  pattern) to prove the `Container` contract behaves.
* **`simpleAndDoubleWideScreenHandlers`** — open each handler server-side with a mock player over
  the fixture container: slot count/layout, shift-click routing in both directions, close returns
  cursor stack.
* **`simpleSeatEntityLifecycle`** — sittable fixture block: mock player sits (entity created at
  expected offset), dismounts (entity removed), block broken while seated (player freed, entity
  removed, **no leaked entities** after 10 repetitions), and dismount position is never inside a
  wall (place the seat against a wall to test the safety logic — currently a known gap per
  POTENTIAL_FEATURES' "SimpleSeatEntity polish").
* **`lootTableModifierInjects`** — subclass `LootTableModifier` in the test to inject a marker
  entry into a vanilla table; assert the loaded table contains it; assert non-targeted tables
  untouched; reload idempotence (inject once, not twice, after `/reload`).

### Datagen verification tests

* **Golden-file style** — run `BlockDataGenerator`/`ItemDataGenerator` for a fixture block in a
  unit test and compare emitted JSON against checked-in expected files (blockstate, model, loot
  table). Catches silent format drift on Minecraft updates before it breaks 200 Minekea blocks.

### Tests for the GameTest harness helpers themselves

Each helper the suite plans (see `POTENTIAL_FEATURES.md` for the consolidated list — container
fill/assert, redstone gate, update detector, mock-player interaction wrappers, villager fixture
builder, config override fixture, loot-table kit, inventory diffing, entity-absence watcher,
menu harness, registry loop scaffolding) gets a self-test GameTest here proving it does what it
claims — e.g. the update-detector fixture is validated against a vanilla stone place (fires) and
nothing (doesn't fire) before Houdini Block trusts it. Helper self-tests double as usage
documentation for the other mods.

## ChimericLib helper opportunities

Not applicable in the usual sense — this mod is the *destination* for the helpers collected from
every other TEST_PLAN.md in the repo. The consolidated, prioritized list lives in
`POTENTIAL_FEATURES.md` under "Developer & testing tools"; treat the per-mod "ChimericLib helper
opportunities" sections as its requirements backlog, and this plan's "helper self-tests" section as
the acceptance criteria.

## Open questions

* Gradle wiring for a JUnit `test` source set under Architectury (loom + common split) — decide the
  canonical pattern here before replicating to other mods (enchantment-numbers-fix is the second
  customer).
* Should fixture content live in a dedicated `chimericlib-testmod` subproject (cleanest) or behind
  the gametest entrypoint in the fabric subproject (simplest)? The `playgrounds` project is a third
  option already sitting in the repo.

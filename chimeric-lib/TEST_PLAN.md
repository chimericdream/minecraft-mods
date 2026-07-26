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

* **Unit tests** — the `test` source set lives in the **fabric** subproject (JUnit, wired in the
  root `build.gradle` under `project.name == 'fabric'`: `fabric-loader-junit` + `useJUnitPlatform()`).
  It went to fabric rather than common because the loader-junit bootstrap that makes `Identifier`,
  `ItemStack`, etc. resolve off-thread is a fabric artifact. Registry-touching tests extend the
  shared `BootstrapMinecraft` helper (see below) — the canonical wiring the other mods copy.
* **Shared test helpers (`testFixtures`)** — cross-mod unit-test helpers live in chimeric-lib
  **`common`'s `testFixtures` source set** (`common/src/testFixtures/java/com/chimericdream/lib/testkit/`)
  and publish as a `testFixtures` variant on `components.java` (a `-test-fixtures.jar` described in
  Gradle Module Metadata). chimeric-lib's own fabric tests consume it via
  `testImplementation(testFixtures(project(":chimeric-lib:common")))`; a downstream mod imports the
  published variant with
  `testImplementation(testFixtures("com.chimericdream.lib:chimericlib-common-<mc>:<version>"))` — no
  copying. `BootstrapMinecraft` is the first helper; the GameTest harness helpers land here too.
* **GameTests** — live in the fabric subproject's dedicated **`gametest` source set**
  (`fabric/src/gametest/...`), created by `fabricApi.configureTests { createSourceSet = true }` in the
  root `build.gradle`. This source set's classpath is extended from `main` (so it sees Minecraft,
  fabric-api's gametest module, and chimeric-lib common) but its output goes only to the `runGameTest`
  run config — **never** into the shipped/published jar. That is the isolation boundary: fixture
  content and test classes both live here and cannot reach production. Test classes are in
  `com.chimericdream.lib.fabric.test`, registered under the `fabric-gametest` entrypoint of a separate
  test mod (`chimericlib_test`, `fabric/src/gametest/resources/fabric.mod.json`); its `main` entrypoint
  registers the fixture content. Fixture content lives in `com.chimericdream.lib.fabric.test.fixture`
  (a test block via `ModRegistryHelper`/`registerWithItem`, an `ImplementedInventory` container BE, a
  `SimpleSeatEntity` `EntityType`, `MenuType`s for the screen handlers, and a `LootTableModifier`
  subclass). Tests use the default `fabric-gametest-api-v1:empty` structure (8×8×8 air) and `setBlock`
  at relative positions, so **no `.snbt` structure files are needed**. Run with
  `./gradlew :chimeric-lib:fabric:runGameTest`.
* **Shared GameTest harness helpers (published)** — the reusable, mod-agnostic helpers
  (`GameTestContainers`, `GameTestEntities`, `GameTestMenus`) live in **common's `testFixtures`**
  source set (`common/src/testFixtures/java/com/chimericdream/lib/testkit/gametest/`), published in the
  same `testFixtures` variant as `BootstrapMinecraft`. chimeric-lib's own `gametest` source set
  consumes them via `gametestImplementation(testFixtures(project(":chimeric-lib:common")))`; a
  downstream mod consumes the published variant the same way from its own `gametest` source set. They
  operate only on vanilla `GameTestHelper`/`Container`/`AbstractContainerMenu`, so they compile in
  common and never drag test code into any production jar.

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
`./gradlew :chimeric-lib:fabric:test`. Registry-touching tests extend the shared `BootstrapMinecraft`
helper (published from common's `testFixtures` — see "Test conventions"), which runs the vanilla
bootstrap and bakes data components so headless `ItemStack` construction works on MC 26.2 (plain
`Bootstrap.bootStrap()` leaves them unbound — see that class's javadoc). This is the canonical JUnit
wiring the other mods copy (enchantment-numbers-fix next).

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

**Status: implemented.** Nine GameTests across five classes, all green via
`./gradlew :chimeric-lib:fabric:runGameTest` (see "Test conventions" for the isolated `gametest`
source set + published-`testFixtures` structure).

* **`RegisterableBlockGameTest`** — ✅ done. Registers a plain block+item and a block+item+block-entity
  through `ModRegistryHelper`, then asserts each resolves under the expected id (`BuiltInRegistries`
  lookups by identity, `hasBlockEntity()` for the trio).
* **`ImplementedInventoryGameTest`** — ✅ done. Fixture container BE: fill via code + slot assertions,
  a `saveWithFullMetadata`→`loadStatic` NBT round-trip preserving contents, and a **vanilla hopper**
  inserting into it (proves the `Container` contract behaves for real game systems).
* **`ScreenHandlerGameTest`** — ✅ done. Opens `SimpleInventoryScreenHandler` and
  `DoubleWideInventoryScreenHandler` server-side with a mock player over a fixture container: asserts
  slot count/layout and shift-click (`quickMoveStack`) routing in both directions.
* **`SimpleSeatEntityGameTest`** — ✅ done (lifecycle). Mock player sits, dismounts, and the seat
  auto-despawns past its 20-tick grace window, leaving **no leaked entities**. Two behaviours the
  earlier draft listed are **deliberately not asserted** because they don't exist yet (known gaps,
  POTENTIAL_FEATURES "SimpleSeatEntity polish"): freeing the rider when the seat block is broken
  mid-sit, and keeping the dismount position out of walls. Asserting them would only encode the gap;
  add the tests when the safety logic lands.
* **`LootTableModifierGameTest`** — ✅ done. Subclass injects a guaranteed marker pool into exactly one
  targeted table id; asserts non-targeted ids are untouched, and rolls the modified table to prove the
  marker actually drops. (Reload idempotence is a property of the fabric loot event firing once per
  load, not of `LootTableModifier` itself, so it's out of scope for this unit-of-code test.)

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

## Resolved decisions

* **JUnit `test` source set wiring** (was an open question) — the `test` source set lives in the
  **fabric** subproject with `fabric-loader-junit` + `useJUnitPlatform()` (root `build.gradle`).
  Shared helpers live in **common's `testFixtures`** source set and publish as a `testFixtures`
  variant, imported downstream via `testImplementation(testFixtures("…:chimericlib-common-<mc>:<ver>"))`.
  One loom wart to copy: the custom `testFixtures` source set doesn't inherit loom's Minecraft
  classpath, so `common/build.gradle` does `sourceSets.testFixtures.compileClasspath += sourceSets.main.compileClasspath`.
* **GameTest fixture-content location** (was an open question) — the fully-isolated approach won: the
  fabric **`gametest` source set** (from `configureTests { createSourceSet = true }`) is the isolation
  boundary. Fixture content and test classes live there and are built only for `runGameTest`, never
  the shipped jar — no separate `chimericlib-testmod` subproject, no gametest code behind the
  production `main` entrypoint. The reusable *helpers* are published from common's `testFixtures`
  variant (see conventions). Hopper X-Treme predates this and puts its gametests in `main`; its
  TEST_PLAN carries a note to refactor to this pattern.

## Open questions

* None outstanding for chimeric-lib's own tests. Remaining work is the broader **GameTest harness
  helper backlog** (POTENTIAL_FEATURES "Developer & testing tools" — redstone gate, update detector,
  villager fixture builder, etc.) and their self-tests; the three helpers shipped so far
  (`GameTestContainers`/`GameTestEntities`/`GameTestMenus`) are the first slice.

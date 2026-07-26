# Testing

How automated tests are wired and run in this monorepo. Test infrastructure is centralized in the root
`build.gradle` and pioneered in **chimeric-lib** (the reference implementation); other mods adopt the
same patterns.

## chimeric-lib source edits are picked up with no publish step

There is **no publish loop**. chimeric-lib is resolved as an in-build **`project()` dependency** by
every consumer mod *and* by chimeric-lib's own `test`/`gametest` source sets, so editing chimeric-lib
source recompiles straight into whatever you build or test — no `bun run publish:lib`, no `~/.m2`.
`mavenLocal()` has been removed from the resolution repositories, so a stale published jar can no
longer shadow your source (it structurally cannot happen). See `DEPENDENCY-PLAN.md` for the wiring
(`build.gradle` chimeric-lib block, the settings.gradle hoist + `evaluationDependsOnChildren()`).

`bun run publish:lib` still exists, but only for **releasing** chimeric-lib to maven-local / GitHub
Packages for *external* consumers — it is not part of the edit→test loop.

## Unit tests (JUnit + fabric-loader-junit)

- Live in the **`fabric`** subproject's `test` source set (e.g.
  `chimeric-lib/fabric/src/test/java/...`), **not** `common`.
- Wiring is in root `build.gradle` under the `project.name == 'fabric'` block:
  `testImplementation "net.fabricmc:fabric-loader-junit:${fabric_loader_version}"` + `test { useJUnitPlatform() }`.
- Run: `./gradlew :chimeric-lib:fabric:test` (chimeric-lib is currently the only mod with unit tests;
  ~30 tests across colors/text/inventory/items/resource/tool/math).

### Bootstrapping Minecraft

Pure-logic tests (Direction tables, string/bit math, `Component` building) need **no** bootstrap.
Tests that touch registries/items/blocks/tags must bootstrap first: outside a live game the static
registries (`Items`, `Blocks`, `BuiltInRegistries`, …) are unpopulated until Minecraft's bootstrap
runs. `fabric-loader-junit` puts the loader and the named Minecraft jar on the test classpath; the
bootstrap then makes registry lookups resolve.

```java
SharedConstants.tryDetectVersion();
Bootstrap.bootStrap();   // guard with a static flag; fabric-loader-junit shares one classloader
```

Canonical helper: **`BootstrapMinecraft`** (`chimeric-lib/common/src/testFixtures/java/com/chimericdream/lib/testkit/`).
Subclass it for registry-touching tests.

> **On MC 26.2 this is not sufficient** — item data components become data-driven and stay *unbound*
> after `Bootstrap.bootStrap()`, so constructing an `ItemStack` throws
> `NullPointerException: Components not bound yet`, and the helper has to bake them via
> `BuiltInRegistries.DATA_COMPONENT_INITIALIZERS`. **That API does not exist on 26.1.2 and is not
> needed here**; components are bound at construction. If you forward-port this branch, restore the
> bake — see `docs/MC-26.2-NOTES.md` on the 26.2 line.

## GameTests (isolated `gametest` source set)

- Loom's `fabricApi.configureTests { createSourceSet = true }` (root `build.gradle`, `project.name == 'fabric'`)
  creates a **`gametest` source set** in every fabric subproject. Its classpath extends from `main`, and
  its output is built **only** for the run tasks — it **never** ships in the published jar. That is the
  isolation boundary: put fixture content + `@GameTest` classes there and nothing test-only leaks out.
- Run: `./gradlew :<mod>:fabric:runGameTest` (e.g. `:chimeric-lib:fabric:runGameTest`,
  `:minekea:fabric:runGameTest`).
- Layout (chimeric-lib reference): tests in
  `chimeric-lib/fabric/src/gametest/java/com/chimericdream/lib/fabric/test/`, methods
  `public void x(GameTestHelper ctx)` annotated `net.fabricmc.fabric.api.gametest.v1.GameTest`.
  `structure()` defaults to `fabric-gametest-api-v1:empty` (an 8×8×8 all-air region) so you rarely need
  `.snbt` files — just `setBlock` at relative positions.
- Test mod: `fabric/src/gametest/resources/fabric.mod.json` declares a separate mod id (e.g.
  `chimericlib_test`) with a `main` entrypoint (registers fixtures) + a `fabric-gametest` entrypoint
  (the test classes). This `processResources` does **not** expand `${version}` — use literal values.

> **Note — hopper-xtreme is the exception.** Its GameTests currently live in the **main** source set
> (`hopper-xtreme/fabric/src/main/.../test/`) and ship (inert) in the jar. `hopper-xtreme/TEST_PLAN.md`
> tracks migrating them into an isolated `gametest` source set to match chimeric-lib.

## Shared test helpers (testFixtures — published, not copied)

chimeric-lib's `common` applies `java-test-fixtures` and publishes a `testFixtures` variant. Shared
helpers live in `chimeric-lib/common/src/testFixtures/java/com/chimericdream/lib/testkit/`:

- `BootstrapMinecraft` (JUnit bootstrap, above).
- GameTest helpers under `.../testkit/gametest/`: `GameTestContainers`, `GameTestEntities`,
  `GameTestMenus` — they touch only vanilla `GameTestHelper`/`Container`/`AbstractContainerMenu`, so
  they compile in `common` and never leak into a production jar.

Consuming them (the root `build.gradle` already wires `test`; a mod's `gametest` opts in per project):
- In-build (the normal case, every mod): `testFixtures(project(":chimeric-lib:common"))` /
  `gametestImplementation(testFixtures(project(":chimeric-lib:common")))`.
- External consumers only (published variant): `testImplementation(testFixtures("com.chimericdream.lib:chimericlib-common:<ver>"))`.
  The artifactId is `chimericlib-common` — the Minecraft version is part of `<ver>`
  (e.g. `26.1.2-5.0.0-alpha.2`), **not** a suffix on the artifactId.

**Loom wart:** a custom `testFixtures` source set does not inherit Loom's Minecraft classpath, so
`common/build.gradle` needs `sourceSets.testFixtures.compileClasspath += sourceSets.main.compileClasspath`.

The GameTest-helper backlog (what to add next, tagged per consumer mod) lives in
`chimeric-lib/POTENTIAL_FEATURES.md` → "Developer & testing tools".

## Visual smoke tests (rendering)

For changes that must be seen (models, block colors, in-world GUI, textures), the workflow is
headless: create a world, build the scene, take a screenshot, read the PNG back. It is temporary
throwaway code — add a client tick hook, capture, then remove it.

> The `mc-visual-smoke-test` Claude Code skill on the 26.2 line automates this, but its code samples
> are written against 26.2 client APIs and were **not** backported. Adapt them against 26.1.2 before
> reusing, or drive the capture by hand.

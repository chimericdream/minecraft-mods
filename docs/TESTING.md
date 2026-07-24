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

### Bootstrapping Minecraft (MC 26.2 gotcha)

Pure-logic tests (Direction tables, string/bit math, `Component` building) need **no** bootstrap.
Tests that touch registries/items/blocks/tags must bootstrap first — and on MC 26.2, `Bootstrap.bootStrap()`
alone is not enough: data components are data-driven and stay **unbound**, so constructing an
`ItemStack` throws `NullPointerException: Components not bound yet`. Bake them once:

```java
SharedConstants.tryDetectVersion();
Bootstrap.bootStrap();
HolderLookup.Provider provider = VanillaRegistries.createLookup();
BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(provider).forEach(p -> p.apply()); // guard with a static flag
```

Canonical helper: **`BootstrapMinecraft`** (`chimeric-lib/common/src/testFixtures/java/com/chimericdream/lib/testkit/`).
Subclass it for registry-touching tests. The bake is guarded by a static boolean because
fabric-loader-junit runs all tests in one classloader and `apply()` is not idempotent.

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
  (e.g. `26.2-6.0.0-alpha.0`), **not** a suffix on the artifactId.

**Loom wart:** a custom `testFixtures` source set does not inherit Loom's Minecraft classpath, so
`common/build.gradle` needs `sourceSets.testFixtures.compileClasspath += sourceSets.main.compileClasspath`.

The GameTest-helper backlog (what to add next, tagged per consumer mod) lives in
`chimeric-lib/POTENTIAL_FEATURES.md` → "Developer & testing tools".

## Visual smoke tests (rendering)

For changes that must be seen (models, block colors, in-world GUI, textures), a headless
create-world → build-scene → screenshot → read-PNG workflow exists as the **`mc-visual-smoke-test`**
Claude Code skill (`.claude/skills/mc-visual-smoke-test/`). It is temporary throwaway code — the skill
covers the exact 26.2 API calls and the cleanup checklist.

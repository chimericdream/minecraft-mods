# Testing

How automated tests are wired and run in this monorepo. Test infrastructure is centralized in the root
`build.gradle` and pioneered in **chimeric-lib** (the reference implementation); other mods adopt the
same patterns.

## chimeric-lib source edits are picked up with no publish step

There is **no publish loop**. chimeric-lib is resolved as an in-build **`project()` dependency** by
every consumer mod *and* by chimeric-lib's own `test`/`gametest` source sets, so editing chimeric-lib
source recompiles straight into whatever you build or test — no `bun run publish:lib`, no `~/.m2`.
`mavenLocal()` has been removed from the resolution repositories, so a stale published jar can no
longer shadow your source (it structurally cannot happen). See `docs/history/DEPENDENCY-PLAN.md` for the wiring
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

### Registration is mandatory and fails silently

A `@GameTest`-annotated method (or a `FabricClientGameTest` implementation, see the
[visual smoke test skill](../.claude/skills/mc-visual-smoke-test/SKILL.md)) only runs if its fully
-qualified class name is **also** listed in that mod's test-only `fabric.mod.json`, under the
`fabric-gametest` (or `fabric-client-gametest`) entrypoint array. A class that compiles fine but isn't
listed there produces **no error and no failing test** — it's just silently absent from the results,
which reads as "everything passed."

Run `bun run verify:gametests` after adding, renaming, or removing a `@GameTest` class (or before
trusting a green `runGameTest` result on a new test) — it cross-checks every mod's `gametest` source set
against its `fabric.mod.json` and reports any class that's missing from the entrypoint list (or listed
but no longer exists on disk).

> **Note — hopper-xtreme is the exception.** Its GameTests currently live in the **main** source set
> (`hopper-xtreme/fabric/src/main/.../test/`) and ship (inert) in the jar. `hopper-xtreme/TEST_PLAN.md`
> tracks migrating them into an isolated `gametest` source set to match chimeric-lib.

## Shared test helpers (testFixtures — published, not copied)

chimeric-lib's `common` applies `java-test-fixtures` and publishes a `testFixtures` variant. Shared
helpers live in `chimeric-lib/common/src/testFixtures/java/com/chimericdream/lib/testkit/`:

- `BootstrapMinecraft` (JUnit bootstrap, above).
- GameTest helpers under `.../testkit/gametest/`: `GameTestContainers`, `GameTestEntities`,
  `GameTestMenus`, `GameTestPlayers` — they touch only vanilla
  `GameTestHelper`/`Container`/`AbstractContainerMenu`/`Player`, so they compile in `common` and never
  leak into a production jar. `GameTestPlayers` covers positioning/orienting a mock player and driving
  an item's general `use()` dispatch (buckets, and anything else `GameTestHelper#useBlock` can't
  reach, since that only drives `useOn`/`useWithoutItem`).

Consuming them (the root `build.gradle` already wires `test`; a mod's `gametest` opts in per project):
- In-build (the normal case, every mod): `testFixtures(project(":chimeric-lib:common"))` /
  `gametestImplementation(testFixtures(project(":chimeric-lib:common")))`.
- External consumers only (published variant): `testImplementation(testFixtures("com.chimericdream.lib:chimericlib-common:<ver>"))`.
  The artifactId is `chimericlib-common` — the Minecraft version is part of `<ver>`
  (e.g. `26.2-6.0.0`), **not** a suffix on the artifactId.

**Loom wart:** a custom `testFixtures` source set does not inherit Loom's Minecraft classpath, so
`common/build.gradle` needs `sourceSets.testFixtures.compileClasspath += sourceSets.main.compileClasspath`.

The GameTest-helper backlog (what to add next, tagged per consumer mod) lives in
`chimeric-lib/ideas/testing/gametest-harness-helpers.md`.

## Visual smoke tests (rendering)

For changes that must be seen (models, block colors, in-world GUI, textures), a headless
create-world → build-scene → screenshot → read-PNG workflow exists as the **`mc-visual-smoke-test`**
Claude Code skill (`.claude/skills/mc-visual-smoke-test/`). It is temporary throwaway code — the skill
covers the exact 26.2 API calls and the cleanup checklist.

### Who runs it: interactive vs. autonomous

- **Interactive/collaborative sessions** (working live with the user on a feature): default to letting
  the user do the visual check themselves in-game — they'd rather look at it directly than review a
  screenshot. Don't launch `mc-visual-smoke-test` unprompted here; ask first, and only run it if they
  say to (or if they've already told you not to bother, as in this session).
- **Autonomous work** (overnight runs, explicitly AFK/unattended tasks): run the automated visual smoke
  test yourself before reporting a rendering change as done — there's no one to look at it live.
- **Unsure which situation you're in**: pause and ask which they'd prefer. If there's no response for
  several minutes, proceed with the automated smoke test rather than blocking indefinitely.

- **If a smoke run is ambiguous, stop and ask.** After two runs that don't settle a visual judgment call
  ("does this look right"), stop and ask the user whether to keep iterating or let them check in a real client,
  instead of re-adjusting camera angles on your own. Each `runClientGameTest` costs about 2 minutes of
  Gradle + JVM startup, and the user can often confirm in seconds what more scripted shots may never settle.
  This applies to the visual judgment only; mechanical failures (crashes, exceptions, wrong file written) have an
  unambiguous pass/fail and don't need the pause.

## GameTest-writing notes (MC 26.2)

- Fixtures in chimeric-lib's gametest set are registered through the library's own `ModRegistryHelper`
  (`new ModRegistryHelper("chimericlib_test", LogManager.getLogger(...))` + suppliers + `.init()` from the
  entrypoint), so registering them *is* what the registration test verifies.
- A `MenuType` self-reference in a static field initializer (even inside the `MenuSupplier` lambda) is a compile
  error: pass `null` as the type in the supplier (Minekea's `CrateScreenHandler` does this) and construct the
  handler directly with the real registered type in the test.
- Block-entity NBT round-trip: `be.saveWithFullMetadata(provider)` then
  `BlockEntity.loadStatic(pos, state, tag, provider)`, with `provider = ctx.getLevel().registryAccess()`.
  Container block entities save/load via `ContainerHelper.saveAllItems` / `loadAllItems(view, items)` in
  `saveAdditional(ValueOutput)` / `loadAdditional(ValueInput)`.
- `GameTestHelper.getEntities(EntityType)` is region-scoped, which makes it good for leak checks.
  `makeMockServerPlayerInLevel()` works but is deprecated for removal.
- **Test-count sanity check.** After adding a `@GameTest` class, compare the run log's `"N tests are now
  running"` to what you expect. A count that didn't change means the class isn't in the `fabric-gametest`
  entrypoint array (see "Registration is mandatory" above). `log-all-the-things` once had a 7-test class that
  never ran for months while every run reported "all passed".

### Minekea's gametest source set

Minekea has its own `gametest` source set, separate from chimeric-lib's:

- Sources: `minekea/fabric/src/gametest/java/com/chimericdream/minekea/fabric/test/`.
- Test-mod manifest: `minekea/fabric/src/gametest/resources/fabric.mod.json` (id `minekea_test`, a
  `fabric-gametest` entrypoint listing each test class, `depends` on minekea, chimericlib, fabric-api and
  architectury). No `main` entrypoint is needed: the real minekea mod loads from the run classpath and registers
  all blocks.
- Wiring is automatic from the root `build.gradle` (`fabricApi.configureTests`), so you only add source files.
- Run: `./gradlew :minekea:fabric:runGameTest` (about 1.5 minutes, headless server). Tests use the real
  registered blocks. `GameTestHelper.useBlock(pos)` synthesizes a center hit with an empty creative hand and
  dispatches straight into `useWithoutItem` (no raycast against the collision shape). The default arena is empty;
  place blocks around relative (2,2,2).
- Harmless log noise: a `minekea:minekea_catalogue` patchouli book parse ERROR (unrelated data issue), and Fabric
  counts a built-in structure self-test, so 2 authored tests report as "3 tests".

# Testing

How automated tests are wired and run in this monorepo. Test infrastructure is centralized in the root
`build.gradle` and pioneered in **chimeric-lib** (the reference implementation); other mods adopt the
same patterns.

## The chimeric-lib publish loop (read first)

chimeric-lib's **own** `gametest` source sets build against the in-repo Gradle **project** dependency
(`project(':chimeric-lib:common')`), so they run your latest source directly — no republish needed.

**Its own JUnit `test` source set is not so lucky.** Under `fabric-loader-junit` the library classes
are loaded by the Fabric `knot` classloader off the *runtime* classpath, which resolves
`chimericlib-fabric` from **maven-local**. Editing chimeric-lib source and running
`./gradlew :chimeric-lib:fabric:test` therefore tests the **last published jar**, not your edit — the
test compiles fine and then asserts stale behaviour (stack traces point at line numbers that no longer
match the source, which is the giveaway). Run `bun run publish:lib` first. Confirm what actually
loaded with:

```java
System.out.println(SomeClass.class.getProtectionDomain().getCodeSource());
```

**Consumer mods are the exception:** they load chimeric-lib from **maven-local (`~/.m2`)**, not from a
project dependency. So after editing chimeric-lib source you **must** republish before a consumer's
build sees the change, or the old bytecode is used:

```bash
bun run publish:lib
# ./gradlew :chimeric-lib:common:publishToMavenLocal :chimeric-lib:fabric:publishToMavenLocal :chimeric-lib:neoforge:publishToMavenLocal
```

Symptom of forgetting: a consumer's build compiles but still asserts the old behavior; the class
actually loads from `~/.m2/repository/com/chimericdream/lib/.../*.jar`. A `clean` / `--rerun-tasks` does
**not** fix it (the stale copy is in `.m2`, not the build dir). You can confirm the source with
`SomeClass.class.getProtectionDomain().getCodeSource().getLocation()`.

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

Consuming them:
- In-repo (chimeric-lib itself): `testFixtures(project(":chimeric-lib:common"))`.
- Downstream unit tests: `testImplementation(testFixtures("com.chimericdream.lib:chimericlib-common-<mc>:<ver>"))`.
- Downstream gametests: `gametestImplementation(testFixtures("com.chimericdream.lib:chimericlib-common-<mc>:<ver>"))`.

**Loom wart:** a custom `testFixtures` source set does not inherit Loom's Minecraft classpath, so
`common/build.gradle` needs `sourceSets.testFixtures.compileClasspath += sourceSets.main.compileClasspath`.

The GameTest-helper backlog (what to add next, tagged per consumer mod) lives in
`chimeric-lib/POTENTIAL_FEATURES.md` → "Developer & testing tools".

## Visual smoke tests (rendering)

For changes that must be seen (models, block colors, in-world GUI, textures), a headless
create-world → build-scene → screenshot → read-PNG workflow exists as the **`mc-visual-smoke-test`**
Claude Code skill (`.claude/skills/mc-visual-smoke-test/`). It is temporary throwaway code — the skill
covers the exact 26.2 API calls and the cleanup checklist.

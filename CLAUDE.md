# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

> **This is the `26.1.2` branch.** `main` tracks Minecraft 26.2. Where the two differ, this file
> describes 26.1.2. Backport status and per-mod plans live in `docs/backport-26.1.2/`.

## Snapshot (verify against `gradle.properties` before relying on exact versions)

- **Minecraft**: `26.1.2` (`minecraft_version` / `minecraft_compatibility` in `gradle.properties`)
- **Mappings**: official Mojang names — the Yarn→Mojang migration is **complete**. There is no
  `mappings` block or `yarn_mappings` property anywhere; the code builds directly against Minecraft's
  shipped 26.1.2 names (e.g. the identifier class is `net.minecraft.resources.Identifier`, **not**
  Yarn's `net.minecraft.util.Identifier` and **not** `ResourceLocation`).
- **Java**: **25** (`sourceCompatibility`/`targetCompatibility = VERSION_25`, `options.release = 25` in
  root `build.gradle`) — *not* 21.
- **Loaders / libs** (from `gradle.properties`): Fabric Loader `0.19.3`, Fabric API `0.154.2+26.1.2`,
  NeoForge `26.1.2.71`, Architectury API `20.0.7`, YACL `3.9.4+26.1`, Mod Menu `18.0.0`,
  Kotlin-for-Forge `6.3.0`, Loom `1.17-SNAPSHOT`, chimeric-lib `26.1.2-5.0.0`.
- **Loom plugin**: `dev.architectury.loom-no-remap`; shadow via `com.gradleup.shadow`.

## Architecture

Multi-mod monorepo using Gradle + Architectury for cross-platform mod development. Each mod supports
both Fabric and NeoForge.

- **Multi-project Gradle build**: root `build.gradle` defines common config applied to every mod's
  `common`/`fabric`/`neoforge` subprojects.
- **Architectury pattern**: each mod has `common/` (shared), `fabric/`, and `neoforge/` subprojects.
- **ChimericLib dependency**: most mods depend on `chimeric-lib`. It is wired as an in-build
  **`project()` dependency** (see `build.gradle`), so editing chimeric-lib source recompiles straight
  into every consumer — no publish step, no `~/.m2`. A published-coordinate fallback is used only when
  chimeric-lib is not part of the build.
- **Shared mod conventions**: every mod's `build.gradle` is one line —
  `apply from: "${rootDir}/gradle/mod-conventions.gradle"`. That script owns coordinates, archive
  naming, `fabric.mod.json` / `neoforge.mods.toml` token expansion, and the shadow
  raw-jar→`shadowJar` reclassification. Per-mod values come from each mod's `gradle.properties`.
- **Compat floors are centralized**: `architectury_compat`, `fabric_compat`, `modmenu_compat`,
  `yacl_compat` and `chimericlib_compat` live in the **root** `gradle.properties` and are inherited by
  every subproject. A mod re-declares one only to pin a different floor (minekea pins
  `chimericlib_compat`; hopper-xtreme and minekea add their own `patchouli_compat`).
  `minecraft_compat` stays **per-mod** — `scripts/create-modpacks.ts` reads it for the jar filename.
- **Project list**: active projects are controlled by `settings.gradle` `projectList` and mirrored in
  `project-list.json` (kept in sync by the `update:*` scripts). `chimeric-lib` is hoisted to the front
  of the include order — see the comment in `settings.gradle` for why that plus
  `evaluationDependsOnChildren()` is required.

### Active vs. inactive mods

**Active (15)** — uncommented in `settings.gradle`:
`archaeology-tweaks`, `artificial-heart`, `athenaeum`, `banner-tweaks`, `beacon-conduit-tweaks`,
`chimeric-lib` (core library), `enchantment-numbers-fix`, `flat-bedrock`, `hopper-xtreme`,
`houdini-block`, `minekea`, `miniblock-merchants`, `shulker-stuff`, `sponj`, `villager-tweaks`.

**Inactive (6)** — commented out but their directories still exist on disk:
`blacklight`, `cobblicious`, `hang-from-slabs`, `jdcrafte`, `pannotia-companion`, `playgrounds`.
They are stranded on older dependencies (Architectury 18.x, chimeric-lib 4.x, MC 1.21.10) and will
not compile as-is. To work on one, uncomment it in `settings.gradle`, run
`bun run update:projectlist`, and expect to port it first.

## chimeric-lib is an in-build project dependency (no publish loop)

Consumer mods and chimeric-lib's own `test`/`gametest` source sets all resolve chimeric-lib as an
in-build **`project()` dependency**, so editing chimeric-lib source recompiles directly into whatever
you build or test — **no `bun run publish:lib` needed** during development. `mavenLocal()` has been
removed from the resolution repositories, so a stale published jar cannot shadow your source. The
wiring (why it depends on both `:common` and the platform project, the settings.gradle hoist +
`evaluationDependsOnChildren()` that orders configuration) is documented in `DEPENDENCY-PLAN.md`.

`bun run publish:lib` is now **release-only**: it publishes chimeric-lib for *external* consumers, not
for the edit→build loop in this repo.

## Commands

### Build & modpacks (Bun scripts, see `package.json`)
- `bun run build` — full build: `clean` → prepare (copy access wideners + update Patchouli books) →
  `./gradlew build` → create modpacks → teardown (revert temp `fabric.mod.json` edits).
- `bun run build:gradle` — `./gradlew build` only.
- `bun run build:modpacks` — create modpack distributions in `build/modpacks/{fabric,neoforge}/`.
- `bun run clean` — `./gradlew clean`.
- `./gradlew build` / `./gradlew clean` — Gradle directly (skips the Bun lifecycle).
- `./gradlew projects` — cheapest check that the whole build still configures.

### chimeric-lib
- `bun run publish:lib` — publish chimeric-lib to maven-local / GitHub Packages for **external**
  consumers (release-only; not needed to develop mods in this repo — see above).

### Project management
- `bun run update:settingsgradle` — regenerate `settings.gradle` from the project list.
- `bun run update:projectlist` — regenerate `project-list.json`.
- `bun run copy:accesswideners` — copy access widener files across projects.
- `bun run update:patchoulibooks` — update Patchouli documentation books.

### Testing (details in `docs/TESTING.md`)
- **JUnit** (unit tests, chimeric-lib is the only adopter so far):
  `./gradlew :chimeric-lib:fabric:test`
- **GameTests** (isolated `gametest` source set, never ships):
  `./gradlew :<mod>:fabric:runGameTest` (e.g. `:chimeric-lib:fabric:runGameTest`, `:minekea:fabric:runGameTest`)
- Tests that touch registries/items must bootstrap Minecraft via `BootstrapMinecraft`
  (chimeric-lib's `testFixtures` variant).
- ⚠ **hopper-xtreme is the exception**: its GameTests live in the **main** source set
  (`hopper-xtreme/fabric/src/main/.../fabric/test/`) and are registered in the shipping
  `fabric.mod.json`. Keep that layout; `hopper-xtreme/TEST_PLAN.md` tracks migrating it.

### Datagen
`./gradlew :<mod>:fabric:runDatagen` — only `minekea` and `hopper-xtreme` define the run config.
Generated output lives in `<mod>/common/src/main/generated/` and is committed; **regenerate, never
hand-edit**.

> ⚠ Datagen needs item data components bound at the top of `buildRecipes()`, because components are
> data-driven and datagen never performs the server reload that binds them:
> ```java
> BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registryLookup).forEach(pending -> pending.apply());
> ```
> Without it, anything reading components at datagen time (e.g. `Item.getDefaultMaxStackSize()`) throws
> `NullPointerException: Components not bound yet`. Same on 26.1.2 and 26.2 — see `docs/TESTING.md`
> for the unit-test equivalent.

## Scaffolding

- **New mod**: `scripts/init-mod.sh` (interactive) runs `bun create mod` against the `.bun-create/mod/`
  template, replaces `{{MOD_ID}}`/`{{CLASS_NAME}}`/etc. placeholders, and updates the project list +
  settings.gradle. Produces the standard `common`/`fabric`/`neoforge` layout.
- **New block family (minekea)**: follow the existing pattern — a `ModThingGroup` registration class
  (`minekea/common/.../block/**`) + a `ChimericLibBlockDataGenerator` subclass under
  `minekea/fabric/src/main/java/com/chimericdream/minekea/fabric/block/**` wired into the category
  aggregator, then run datagen. minekea has 68 such `*DataGenerator` classes as references (e.g.
  `fabric/block/furniture/ArmoireBlockDataGenerator.java`).

## Conventions

- **Line endings**: LF everywhere, enforced by the root `.gitattributes` (`* text=auto eol=lf`).
  `gradlew.bat` is the one deliberate exception (`eol=crlf` in the working tree).
- **demo-world (minekea)**: `minekea/demo-world/` is a deterministic showcase generator. Generated
  files (`demo_build.mcfunction`, manifests) are produced by `generate_layout.py` /
  `extract_jar_contents.py` — **never hand-edit them**; regenerate. See `minekea/demo-world/README.md`.
- **Container block-entity inventories**: pick the abstraction by capability, don't mix arbitrarily.
  - A block entity that should support **loot tables** (chest/barrel-like) extends
    `RandomizableContainerBlockEntity` and honors `trySaveLootTable`/`tryLoadLootTable` in its
    save/load. Don't also implement `ImplementedInventory` on these — its defaults are dead weight and
    it's easy to bypass the loot-table plumbing.
  - `ImplementedInventory` (chimeric-lib) is for inventories that are **not** loot-table-capable —
    typically item-backed or purpose-built containers (glass jar, shelf, display case, the hopper
    filter item). Back them with a `NonNullList.withSize(...)`.
  - `setItems` must preserve the fixed slot count: copy in place
    (`for i: items.set(i, incoming.get(i))`), never `clear()+addAll` on a fixed-size `NonNullList`
    (that can throw or let the size drift). Same fixed-size rule as `ImplementedInventory.clearContent`.

## Planning & reference docs

- `docs/backport-26.1.2/` — the `main` → `26.1.2` backport plan: `README.md` for the overarching plan
  and shared changes, one file per mod. **Start here** if you are working the backport.
- `docs/TESTING.md` — how tests are wired and run (JUnit bootstrap, GameTest harness, testFixtures).
- `DEPENDENCY-PLAN.md` — how chimeric-lib is wired as an in-build project dependency (no publish loop)
  and the remaining monorepo build-structure improvements.
- `docs/BLOCK-MIGRATION.md` — non-breaking block/item deprecation & rename across both loaders (no DataFixerUpper).
- `CODE-REVIEW-PLAN.md` (repo root) — phased code-review plan; the source of the `N.M` item numbers
  referenced throughout the backport commits.
- `UPDATE-PLAN.md` (repo root) — the Yarn→Mojang update runbook (migration complete).
- Per-mod `TEST_PLAN.md` and `POTENTIAL_FEATURES.md` — testing plans and feature backlogs.

## Minecraft Asset Reference

When the user asks to check for asset changes between Minecraft versions, use the minecraft-assets
repository as a reference:

**Repository**: https://github.com/InventivetalentDev/minecraft-assets

- Every Minecraft version is available as a tag in this repository.
- Compare tags to identify changes in vanilla assets between versions.
- Common changes include texture file renames, model structure updates, and recipe format changes.

**Example**: Between Minecraft 1.21.4 and 1.21.5, creaking heart texture files were renamed:
- `minecraft:block/creaking_heart_active` → `minecraft:block/creaking_heart_awake`
- `minecraft:block/creaking_heart_top_active` → `minecraft:block/creaking_heart_top_awake`

**Note**: Only check this repository when explicitly asked by the user. Do not proactively check it
during routine version updates.

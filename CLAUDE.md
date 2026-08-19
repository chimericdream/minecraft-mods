# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Snapshot (verify against `gradle.properties` before relying on exact versions)

- **Minecraft**: `26.2` (`minecraft_version` / `minecraft_compatibility` in `gradle.properties`)
- **Mappings**: official Mojang names — the Yarn→Mojang migration is **complete**. There is no
  `mappings` block or `yarn_mappings` property anywhere; the code builds directly against Minecraft's
  shipped 26.2 names (e.g. the identifier class is `net.minecraft.resources.Identifier`, **not** Yarn's
  `net.minecraft.util.Identifier` and **not** `ResourceLocation`).
- **Java**: **25** (`sourceCompatibility`/`targetCompatibility = VERSION_25`, `options.release = 25` in root `build.gradle`) — *not* 21.
- **Loaders / libs** (from `gradle.properties`): Fabric Loader `0.19.3`, Fabric API `0.154.2+26.2`,
  NeoForge `26.2.0.15-beta`, Architectury API `21.0.4`, YACL `3.9.5+26.2`, Mod Menu `20.0.1`,
  Kotlin-for-Forge `6.3.0`, Loom `1.17-SNAPSHOT`, chimeric-lib `26.2-6.0.0`.
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
- **Project list**: active projects are controlled by `settings.gradle` `projectList` and mirrored in
  `project-list.json` (kept in sync by the `update:*` scripts).

### Active vs. inactive mods

**Active (15)** — uncommented in `settings.gradle`:
`archaeology-tweaks`, `artificial-heart`, `athenaeum`, `banner-tweaks`, `beacon-conduit-tweaks`,
`chimeric-lib` (core library), `enchantment-numbers-fix`, `flat-bedrock`, `hopper-xtreme`,
`houdini-block`, `minekea`, `miniblock-merchants`, `shulker-stuff`, `sponj`, `villager-tweaks`.

**Inactive (6)** — commented out but their directories still exist on disk:
`blacklight`, `cobblicious`, `hang-from-slabs`, `jdcrafte`, `pannotia-companion`, `playgrounds`.

To work on an inactive mod, uncomment it in `settings.gradle` (and run `bun run update:projectlist`).

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
  `./gradlew build` → create modpacks → teardown (revert temp `fabric.mod.json` edits). Pass
  `--mods=<id,id,...>` (comma-separated `mod_id`s from each mod's `gradle.properties`, e.g.
  `--mods=chimericlib,minekea`) to scope every one of those steps — including the Gradle task
  selection and `clean` — to just those mods instead of the whole repo; omit it to build everything,
  as before. `--exclude=<id,id,...>` is the inverse — it removes matching mods from whatever set
  `--mods` selected (or from the full project list if `--mods` was omitted), e.g.
  `--exclude=minekea` builds everything except minekea, or `--mods=chimericlib,minekea
  --exclude=minekea` builds just chimericlib. Both flags work on `build:gradle`, `build:modpacks`,
  `clean`, `copy:accesswideners`, and `update:patchoulibooks` when run standalone.
- `bun run build:gradle` — `./gradlew build` only (or scoped `:mod:build` tasks with `--mods`/`--exclude`).
- `bun run build:modpacks` — create modpack distributions in `build/modpacks/{fabric,neoforge}/`.
- `bun run clean` — `./gradlew clean` (or scoped `:mod:clean` tasks with `--mods`/`--exclude`).
- `./gradlew build` / `./gradlew clean` — Gradle directly (skips the Bun lifecycle).

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
  (bakes data components — required on MC 26.2, see below).

## Access wideners are build-time-only outside `common`

Each mod that needs one has a **canonical, always-committed** `<mod_id>.accesswidener` in
`<mod>/common/src/main/resources/`, wired into Loom via `accessWidenerPath` in **both**
`common/build.gradle` and `fabric/build.gradle` (the fabric block reads it off the common project,
e.g. `accessWidenerPath = project(":minekea:common").loom.accessWidenerPath`). That wiring alone is
enough for IDE/dev compilation — nothing else is required to edit or build a mod day-to-day.

`fabric.mod.json`'s `"accessWidener"` field and a **copy** of the file into
`<mod>/fabric/src/main/resources/` are added **only transiently**, by `bun run build`'s `prebuild` step
(`copy:accesswideners`), and removed again by `postbuild` (`teardown:build` /
`scripts/revert-fabricmodjson.ts`) once the build finishes. This copy is needed for the *packaged
runtime jar* (Fabric reads the AW path from `fabric.mod.json`, not from Loom's dev-time config), but
having it declared in both `common` and `fabric` at once — as it would be if these were left
committed — causes "duplicate accessWidener" errors in the IDE. That's why the scripts add it right
before a full build and strip it right after, instead of just committing it once.

**Practical implication**: `<mod>/fabric/src/main/resources/<mod_id>.accesswidener` and a
`fabric.mod.json` with an `"accessWidener"` line should **never** be sitting in a commit. If you see
either — e.g. because `bun run copy:accesswideners` was run by hand while debugging (its normal
callers are `prebuild`/`bun run build`, not something you'd invoke standalone) and the resulting files
got swept up in a `git add` — delete the copied `.accesswidener` file and remove the `"accessWidener"`
line from `fabric.mod.json` before committing. This is easy to forget since nothing about it looks
wrong at a glance (the mod still compiles and builds fine either way).

## Datagen gotcha (MC 26.2)

Item data components are bound **lazily during a server reload**, not at bootstrap. Datagen never does
that reload, so any generator reading `Item.components()` (e.g. `getDefaultMaxStackSize()`) throws
`NullPointerException: Components not bound yet`. Bind them at the **top of every `buildRecipes()`**
(or any datagen path that reads components):

```java
BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registryLookup)
    .forEach(pending -> pending.apply());
```

Reference: `minekea/fabric/.../data/ModDataGenerator.java`. Full write-up: `docs/MC-26.2-NOTES.md`.

## Mixin-added `SynchedEntityData` fields crash on NeoForge only

Adding a new synced-data field to a **vanilla** entity via `@Mixin` — a `@Unique static final
EntityDataAccessor<T> FOO = SynchedEntityData.defineId(TargetEntity.class, ...)` merged into the
target class by Mixin — compiles and works fine on Fabric, but crashes NeoForge at bootstrap with an
opaque `net.neoforged.fml.ModLoadingException: ... ExceptionInInitializerError: null` during "Registry
initialization." The crash report and logs give **no stack trace or cause** for this one — FML's
`ModLoader.waitForFuture` only formats the caught exception's class + message, never its `getCause()`
chain, so the real reason never surfaces anywhere on disk. Confirmed by attaching a debugger and by
bisecting the mixin file line-by-line.

**Root cause**: NeoForge patches `SynchedEntityData.defineId` to verify the calling code's declaring
class actually *is* the entity class being registered against, and hard-rejects mismatches as "attempt
to add synced data to a foreign entity." Mixin's bytecode merge makes the field's initializer *execute*
as part of the target class's own `<clinit>`, but the merged code still isn't recognized as declared by
the entity class, so NeoForge (and only NeoForge — Fabric has no equivalent check) blocks it
unconditionally. This is deliberate on NeoForge's part (preventing synced-data id collisions between
mods), not a bug to work around with mixin priority/ordering tricks.

**Fix**: don't use `SynchedEntityData` for mixin-added fields on vanilla entities. Use each platform's
native attachment API instead (NeoForge's `AttachmentType`, Fabric's
`AttachmentType`/`AttachmentRegistry`/`AttachmentTarget`), which isn't drawn from a fixed id space and
supports both persistence (`.serialize(MapCodec)` / `.persistent(Codec)`) and client sync
(`.sync(StreamCodec)` / `.syncWith(StreamCodec, AttachmentSyncPredicate)`). Put the platform split
behind a common `Provider` interface set via `setProvider(...)` at platform mod-init — **not**
`@ExpectPlatform`, since NeoForge's dev run resolves `common` and the `neoforge` source set as separate
JPMS modules, and an `@ExpectPlatform`-generated `Impl` class in the same package as its interface
causes two modules to export that package, which fails FML startup ("Modules ... export package ...").
Reference implementations: `sneaky-tweaks`'s `CampfireGraceHolder` (the original instance of this
pattern) and `camel-nostrils`'s `CN$CamelSnoutState`.

## Scaffolding

- **New mod**: `scripts/init-mod.sh` (interactive) runs `bun create mod` against the `.bun-create/mod/`
  template, replaces `{{MOD_ID}}`/`{{CLASS_NAME}}`/etc. placeholders, and updates the project list +
  settings.gradle. Produces the standard `common`/`fabric`/`neoforge` layout.
- **New block family (minekea)**: follow the existing pattern — a `ModThingGroup` registration class
  (`minekea/common/.../block/**`) + a `ChimericLibBlockDataGenerator` subclass
  (`minekea/fabric/.../block/**DataGenerator.java`) wired into the category aggregator, then run
  datagen. minekea has ~55 such datagen classes as references (e.g. `ArmoireBlockDataGenerator`).

## Versioning & releases

- **Only bump `mod_version` (in each mod's `gradle.properties`) when actually cutting a release.**
  Day-to-day commits between releases do not get their own version number or dated changelog entry,
  even if they'd otherwise look changelog-worthy (new feature, bug fix, etc.) — none of this is
  published anywhere until a release is explicitly cut, so there's no reader for an intermediate
  version.
- **Official releases are tagged in git** (e.g. `chimericlib/26.2-6.0.0`, `minekea/26.2-10.0.0`,
  `chimericlib/3.1.0-beta.1` — see `git tag --list`). A pre-release (`-beta.x`) tag counts as a real
  release just as much as a final one — the distinction that matters is tagged vs. untagged, not
  beta vs. final. A mod's current `mod_version` therefore reflects one of two states:
  - **At the tagged commit itself**: the exact released version, matching the tag (e.g. `6.0.0`, or
    `3.1.0-beta.1`).
  - **Any commit after that tag, until the next release is cut**: the next anticipated version,
    suffixed `-beta.0` if the prior release was final, or `-beta.<x+1>` if the prior release was
    itself `-beta.x` (e.g. after tagging `6.0.0`, `mod_version` becomes `6.1.0-beta.0`; after tagging
    `3.1.0-beta.1`, it becomes `3.1.0-beta.2`). Either way it *stays* there through every commit —
    features, fixes, refactors — until it's actually time to cut the next release, at which point it's
    renamed to whatever that release's real version is and tagged. Don't increment the beta number
    per-commit or per-session; it only moves when a release actually ships.
- **Changelog structure follows the same split.** Each mod's `CHANGELOG.md` accumulates all untagged
  work under a single `### Unreleased changes` heading at the top (with the usual `#### New
  Features`/`#### Bug Fixes`/`#### Changes` subheadings) — not a new dated/versioned heading per
  commit or session. When a release is cut (beta or final), `### Unreleased changes` is renamed to a
  dated `### <mc_version> - <version>` heading (matching the git tag) and a fresh, empty `###
  Unreleased changes` starts collecting the next round.
- **Changelog/README tone**: player-facing docs (changelogs, READMEs) must be concise and
  non-technical — the audience is Minecraft players, not programmers. Editing test: for each
  sentence, if removing it still conveys the information accurately, delete it. **Exception:
  chimeric-lib** — it's a shared library consumed by other mods, so its changelog/README audience
  is developers; stay concise but technical detail (API names, method signatures, behavior) is
  appropriate there.

## Conventions

- **Line endings**: LF everywhere, enforced by the root `.gitattributes` (`* text=auto eol=lf`).
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

- `docs/TESTING.md` — how tests are wired and run (JUnit bootstrap, GameTest harness, testFixtures).
- `DEPENDENCY-PLAN.md` — how chimeric-lib is wired as an in-build project dependency (no publish loop)
  and the remaining monorepo build-structure improvements.
- `docs/MC-26.2-NOTES.md` — MC 26.2 port gotchas: datagen component binding, API renames, reading
  decompiled vanilla source, the shutdown-watchdog false crash.
- `docs/BLOCK-MIGRATION.md` — non-breaking block/item deprecation & rename across both loaders (no DataFixerUpper).
- `CODE-REVIEW-PLAN.md` (repo root) — phased code-review plan. Phase 1 (critical bugs) done on unmerged
  `fix/*` branches; Phase 2+ not started.
- `UPDATE-PLAN.md` (repo root) — the Yarn→Mojang + MC 26.2 update runbook (migration now complete).
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

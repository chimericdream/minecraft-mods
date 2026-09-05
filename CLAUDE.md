# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

> **This is the `26.1.2` branch.** `main` tracks Minecraft 26.2. Where the two differ, this file
> describes 26.1.2. Backport status and per-mod plans live in `docs/backport-26.1.2/`
> (`ROUND-2.md` is the current one).

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

### Content `main` has that this version doesn't

Sulfur, Cinnabar (both the blocks and the `SulfurCube` entity family) and the `MUSIC_DISC_BOUNCE`
item don't exist on 26.1.2 — they're 26.2-only vanilla content. Anything in `main` that depends on
them is carved out here rather than ported:

- **minekea**: no small sulfur cubes capturable in a glass jar, and no cinnabar/sulfur building
  blocks (beams, covers, slabs, stairs, compressed blocks, bookshelves).
- **but-what-about**: no Chiseled Sulfur stairs/slabs/walls.

Before porting a future round's payload, re-check whether the running Minecraft version has caught
up — if so, these carve-outs (and their `#### Known omissions` / changelog notes in the two mods
above) can finally be filled in. `docs/backport-26.1.2/ROUND-2.md` §7 has the full verification
history (a field-level diff of `Blocks`/`Items` plus the `SulfurCube` entity check).

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
- **`tools/`**: developer tooling that is **not** part of the mod build. Each subdirectory is its own
  standalone Gradle build with its own wrapper, deliberately absent from `settings.gradle`, so
  `./gradlew build` at the repo root never sees it. See "tools/" below.

### Active vs. inactive mods

`settings.gradle`'s `projectList` is the source of truth — the lists below are a snapshot and drift as
mods are added, so check it before relying on them.

**Active (27)** — uncommented in `settings.gradle`:
`archaeology-tweaks`, `artificial-heart`, `athenaeum`, `banner-tweaks`, `beacon-conduit-tweaks`,
`better-portal-linking`, `better-target-dummies`, `but-what-about`, `camel-nostrils`,
`chimeric-lib` (core library), `effective-gear`, `enchantment-numbers-fix`, `flat-bedrock`,
`hang-from-slabs`, `hopper-xtreme`, `houdini-block`, `jdcrafte`, `log-all-the-things`, `minekea`,
`miniblock-merchants`, `next-update-now`, `shulker-stuff`, `sneaky-tweaks`, `sponj`, `stack-it-up`,
`toy-box`, `villager-tweaks`.

**Inactive (4)** — commented out but their directories still exist on disk:
`blacklight`, `cobblicious`, `pannotia-companion`, `playgrounds`. They are stranded on older
dependencies (Architectury 18.x, chimeric-lib 4.x, MC 1.21.10) and will not compile as-is.

To work on an inactive mod, uncomment it in `settings.gradle`, run `bun run update:projectlist`, and
expect to port it first.

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
  selection and `clean` — to just those mods instead of the whole repo; omit it to build everything.
  `--exclude=<id,id,...>` is the inverse — it removes matching mods from whatever set `--mods`
  selected (or from the full project list if `--mods` was omitted), e.g. `--exclude=minekea` builds
  everything except minekea. Both flags work on `build:gradle`, `build:modpacks`, `clean`,
  `copy:accesswideners`, `update:patchoulibooks` and `datagen` when run standalone.
- `bun run build:gradle` — `./gradlew build` only (or scoped `:mod:build` tasks with `--mods`/`--exclude`).
- `bun run build:modpacks` — create modpack distributions in `build/modpacks/{fabric,neoforge}/`.
- `bun run clean` — `./gradlew clean` (or scoped `:mod:clean` tasks with `--mods`/`--exclude`).
- `./gradlew build` / `./gradlew clean` — Gradle directly (skips the Bun lifecycle).
- `./gradlew projects` — cheapest check that the whole build still configures.

### Data generation
- `bun run datagen` — runs `runDatagen` (Fabric) and/or `runData` (NeoForge) for every selected mod
  that declares `has_fabric_datagen = true` / `has_neoforge_datagen = true` in its
  `gradle.properties` (see "Datagen gotcha" below and the flags rule under it). Mods that declare
  neither flag are skipped. Supports the same `--mods=` / `--exclude=` scoping as the build scripts.
  **Run this before cutting any release** (see Versioning & releases) so generated data on disk
  reflects the latest code, not a stale prior run.
- `./gradlew :<mod>:fabric:runDatagen` — the single-mod Gradle task directly. Generated output lives
  in `<mod>/common/src/main/generated/` and is committed; **regenerate, never hand-edit**.

### Project status
- `bun run status` — lists every mod with its `mod_version` and whether its `CHANGELOG.md` has
  content under `### Unreleased changes`.

### chimeric-lib
- `bun run publish:lib` — publish chimeric-lib to maven-local / GitHub Packages for **external**
  consumers (release-only; not needed to develop mods in this repo — see above).

### Project management
- `bun run update:settingsgradle` — regenerate `settings.gradle` from the project list.
- `bun run update:projectlist` — regenerate `project-list.json`.
- `bun run copy:accesswideners` — copy access widener files across projects.
- `bun run update:patchoulibooks` — update Patchouli documentation books.

### Testing (details in `docs/TESTING.md`)
- **JUnit** (unit tests): `./gradlew :<mod>:fabric:test`
- **GameTests** (isolated `gametest` source set, never ships):
  `./gradlew :<mod>:fabric:runGameTest` (e.g. `:chimeric-lib:fabric:runGameTest`, `:minekea:fabric:runGameTest`)
- Tests that touch registries/items must bootstrap Minecraft via `BootstrapMinecraft`
  (chimeric-lib's `testFixtures` variant) — it bakes data components, which is required here for the
  same reason datagen needs the bind below.
- ⚠ **hopper-xtreme is the exception**: its GameTests live in the **main** source set
  (`hopper-xtreme/fabric/src/main/.../fabric/test/`) and are registered in the shipping
  `fabric.mod.json`. Keep that layout; `hopper-xtreme/TEST_PLAN.md` tracks migrating it.

## Access wideners are build-time-only outside `common`

Each mod that needs one has a **canonical, always-committed** `<mod_id>.accesswidener` in
`<mod>/common/src/main/resources/`, wired into Loom via `accessWidenerPath` in **both**
`common/build.gradle` and `fabric/build.gradle` (the fabric block reads it off the common project,
e.g. `accessWidenerPath = project(":minekea:common").loom.accessWidenerPath`). That wiring alone is
enough for IDE/dev compilation — nothing else is required to edit or build a mod day-to-day.

`fabric.mod.json`'s `"accessWidener"` field and a **copy** of the file into
`<mod>/fabric/src/main/resources/` are added **only transiently**, by `bun run build`'s prepare step
(`copy:accesswideners`), and removed again by its teardown step (`scripts/revert-fabricmodjson.ts`)
once the build finishes. This copy is needed for the *packaged runtime jar* (Fabric reads the AW path
from `fabric.mod.json`, not from Loom's dev-time config), but having it declared in both `common` and
`fabric` at once — as it would be if these were left committed — causes "duplicate accessWidener"
errors in the IDE. That's why the scripts add it right before a full build and strip it right after,
instead of just committing it once.

**Practical implication**: `<mod>/fabric/src/main/resources/<mod_id>.accesswidener` and a
`fabric.mod.json` with an `"accessWidener"` line should **never** be sitting in a commit. If you see
either — e.g. because `bun run copy:accesswideners` was run by hand while debugging and the resulting
files got swept up in a `git add` — delete the copied `.accesswidener` file and remove the
`"accessWidener"` line from `fabric.mod.json` before committing. This is easy to forget since nothing
about it looks wrong at a glance (the mod still compiles and builds fine either way).

## tools/

Standalone builds that support development in this repo but ship nothing to players. Each has its own
Gradle wrapper and is **not** in `settings.gradle` — run them from their own directory, and don't add
them to the project list or `project-list.json`.

- **`tools/mod-status-plugin/`** — an IntelliJ plugin that shows each mod's `mod_version` next to its
  folder in the Project View, plus an amber dot when that mod's `CHANGELOG.md` has content under
  `### Unreleased changes`. It reads the same two files `bun run status`
  (`scripts/mod-status.ts`) does, so **if the rule for either ever changes, change it in both places**
  — `ModStatusReader.java` and `mod-status.ts`. Build with `./gradlew buildPlugin` from
  `tools/mod-status-plugin/` and install the zip from `build/distributions/`; see that directory's
  `README.md` for the platform details that shaped it.

## Gradle: `PKIX path validation failed` on this machine

Avast's "Web/Mail Shield" intercepts HTTPS and re-signs it with a root CA that lives in the **Windows
certificate store** but not in any JDK's `cacerts`. `curl` works, every JVM fails, and Gradle swallows
the handshake error and reports it as a bogus resolution miss:

> Plugin [id: '...'] was not found in any of the following sources

Only `--debug` reveals the real cause (`Starting handshake` → `Shutdown connection`, and
`SSLHandshakeException: PKIX path validation failed`). Fix by pointing the daemon at the Windows store,
which already trusts that root:

```properties
org.gradle.jvmargs = -Xmx2G -Djavax.net.ssl.trustStoreType=WINDOWS-ROOT
```

The mod build doesn't hit this because its dependencies are already in `~/.gradle/caches` — it bites
**new** Gradle builds that must download something. `tools/mod-status-plugin/gradle.properties` carries
the flag for that reason. Don't chase repository URLs or plugin versions before ruling this out.

## Datagen gotcha

Item data components are bound **lazily during a server reload**, not at bootstrap. Datagen never does
that reload, so any generator reading `Item.components()` (e.g. `getDefaultMaxStackSize()`) throws
`NullPointerException: Components not bound yet`. Bind them at the **top of every `buildRecipes()`**
(or any datagen path that reads components):

```java
BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registryLookup)
    .forEach(pending -> pending.apply());
```

This is **not** a 26.2-only quirk — `DATA_COMPONENT_INITIALIZERS` exists on 26.1.2 and the failure
mode is identical here. Reference: `minekea/fabric/.../data/ModDataGenerator.java`. The unit-test
equivalent (baking components in the JUnit bootstrap) is in `docs/TESTING.md`.

### Keep the `gradle.properties` datagen flags in sync

Each mod's `gradle.properties` declares `has_fabric_datagen = true` and/or `has_neoforge_datagen =
true` when that platform registers a datagen entrypoint (Fabric: a `fabric-datagen` entrypoint in
`fabric.mod.json` pointing at a `ModDataGenerator`; NeoForge: a `data { ... }` run + `GatherDataEvent`
listener). `bun run datagen` reads these flags to decide what to run, so **whenever you add, remove,
or port a mod's data generation** (either platform), update that mod's `gradle.properties` in the same
change. A mod with no datagen on either platform has neither flag.

## Platform-specific gotchas

If you're writing a `@Mixin` on a vanilla entity, registering entity/block-entity renderers, or
otherwise hitting behavior that only breaks on one loader, check `docs/NEOFORGE.md` or
`docs/FABRIC.md` first — both accumulate confirmed loader-specific runtime/build gotchas (root cause +
fix + reference implementation) found while working in this repo.

## Custom block-model rendering: check for z-fighting on coplanar surfaces

Whenever a custom/overlay model draws a surface flush against another block's real geometry — a decal
sitting on a host's face, a full block model rendered via `submitMovingBlock` inside/against another
block's space — check for z-fighting (two coplanar quads at the same depth flicker between which one
wins per pixel) wherever those surfaces are actually coincident. The fix is to nudge the overlay a
hair off the coincident plane — imperceptible at the magnitude that works (1/2048 of a block), so it
still reads as flush with no visible gap. Two forms: a per-vertex nudge for hand-authored/hand-cut
geometry, and a whole-block `poseStack` translate/scale nudge for the case where the overlay is a
real, unmodified block model submitted via `submitMovingBlock` (individual vertices aren't available
to nudge there).

## Scaffolding

- **New mod**: `scripts/init-mod.sh` (interactive) runs `bun create mod` against the `.bun-create/mod/`
  template, replaces `{{MOD_ID}}`/`{{CLASS_NAME}}`/`{{MC_COMPAT}}`/etc. placeholders, and updates the
  project list + settings.gradle. Produces the standard `common`/`fabric`/`neoforge` layout.
- **New block family (minekea)**: follow the existing pattern — a `ModThingGroup` registration class
  (`minekea/common/.../block/**`) + a `ChimericLibBlockDataGenerator` subclass under
  `minekea/fabric/src/main/java/com/chimericdream/minekea/fabric/block/**` wired into the category
  aggregator, then run datagen. minekea has dozens of such `*DataGenerator` classes as references
  (e.g. `fabric/block/furniture/ArmoireBlockDataGenerator.java`).
- **Stairs/slabs/walls for an existing block**: use chimeric-lib's `lib.blocks.family.BlockFamily`
  rather than hand-registering each variant — one `BlockConfig` template drives all three.

## Versioning & releases

- **Before cutting any release (tagging a beta or final version), run `bun run datagen`** scoped
  to the mod being released (e.g. `bun run datagen --mods=<mod_id>`) and commit any resulting
  changes under `<mod>/*/src/main/generated`. This is a required safety check — released jars must
  ship data generated from the code at the tagged commit, not from whatever the generated files
  happened to contain from an earlier run.
- **Day-to-day commits between releases do not get their own dated changelog entry or release
  version number.** Nothing accumulating under `### Unreleased changes` is published anywhere until a
  release is explicitly cut. What *does* need to stay current between releases is `mod_version` itself
  — see the next two bullets — since it's what tells you, at any commit, what the next release would
  be if cut right now.
- **Official releases are tagged in git** (e.g. `chimericlib/26.1.2-5.0.0`, `minekea/26.1.2-9.0.0` —
  see `git tag --list`). A pre-release (`-beta.x`) tag counts as a real release just as much as a
  final one — the distinction that matters is tagged vs. untagged, not beta vs. final. A mod's
  current `mod_version` therefore reflects one of these states:
  - **At the tagged commit itself**: the exact released version, matching the tag.
  - **Continuing pre-release iterations of a target whose most recent tag was itself `-beta.x`**: the
    same `x.y.z`, suffixed `-beta.<x+1>` (e.g. after tagging `3.1.0-beta.1`, `mod_version` becomes
    `3.1.0-beta.2`). The target version doesn't change here — the prior tag already committed to
    `x.y.z` as the eventual release, this just continues iterating toward it.
  - **Otherwise, mid-cycle after a final tag (or after the target escalates — see below)**: the next
    anticipated pre-release version, chosen by what has actually accumulated under `### Unreleased
    changes` so far:
    - Only bug fixes so far → a patch bump: `x.y.(z+1)-beta.0`.
    - Any feature present → a minor bump: `x.(y+1).0-beta.0`.
  - **This target can escalate mid-cycle.** If `mod_version` is currently a patch-level pre-release
    (`x.y.(z+1)-beta.0`) from fixes only, and a feature then gets committed, re-target it up to the
    minor-level pre-release (`x.(y+1).0-beta.0`) instead — e.g. `5.0.0` → (first fix) `5.0.1-beta.0` →
    (later feature) `5.1.0-beta.0`. Once escalated to minor for a cycle, further fixes or features in
    that same cycle don't downgrade it back to patch.
- **When asked to "cut a release" with no other qualifier, that means a final release.** If
  `mod_version` is currently `a.b.c-beta.x`, bump it straight to `a.b.c` (update `mod_version`, retitle
  the changelog heading, tag `a.b.c`) — do not tag the beta first and promote it in a separate step.
  Only cut an actual beta tag (`-beta.x`) when the user explicitly asks for a beta/pre-release.
- **Check `mod_version` every time you touch `CHANGELOG.md`, not on a separate cadence.** Whenever a
  commit adds an entry under `### Unreleased changes`, also check whether `gradle.properties`'
  `mod_version` already reflects the correct next pre-release version per the rule above. If it does,
  no action is needed. If not, bump it in the same commit — using the patch/minor/escalation logic
  above, based on what's now in `### Unreleased changes` taken as a whole (not just the entry you're
  adding). Don't increment the pre-release number for any other reason.
- **Changelog structure follows the same split.** Each mod's `CHANGELOG.md` accumulates all untagged
  work under a single `### Unreleased changes` heading at the top (with the usual `#### New
  Features`/`#### Bug Fixes`/`#### Changes` subheadings) — not a new dated/versioned heading per
  commit or session. When a release is cut (beta or final), `### Unreleased changes` is renamed to a
  dated `### <mc_version> - <version>` heading (e.g. `### 26.1.2 - 3.2.0`, matching the git tag) and a
  fresh, empty `### Unreleased changes` starts collecting the next round.
- **Group related Unreleased entries instead of appending each as its own top-level bullet.** When a
  single work session adds several related items in the same category (e.g. a handful of new trim
  materials, or a batch of set-bonus effects tied to those materials), nest them under one descriptive
  bullet rather than writing N separate top-level bullets. Still keep the `#### New Features`/`####
  Bug Fixes`/`#### Changes` top-level structure — this is about grouping *within* those sections.
- **Changelog/README tone**: player-facing docs (changelogs, READMEs) must be concise and
  non-technical — the audience is Minecraft players, not programmers. Editing test: for each
  sentence, if removing it still conveys the information accurately, delete it. **Exception:
  chimeric-lib** — it's a shared library consumed by other mods, so its changelog/README audience
  is developers; stay concise but technical detail (API names, method signatures, behavior) is
  appropriate there.
- **Before committing, check whether the affected mod's `CHANGELOG.md` and/or `README.md` need
  updating.** A changelog entry belongs under that mod's `### Unreleased changes` heading whenever the
  commit changes player-visible behavior (new feature, bug fix, balance/behavior change) — a pure
  internal refactor with no behavior change does not need one. A README update belongs alongside any
  change to something the README documents. Skip either file when nothing it covers actually changed
  — don't add an entry just to have one.

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

## Git commits

- **This repo routinely has multiple unrelated efforts staged/in-progress at once** (different mods,
  scaffolding output, WIP from earlier in the session). Before running any `git commit`, always run
  `git status` (and `git diff --cached` if anything looks unfamiliar) right before committing and
  confirm the staged set matches only what was actually asked for — don't assume the index reflects
  just the current task. If unrelated files are already staged, commit by explicit pathspec (`git
  commit <path> <path> ...`) rather than a bare `git commit`, so the unrelated staged changes are left
  staged/untouched for their own commit later instead of being swept in.
- ⚠ **This repo's `diff.external` config points at a script that is not installed.** Every `git diff`
  / `git show` that prints a patch must pass `--no-ext-diff`, or it aborts with
  `cannot spawn … git-diff-wrapper.sh`.

## Planning & reference docs

- `docs/backport-26.1.2/` — the `main` → `26.1.2` backport. `ROUND-2.md` is the **current** plan;
  `README.md` is round 1 (complete) and still the best reference for how a mod's 26.1.2 source came
  to look the way it does. Round 2's §7 reverse API map supersedes round 1's §5 where they disagree.
- `docs/NEOFORGE.md` / `docs/FABRIC.md` — confirmed loader-specific runtime/build gotchas (mixins,
  renderer registration, etc.) — see "Platform-specific gotchas" above.
- `docs/TESTING.md` — how tests are wired and run (JUnit bootstrap, GameTest harness, testFixtures).
- `DEPENDENCY-PLAN.md` — how chimeric-lib is wired as an in-build project dependency (no publish loop)
  and the remaining monorepo build-structure improvements.
- `docs/BLOCK-MIGRATION.md` — non-breaking block/item deprecation & rename across both loaders (no DataFixerUpper).
- `docs/LOOT-CHEST-GENERATION.md`, `docs/SPAWNER-FLAME-PARTICLE-RESEARCH.md` — vanilla-mechanism
  research notes. Both were derived from the **26.2** jar; re-verify against 26.1.2 before relying on
  exact method bodies.
- `CODE-REVIEW-PLAN.md` (repo root) — phased code-review plan; the source of the `N.M` item numbers
  referenced throughout the backport commits.
- `UPDATE-PLAN.md` (repo root) — the Yarn→Mojang update runbook (migration complete).
- Per-mod `TEST_PLAN.md` and `POTENTIAL_FEATURES.md` — testing plans and feature backlogs.

## Long-running Gradle tasks can hang after finishing

**Any build/datagen/GameTest/`run*` task can finish its real work and then hang indefinitely instead
of exiting the JVM.** Check status every **~10 minutes**, not every 20-30+ — the vast majority of
these tasks finish in well under that, so a check still coming back "still running" past ~10 minutes
means it's hung, not slow. Poll the expected *output* (log tail, generated-file timestamps, whether
the artifact already exists), not just whether the process/shell command reports completion. To
unblock, find and kill the stuck `java.exe` for that task.

Separately, on exit you may see a `java.lang.Error: Watchdog (Client shutdown from post-main)` crash.
**Ignore it** — it's a non-daemon thread leak in a shared dependency, not your code and not a real
failure. The game did fully close.

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

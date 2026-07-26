# Backport plan: `main` (MC 26.2) → `26.1.2`

**Status:** ready to execute, pending the dependency version numbers in
[§6](#6-version-numbers-needed-before-wave-0-starts).

This directory holds the full plan for backporting ~2 weeks of work from `main` onto the `26.1.2`
branch. This file is the overarching plan and owns **every shared / cross-cutting change**. Each
active mod then has its own file, listed in [§9](#9-mod-file-index).

---

## 1. The commit topology (verified)

```
                                             ┌── 17 "update to 26.2" commits ──┐
e4817fa4 ──────────────────────────────────► c5f2cc4d ──── 112 commits ───► 750a327b (main)
   │                                          (26.2 port)      (payload)
   └── 26.1.2 (HEAD, identical tree)
```

| Anchor | SHA | Meaning |
|---|---|---|
| `BASE` | `e4817fa4bce0880c4983e6ca83c4758d5dfb97f2` | `26.1.2` HEAD **and** `git merge-base main 26.1.2` — verified identical |
| `PORT` | `c5f2cc4d` | last of the 17 "update to 26.2" commits (`feat(minekea): update to 26.2`) |
| `HEAD` | `750a327b` | current `main` |

Two facts make this backport tractable, and both were verified against the repo:

1. **`26.1.2` HEAD *is* the merge-base.** The branch has not diverged. Every commit on `main` is
   either a 26.2 version bump or backport payload — nothing else.
2. **`BASE..PORT` is exactly the 26.2 port** (109 files, all mechanical renames + version bumps).
   Therefore `PORT..HEAD` is *pure feature work, expressed in 26.2 terms*. That diff — not the
   commit list — is the unit of backport.

### The core technique

For any path set `P`:

```bash
git diff PORT..HEAD -- P    # = the feature work, and nothing else
```

Applied onto `26.1.2` it will apply cleanly **except** where the 26.2 port also touched the same
lines. Those collisions are the entire adaptation workload, and they are enumerated per mod.

**Conflict surface, measured:** of 4,226 files in the payload, only **31** were also touched by the
26.2 port. Of those 31, 16 are `gradle.properties` (handled centrally in Wave 0), leaving **15 source
files** with any genuine merge risk across the whole repo. They are listed in
[§8](#8-the-15-conflict-risk-source-files) and repeated in the owning mod's file.

---

## 2. What is explicitly NOT backported

| Excluded | Why |
|---|---|
| The 17 `BASE..PORT` version-bump commits | That *is* the 26.2 port. Backporting it would defeat the purpose. |
| `mod_version` values from `main` (`6.0.0-alpha.*`) | `26.1.2` is on the `5.x` line. Version policy is a decision for §6. |
| Root `gradle.properties` dependency versions from `main` | 26.2 coordinates. Replaced with 26.1.2 values from §6. |
| `docs/MC-26.2-NOTES.md` | Documents a port the `26.1.2` branch has not done. Keeping it there is actively misleading. **Skip.** |
| `.claude/skills/mc-visual-smoke-test/SKILL.md` *as written* | Its code samples use 26.2 render APIs. Backport it **only** after re-verifying each API against 26.1.2, or skip. Low value, non-blocking — deferred to a follow-up. |
| `bun.lockb` → `bun.lock` swap | Optional, orthogonal. Include only if the branch is expected to build with the same Bun version. Non-blocking. |

---

## 3. Wave structure

Waves are strictly ordered. Everything inside a wave is parallel-safe: **no two branches in the same
wave touch the same file.** This was verified against the payload's file list.

```
Wave 0  ── backport/26.1.2/shared-build        (SERIAL, blocking)
              │  root build files + ALL 15 mods' build.gradle & gradle.properties
              │  + CI + .gitattributes renormalization + docs/
              ▼
Wave 1  ── backport/26.1.2/chimeric-lib        (SERIAL, blocking)
              │  every other mod compiles against the new chimeric-lib API
              ▼
Wave 2  ── 14 parallel branches, one per mod:
           backport/26.1.2/archaeology-tweaks      backport/26.1.2/houdini-block
           backport/26.1.2/artificial-heart        backport/26.1.2/minekea
           backport/26.1.2/athenaeum               backport/26.1.2/miniblock-merchants
           backport/26.1.2/banner-tweaks           backport/26.1.2/shulker-stuff
           backport/26.1.2/beacon-conduit-tweaks   backport/26.1.2/sponj
           backport/26.1.2/enchantment-numbers-fix backport/26.1.2/villager-tweaks
           backport/26.1.2/flat-bedrock
           backport/26.1.2/hopper-xtreme
```

**Why Wave 0 owns every mod's build files.** `gradle/mod-conventions.gradle` collapses each mod's
~68-line `build.gradle` to a single `apply from:` line, and the `*_compat` floors move from 15
per-mod `gradle.properties` files to the root. Doing that inside each mod's Wave 2 branch would make
15 branches fight over the same shared script. Wave 0 does all of it in one commit; Wave 2 branches
then never touch `<mod>/build.gradle` or `<mod>/gradle.properties`.

**Why Wave 1 is serial.** Wave 2 mods consume new chimeric-lib API that does not exist on `26.1.2`
yet: `AbstractWrenchItem` (hopper-xtreme, minekea), `ContainerOpenersCounters` (minekea,
shulker-stuff), `LootModifierHelper` (shulker-stuff, miniblock-merchants), `BlockUtils` (sponj),
`InventoryScreenHandler` (all screen-handler consumers), and the `testFixtures` variant (every mod
with tests). They cannot compile until chimeric-lib lands.

Branch each wave off the previous wave's branch (not off `26.1.2`) so the dependencies are present;
merge waves into `26.1.2` in order.

---

## 4. Wave 0 — shared infrastructure (the "shared changes" deliverable)

Branch: `backport/26.1.2/shared-build`, off `26.1.2`.

This is the only wave that must be done by a single agent start-to-finish. Six sub-steps, each a
separate commit so the wave is bisectable.

### 0.1 — Enforce LF line endings *first*

Backport `.gitattributes` (commit `b9c55cb1`, 43 lines) verbatim, then renormalize:

```bash
git add --renormalize .
git commit -m "chore: enforce LF line endings via .gitattributes"
```

> **Correction (measured on the branch, after this section was first written).** An earlier draft
> claimed ~4,000 of the payload's files were CRLF→LF churn. That is wrong, and the mistake was using
> `git show <rev>:<path>` — which applies working-tree eol conversion — instead of
> `git cat-file blob`. The real census, via `git grep -l -I $'\r' <rev>`:
>
> | Ref | Files containing CR |
> |---|---|
> | `26.1.2` | **1** (`gradlew.bat`) |
> | `c5f2cc4d` | 12 |
> | `main` | 9 |
>
> So `26.1.2` is already LF, and `git add --renormalize .` here restages exactly one file. Only
> **three payload files** carry a CRLF mismatch against this branch, all in minekea and all already
> listed in §8 as conflict-risk: `Beams.java`, `CompressedBlocks.java`, `Covers.java`.
>
> What the 3,993-file `minekea/common/src/main/generated/**` churn actually is: **3,840 of those are
> `0+/1−` — a removed trailing newline** from datagen being regenerated, not a line-ending change.
> `minekea.md` already says this and already prescribes regenerating rather than porting, so the
> minekea plan is unaffected.

This still belongs first, and it is still worth doing — it makes LF structural rather than
conventional, and it normalizes `gradlew.bat` (blob LF, worktree CRLF, which is what a Windows batch
file wants). It is simply not load-bearing for diff noise the way the first draft claimed. **Do not
skip it**, but do not expect a large diff either: if `git add --renormalize .` restages more than
`gradlew.bat`, something is wrong with your checkout.

### 0.2 — Root `gradle.properties`

Two changes (`b1e8709a`, `6a7e791a`):

- `org.gradle.parallel = false` → `true`. Verified safe on `main` with a forced multi-mod,
  multi-loader `--rerun-tasks` build. Configuration cache stays off.
- Append the five centralized compat floors. Values come from §6:

```properties
# Mod-metadata compatibility floors, shared by every mod's fabric.mod.json / neoforge.mods.toml.
# These are inherited by every subproject; a mod only needs to set one in its own gradle.properties
# to pin an older floor. (minecraft_compat stays per-mod: the modpack script reads it from each mod's
# gradle.properties for the jar filename.)
architectury_compat = <see §6>
fabric_compat       = <see §6>
modmenu_compat      = <see §6>
yacl_compat         = <see §6>
chimericlib_compat  = <see §6>
```

Leave the existing 26.1.2 dependency-version block alone unless §6 says otherwise.

### 0.3 — Root `build.gradle` (`8999bc77`, `05bf336f`, `5eaceef0`, `e0e638e6`, `524e60a8`)

Apply `git diff PORT..HEAD -- build.gradle` (79+/26−). It is version-independent and should apply
with at most trivial context fixes. It does five things:

1. **chimeric-lib becomes an in-build `project()` dependency.** Consumers get
   `common(project(':chimeric-lib:common'))` + `common(project(':chimeric-lib:<platform>'))`, with a
   published-coordinate fallback guarded by `rootProject.findProject(':chimeric-lib') != null`. This
   kills the publish→consume loop. The `:common` **and** platform pair is required: platform
   subprojects reclassify `jar` as `raw` and put shaded output in `shadowJar`, so a project dep on
   the platform alone is missing common's classes.
2. **Drops `mavenLocal()`** from resolution repositories, so a stale `~/.m2` chimeric-lib can never
   shadow in-repo source. (Publishing to maven-local still works — different repository list.)
3. **Drops the `maven.pkg.github.com` repo and `GITHUB_TOKEN`**, so fork PRs build without secrets.
4. **Points the Terraformers maven at the host root** (`https://maven.terraformersmc.com/`, no
   `/releases/`). Terraformers migrated off Reposilite on 2026-07-24; the old path 404s and modmenu
   is only obtainable there. ⚠ **Do this even though it looks unrelated** — without it a clean CI
   checkout cannot resolve modmenu at all.
5. **Adds the fabric `test` wiring:** `fabric-loader-junit`, `test { useJUnitPlatform() }`, and
   `testImplementation(testFixtures(project(":chimeric-lib:common")))`.

`26.1.2` already has the `fabricApi { configureTests { ... } }` block and already builds at
`options.release = 25` — verified. No change needed there.

### 0.4 — `settings.gradle` + `scripts/settings.gradle.tpl` (`8999bc77`)

Hoist `chimeric-lib` to the front of `projectList`. Loom eagerly resolves each mod's classpath in
that mod's `afterEvaluate`; a consumer resolving chimeric-lib before it finishes configuring fails
with *"project components has not been calculated yet"*. The hoist plus
`evaluationDependsOnChildren()` in `chimeric-lib/build.gradle` (Wave 1) are **both** required — one
without the other does not work.

Apply the same edit to the template so `bun run update:settingsgradle` does not revert it.
`26.1.2`'s `projectList` already has the inactive mods commented out; only `project-list.json` needs
the corresponding trim.

### 0.5 — `gradle/mod-conventions.gradle` + all 15 mods' build files (`a73f6b92`, `b98aa703`, `b1e8709a`)

**Wave 0 owns every `*.gradle` and every `gradle.properties` in the repo.** No later wave edits a
build file. Add `gradle/mod-conventions.gradle` (new, 81 lines, copy from `main` verbatim —
version-independent), then for **every** mod including chimeric-lib:

- `<mod>/build.gradle` → the single line `apply from: "${rootDir}/gradle/mod-conventions.gradle"`.
- `<mod>/gradle.properties` → delete `architectury_compat`, `chimericlib_compat`, `fabric_compat`,
  `modmenu_compat`, `yacl_compat`. **Keep `minecraft_compat`** — `scripts/create-modpacks.ts` reads
  it per-mod for the jar filename.

chimeric-lib additionally keeps, in its own `build.gradle`, the `evaluationDependsOnChildren()` call
(paired with §0.4's hoist) and the GitHub Packages publishing block. Its two subproject build files
land here too, even though the code they serve arrives in Wave 1:

- `chimeric-lib/common/build.gradle` → `apply plugin: 'java-test-fixtures'`, share
  `sourceSets.main.compileClasspath` into `testFixtures` (a custom `testFixtures` source set does not
  inherit Loom's Minecraft classpath), and add `testFixturesImplementation` on `fabric-loader-junit`.
- `chimeric-lib/fabric/build.gradle` → `gametestImplementation(testFixtures(project(":chimeric-lib:common")))`.

An empty `testFixtures` source set between Wave 0 and Wave 1 is harmless.

The conventions script also changes the coordinate scheme:

```
before:  archivesName = <name>-<platform>-<mc>   version = 5.0.0-alpha.0
after:   archivesName = <name>-<platform>        version = <mc>-5.0.0-alpha.0
```

The **produced jar filename is byte-identical** (`sponj-fabric-26.1.2-5.0.0-alpha.0.jar` either
way), so `create-modpacks.ts` needs no change. Verify this by name after the first build.

Note the new `version` uses `rootProject.minecraft_compatibility` (root property), not the per-mod
`minecraft_compat`. On `26.1.2` that root property is already `26.1.2`.

Also drop the four inactive mods' `build.gradle` stubs to the same one-liner (`blacklight`,
`cobblicious`, `hang-from-slabs`, `jdcrafte`, `pannotia-companion`, `playgrounds` — 4 lines each on
`main`). They are not in `settings.gradle` but keeping them consistent costs nothing.

### 0.6 — CI, docs, and repo config

| Path | Action |
|---|---|
| `.github/workflows/build.yml` | JDK matrix `21` → `25` (the build has `options.release = 25`; CI literally could not compile), `ubuntu-20.04` → `ubuntu-latest`, drop `GITHUB_TOKEN` env, update the artifact-upload `if:` guard to `'25'`. |
| `.gitignore` | add `.fabric` and `logs/`. |
| `package.json` | add the `publish:lib` script. |
| `project-list.json` | trim the inactive mods (`8f550ec9`). |
| `docs/TESTING.md` | new — copy from `main`. Describes the JUnit bootstrap, GameTest harness, testFixtures. ⚠ **Edit before committing:** strip the MC-26.2 "components not bound" section; on 26.1.2 `BuiltInRegistries.DATA_COMPONENT_INITIALIZERS` does not exist (verified: zero hits on the `26.1.2` tree). |
| `docs/BLOCK-MIGRATION.md` | new — copy from `main` verbatim. Version-independent. |
| `CODE-REVIEW-PLAN.md`, `DEPENDENCY-PLAN.md`, `MOD_IDEAS.md` | new — copy from `main` verbatim. These are the rationale documents every per-mod file references; without them the mod plans lose their "why". |
| `CLAUDE.md` | ⚠ **Do not copy from `main`.** Port the *structure* but keep 26.1.2 facts: MC `26.1.2`, the 26.1.2 dependency versions, no `docs/MC-26.2-NOTES.md` entry, no datagen-component-binding section. Everything else (chimeric-lib project() wiring, test commands, container-inventory conventions, scaffolding) applies as-is. |
| `.claude/settings.json` | copy from `main`. Permissions allowlist only, no version content. |
| `docs/MC-26.2-NOTES.md`, `.claude/skills/mc-visual-smoke-test/` | **skip** — see §2. |

### Wave 0 acceptance

```bash
./gradlew clean build          # all 15 mods, both loaders
bun run build                  # full lifecycle incl. modpacks
ls build/modpacks/fabric/      # jar filenames unchanged from before the wave
```

Plus: `git diff --stat 26.1.2..HEAD -- '*.java'` should be **empty**. Wave 0 changes no Java.

---

## 5. The 26.2 → 26.1.2 reverse API map

Sub-agents apply this when a patch hunk rejects or the build fails. Derived from
`docs/MC-26.2-NOTES.md` and verified against the `26.1.2` tree.

### Confirmed present on 26.1.2 (do **not** "fix" these)

| API | Note |
|---|---|
| `net.minecraft.resources.Identifier` | Same package on both. Not `ResourceLocation`. |
| `net.minecraft.client.renderer.SubmitNodeCollector` | ✅ The render-feature overhaul landed in **26.1**, not 26.2. `BlockEntityRenderer.extractRenderState`, `ModelFeatureRenderer.CrumblingOverlay`, `ItemStackRenderState`, `CameraRenderState` all exist on 26.1.2. This removes most of the perceived risk from minekea's armoire renderer. |
| `options.release = 25` / Java 25 | Already the 26.1.2 baseline. |
| `fabricApi { configureTests { ... } }` | Already in 26.1.2's root `build.gradle`. |

### Must be reversed (26.2-only on the left)

| 26.2 (on `main`) | 26.1.2 (write this) |
|---|---|
| `BlockEntityTypes.X` | `BlockEntityType.X` |
| `EntityTypes.X` (`net.minecraft.world.entity.EntityTypes`) | `EntityType.X` (`net.minecraft.world.entity.EntityType`) |
| `net.minecraft.advancements.triggers.CriteriaTriggers` | `net.minecraft.advancements.CriteriaTriggers` |
| `net.minecraft.advancements.predicates.*` | `net.minecraft.advancements.criterion.*` |
| `net.minecraft.util.LightCoordsUtil.getLightCoords(...)` | `LevelRenderer.getLightCoords(...)` |
| `Minecraft.getInstance().gui.hud.isHidden()` | `!Minecraft.getInstance().renderNames()` |
| `view.getStringOr("k", d)` / `getBooleanOr` | `ValueInput.contains("k")` + the 26.1.2 getters |
| `Blocks.WOOL.white()`, `Items.DYE.red()`, … (`ColorCollection`) | `Blocks.WHITE_WOOL`, `Items.RED_DYE`, … |
| `Blocks.CUT_COPPER.weathering().exposed()` | `Blocks.EXPOSED_CUT_COPPER` |
| `Blocks.COPPER_BLOCK.weathering().unaffected()` | `Blocks.COPPER_BLOCK` |
| `BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(p).forEach(...)` | **Delete.** Components are not lazily bound on 26.1.2. |
| `TagAppender<T>` | `TagAppender<T, T>` |
| `TagAppender.add(X.builtInRegistryHolder().key())` | `TagAppender.add(X)` |
| `FabricTagProvider.builder(TagKey<T>)` | `FabricTagProvider.valueLookupBuilder(TagKey<T>)` |
| `Pair.of(a,b)` / `.getFirst()` / `.getSecond()` (where the port replaced `Tuple`) | `net.minecraft.util.Tuple` still exists on 26.1.2 — but see note below |
| `supplier.getId().toString()` | `supplier.getRegisteredName()` (Architectury 20.x) |
| `new ItemStack(supplier.get())` | `new ItemStack(supplier)` resolves on Architectury 20.x — either form is fine, prefer leaving the payload's `.get()` |
| `Optional.ofNullable(Identifier.tryParse(id)).flatMap(BuiltInRegistries.ENTITY_TYPE::getOptional)` | `EntityType.byString(id)` |
| `blockPos.distToCenterSqr(vec3)` | `blockPos.getCenter().distanceToSqr(vec3)` |
| `new EntitySpawnRequest(EntitySpawnReason.X, false)` | pass `EntitySpawnReason.X` directly |
| `MenuScreens.register` / `TextureSlot.create` via access widener | public on 26.1.2 — the AW entries are harmless but unnecessary |

> **`Tuple` note.** The 26.2 port swapped `net.minecraft.util.Tuple` for `com.mojang.datafixers.util.Pair`
> because `Tuple` was removed. Separately, payload commit `3defb97e` replaced minekea's *`oshi.util.tuples`*
> (`Pair`/`Triplet`/`Quartet` from the hardware-info library) with named domain records. Those are
> **different changes**. The records refactor is version-independent — backport it as-is. Confirmed:
> `oshi.util.tuples` is still imported on `26.1.2` in `CompressedBlocks`, `DyedBlocks`, and
> `ArmoireBlockEntity`, so the refactor is just as applicable there.

### How to verify an API against 26.1.2 rather than guessing

The Loom 26.1.2 sources jar may be a stub. Use the binary jar:

```bash
JAR=~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-deobf/26.1.2/minecraft-merged-deobf-26.1.2.jar
unzip -o "$JAR" 'net/minecraft/<path>/<Class>.class' -d /tmp/mc
javap -p /tmp/mc/net/minecraft/<path>/<Class>.class
```

Cheaper first check: `git grep '<Symbol>' 26.1.2 -- '*.java'`. If the repo already used it on that
branch, it exists.

---

## 6. Version numbers needed before Wave 0 starts

These are the only blocking unknowns. Below is what `26.1.2` currently declares — confirm or
override each.

### Root `gradle.properties` — existing keys

| Key | Current on `26.1.2` | Use? |
|---|---|---|
| `minecraft_version` / `minecraft_compatibility` | `26.1.2` | keep |
| `loom_version` | `1.17-SNAPSHOT` | ? |
| `fabric_loader_version` | `0.19.3` | ? |
| `fabric_api_version` | `0.154.2+26.1.2` | ? |
| `architectury_api_version` | `20.0.7` | ? |
| `neoforge_version` | `26.1.2.71` | ? |
| `yacl_version` | `3.9.4+26.1` | ? |
| `mod_menu_version` | `18.0.0` | ? |
| `kff_version` | `6.3.0` | ? |
| `chimericlib_version` | `26.1.2-5.0.0-alpha.2` | see note |

> `chimericlib_version` is now only the **fallback** coordinate used when chimeric-lib is absent from
> the build. It should still be set correctly, but it stops mattering for normal development.

### Root `gradle.properties` — new centralized floors (§0.2)

| Key | `26.1.2` per-mod value today | Confirm |
|---|---|---|
| `architectury_compat` | `20.0.7` | ? |
| `fabric_compat` | `0.19` | ? |
| `modmenu_compat` | `18.0.0` | ? |
| `yacl_compat` | `3.9.4` | ? |
| `chimericlib_compat` | `5.0.0-alpha.0` | ? |

### Per-mod `mod_version` policy

`main` bumped every mod to `6.0.0-alpha.0` for the 26.2 line. `26.1.2` is on `5.x`
(chimeric-lib `5.0.0-alpha.2`, most mods `5.0.0-alpha.0`). **Decide:** leave the `5.x` numbers
untouched, or bump the patch/alpha to mark the backported content. Default assumption if unanswered:
**leave them untouched**, since the backport does not change any mod's public surface in a way that
requires a version signal.

---

## 7. Procedure for a Wave 2 sub-agent

Every mod file in this directory assumes this procedure. It is written for a Sonnet 5 agent working
alone in a worktree.

```bash
MOD=<mod-name>
PORT=c5f2cc4d

# 1. Branch off the previous wave (chimeric-lib), NOT off 26.1.2.
git checkout -b backport/26.1.2/$MOD backport/26.1.2/chimeric-lib

# 2. Extract this mod's payload. This diff is feature work only — the 26.2
#    port is already excluded because PORT is its last commit.
git diff --binary $PORT..main -- $MOD/ > /tmp/$MOD.patch

# 3. Apply with 3-way so git can use blob context to auto-resolve.
git apply --3way /tmp/$MOD.patch
#    On failure, fall back to:  git apply --reject /tmp/$MOD.patch
#    and hand-merge the .rej hunks.

# 4. Resolve conflicts using §5's reverse map. Your mod's file lists exactly
#    which files can conflict and why — there are few, and they are known.

# 5. Compile both loaders.
./gradlew :$MOD:common:build :$MOD:fabric:build :$MOD:neoforge:build

# 6. Run whatever tests the mod has (see the mod's file).
./gradlew :$MOD:fabric:runGameTest      # if the mod has gametests
./gradlew :$MOD:fabric:test             # if the mod has JUnit tests
```

### Rules for every sub-agent

1. **Never touch `<mod>/build.gradle` or `<mod>/gradle.properties`.** Wave 0 owns them. If the patch
   tries to, drop that hunk.
2. **Never touch another mod's directory or a root file.** If you believe a shared file needs a
   change, stop and report it rather than editing — it means Wave 0 missed something.
3. **Do not "fix" 26.2 APIs you find in the existing `26.1.2` code.** If a symbol is already used on
   the branch, it exists on 26.1.2. Check with `git grep '<Symbol>' 26.1.2` before changing anything.
4. **Preserve the payload's intent, not its literal text.** Where an adaptation is needed, keep the
   *behavior* the commit message describes and re-express it in 26.1.2 API terms.
5. **Keep the explanatory comments and javadoc.** A large share of this payload is documentation of
   *why* (the `@Overwrite` justifications, the container-inventory conventions, the build comments).
   That is the durable value; do not strip it as "just comments".
6. **LF endings.** `.gitattributes` from Wave 0 handles this. Do not add a `.gitattributes`.
7. **Report honestly.** If a piece cannot be backported (missing 26.1.2 API, a test that cannot run),
   finish everything else and say explicitly what was left out and why. Do not silently narrow scope.

---

## 8. The 15 conflict-risk source files

Every other payload file applies onto `26.1.2` without touching 26.2-port territory. These 15 are the
complete set that can conflict, with what the port did to each:

| File | Owner | 26.2 port change → what to reverse |
|---|---|---|
| `archaeology-tweaks/.../ATBrushableBlockEntity.java` | archaeology-tweaks | `CriteriaTriggers` package, `EntityType.ITEM`→`EntityTypes.ITEM`. Payload hunk is a 1-line deletion far from both. |
| `chimeric-lib/.../colors/ColorHelpers.java` | chimeric-lib | Colored `Blocks.*`/`Items.*` → `ColorCollection` accessors. Payload adds `getTints()` + privatizes the palette; **write the new switch bodies in 26.1.2 flat-constant form.** |
| `chimeric-lib/.../inventories/ImplementedInventory.java` | chimeric-lib | Port added `@NonNull` (jspecify) to `getItem`/`removeItem`/`removeItemNoUpdate`. Check whether jspecify is on the 26.1.2 classpath; if not, drop the annotations. |
| `chimeric-lib/.../screen/SimpleInventoryScreenHandler.java` | chimeric-lib | Both are *deleted* by the payload (folded into `InventoryScreenHandler`), so the port's edits are moot. |
| `chimeric-lib/.../screen/DoubleWideInventoryScreenHandler.java` | chimeric-lib | ditto |
| `minekea/.../block/building/beams/Beams.java` | minekea | Copper `weathering()` collection. Real payload change is **1 line** (a texture id); the other 177 are CRLF. |
| `minekea/.../block/building/covers/Covers.java` | minekea | ditto — **1 real line**. |
| `minekea/.../block/building/compressed/CompressedBlocks.java` | minekea | Copper `weathering()`. Payload is the oshi-tuples→records refactor. Real work: apply the records refactor over the 26.1.2 flat copper constants. |
| `minekea/.../block/containers/GlassJarBlock.java` | minekea | port renames only |
| `minekea/.../entity/block/containers/GlassJarBlockEntity.java` | minekea | `getStringOr`/`getBooleanOr` ← `ValueInput.contains`. **Highest-value payload file in minekea** (244+/113−). |
| `minekea/.../client/render/block/GlassJarBlockEntityRenderer.java` | minekea | port renames only |
| `minekea/common/src/main/resources/minekea.accesswidener` | minekea | port added `MenuScreens.register` / `TextureSlot.create` entries that 26.1.2 does not need |
| `minekea/fabric/.../data/ModDataGenerator.java` | minekea | `TagAppender` arity, `builder`←`valueLookupBuilder`, and the `DATA_COMPONENT_INITIALIZERS` bind (**delete on 26.1.2**) |
| `sponj/.../blocks/SponjBlock.java` | sponj | port renames. Payload rewrites the file onto `AbstractSponjBlock` anyway. |
| `sponj/.../blocks/LavaSponjBlock.java` | sponj | ditto |

---

## 9. Mod file index

Ordered by wave, then by risk. "Payload" counts files in `git diff c5f2cc4d..main -- <mod>/`.

| Wave | Mod | File | Payload | Risk | Notes |
|---|---|---|---|---|---|
| 1 | chimeric-lib | [chimeric-lib.md](chimeric-lib.md) | 41 | **High** | Blocks everything. New public API + testFixtures + GameTests. |
| 2 | minekea | [minekea.md](minekea.md) | 4,035 | **High** | Only 24 real Java files; 3,993 are regenerable datagen output. |
| 2 | hopper-xtreme | [hopper-xtreme.md](hopper-xtreme.md) | 29 | **High** | The 3,700→450-line BE base-class extraction. Zero conflict files, but the biggest single refactor. |
| 2 | sponj | [sponj.md](sponj.md) | 15 | Medium | Block hierarchy merge + `BlockUtils` moves to chimeric-lib. 2 conflict files. |
| 2 | shulker-stuff | [shulker-stuff.md](shulker-stuff.md) | 10 | Medium | Dye-station fixes + 2 chimeric-lib adoptions + new GameTests. |
| 2 | houdini-block | [houdini-block.md](houdini-block.md) | 10 | Medium | Two real bugfixes + loot-table deletion + new GameTest. |
| 2 | villager-tweaks | [villager-tweaks.md](villager-tweaks.md) | 8 | Low | One mixin fix + new GameTest. |
| 2 | enchantment-numbers-fix | [enchantment-numbers-fix.md](enchantment-numbers-fix.md) | 7 | Low | `@Overwrite`→`@Redirect`. Verify the redirect target descriptor on 26.1.2. |
| 2 | banner-tweaks | [banner-tweaks.md](banner-tweaks.md) | 6 | Trivial | Javadoc only. |
| 2 | archaeology-tweaks | [archaeology-tweaks.md](archaeology-tweaks.md) | 6 | Trivial | One dead local + docs. |
| 2 | miniblock-merchants | [miniblock-merchants.md](miniblock-merchants.md) | 6 | Trivial | One-line `LootModifierHelper` adoption + docs. |
| 2 | artificial-heart | [artificial-heart.md](artificial-heart.md) | 5 | Trivial | Docs only. |
| 2 | athenaeum | [athenaeum.md](athenaeum.md) | 5 | Trivial | Docs only. |
| 2 | beacon-conduit-tweaks | [beacon-conduit-tweaks.md](beacon-conduit-tweaks.md) | 5 | Trivial | Docs only. |
| 2 | flat-bedrock | [flat-bedrock.md](flat-bedrock.md) | 5 | Trivial | Docs only. |

The five docs-only / trivial mods (`artificial-heart`, `athenaeum`, `beacon-conduit-tweaks`,
`flat-bedrock`, plus `banner-tweaks` and `archaeology-tweaks`) can reasonably be handled by a single
agent in one branch if you would rather not spawn six for ~15 minutes of work. Their files note this.

---

## 10. Overall acceptance criteria

The backport is done when, on the merged `26.1.2` branch:

1. `./gradlew clean build` is green for all 15 mods across both loaders.
2. `bun run build` completes and produces modpacks with unchanged jar filenames.
3. `./gradlew :chimeric-lib:fabric:test` — the ~30 unit tests pass.
4. `./gradlew :chimeric-lib:fabric:runGameTest` — 5 GameTests pass.
5. `./gradlew :hopper-xtreme:fabric:runGameTest` — 20 GameTests pass.
6. `:minekea:fabric:runGameTest`, `:sponj:…`, `:shulker-stuff:…`, `:houdini-block:…`,
   `:villager-tweaks:…` all pass.
7. CI (`.github/workflows/build.yml`) is green on JDK 25 **without** a `GITHUB_TOKEN`.
8. `git grep -n 'EntityTypes\.\|BlockEntityTypes\.\|weathering()\|DATA_COMPONENT_INITIALIZERS' -- '*.java'`
   returns nothing — no 26.2-only API leaked in.
9. `git ls-files --eol | grep -v 'w/lf'` returns nothing for text files — LF everywhere.

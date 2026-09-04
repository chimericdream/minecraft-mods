# Backport round 2: `main` (MC 26.2) → `26.1.2`

## Context

The `26.1.2` branch tracks Minecraft 26.1.2; `main` tracks 26.2. Round 1 of the backport
(`docs/backport-26.1.2/`) brought `26.1.2` level with `main` as of the `hopper-xtreme/26.2-4.0.1`
tag (commit `23d614746`, 2026-07-27). Since then `main` has accumulated **235 commits** (2026-08-02
→ 2026-09-03): ten brand-new mods, two revived mods, substantial feature work on nine existing mods,
a large chimeric-lib API expansion, and a repo-wide tooling/convention overhaul.

None of that exists on `26.1.2`. This plan brings it across, so the two branches carry the same
content and the 26.1.2 line stays a real, shippable target rather than a frozen snapshot.

The work is sized for a fleet of Sonnet sub-agents working in parallel, one mod each.

---

## 1. Topology and the core technique

```
                        ┌── round 1 backport (29 commits) ──┐
e4817fa4 ──────────────────────────────────────────────────► 26.1.2 (HEAD, moves)
   │  (merge-base)
   └──► c5f2cc4d ──► 23d614746 ──────── 235 commits ────────► 34066bb59  (main, HEAD)
         (26.2 port)   (PORT2)              (payload)
```

| Anchor | SHA | Meaning |
|---|---|---|
| `PORT2` | `23d614746` | tag `hopper-xtreme/26.2-4.0.1` — the last `main` commit whose content is already on `26.1.2` |
| `HEAD` | `34066bb59` | current `origin/main` |
| `BASE` | `26.1.2` | current `26.1.2` branch head — always branch off the **ref**, not a SHA; this doc's own commit has since moved it past `fb2df9a94` |

**The unit of backport is the diff, not the commit list:**

```bash
git diff --no-ext-diff --binary 23d614746..origin/main -- <paths>
```

This is *pure feature work expressed in 26.2 terms* — the 26.2 port is already behind `PORT2`.

> ⚠ **This repo has a broken `diff.external` config** (`git-diff-wrapper.sh` is missing). Every
> `git diff` / `git show` that prints a patch **must** pass `--no-ext-diff`, or it dies with
> `cannot spawn … git-diff-wrapper.sh`. This bites every sub-agent; put it in their prompt.

### Why `26.1.2` differs from `PORT2` (the conflict surface)

`26.1.2`'s tree = `main@PORT2`'s tree, minus three things:

1. **26.2 → 26.1.2 API reversals** applied in round 1 (see §5). This is the big one: **2,702 files**
   differ between `26.1.2` and `PORT2`, and round 1 is 29 commits, not a thin patch.
2. **Version numbers** — every mod is one major behind, MC compat is `26.1.2`.
3. **A handful of 26.1.2-only commits** on top of the round-1 work: `2178c727d` (hopper-xtreme
   filter-GUI dupe fix — *its counterpart `0e8b9f1d6` is already on `main` behind `PORT2`, so there
   is no risk of undoing it*), `dd0fc7cc8` / `c29ef1a43` (version bumps + release changelog
   headings), `fb2df9a94` (bun.lock), `50da5d636` (this document).

### The real conflict surface — measure it, don't assume it

A payload file conflicts where **both** the payload changed it **and** `26.1.2` diverged from
`PORT2` *in overlapping hunks*. Measured with a full three-way merge:

```bash
git merge-tree --write-tree --merge-base=23d614746 26.1.2 origin/main
```

**82 files genuinely conflict.** Breakdown:

| Kind | Count | Who resolves it |
|---|---|---|
| `*/CHANGELOG.md` | 15 | Wave 2 (each mod's agent owns its changelog — trivial, keep 26.1.2's dated headings, add payload content under `### Unreleased changes`) |
| `*/README.md` | 14 | Wave 0 sweep `a3c4ba15d` (Minecraft-versions section) |
| `*/gradle.properties` | 11 | Wave 2 (version numbers — just set the §3 target) |
| `minekea/common/**/*.java` | **11** | **Wave 2-A1 — see §9** |
| `minekea/fabric/**/*.java` | **26** | **Wave 2-A2 — see §9** |
| `chimeric-lib/…/family/WallBlockDataGenerator.java` | 1 | Wave 1 — a *rename*, see below |
| `hopper-xtreme/…/XtremeHopperRecipeGenerator.java` | 1 | Wave 2-B |
| `CLAUDE.md`, `docs/MC-26.2-NOTES.md`, `.claude/skills/mc-visual-smoke-test/SKILL.md` | 3 | Wave 0 (§0.3, §0.4 — all three are already handled there) |

Two files this plan previously called conflicts are **not**:
`archaeology-tweaks/.../ATBrushableBlockEntity.java` and
`beacon-conduit-tweaks/.../BCTweaksBeaconMixin.java` diverge from `PORT2` but their hunks don't
overlap the payload's, so they merge clean. They still need the `CriteriaTriggers` / `EntityTypes` /
`BlockEntityTypes` reversals applied to the **newly added** lines — that is API adaptation, not
merge resolution. Wave 2-J and 2-M still own them.

⚠ **One conflict is a cross-mod file move.** `main` moved
`minekea/fabric/.../block/building/WallBlockDataGenerator.java` into chimeric-lib as
`chimeric-lib/fabric/.../blocks/family/WallBlockDataGenerator.java`; git rename-detects it, so the
two waves collide on what git considers one file. **Wave 1 adds the chimeric-lib copy; Wave 2-A2
deletes minekea's.** Neither agent will infer this on its own — it is called out again in §8 and §9.

---

## 2. Decisions already made

| Question | Decision |
|---|---|
| **New-mod versions** | **Flat `0.9.0`** for every new mod, regardless of its 26.2 version, and regardless of `-beta` on `main`. The 26.1.2 line for a new mod begins at `0.9.0`. |
| **Existing-mod versions** | `main` major **− 1**, minor/patch/pre-release kept. `archaeology-tweaks 4.2.0 → 3.2.0`, `chimeric-lib 6.5.0-beta.0 → 5.5.0-beta.0`, `minekea 10.1.0 → 9.1.0`, etc. Full table in §3. |
| **Scope** | **All 12** new/revived mods, including `toy-box` (bare scaffold) and the three currently-beta mods. |
| **Dependency pins** | **Unchanged.** `26.1.2`'s root `gradle.properties` dependency block is authoritative; `main`'s 26.2 coordinates are never copied. |
| **End state** | **Unreleased.** Every changed mod gets its content under `### Unreleased changes` and its `mod_version` set to the target number. **No tags are cut** by this backport. |
| **`docs/MC-26.2-NOTES.md`** | **Skip** — it documents a port `26.1.2` has not done. |

---

## 3. Target versions

| Mod | `main` | `26.1.2` target | Mod | `main` | `26.1.2` target |
|---|---|---|---|---|---|
| chimeric-lib | 6.5.0-beta.0 | **5.5.0-beta.0** | better-portal-linking | 1.0.0 | **0.9.0** |
| archaeology-tweaks | 4.2.0 | **3.2.0** | better-target-dummies | 1.0.0 | **0.9.0** |
| artificial-heart | 3.1.0 | **2.1.0** | but-what-about | 1.0.0-beta.0 | **0.9.0** |
| athenaeum | 4.0.0 | **3.0.0** (no change) | camel-nostrils | 1.1.0 | **0.9.0** |
| banner-tweaks | 5.1.0 | **4.1.0** | effective-gear | 1.2.0 | **0.9.0** |
| beacon-conduit-tweaks | 4.1.0 | **3.1.0** | hang-from-slabs | 1.0.0 | **0.9.0** |
| enchantment-numbers-fix | 4.0.0 | **3.0.0** (no change) | jdcrafte | 1.0.0-beta.0 | **0.9.0** |
| flat-bedrock | 4.1.0 | **3.1.0** | log-all-the-things | 1.0.0 | **0.9.0** |
| hopper-xtreme | 4.1.0 | **3.1.0** | next-update-now | 1.0.1 | **0.9.0** |
| houdini-block | 3.0.0 | **2.0.0** (no change) | sneaky-tweaks | 1.1.0-beta.0 | **0.9.0** |
| minekea | 10.1.0 | **9.1.0** | stack-it-up | 1.0.0 | **0.9.0** |
| miniblock-merchants | 7.0.0 | **6.0.0** (no change) | toy-box | 1.0.0 | **0.9.0** |
| shulker-stuff | 4.0.2 | **3.0.2** | | | |
| sponj | 6.1.0 | **5.1.0** | | | |
| villager-tweaks | 6.2.0 | **5.2.0** | | | |

Every new mod also needs `minecraft_compat = 26.1.2` (not `26.2`) in its `gradle.properties`, and
`effective-gear`'s pinned `chimericlib_compat = 6.4.0` becomes **`5.4.0`**.

---

## 4. Wave structure

Waves are strictly ordered. Everything inside a wave is parallel-safe: **no two branches in the
same wave touch the same file.**

```
Wave 0  ── backport/26.1.2/r2-shared            (SERIAL, blocking, one agent)
              │  root build files, scripts/, tools/, .bun-create/, .claude/, docs/, CLAUDE.md
              │  + the 7 repo-wide sweeps across every mod
              │  + verbatim import of the 12 new mod directories (26.2 source, not yet compiling)
              ├──► review gate: layers 1+2, layer 3 on hand-written src  §10
              ▼
Wave 1  ── backport/26.1.2/r2-chimeric-lib      (SERIAL, blocking, one agent)
              │  ~3,100 lines of new public API that 6 other mods compile against
              ├──► review gate: layers 1+2+3 (highest-risk branch)       §10
              ▼
Wave 2  ── 15 assignments / 14 parallel lanes (see §9 for the grouping)
              │  minekea is split: A1 (`common/`, registration) ──► A2 (`fabric/` + datagen regen)
              │  branch off r2-chimeric-lib, one per mod (or per small group)
              ├──► review gate per assignment, layers tiered by risk     §10
              ▼
Wave 3  ── backport/26.1.2/r2-integration       (SERIAL, one agent)
              │  full build, modpacks, all test suites, datagen re-run, final doc pass
              └──► review gate: layer 1 repo-wide + layer 2 vs §12       §10
```

**Merge model.** Each wave branches off the previous wave's branch and merges into `26.1.2` in
order. Wave 2 branches all fork from `backport/26.1.2/r2-chimeric-lib` (except A2, which forks
from A1). **No branch merges until its §10 reviewer has reported and its author has answered
every finding** — the review gate is part of the wave ordering, not an optional extra pass.

**Expect a red full build between Wave 0 and Wave 3.** Wave 0 puts the twelve new mods' 26.2 source
into the tree and activates them in `settings.gradle`, so `./gradlew build` and a bare `bun run build`
fail until every Wave 2 branch has landed. This is deliberate — it means Wave 2 agents touch *only*
their own mod directory and never fight over `settings.gradle`. Use the scoping flags meanwhile:

```bash
./gradlew :<mod>:common:build :<mod>:fabric:build :<mod>:neoforge:build   # per-mod, always works
bun run build --mods=<mod-id>                                            # per-mod full lifecycle
bun run build --exclude=<not-yet-ported,ids>                              # everything else
./gradlew projects                                                        # config-only smoke test
```

---

## 5. Wave 0 — shared infrastructure (one agent, serial)

Branch `backport/26.1.2/r2-shared` off `26.1.2`. Six commits, each independently bisectable.

### 0.1 — Root build files

| Path | Change |
|---|---|
| `build.gradle` | Apply `git diff --no-ext-diff 23d614746..origin/main -- build.gradle` verbatim (+13/−2). Two version-independent fixes: chimeric-lib's platform dep moves `common(...)` → `implementation(...)`; and non-chimeric-lib fabric subprojects gain `testImplementation(project(":<mod>:common"))`, because `testCompileClasspath` never extends `compileClasspath`, so a mod's own `:common` classes were invisible to its `fabric/src/test`. **Keep the explanatory comment.** |
| `gradle/mod-conventions.gradle` | **No change** — verified byte-identical between `PORT2` and `main`. |
| root `gradle.properties` | **No change** — dependency pins stay (see §2). |
| `.gitignore` | Add `.sources/` (decompiled-source cache). |
| `package.json` | Take `main`'s `scripts` block verbatim: `build`/`build:gradle`/`clean` delegate to the new `scripts/*.ts`; add `datagen` and `status`; drop the `prebuild`/`postbuild` npm lifecycle hooks (folded into `scripts/build.ts`). |
| `.github/workflows/` | **No change** — verified identical between `26.1.2` and `main`. |

### 0.2 — `scripts/` and `tools/`

All version-independent; copy from `main` verbatim.

- **New**: `scripts/build.ts`, `scripts/build-gradle.ts`, `scripts/clean.ts`, `scripts/run-datagen.ts`,
  `scripts/mod-status.ts`, `scripts/util/gradle.ts`, `scripts/util/shared.ts`.
- **Changed**: `scripts/copy-accesswideners.ts`, `scripts/revert-fabricmodjson.ts`,
  `scripts/update-patchouli-books.ts`, `scripts/create-modpacks.ts` — all refactored onto
  `resolveSelectedProjects()` so they honour `--mods=` / `--exclude=`. `scripts/init-mod.sh` gains
  `README.md.tpl` / `POTENTIAL_FEATURES.md.tpl` templating and a `{{MC_COMPAT}}` substitution read
  from root `gradle.properties`' `minecraft_compatibility` (already `26.1.2` here).
- **New**: `tools/mod-status-plugin/**` — a standalone IntelliJ plugin with its own Gradle wrapper,
  deliberately **not** in `settings.gradle`. It reads only `gradle.properties` (`mod_id`,
  `mod_version`) and `CHANGELOG.md`. Zero MC coupling. Copy verbatim, binary wrapper jar included
  (`git checkout origin/main -- tools/`).

`scripts/mod-status.ts` and `tools/.../ModStatusReader.java` parse the `### Unreleased changes`
heading with the same rules by convention, not shared code — keep them in lockstep.

### 0.3 — `.bun-create/` template, `.claude/skills/`, `docs/`

| Path | Action |
|---|---|
| `.bun-create/mod/**` | Copy from `main` verbatim. `build.gradle.tpl` collapses to the `apply from:` one-liner (`mod-conventions.gradle` already exists here); `gradle.properties.tpl` drops the hardcoded `*_compat` floors in favour of `{{MC_COMPAT}}`; `neoforge/build.gradle.tpl` adopts `loom { neoForge { convertAccessWideners(...) } }` — **verified already in use on `26.1.2`** (`minekea/neoforge/build.gradle`), so it is safe. New `README.md.tpl`, `POTENTIAL_FEATURES.md.tpl`, `design/icon.pdn`. |
| `.claude/skills/mc-source-decompile/SKILL.md` | **New** — copy verbatim. Generic; keyed by `{mc_version}` under `.sources/`. |
| `.claude/skills/mc-visual-smoke-test/SKILL.md` | **New** — copy, then re-verify its two version-sensitive asides on 26.1.2: the `mc.gui.screen() instanceof TitleScreen` gate, and any `docs/MC-26.2-NOTES.md` cross-reference (rewrite the citation, that file is not being ported). The core `fabric-client-gametest-api-v1` technique is version-independent. |
| `docs/NEOFORGE.md` | **New** — copy. Three genuinely platform-level gotchas (mixin-added `SynchedEntityData` on vanilla entities; entity renderers must register in the NeoForge **mod constructor**; NeoForge's event-hook patches renumber synthetic lambdas and break vanilla-derived mixin targets). ⚠ The third gotcha's worked example quotes 26.2 lambda indices — keep the *pattern*, mark the indices as 26.2-derived. |
| `docs/FABRIC.md` | **New** — currently an empty stub. Copy as-is. |
| `docs/TESTING.md` | Add the `GameTestPlayers` entry to the testFixtures helper list (+7). |
| `docs/LOOT-CHEST-GENERATION.md`, `docs/SPAWNER-FLAME-PARTICLE-RESEARCH.md` | **New** — copy, but add a one-line provenance header: *"Derived from the MC 26.2 merged-deobf jar; re-verify against 26.1.2 before relying on exact method bodies."* |
| `docs/MC-26.2-NOTES.md` | **Skip** (§2). |
| `MOD_IDEAS.md` | Copy from `main`. |
| `docs/backport-26.1.2/README.md` | Append a short "Round 2" pointer to this plan so the round-1 doc doesn't read as current. |

### 0.4 — `CLAUDE.md`

**Do not copy `main`'s file.** Port the *structure*, keep 26.1.2 facts. Port these new sections:

- `tools/` — the standalone mod-status plugin build.
- **Access wideners are build-time-only outside `common`** — the `accessWidenerPath` /
  `copy:accesswideners` / `revert-fabricmodjson.ts` mechanism.
- **Gradle `PKIX path validation failed`** — the local Avast TLS workaround
  (`-Djavax.net.ssl.trustStoreType=WINDOWS-ROOT`).
- **Data generation** — `bun run datagen`, and the rule to keep the `has_fabric_datagen` /
  `has_neoforge_datagen` flags in sync when a mod gains or loses datagen.
- **Platform-specific gotchas** — pointers to `docs/NEOFORGE.md` / `docs/FABRIC.md`.
- **Versioning & releases** — the whole section (see §6).
- **Git commits** — check `git status` / `git diff --cached` before committing; this repo runs
  several concurrent efforts and a bare `git commit` can sweep in unrelated staged work.
- **`--mods=` / `--exclude=` build scoping**.
- Rewrite "Active vs. inactive mods" to point at `settings.gradle` as the source of truth, and
  update the counts for the 12 newly-active mods.

Do **not** port: the "Datagen gotcha (MC 26.2)" heading as 26.2-scoped (the *content* applies —
lazy component binding is confirmed present on 26.1.2 — so keep the section but drop the version
qualifier), any `docs/MC-26.2-NOTES.md` citation, and the z-fighting note's `log-all-the-things`
class references until that mod lands in Wave 2.

### 0.5 — The seven repo-wide sweeps

Four mods (`athenaeum`, `enchantment-numbers-fix`, `houdini-block`, `miniblock-merchants`) are
touched by **nothing but** these. Doing them once here keeps 26 agents from each reinventing them.

| Commit | Sweep |
|---|---|
| `d347dfba0` | every `*.mixins.json`: `"compatibilityLevel": "JAVA_21"` → `"JAVA_25"` (24 files on this branch). ✅ Correct on 26.1.2 — this branch already compiles at `options.release = 25` (`build.gradle:217-222`). ⚠ `miniblock-merchants/common/src/main/resources/miniblockmerchants.mixins.json` is `JAVA_17`, not `JAVA_21`. It is `JAVA_17` on `main` too and that sweep left it alone — **leave it alone here as well**, so the branches stay identical. |
| `1efade63c` | every `fabric.mod.json`: `"java": ">=21"` → `">=25"`; delete the redundant per-subproject `.gitignore` files. |
| `710359e2a` | every `LICENSE`: copyright year → 2026. |
| `f891bed6c` | every `README.md`: issue-tracker URL → `https://github.com/chimericdream/minecraft-mods/issues`. |
| `68f442229` | every `POTENTIAL_FEATURES.md`: append the `## From the idea backlog (2026-08-13)` section (creating the file for mods that lack one; chimeric-lib excluded). |
| `8dcee7b1a` | every `CHANGELOG.md`: ensure exactly one `### Unreleased changes` heading at the top (create `CHANGELOG.md` where missing). Load-bearing — `mod-status.ts` and the IntelliJ plugin both key off it. On `26.1.2` the existing dated headings from `c29ef1a43` stay below it. |
| `a3c4ba15d` | every `README.md`: standardize the `### Minecraft Versions` section. Copy `main`'s list verbatim (it describes the mod across all branches, not this branch). For the 12 new mods, whose lists only mention 26.2, add `* 26.1.2: Supported`. |

Apply these to **inactive** mods too (`blacklight`, `cobblicious`, `pannotia-companion`,
`playgrounds`) wherever `main` did — they are doc/metadata-only and cost nothing.

### 0.6 — Import the 12 new mod directories

```bash
for m in better-portal-linking better-target-dummies but-what-about camel-nostrils \
         effective-gear hang-from-slabs jdcrafte log-all-the-things next-update-now \
         sneaky-tweaks stack-it-up toy-box; do
  git rm -r --quiet --ignore-unmatch "$m"        # jdcrafte + hang-from-slabs have stale 1.21.x dirs
  git checkout origin/main -- "$m"
done
```

`jdcrafte` and `hang-from-slabs` exist on `26.1.2` as **stranded 1.21.10-era directories**. Replace
them wholesale — do not attempt to merge; `main`'s versions were rebuilt on current conventions
(`7dadbbef2`, `b0836ee3b`).

Then, for each of the 12, edit only `gradle.properties`:

```properties
mod_version    = 0.9.0        # was 1.x / 1.x-beta.0
minecraft_compat = 26.1.2     # was 26.2
```

plus `effective-gear`: `chimericlib_compat = 5.4.0` (was `6.4.0`).

Finally add all 12 to `project-list.json` (sorted) and `settings.gradle` (sorted, matching `main`'s
4-space formatting; note `project-list.json` is the source of truth and
`bun run update:settingsgradle` regenerates `settings.gradle` from it — **the `//`-commented
inactive entries are hand-maintained and would be lost by a regenerate, so edit both by hand**).

### Wave 0 acceptance

```bash
./gradlew projects                       # must succeed — proves every project still configures
./gradlew build --exclude-task ...       # or: bun run build --exclude=<the 12 new mod ids>
bun run status                           # the new script runs and lists every mod
git diff --stat 26.1.2..HEAD -- '*.java' # only the 12 new mods' Java should appear
```

The 15 pre-existing mods must still build green. The 12 new ones will not compile yet — expected.

---

## 6. The versioning & changelog conventions (port to `CLAUDE.md`, then follow them)

Every Wave 2 agent must apply these. They are process rules with no MC-version coupling.

**CHANGELOG.md**
- Exactly one `### Unreleased changes` heading at the top, accumulating *all* untagged work under
  `#### New Features` / `#### Bug Fixes` / `#### Changes`. Never a new dated heading per session.
- A tagged release renames that heading to `### <mc_version> - <version>` (e.g. `### 26.1.2 - 3.2.0`)
  and starts a fresh empty `### Unreleased changes`. **This backport cuts no tags** (§2), so every
  mod's new content lands under `### Unreleased changes` and nothing gets a dated heading.
- Related entries added in one batch nest under one descriptive intro bullet, not N flat bullets.
- Tone: player-facing mods get concise, non-technical prose (deletion test: if cutting a sentence
  loses nothing, cut it). **chimeric-lib is the exception** — developer audience, name the APIs.
- A changelog entry is required for any player-visible change. Pure internal refactors with no
  behaviour change do not need one.

**`mod_version`** — tag-driven and reactive. For this backport the target is fixed by §3, so an
agent simply *sets* it; the general rules (bump reactively from accumulated changelog content,
patch for fixes-only, minor once a feature lands, escalate but never downgrade, "cut a release"
defaults to final not beta) go into `CLAUDE.md` for future work.

**Pre-release datagen check** — before cutting any release, run `bun run datagen --mods=<mod>` and
commit the result. Wave 3 runs it repo-wide.

**Datagen flags** — a mod with a Fabric `fabric-datagen` entrypoint declares
`has_fabric_datagen = true`; one with a NeoForge `data { }` run config declares
`has_neoforge_datagen = true`. On `main` these nine do:
`archaeology-tweaks` (both), `artificial-heart` (both), `effective-gear` (both), `but-what-about`,
`hopper-xtreme`, `jdcrafte`, `minekea`, `sponj`, `villager-tweaks` (fabric only). Each mod's Wave 2
agent owns adding its own flags.

---

## 7. The 26.2 → 26.1.2 reverse API map

Round 1's map (`docs/backport-26.1.2/README.md` §5) still applies in full. It was `javap`-verified
against the 26.1.2 deobf jar. The essentials:

### Confirmed present on 26.1.2 — do **not** "fix" these

`net.minecraft.resources.Identifier` · `SubmitNodeCollector` and the whole render-feature overhaul
(landed in **26.1**) · `BlockEntityRenderer.extractRenderState` · `ItemStackRenderState` ·
`CameraRenderState` · `ModelFeatureRenderer.CrumblingOverlay` · Java 25 / `options.release = 25` ·
`fabricApi { configureTests { … } }` · `getStringOr` / `getIntOr` · `BlockBehaviour.Properties.noLootTable()` ·
`com.mojang.datafixers.util.Pair` · jspecify annotations · `loom { neoForge { convertAccessWideners(…) } }` ·
**lazy data-component binding** (`BuiltInRegistries.DATA_COMPONENT_INITIALIZERS` exists and
`ItemStack` construction throws `Components not bound yet` without the bake — the bootstrap bake
and the datagen `buildRecipes()` bind are both **required** on 26.1.2 too).

### Must be reversed (26.2 on the left)

| 26.2 (on `main`) | 26.1.2 (write this) |
|---|---|
| `BlockEntityTypes.X` | `BlockEntityType.X` |
| `EntityTypes.X` (`world.entity.EntityTypes`) | `EntityType.X` (`world.entity.EntityType`) |
| `net.minecraft.advancements.triggers.CriteriaTriggers` | `net.minecraft.advancements.CriteriaTriggers` |
| `net.minecraft.advancements.predicates.*` | `net.minecraft.advancements.criterion.*` |
| `EntityPredicate.Builder…put(EntityNbtPredicate.CODEC, …)` | `EntityPredicate.Builder.entity().nbt(x)` |
| `net.minecraft.util.LightCoordsUtil.getLightCoords(…)` | `LevelRenderer.getLightCoords(…)` (same for `BrightnessGetter`) |
| `Minecraft.getInstance().gui.hud.isHidden()` | `!Minecraft.getInstance().renderNames()` |
| `Blocks.WOOL.white()`, `Items.DYE.red()`, … | flat constants `Blocks.WHITE_WOOL`, `Items.RED_DYE`, … ⚠ the `DYED_` prefix is **dropped**: `DYED_TERRACOTTA.white()` → `WHITE_TERRACOTTA`, same for `DYED_SHULKER_BOX`, `DYED_CANDLE`, `DYED_CANDLE_CAKE`. `Blocks.TERRACOTTA` stays the plain block. |
| `Blocks.CUT_COPPER.weathering().exposed()` | `Blocks.EXPOSED_CUT_COPPER`; `.weathering().unaffected()` → the bare constant; `.waxed().X()` → `WAXED_*` |
| `TagAppender<T>` | `TagAppender<T, T>` |
| `TagAppender.add(X.builtInRegistryHolder().key())` | `TagAppender.add(X)` |
| `FabricTagProvider.builder(TagKey<T>)` | `FabricTagProvider.valueLookupBuilder(TagKey<T>)` (`this::builder` → `this::valueLookupBuilder`) |
| `supplier.getId().toString()` | `supplier.getRegisteredName()` — Architectury **20.x** on this branch |
| `new ItemStack(supplier.get())` | either resolves on Architectury 20.x; leave the `.get()` |
| `Optional.ofNullable(Identifier.tryParse(id)).flatMap(BuiltInRegistries.ENTITY_TYPE::getOptional)` | `EntityType.byString(id)` |
| ~~`blockPos.distToCenterSqr(vec3)`~~ ⚠ **NOT a reversal — struck 2026-09-04** | **Leave it alone.** `Vec3i.distToCenterSqr(Position)` and `(double,double,double)` are byte-identical on both jars, and `Vec3 implements Position`, so the 26.2 form compiles verbatim here. `banner-tweaks` has used it on this branch since round 1 and builds green. Rewriting it to `getCenter().distanceToSqr(...)` is gratuitous divergence. |
| `new EntitySpawnRequest(EntitySpawnReason.X, false)` | pass `EntitySpawnReason.X` directly |
| `Pair.of(a,b)` where the port replaced `Tuple` | `Tuple` still exists on 26.1.2, but **prefer `Pair`** so the branches don't gratuitously diverge |

### Round-2 findings — `javap`-verified against both jars while writing this plan

These correct or extend the round-1 map. **Trust these over round-1 where they disagree.**

| Symbol | Verdict on 26.1.2 |
|---|---|
| `net.minecraft.util.LightCoordsUtil` | **PRESENT** (round 1 called it 26.2-only — wrong). It has `pack`/`block`/`sky`/`withBlock`/`smoothPack`/`smoothBlend`/… but **no `getLightCoords`, and no nested `BrightnessGetter`**. `BrightnessGetter` is not gone, it *moved*: it is `LevelRenderer$BrightnessGetter` on 26.1.2 and `LightCoordsUtil$BrightnessGetter` on 26.2. So `log-all-the-things`' `FaceLighting.java` mostly stands; check method-by-method rather than rewriting the class. **Resolved:** 26.1.2 has `LevelRenderer.getLightCoords(BlockAndLightGetter, BlockPos)` and `getLightCoords(LevelRenderer$BrightnessGetter, BlockAndLightGetter, BlockState, BlockPos)` — the 2-arg overload is the one `FaceLighting.java:31` needs, and that is the file's **only** call site. |
| `net.minecraft.world.attribute.BedRule` | **PRESENT.** `camel-nostrils`' sleep mixin is far lower-risk than it looked. |
| `net.minecraft.client.gui.GuiGraphicsExtractor` | **PRESENT**, with its nested `HoveredTextEffects` / `RenderingTextCollector` / `ScissorStack`. |
| `net.minecraft.client.gui.Hud` | **ABSENT** — the class does not exist anywhere in the 26.1.2 jar. `sneaky-tweaks`' `@Mixin(Hud.class)` retargets to `net.minecraft.client.gui.Gui`. |
| `Gui.extractAirBubbles(GuiGraphicsExtractor, Player, int, int, int)` | **PRESENT and private, identical signature.** `sneaky-tweaks`' injection point survives verbatim once the mixin target is changed. |
| `Minecraft.renderNames()` | **PRESENT**, `public static`. Confirms `gui.hud.isHidden()` → `!Minecraft.renderNames()`. |
| `net.minecraft.advancements.criterion.PlayerTrigger` | **PRESENT.** So `advancements.triggers.PlayerTrigger` → `advancements.criterion.PlayerTrigger`. |
| `net.minecraft.advancements.CriteriaTriggers` | **PRESENT** at that (non-`.triggers`) package. |
| `BlockEntityType(BlockEntitySupplier, Set<Block>)` | **PRIVATE** on 26.1.2. Every `new BlockEntityType<>(…)` needs an access-widener entry, or `FabricBlockEntityTypeBuilder.create(…).build()` in fabric-only source. **Affects `better-target-dummies`, `camel-nostrils`, `jdcrafte` (×2), `log-all-the-things` (×3)** — none of which ship an AW entry today. |
| `GameTestHelper.makeMockServerPlayer(GameType)` | **ABSENT.** Only `makeMockPlayer(GameType)` (returns a plain `Player`) and `makeMockServerPlayerInLevel()` (a `ServerPlayer` hardcoded to CREATIVE that ignores `setGameMode`). For a survival `ServerPlayer`, construct one directly (`server, level, GameProfile, ClientInformation.createDefault()`) and override `gameMode()` — `ServerPlayerGameMode` reads it back through that accessor. **chimeric-lib's new `GameTestPlayers` must be re-expressed on this basis in Wave 1.** |
| `Blocks.COPPER_BARS` / `COPPER_CHAIN` / `COPPER_LANTERN` | **PRESENT and already `WeatheringCopperBlocks` records** on 26.1.2 — but that record has **no `.weathering()` method**; the stage accessors sit directly on it. So `Blocks.COPPER_BARS.weathering().unaffected()` → **`Blocks.COPPER_BARS.unaffected()`**, *not* a flat constant. This is a third reversal form the round-1 map didn't have. |
| Every other copper (`CUT_COPPER`, `COPPER_BLOCK`, `CHISELED_COPPER`, `COPPER_GRATE`, `COPPER_BULB`, `COPPER_DOOR`, `COPPER_TRAPDOOR`, `COPPER_CHEST`, `LIGHTNING_ROD`, `COPPER_GOLEM_STATUE`) | flat constants on 26.1.2 → `EXPOSED_CUT_COPPER`, `WAXED_OXIDIZED_COPPER_GRATE`, … |
| `ColorCollection` fields (`Blocks.WOOL`, `CARPET`, `CONCRETE`, `CONCRETE_POWDER`, `STAINED_GLASS`, `STAINED_GLASS_PANE`, `GLAZED_TERRACOTTA`, `DYED_TERRACOTTA`, `BANNER`, `WALL_BANNER`, `BED`, `DYED_SHULKER_BOX`, `DYED_CANDLE`, `DYED_CANDLE_CAKE`; `Items.DYE`, `Items.DYED_BUNDLE`, `Items.HARNESS`) | **ABSENT** on 26.1.2 — confirmed by a full field diff of `Blocks`/`Items` between the two jars. Reverse to flat constants; remember the `DYED_` prefix is dropped. |

### Found in flight (2026-09-04) — not in the original map

Two whole *categories* the map missed. Both were caught only by a real build, and both bit more
than one row:

| Symbol | Verdict on 26.1.2 |
|---|---|
| `OrderedSubmitNodeCollector.submitMovingBlock` | **Arity differs.** 26.1.2: `submitMovingBlock(PoseStack, MovingBlockRenderState)` — **2 args**. 26.2 adds a trailing `int`. Every call site found so far passed a constant `0` for it, so dropping the argument is behaviour-preserving. `submitCustomGeometry` is **identical (3 args) on both** — don't "fix" that one. Hit independently by Wave 1 (`FallingUpwardBlockEntity`) and Wave 2-C (7 call sites across three block-entity renderers). Note the class sits at `net/minecraft/client/renderer/`, not `.../renderer/submit/`. |
| **Architectury 20.0.7 event signatures** | ⚠ **Not a Minecraft difference at all** — a *library* one, which is why no jar diff or vanilla-symbol grep can find it. This branch pins Architectury **20.0.7**; `main` is on 21.x. On 20.x, `InteractionEvent.RIGHT_CLICK_BLOCK.click(...)` returns **`InteractionResult`**, not `EventResult` (adapt with `EventResult#asMinecraft()`), and `BlockEvent.BREAK.breakBlock(...)` takes an **extra trailing `IntValue`** (dropped exp) parameter. Bare method references written against 21.x will not bind. Check any Architectury event subscription. |

**The general lesson:** §7's tables and the acceptance greps only catch *known* patterns. A plain new
method on a vanilla class, a changed overload arity, or a dependency's API drift are all invisible to
them — `Villager.getVillagerDataFinalized()` (26.2-only, caught as a dead `@Shadow` that failed
mixin application) is a third example. **Build early; treat the greps as a backstop, never as proof.**
And when probing a jar for absence, get the package right — a wrong path yields a false "absent"
(`Villager` is at `world/entity/npc/villager/`, not `world/entity/npc/`).

### ⛔ Vanilla content that exists in 26.2 but **not** 26.1.2 — features that cannot be backported

A full field-level diff of `Blocks` and `Items` between the two jars found exactly two families of
genuinely new vanilla content (everything else in that diff is the collection refactor above).
⚠ A **class**-level diff of the two jars adds a third thing the field diff cannot see: the
sulfur *mob* and its supporting types — `world.entity.monster.cubemob.SulfurCube`,
`AbstractCubeMob`, `SulfurCubeArchetype(s)`, `world.item.component.SulfurCubeContent`,
`PotentSulfurBlock(Entity)`, `SulfurSpikeBlock` — none of which exist on 26.1.2. Check classes,
not just registry fields, before assuming a feature ports.

- **Sulfur**: `SULFUR`, `POTENT_SULFUR`, `SULFUR_SPIKE`, `SULFUR_{SLAB,STAIRS,WALL}`,
  `POLISHED_SULFUR{,_SLAB,_STAIRS,_WALL}`, `SULFUR_BRICKS`, `SULFUR_BRICK_{SLAB,STAIRS,WALL}`,
  `CHISELED_SULFUR`, and items `SULFUR_CUBE_BUCKET`, `SULFUR_CUBE_SPAWN_EGG`.
- **Cinnabar**: `CINNABAR`, `CINNABAR_{SLAB,STAIRS,WALL}`, `POLISHED_CINNABAR{,_SLAB,_STAIRS,_WALL}`,
  `CINNABAR_BRICKS`, `CINNABAR_BRICK_{SLAB,STAIRS,WALL}`, `CHISELED_CINNABAR`.
- Plus `Items.MUSIC_DISC_BOUNCE`.

**Consequences — these are scope carve-outs, not adaptation work:**

| Mod | What must be dropped |
|---|---|
| **minekea** | The sulfur and cinnabar halves of `670b08f78` (Chaos Cubed variations) and all of `1e001f7a2` (small sulfur cubes in glass jars). Sulfur/cinnabar appear in **7 Java files — all under `common/`, so Wave 2-A1 owns the whole carve-out** (`Beams`, `Covers`, `Slabs`, `Stairs`, `CompressedBlocks`, `Bookshelves`, and `GlassJarItem`, whose payload change imports the 26.2-only entity `SulfurCube`) — plus **two Python demo-world generators** (`generate_layout.py`, `extract_jar_contents.py`, owned by A2) and **1,303 generated files** (filename match; they disappear on regen, so don't delete them by hand). |
| **but-what-about** | The Chiseled Sulfur entries in `block/BlockFamilies.java` (1 Java file, 25 generated files — regenerated, not deleted). |

Record each omission in the mod's `CHANGELOG.md` under `### Unreleased changes` — say plainly that
the feature needs 26.2 vanilla blocks — and in its `POTENTIAL_FEATURES.md` if it should return later.

### How to check an API rather than guess

`git grep '<Symbol>' 26.1.2 -- '*.java'` proves a symbol is **used**, never that it is **absent**.
For absence, use the jars — **both versions are in the Loom cache**, so diff them directly:

```bash
J1=~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-deobf/26.1.2/minecraft-merged-deobf-26.1.2.jar
J2=~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-deobf/26.2/minecraft-merged-deobf-26.2.jar
unzip -l "$J1" | grep 'YourClass.class'                      # does it exist at all?
unzip -o -q "$J1" 'net/minecraft/<pkg>/<Class>.class' -d /tmp/mc1212 && javap -p /tmp/mc1212/net/minecraft/<pkg>/<Class>.class
```

The `mc-source-decompile` skill (landing in Wave 0) caches decompiled output under `.sources/`.

---

## 8. Wave 1 — chimeric-lib (one agent, serial, blocking)

Branch `backport/26.1.2/r2-chimeric-lib` off `backport/26.1.2/r2-shared`.
Payload: `git diff --no-ext-diff 23d614746..origin/main -- chimeric-lib/` — 52 files, +3,107/−20.
Target `mod_version = 5.5.0-beta.0`.

**Six new API surfaces, all consumed by Wave 2 mods — this is why Wave 1 blocks everything:**

| Package / class | What it is | Wave 2 consumers |
|---|---|---|
| `lib.blocks.family.{BlockFamily, BlockFamilyVariant}` + fabric `blocks.family.{BlockFamilyDataGenerators, FamilyBlockModels, SlabBlockDataGenerator, StairsBlockDataGenerator, WallBlockDataGenerator}` | Registers a base block's stairs/slab/wall from one shared `BlockConfig` template. `BlockConfig` derivation is deferred to registration time (`bfbbd0db9`) so it can reference an unresolved `RegistrySupplier`. | **but-what-about**, **minekea** |
| `lib.blocks.FallingUpwardBlock`, `lib.blocks.Risable`, `lib.entities.FallingUpwardBlockEntity` (414 L) | Inverse of vanilla `FallingBlock`/`Fallable`/`FallingBlockEntity` — rises, despawns past world-top, carries block-entity NBT. | **camel-nostrils** (Livna) |
| `lib.commands.{ChimericCommand, ChimericCommands, PlatformCommandArgumentTypes}` + `commands.blockstate.{BlockPropertiesArgument, BlockStateCommand}` + per-loader `PlatformCommandArgumentTypesImpl` | `/chimericlib blockstate get\|set\|modify`. `PlatformCommandArgumentTypes` is deliberately **not** `@ExpectPlatform` — see its own comment about a NeoForge JPMS module-export failure. | **stack-it-up** (`/stackitup`) |
| `lib.trims.{TrimMaterialConfig, TrimMaterialRegistryHelper, ArmorTrimAtlasProvider}` + fabric `trims.{TrimmedArmorItemModel, TrimmedArmorModelLoadingPlugin}` | Datagen + registry + atlas-override plumbing for custom armor trim materials; the Fabric `TrimmedArmorItemModel` is a port of NeoForge's `neoforge:trimmed_armor`, reaching two private vanilla classes reflectively (an access widener worked at compile time but threw `IllegalAccessError` at runtime — **keep the reflection**). | **effective-gear** |
| fabric `blocks.{RecipeUtils, TagUtils, TranslationUtils}`, `blocks.model.{CustomBlockModel, CustomCropModel, ModelUtils}`, `data.{JarAccess, TextureGenerator}` | Datagen idioms generalized out of minekea (`db12c19ef`); `TextureGenerator`/`JarAccess` do programmatic PNG datagen and safe vanilla-jar asset reads, now per-mod-id instead of hardcoded `"minekea"`. | **minekea**, **jdcrafte**, **artificial-heart** |
| `testFixtures` `lib.testkit.gametest.GameTestPlayers` | `makeFacingPlayer` + `useItem`, working around an NPE in `ServerPlayer#lookAt` on mock players. | **log-all-the-things** (3 GameTests) |

**Also in the payload**

- `lib.util.ProfileUtils` (**camel-nostrils**), `lib.util.ChimericLibParticleUtils`.
- `BlockConfig` — additive only (`getTextureOrDefault` overloads). Not source-breaking.
- `BlockDataGenerator` — **source-breaking**: `configureRecipes(HolderLookup.Provider, RecipeOutput)`
  → `(…, RecipeOutput, RecipeProvider)`, and `configureBlockLootTables(BlockLootSubProvider)` →
  `(…, HolderLookup.Provider)`. **Every implementer must be updated in the same wave chain.**
  ⚠ **Corrected 2026-09-04:** this read "minekea alone has ~57 such classes, plus
  but-what-about and jdcrafte". **minekea is not affected** — it has its own independent
  `ChimericLibBlockDataGenerator` base (`minekea/fabric/.../data/`) that already carries the
  new signatures, and nothing in minekea imports `lib.blocks.BlockDataGenerator` at all. The
  real implementers are **but-what-about (1 file) and jdcrafte (5)**, both via
  `FabricBlockDataGenerator`, and both already in 26.2 form from Wave 0's import — so no
  mod's build is broken by this change today.
- New `chimericlib.accesswidener` (widens `ModelTemplate.createMap` and its `model` / `requiredSlots`
  / `suffix` fields, for `CustomBlockModel`), and the `accessWidenerPath` blocks in
  `common/build.gradle` + `fabric/build.gradle` get **uncommented**. Verify the four AW entries
  resolve against the 26.1.2 `ModelTemplate` before committing.
- New `assets/chimericlib/lang/en_us.json` (14 keys — command messages + the falling-upward entity name).
- `ChimericLib.init()` now registers `BlockPropertiesArgument` + `BlockStateCommand`.

**26.1.2 adaptations required in this wave**

1. `GameTestPlayers` — rewrite against `makeMockPlayer` / a hand-built `ServerPlayer` (§7).
2. `ArmorTrimAtlasProvider.VANILLA_TRIM_PATTERNS` is a hardcoded 18-entry list commented "MC 26.2".
   **Already verified — this is a no-op.** The vanilla trim patterns are identical on both versions
   (`bolt coast dune eye flow host raiser rib sentry shaper silence snout spire tide vex ward
   wayfinder wild`), so the list ports verbatim. Only update the javadoc's "MC 26.2" reference.
   **Do not invent a difference here.**
3. `TrimmedArmorItemModel` reads `ArmorTrim`/`Equippable` off the live stack at **render** time
   rather than bake time, because default data components aren't bound until a server reload. That
   reasoning **holds on 26.1.2 too** (lazy binding is confirmed present here) — keep the design.
4. `chimeric-lib/common/src/testFixtures/.../BootstrapMinecraft` already exists on `26.1.2` (round 1
   backported it). Do not re-add it; only `GameTestPlayers` is new.
5. ⚠ **`fabric/.../blocks/family/WallBlockDataGenerator.java` is a move, not a new file.** `main`
   lifted it out of `minekea/fabric/.../block/building/WallBlockDataGenerator.java`, and because
   `26.1.2` reversed APIs in minekea's copy, git reports it as a rename **conflict** (§1). Add the
   chimeric-lib copy here in 26.1.2 terms; **Wave 2-A2 deletes minekea's copy and repoints its
   callers.** Do not touch anything under `minekea/` from this branch — just flag it to Wave 2-A2.

**Acceptance**

```bash
./gradlew :chimeric-lib:common:build :chimeric-lib:fabric:build :chimeric-lib:neoforge:build
./gradlew :chimeric-lib:fabric:test          # JUnit incl. the new BlockFamilyTest
./gradlew :chimeric-lib:fabric:runGameTest
```

---

## 9. Wave 2 — per-mod branches

Every branch forks from `backport/26.1.2/r2-chimeric-lib` — **with one exception: `r2-minekea-fabric` (A2) forks from `r2-minekea-common` (A1)**, because minekea's datagen classes will not compile without its block registrations. Fifteen assignments, one agent each; fourteen run in parallel and A2 waits on A1.

`athenaeum`, `enchantment-numbers-fix`, `houdini-block` and `miniblock-merchants` appear nowhere
here — their entire payload is the Wave 0 sweeps.

| # | Branch suffix | Mod(s) | Payload (files / Java) | Risk | The actual work |
|---|---|---|---|---|---|
| A1 | `minekea-common` | minekea (`common/`) | 24 java / 11 conflicts | **High** | **Registration side only.** Owns `minekea/common/src/main/java/**`, `common/src/main/resources/**` (16 files incl. `minekea.mixins.json` and the new compressed-block textures/models), plus `gradle.properties`, `CHANGELOG.md`, `README.md`, `POTENTIAL_FEATURES.md`. Apply the ColorCollection + weathering reversals across the block/item registration classes. **Owns the entire sulfur/cinnabar carve-out** — all six affected Java files are here (`Beams`, `Covers`, `Slabs`, `Stairs`, `CompressedBlocks`, `Bookshelves`), plus `item/containers/GlassJarItem.java`, whose payload change imports the 26.2-only **entity** `world.entity.monster.cubemob.SulfurCube`: drop commit `1e001f7a2` whole. 11 of the 37 minekea conflicts land here; resolve toward 26.1.2 (rule 5). ⛔ **Do not touch `common/src/main/generated/`** — A2 regenerates it. Do not touch `fabric/`, `neoforge/` or `demo-world/`. **Acceptance:** `./gradlew :minekea:common:build`. You cannot run datagen from this branch — that is A2's job. **Handoff:** hand A2 the list of block/item ids you added and dropped, so it knows what the regen must and must not produce. |
| A2 | `minekea-fabric` | minekea (`fabric/` + datagen output) | 57 java / 26 conflicts + 3,632 generated | **High** | ⚠ **Branches off `backport/26.1.2/r2-minekea-common`, not off `r2-chimeric-lib`** — the fabric datagen classes reference A1's block registrations and will not compile without them. Owns `minekea/fabric/**` (incl. `fabric.mod.json`), `minekea/common/src/main/generated/**` (**by regeneration only — never hand-edit**), and `minekea/demo-world/**`. Work: ⚠ the `BlockDataGenerator` signature migration this row used to list does **not exist** — see the correction in §8; minekea has its own base class that already matches, so skip it. The real work is the `TagAppender<T,T>` + `this::valueLookupBuilder` reversals, and **delete `fabric/.../block/building/WallBlockDataGenerator.java`, repointing its callers at chimeric-lib's `lib.fabric.blocks.family.WallBlockDataGenerator`** (§8 item 5). Strip sulfur/cinnabar from `demo-world/generate_layout.py` and `extract_jar_contents.py`, then regenerate the demo-world files — **never hand-edit them**. Finally `./gradlew :minekea:fabric:runDatagen`. 26 of the 37 conflicts land here. **Acceptance:** `:minekea:common:build`, `:minekea:fabric:build`, `:minekea:neoforge:build`, `:minekea:fabric:runGameTest`, and a second `runDatagen` that produces no diff. |
| B | `hopper-xtreme` | hopper-xtreme | 154 / 29 | **High** | Nether Star Hopper tier (16 items/transfer), Diamond Hopper Item Filter (10 slots) via a new `AbstractHopperItemFilterScreenHandler` base, and the filter-drop-on-break fix. **One real conflict: `fabric/.../XtremeHopperRecipeGenerator.java`** — `26.1.2` uses `Tuple` + `Items.GRAY_GLAZED_TERRACOTTA`; the payload adds lines in `Pair` + `Items.GLAZED_TERRACOTTA.gray()` form. Keep 26.1.2's style, splice in the new recipes. ⚠ **The anti-dupe `FilterSlot` logic (`mayPlace`/`safeInsert`/`remove`/`getMaxStackSize`) must survive the base-class extraction verbatim** — it moves into the new abstract base. Verified safe on `main`; do not regress it. |
| C | `log-all-the-things` | log-all-the-things (new) | 112 / 66 | **High** | Lava-, carpet-, snow- and window-logging. 12 shared mixins + loader-split `FireBlock`/`BucketItem` mixins. 3 new block entities with custom renderers. `LightCoordsUtil` **exists** on 26.1.2 but lacks `getLightCoords` — **resolved:** `client/FaceLighting.java:31` is the single call site; rewrite `LightCoordsUtil.getLightCoords(level, scratch)` as `LevelRenderer.getLightCoords(level, scratch)` and drop the `net.minecraft.util.LightCoordsUtil` import. Everything else in that class stands. 3× `new BlockEntityType<>` need AW entries. GameTests in a `gametest` source set with their own `fabric.mod.json` — `Blocks.COPPER_BARS.weathering().unaffected()` → `Blocks.COPPER_BARS.unaffected()`, `Blocks.CARPET.white()` → `Blocks.WHITE_CARPET`, `makeMockServerPlayer` → chimeric-lib's adapted `GameTestPlayers`. |
| D | `camel-nostrils` | camel-nostrils (new) | 226 / 50 | **High** | Camel snout removal, zombie fish, golden crops, Livna (upside-down anvil), upside-down bed/chest/crafting table, 8 advancements, 3 new entities. 11 shared mixins **plus loader-split `CN$ServerPlayerMixin`** (NeoForge restructures the sleep method's lambdas — see `docs/NEOFORGE.md`). `BedRule` **exists** on 26.1.2. `advancements.triggers.PlayerTrigger` → `advancements.criterion.PlayerTrigger` (2 files). `new BlockEntityType<>` at `block/ModBlocks.java:63` needs an AW entry. Depends on chimeric-lib `FallingUpwardBlock(Entity)` + `ProfileUtils`. ⚠ Register entity renderers in the **NeoForge mod constructor**, not a lifecycle event. |
| E | `effective-gear` | effective-gear (new) | 103 / 49 | **High** | Preserving enchantment for shears, 16 trim-material set bonuses, ender-pearl trim calming endermen, 2 advancements, a keybinding, a network payload. **20 shared mixins**, several on AI classes (`HoglinAi`, `PiglinAi`, `PiglinBruteAi`, `VibrationSystem.Listener`) — verify each target *signature* against the 26.1.2 jar. All 20 target **classes** are confirmed present on 26.1.2 (`VibrationSystem$Listener` sits at `world/level/gameevent/vibrations/`, `WeatherCheck` at `world/level/storage/loot/predicates/`), so only method shapes are at risk. Datagen on **both** loaders. Hard-depends on chimeric-lib's whole `trims` package. `chimericlib_compat` → `5.4.0`. No 26.2-only symbols found in its own source. |
| F | `stack-it-up` | stack-it-up (new) | 56 / 34 | **High** | AllStackable reborn: configurable stack sizes plus 14 shared mixins working around stack-size-sensitive vanilla systems, a loader-split `MixinItemStackDamage`, a Gson config with a migration path, and `/stackitup` (needs chimeric-lib's new `commands` package). ⚠ **Two mixins target numbered anonymous inner classes** — `DispenseItemBehavior$12` and `HorseInventoryMenu$1`. Anonymous-class numbering is not stable across MC versions. **Both already re-derived against the 26.1.2 jar:** `MixinDispenserBehavior9` → **`DispenseItemBehavior$13`** (it is the honeycomb-waxing behavior — on 26.1.2 `$13` is the one calling `HoneycombItem.getWaxed`, while `$12` is `ItemStack.hurtAndBreak`; the class's own header comment already records this). `MixinHorseScreenHandler` → **`HorseInventoryMenu$1` is unchanged** (structurally identical on both versions: `extends ArmorSlot`, same ctor descriptor, same `isActive()`). Update the header comment's version list rather than deleting it. JUnit `ConfigMigrationTest` must pass. |
| G | `but-what-about` | but-what-about (new) | 1061 / 9 | **High** | Stairs/slabs/walls for vanilla blocks Mojang skipped. Only 9 Java files, but 1,028 generated — **regenerate via `runDatagen`, don't patch**. `block/BlockFamilies.java` is written almost entirely in `.weathering()` + `Blocks.CONCRETE.pick(color)` form; rewrite in 26.1.2 flat-constant form. **Drop the Chiseled Sulfur family** (§7). Depends on chimeric-lib `blocks.family`. |
| H | `jdcrafte` | jdcrafte (revived) | 387 / 20 | **High** | Feeding trough (real inventory, shift-right-click to empty), weathervane, trellis + trellis arch in every wood type. Wave 0 already replaced the stale 1.21.10-era directory wholesale, so this is a straight port. 327 generated files — regenerate. `TagAppender<Block>` → `TagAppender<Block, Block>` across 5 datagen classes. 2× `new BlockEntityType<>` need AW entries. Depends on chimeric-lib fabric `TranslationUtils`. |
| I | `next-update-now` | next-update-now (new) | 903 / 20 | **Med-High** | Poplar wood set (custom foliage/trunk placers, three sapling colours) + colored concrete/wool slabs & stairs. 870 **hand-authored** resources (no datagen) — copy verbatim. `BlockEntityTypes.SIGN` / `.HANGING_SIGN` → `BlockEntityType.*` at `block/ModBlocks.java:201,202,216,217` and `ModBlockEntityValidBlocks.java`. That sign-integration mixin was already the source of a crash fixed in 1.0.1 — **test placing poplar signs and hanging signs on both loaders**. AW widens `FoliagePlacerType.<init>` / `TrunkPlacerType.<init>`. |
| J | `worldgen-mods` | archaeology-tweaks, artificial-heart | 91+86 / 28+26 | **Medium** | Both newly gain worldgen the same way — Fabric registers `configured_feature`/`placed_feature` through a `FabricDynamicRegistryProvider`; NeoForge additionally needs a `BiomeModifier`. One agent learns the pattern once. **archaeology-tweaks**: 8 naturally-generating suspicious blocks + loot tables, suspicious rooted dirt via a mixin redirect on azalea root placement (2%), the **Gentle Touch** enchantment, 4 advancements. `ATBrushableBlockEntity.java` — **not a merge conflict** (it diverges from `PORT2`, but the hunks don't overlap, so it merges clean); what it needs is the reversal applied to the payload's *newly added* lines: `advancements.triggers.CriteriaTriggers` and `EntityTypes.ITEM` ×2, both already reversed elsewhere in this same file, so copy the surrounding style. **artificial-heart**: pale pumpkin block/crop/seeds/carved, Pale Garden patch worldgen, passive Creaking Golem summoning, 2 advancements. `PaleCarvedPumpkinBlock.java` + 2 mixins need the `CriteriaTriggers` / `EntityTypes` reversals; no merge conflicts. |
| K | `new-small-mods` | better-target-dummies, better-portal-linking | 59+38 / 19+17 | **Medium** | **better-target-dummies**: target dummy block that takes any mob's skin, dummy spawn egg, mob-picker screen. `new BlockEntityType<>` at `block/ModBlocks.java:22` needs an AW entry. Its AW already widens `MenuScreens.register` — harmless but unnecessary on 26.1.2. **better-portal-linking**: portal-corner blocks address which Nether portal links where; YACL config; 2 mixins (`NetherPortalBlock`, `PortalForcer`). Its JUnit tests use `Blocks.CONCRETE.red()` / `Blocks.DYED_TERRACOTTA.white()` 20 times across `PortalAddressTest` (17) and `PortalAddressLinkerSelectTest` (3) — reverse to flat constants. `BootstrapMinecraft` already exists on `26.1.2`. |
| L | `scaffold-mods` | sneaky-tweaks, toy-box, hang-from-slabs | 46+21+10 / 20+5+0 | **Low** | **sneaky-tweaks**: berry-bush immunity, timed campfire immunity with a HUD grace meter, crouch bridging, 6 advancements, YACL config. One real fix: `@Mixin(Hud.class)` → `@Mixin(net.minecraft.client.gui.Gui.class)`; `Gui.extractAirBubbles(…)` exists with the identical signature, so the injection point is unchanged. **toy-box** and **hang-from-slabs** are empty scaffolds — Wave 0's import plus a build is the whole job. |
| M | `client-and-config` | beacon-conduit-tweaks, flat-bedrock, banner-tweaks | 14+17+10 / 7+8+1 | **Medium** | **beacon-conduit-tweaks**: hide the beacon beam with a carpet on top, or tinted glass in the column (a second pane re-reveals it); 5 new render-state/section mixins. `BCTweaksBeaconMixin.java` — **not a merge conflict** (diverges from `PORT2`, but merges clean); apply `BlockEntityTypes.BEACON` → `BlockEntityType.BEACON` to the payload's new lines (26.1.2 already reversed the existing ones) and verify the `getRegisteredName()` call. **flat-bedrock**: brand-new YACL/Mod Menu config — per-dimension thickness, replacement block, Nether no-roof; check against YACL `3.9.4`, not `3.9.5`. **banner-tweaks**: one mixin line adding "N/12 layers" to banner tooltips. |
| N | `additive-features` | sponj, villager-tweaks, shulker-stuff | 25+24+11 / 9+9+3 | **Low** | **sponj**: datagen scaffolding, a custom stat, 4 advancements (Big Gulp, Spill Response Team, Dry Heat, Space Heater). **villager-tweaks**: configurable baby growth time (two vanilla code paths — spawned and bred), a max-discount cap, leashable nitwits, datagen + 2 advancements. **shulker-stuff**: stackable-shulker dupe fix and a config gutted of ~230 lines of copy-pasted dead fields. No conflicts, no 26.2-only symbols in any of the three. |

### Procedure for a Wave 2 agent

```bash
MOD=<mod-name>
PORT2=23d614746

git checkout -b backport/26.1.2/r2-$MOD backport/26.1.2/r2-chimeric-lib
#   EXCEPT minekea-fabric (A2), which forks from the A1 branch instead:
#   git checkout -b backport/26.1.2/r2-minekea-fabric backport/26.1.2/r2-minekea-common

# The payload for this mod — feature work only; the 26.2 port is already behind PORT2.
git diff --no-ext-diff --binary $PORT2..origin/main -- $MOD/ > /tmp/$MOD.patch

git apply --3way /tmp/$MOD.patch
#   on failure:  git apply --reject /tmp/$MOD.patch   and hand-merge the .rej hunks
#   EXPECT failure if §1 lists your mod: minekea-common (11), minekea-fabric (26) and
#   hopper-xtreme (1) all conflict. See rule 4b — always resolve toward the 26.1.2 side.
#   for a NEW mod Wave 0 already imported: there is no patch to apply — the 26.2 source
#   is already in your tree. Your job is to make it compile and behave on 26.1.2.

# Reverse the 26.2 APIs (§7). Then:
./gradlew :$MOD:common:build :$MOD:fabric:build :$MOD:neoforge:build
./gradlew :$MOD:fabric:runDatagen        # if the mod declares has_fabric_datagen
./gradlew :$MOD:fabric:runGameTest       # if it has GameTests
./gradlew :$MOD:fabric:test              # if it has JUnit tests
```

### Rules for every Wave 2 agent

1. **Pass `--no-ext-diff` to every `git diff` / `git show` that prints a patch.** This repo's
   `diff.external` config points at a missing script; without the flag git aborts.
2. **Touch only your own mod's directory.** Wave 0 owns every root and shared file, including
   `settings.gradle` and `project-list.json`. If you think a shared file needs changing, **stop and
   report** — it means Wave 0 missed something.
3. **You own your mod's `gradle.properties` and `CHANGELOG.md`.** Set `mod_version` to the §3
   target, add `has_fabric_datagen` / `has_neoforge_datagen` if the mod has datagen, and put your
   content under `### Unreleased changes`. **Cut no tags and add no dated heading.**
4. **Don't "fix" 26.2-looking APIs that are already in the existing `26.1.2` code.** If a symbol is
   used on this branch, it exists. Check with `git grep '<Symbol>' 26.1.2` first.
5. **Resolve every conflict toward `26.1.2`, never toward `main`.** A conflict means round 1
   already reversed that code. Take 26.1.2's side as the base and splice the payload's *new* entries
   into it in 26.1.2 form. Resolving the other way silently re-introduces a 26.2 API that compiles
   nowhere. §1 lists which files conflict and who owns them — check it before you start, and if you
   hit a conflict in a file §1 doesn't list, **stop and report** rather than guessing.
6. **For absence, use the jar, never `git grep`.** §7 has the command; both version jars are cached.
7. **Preserve intent, not literal text.** Where adaptation is needed, keep the behaviour the commit
   message describes and re-express it in 26.1.2 terms.
8. **Keep the explanatory comments and javadoc.** A large share of this payload is documentation of
   *why* — the `@Overwrite` justifications, the reflection rationale in `TrimmedArmorItemModel`, the
   loader-split mixin explanations. That is the durable value; do not strip it as "just comments".
9. **Regenerate datagen; never hand-edit `src/main/generated/`.** If your mod has thousands of
   generated files in the payload, port the Java and re-run `runDatagen`.
10. **Expect to be reviewed twice, two different ways (§10).** One pass reconciles your branch
    against the payload diff and will surface anything you dropped without saying so — so say so.
    A second, on the higher-risk assignments, reads your files cold with no idea what changed or
    why. Write for that reader: keep the explanatory comments, and make the changelog match what
    the code actually does.
11. **Report honestly.** If something cannot be backported (26.2-only vanilla block, missing API, a
   test that can't run), finish everything else and say explicitly what you left out and why.
   Do not silently narrow scope.

---

## 10. The review gate

Every authoring branch passes a review gate before it merges — Wave 0, Wave 1, each Wave 2
assignment, and Wave 3. The gate has **three layers, cheapest first**, and not every branch gets
all three (§10.5).

**It is blocking.** A branch does not merge until its layers have run and every finding is
answered. Because Wave 1 blocks all of Wave 2, Wave 1's gate does too.

### 10.1 — Why it is split three ways

Two failure modes pull in opposite directions, and one reviewer cannot cover both:

- **The author's story anchors the reviewer.** Tell a reviewer "I ported X, dropped sulfur,
  reversed ColorCollection" and it checks those three boxes and stops. Guarding against this wants
  a reviewer that knows *nothing* about the intended change.
- **A reviewer that knows nothing cannot see an omission.** If an agent silently drops a feature,
  narrows scope, or skips a file, there is no trace in the resulting code — absence looks exactly
  like "this mod doesn't do that." Catching this *requires* the payload diff and the assignment.
  Across 15 agents doing mechanical porting under conflict pressure, silent scope-narrowing is the
  **more likely** failure; rule 11 in §9 exists because of it.

So the two questions get two agents with opposite information, and everything mechanical is handed
to a script that needs neither.

### 10.2 — Layer 1: the automated gate (no agent, runs on every branch)

Deterministic, free, and no false positives. Most of it already exists as §12's end-of-project
acceptance criteria — the change is that it now runs **per branch, before every merge**, instead of
once at Wave 3. Wave 3 re-runs the same checks repo-wide.

```bash
MOD=<mod-id>          # the gradle project name

# 1 — builds and tests
./gradlew :$MOD:common:build :$MOD:fabric:build :$MOD:neoforge:build
./gradlew :$MOD:fabric:test           # if the mod has JUnit tests
./gradlew :$MOD:fabric:runGameTest    # if the mod has GameTests

# 2 — datagen is idempotent (this is also what proves generated/ was not hand-edited)
./gradlew :$MOD:fabric:runDatagen && git status --porcelain -- "$MOD/"
#   ^ must print nothing

# 3 — no 26.2-only API leaked into this mod (same regex as §12.8)
git grep -nE 'EntityTypes\.|BlockEntityTypes\.|\.weathering\(\)|\.pick\(|advancements\.triggers|advancements\.predicates|makeMockServerPlayer\(|LightCoordsUtil\.getLightCoords|EntitySpawnRequest' -- "$MOD/**/*.java"
#   ^ must print nothing

# 4 — line endings
git ls-files --eol -- "$MOD/" | grep 'w/crlf'
#   ^ must print nothing. NB: `grep -v 'w/lf'` is NOT the same check — it also matches
#     binaries (w/-text) and empty files (w/none), 2,276 of them repo-wide.

# 5 — version and changelog hygiene
grep -E '^\s*mod_version' "$MOD/gradle.properties"    # must equal the §3 target
grep -c '^### Unreleased changes' "$MOD/CHANGELOG.md" # must be exactly 1
grep -m1 '^### ' "$MOD/CHANGELOG.md"                  # must BE '### Unreleased changes' —
#   a dated heading above it means someone cut a release; this backport cuts none (§2)
```

Layer 1 findings are **non-negotiable**: fix them, or the branch does not merge. They never reach a
human. Handing these to an agent is waste — and worse, it gives a reviewer trivia to pad with.

### 10.3 — Layer 2: completeness reconciliation (informed, narrow, cheap)

**Given:** the payload diff for its scope (`git diff --no-ext-diff $PORT2..origin/main -- <paths>`),
its §9 assignment row, its §3 target version, and the author's branch and report.

**Asks exactly one question:** *is anything in the assignment missing, silently dropped, or
contradicted?* Concretely — is every feature in the payload either present or recorded as an
intentional omission; is every §7 carve-out written into `CHANGELOG.md` and, where it should return
later, `POTENTIAL_FEATURES.md`; are the `has_*_datagen` flags right; is `mod_version` the §3 target.

**Must not judge code quality.** Ugly but complete is a **PASS** here. Style, structure and
refactors belong to layer 3 and are out of scope for this agent.

**Output:** a reconciliation list — *accounted for* / *missing* / *dropped and documented* /
**dropped silently**. Only that last category is a real finding.

This is a checklist reconciliation, not a code read, so it is fast. Anchoring does not hurt it: it
is not forming a quality judgement, so having the author's story costs nothing.

### 10.4 — Layer 3: the cold read (blind, scoped to the delta's files)

**Given:**

- **The list of files to review — paths only.** Not the diff, not why those files were selected.
  This is the whole trick: it says *where* to look without saying *what changed* or *why*, so the
  reviewer still cannot confirm the author's story, but it reads 24 files instead of a whole mod.
  It may read anything else in the mod for context; it reports only on the listed files.
- The target: **MC 26.1.2**, **Java 25**, Fabric + NeoForge via Architectury, official Mojang
  mappings (`net.minecraft.resources.Identifier`).
- Both deobf jars as ground truth, plus §7's `unzip` / `javap` recipe.
- Its build and test commands, and the repo's `CLAUDE.md` for conventions.
- The mod's `CHANGELOG.md` under `### Unreleased changes` — a deliverable in its scope and a public
  claim about the code, so *"the changelog says X, does the code do X?"* is fair game. It states
  what the mod now claims to do, not what the patch touched.

**Withheld — must not be in the prompt, and the reviewer must not go looking:**

- **This plan**, and everything else under `docs/backport-26.1.2/`.
- **`origin/main`, `23d614746`, or any other 26.2 ref** — no `git diff` / `git log` / `git show` /
  `git grep` against another branch, and no `git log` on its own branch either.
- The author's report, transcript, patch, or commit messages.

> ⚠ Do not paste the author's summary into the prompt "for context". That one shortcut turns layer
> 3 into a rubber stamp, and layer 2 already covers what the summary would tell it.

**What it looks for** (layer 1 owns everything mechanical; do not re-check it):

1. **Correctness bugs.** Fixed-size `NonNullList` handling, mixin targets that resolve to the wrong
   member or silently match nothing, loader-split code whose halves behave differently, NPE paths,
   unbalanced open/close or viewer counts, leaked resources.
2. **Symbols that do not exist on 26.1.2.** Verify against the jar, not intuition. Author and
   reviewer tend to be wrong in the same direction here, which is what makes it worth a second pass.
3. **Cross-loader parity.** Anything Fabric does that NeoForge does not, or the reverse.
4. **Slop.** Dead code, copy-paste, comments that contradict the code, stripped javadoc.

**Must not:**

- **Fix anything.** Report only — a reviewer that edits becomes a second unreviewed author.
- **Widen scope.** A finding pointing outside its file list is reported as a pointer, not chased.
- **Treat unfamiliarity as a defect.** *"I don't know why this exists"* is not a finding;
  *"this throws when the jar is full"* is.
- **Pad.** An empty findings list is a valid, useful result.

**Output:** findings ranked most-severe first, each with `file:line`, a one-sentence statement of
the defect, and a concrete failure scenario (inputs or state → wrong behaviour).

### 10.5 — Which layers each assignment gets

Tied to the risk column in §9, so effort follows exposure:

| Risk | Assignments | Layers |
|---|---|---|
| **High** | Wave 1, A1, A2, B, C, D, E, F, G, H | 1 + 2 + 3 |
| **Medium** | I, J, K, M, and Wave 0 | 1 + 2, plus layer 3 on hand-written source only — skip generated and resource-heavy directories (I and G are mostly datagen output, which layer 1 already proves by regeneration) |
| **Low** | L, N | 1 + 2 |
| Wave 3 | — | 1 repo-wide, plus 2 against §12 |

Row L is `sneaky-tweaks`, `toy-box` and `hang-from-slabs` — two of them empty scaffolds, one with
**zero** changed Java files. A blind cold read there is ceremony, not review.

### 10.6 — Disposition

Each layer's findings are answered differently, which keeps the human queue short:

| Layer | Nature | Who resolves it |
|---|---|---|
| 1 | Deterministic pass/fail | Author fixes; no discussion, no escalation. |
| 2 | Factual — present or not | Author completes the work, or records the omission per §7. Escalates only if the omission is a genuine scope decision. |
| 3 | Judgement | Author gets one remediation pass and answers every finding: fixed, pre-existing, out of scope, or disagreed-with-a-reason. Anything still disputed goes to a human. |

A reviewer never lands a change and never has the last word on its own.

Layer 3 still reads its whole file list rather than the diff, so it will occasionally raise a
finding on a line the author never touched. With the list scoped to the delta that is a thin
margin, and it is sometimes where the good findings come from — dispose of it as "pre-existing"
and move on.

---

## 11. Wave 3 — integration (one agent, serial)

Branch `backport/26.1.2/r2-integration` off `26.1.2` with every Wave 2 branch merged in.

1. `bun run datagen` repo-wide; commit the result. Confirm no mod's generated output drifts.
2. `bun run build` (full lifecycle, no scoping flags) — clean, access-wideners, Patchouli books,
   Gradle build, modpacks, teardown.
3. Verify modpack jar filenames: `<archives_name>-<platform>-26.1.2-<version>.jar`.
4. Confirm `git status` is clean after the build — `revert-fabricmodjson.ts` must have stripped the
   transient `fabric.mod.json` / access-widener edits.
5. Run every test suite (§12).
6. Final doc pass: `CLAUDE.md`'s active/inactive mod lists, `docs/backport-26.1.2/README.md`'s
   round-2 pointer, and each mod's `README.md` "Minecraft Versions" section.
7. Record the sulfur/cinnabar carve-out in `docs/` so it isn't rediscovered next round.

---

## 12. Overall acceptance criteria

The backport is done when, on the merged `26.1.2` branch:

1. `./gradlew clean build` is green for all **27** mods across both loaders (15 Wave 2 branches
   merged, minekea contributing two).
2. `bun run build` completes and produces modpacks for every mod.
3. `bun run status` lists every mod with the §3 version and shows unreleased changes where expected.
4. `./gradlew :chimeric-lib:fabric:test` and `:chimeric-lib:fabric:runGameTest` pass.
5. `:hopper-xtreme:fabric:runGameTest`, `:minekea:…`, `:sponj:…`, `:shulker-stuff:…`,
   `:houdini-block:…`, `:villager-tweaks:…`, `:log-all-the-things:fabric:runGameTest` pass.
6. `:better-portal-linking:fabric:test` and `:stack-it-up:fabric:test` (JUnit) pass.
7. `bun run datagen` is a no-op — every mod's committed generated output is current.
8. No 26.2-only API leaked in. The earlier form of this grep missed half the `ColorCollection`
   fields; use this one, which covers every symbol the full `Blocks`/`Items` field diff found:
   ```bash
   git grep -nE 'EntityTypes\.|BlockEntityTypes\.|\.weathering\(\)|\.pick\(|advancements\.triggers|advancements\.predicates|makeMockServerPlayer\(|LightCoordsUtil\.getLightCoords|EntitySpawnRequest|Blocks\.(WOOL|CARPET|CONCRETE|CONCRETE_POWDER|STAINED_GLASS|STAINED_GLASS_PANE|GLAZED_TERRACOTTA|DYED_TERRACOTTA|DYED_SHULKER_BOX|DYED_CANDLE|DYED_CANDLE_CAKE|BANNER|WALL_BANNER|BED)\.|Items\.(DYE|DYED_BUNDLE|HARNESS)\.' -- '*.java'
   ```
   returns nothing. Same for `SULFUR` / `CINNABAR` / `MUSIC_DISC_BOUNCE` outside a changelog note.

   > `\.pick\(` and `\.weathering\(\)` are the two easiest to miss by eye — `but-what-about`'s
   > `BlockFamilies.java` is written almost entirely in those two forms.
9. `git ls-files --eol | grep 'w/crlf'` returns nothing but `gradlew.bat`. (The older form of
   this check, `grep -v 'w/lf'`, is wrong — it also matches binaries (`w/-text`) and empty
   files (`w/none`), 2,276 of them repo-wide, so it can never pass.)
10. CI (`.github/workflows/build.yml`) is green on JDK 25 without a `GITHUB_TOKEN`.
11. Every branch passed its §10 review gate at the depth its risk tier requires, and every
    finding is fixed or explicitly dispositioned. No branch merged on an unanswered finding, and
    no layer-1 check was waived.

## 13. Manual verification worth doing before release

These are the behaviours a compile can't prove, ordered by how likely they are to be wrong:

- **hopper-xtreme**: the item-filter GUI cannot duplicate items after the `AbstractHopperItemFilterScreenHandler`
  extraction — shift-click, drag, hotbar-swap and cursor-drop against both the 5- and 10-slot filters.
- **next-update-now**: place and break poplar signs and hanging signs on both loaders (this exact
  path crashed on 26.2 before 1.0.1).
- **stack-it-up**: dispensers and the horse inventory screen still behave — those are the two
  re-derived anonymous-class mixin targets.
- **camel-nostrils**: sleeping in an upside-down bed on **NeoForge** specifically, plus the Livna
  block rising and the entity renderers appearing (a null renderer here is a hard crash).
- **effective-gear**: a full trim set's bonus actually fires, and trimmed armor renders — the Fabric
  `TrimmedArmorItemModel` reflection is the fragile part.
- **log-all-the-things**: window-logged stairs/slabs render on the right axis and don't z-fight.
- **archaeology-tweaks / artificial-heart**: new-world generation actually places suspicious blocks
  and pale pumpkin patches on **both** loaders (NeoForge needs the biome modifier).

---

## 14. Verification provenance

Every factual claim in §1, §3, §5 and §7 was re-checked against the repo and both deobf jars on
2026-09-03, after the first draft of this plan was written. Corrections applied in that pass:

- **§1 conflict surface** — the first draft claimed "exactly three files". A real three-way merge
  finds **82**, including **37 minekea Java files**. §9's minekea row (now split into A1/A2) previously said "zero merge conflicts
  — no minekea Java diverged"; 67 minekea Java files diverge. This was the plan's most dangerous
  error and is now corrected in both places.
- **§1** — the minekea → chimeric-lib `WallBlockDataGenerator.java` move was undocumented.
- **§4 / §10** — added a review gate after every authoring branch. It was first written as a
  single fully-blind adversarial reviewer; that version was replaced, because a blind reviewer is
  structurally unable to detect a *silent omission* — the likelier failure across 15 porting
  agents — and it paid for a whole-mod read to get there. The gate is now three layers: a free
  deterministic script, an informed completeness reconciliation, and a cold read scoped to the
  delta's file list (paths only, so it still cannot confirm the author's story). Depth is tiered
  by the §9 risk column.
- **§12.9** — the line-ending criterion was `git ls-files --eol | grep -v 'w/lf'`, which matches
  2,276 binaries and empty files and can never pass. Corrected to `grep 'w/crlf'`, which returns
  exactly `gradlew.bat`.
- **§4 / §9** — minekea was a single agent carrying 81 payload Java files, 37 conflicts and a
  3,632-file datagen regen, which is several times any other Wave 2 assignment. It is now split
  into **A1 (`common/`, registration: 24 java / 11 conflicts)** and **A2 (`fabric/` + datagen
  output: 57 java / 26 conflicts)**. The seam is real, not cosmetic: the two file sets are
  disjoint, all six sulfur/cinnabar registration files sit in `common/`, and every conflict
  falls cleanly on one side. It is **serial, not parallel** — fabric's datagen classes reference
  common's block registrations — so A2 forks from A1's branch. The one crossing edge is
  `common/src/main/generated/`, which physically lives under `common/` but is produced by
  `:minekea:fabric:runDatagen`; **A2 owns it, by regeneration only.**
- **§7** — the carve-out was justified by a `Blocks`/`Items` *field* diff, which misses the
  sulfur mob. `SulfurCube` and friends are 26.2-only classes, which is what actually blocks
  `1e001f7a2` (`GlassJarItem` imports the entity). Counts corrected: 7 Java files not 8,
  1,303 generated not 1,235, but-what-about 25 not 28.
- **§8** — the `VANILLA_TRIM_PATTERNS` verification is a confirmed no-op (both versions ship the
  same 18 patterns).
- **§9 F** — `DispenseItemBehavior$13` and `HorseInventoryMenu$1` re-derived; no longer open work.
- **§9 C** — `FaceLighting.java`'s single call site and its exact 26.1.2 replacement identified.
- **§12.8** — the leak grep was missing 9 `ColorCollection` symbols and `.pick(`.
- Per-mod Java counts corrected for hopper-xtreme, jdcrafte, archaeology-tweaks, artificial-heart,
  hang-from-slabs; effective-gear is 20 mixins, not 19.

**Confirmed accurate, no change needed** (listed so nobody re-litigates them): all 15 referenced
commit SHAs; the §3 version table against `main`; `gradle/mod-conventions.gradle`, `.github/` and
root `gradle.properties` being unchanged by the payload; chimeric-lib's payload at 52 files
`+3107/−20`; the broken `diff.external`; and the entirety of §7's reverse API map — `Hud` absent,
`Gui.extractAirBubbles` private with an identical signature, `BedRule` and `GuiGraphicsExtractor`
present, `Minecraft.renderNames()` public static, `advancements.criterion.PlayerTrigger`,
`BlockEntityType`'s constructor private on 26.1.2 (four mods already ship the exact access-widener
line to copy), `makeMockServerPlayer` absent, `COPPER_BARS`/`COPPER_CHAIN`/`COPPER_LANTERN` as
`WeatheringCopperBlocks` with direct stage accessors, `TagAppender<E,T>` vs `<T>`, and
`EntitySpawnRequest` being 26.2-only. The sulfur/cinnabar carve-out list is **exactly complete**
against a full field-level diff of `Blocks` and `Items`, `MUSIC_DISC_BOUNCE` included.

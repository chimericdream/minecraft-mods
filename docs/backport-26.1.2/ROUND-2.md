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
                        ┌── round 1 backport (28 commits) ──┐
e4817fa4 ──────────────────────────────────────────────────► fb2df9a94  (26.1.2, HEAD)
   │  (merge-base)
   └──► c5f2cc4d ──► 23d614746 ──────── 235 commits ────────► 34066bb59  (main, HEAD)
         (26.2 port)   (PORT2)              (payload)
```

| Anchor | SHA | Meaning |
|---|---|---|
| `PORT2` | `23d614746` | tag `hopper-xtreme/26.2-4.0.1` — the last `main` commit whose content is already on `26.1.2` |
| `HEAD` | `34066bb59` | current `origin/main` |
| `BASE` | `fb2df9a94` | current `26.1.2` |

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

1. **26.2 → 26.1.2 API reversals** applied in round 1 (see §5).
2. **Version numbers** — every mod is one major behind, MC compat is `26.1.2`.
3. **Three 26.1.2-only commits**: `2178c727d` (hopper-xtreme filter-GUI dupe fix — *its
   counterpart `0e8b9f1d6` is already on `main` behind `PORT2`, so there is no risk of undoing it*),
   `dd0fc7cc8` / `c29ef1a43` (version bumps + release changelog headings), `fb2df9a94` (bun.lock).

A payload file can only conflict where **both** the payload changed it **and** `26.1.2` differs
from `PORT2`. Measured across the whole payload, that is exactly **three files**, all named in §9:
`archaeology-tweaks/.../ATBrushableBlockEntity.java`,
`beacon-conduit-tweaks/.../BCTweaksBeaconMixin.java`, and
`hopper-xtreme/fabric/.../XtremeHopperRecipeGenerator.java`. Everything else applies clean; the
work is API adaptation, not merge resolution.

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
              ▼
Wave 1  ── backport/26.1.2/r2-chimeric-lib      (SERIAL, blocking, one agent)
              │  ~3,100 lines of new public API that 6 other mods compile against
              ▼
Wave 2  ── 14 parallel branches (see §9 for the grouping)
              │  branch off r2-chimeric-lib, one per mod (or per small group)
              ▼
Wave 3  ── backport/26.1.2/r2-integration       (SERIAL, one agent)
                 full build, modpacks, all test suites, datagen re-run, final doc pass
```

**Merge model.** Each wave branches off the previous wave's branch and merges into `26.1.2` in
order. Wave 2 branches all fork from `backport/26.1.2/r2-chimeric-lib`.

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
| `d347dfba0` | every `*.mixins.json`: `"compatibilityLevel": "JAVA_21"` → `"JAVA_25"`. ✅ Correct on 26.1.2 — this branch already compiles at `options.release = 25` (`build.gradle:217-222`). |
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
| `blockPos.distToCenterSqr(vec3)` | `blockPos.getCenter().distanceToSqr(vec3)` |
| `new EntitySpawnRequest(EntitySpawnReason.X, false)` | pass `EntitySpawnReason.X` directly |
| `Pair.of(a,b)` where the port replaced `Tuple` | `Tuple` still exists on 26.1.2, but **prefer `Pair`** so the branches don't gratuitously diverge |

### Round-2 findings — `javap`-verified against both jars while writing this plan

These correct or extend the round-1 map. **Trust these over round-1 where they disagree.**

| Symbol | Verdict on 26.1.2 |
|---|---|
| `net.minecraft.util.LightCoordsUtil` | **PRESENT** (round 1 called it 26.2-only — wrong). It has `pack`/`block`/`sky`/`withBlock`/`smoothPack`/`smoothBlend`/… but **no `getLightCoords` and no `BrightnessGetter`**. So `log-all-the-things`' `FaceLighting.java` mostly stands; check method-by-method rather than rewriting the class. |
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

### ⛔ Vanilla content that exists in 26.2 but **not** 26.1.2 — features that cannot be backported

A full field-level diff of `Blocks` and `Items` between the two jars found exactly two families of
genuinely new vanilla content (everything else in the diff is the collection refactor above):

- **Sulfur**: `SULFUR`, `POTENT_SULFUR`, `SULFUR_SPIKE`, `SULFUR_{SLAB,STAIRS,WALL}`,
  `POLISHED_SULFUR{,_SLAB,_STAIRS,_WALL}`, `SULFUR_BRICKS`, `SULFUR_BRICK_{SLAB,STAIRS,WALL}`,
  `CHISELED_SULFUR`, and items `SULFUR_CUBE_BUCKET`, `SULFUR_CUBE_SPAWN_EGG`.
- **Cinnabar**: `CINNABAR`, `CINNABAR_{SLAB,STAIRS,WALL}`, `POLISHED_CINNABAR{,_SLAB,_STAIRS,_WALL}`,
  `CINNABAR_BRICKS`, `CINNABAR_BRICK_{SLAB,STAIRS,WALL}`, `CHISELED_CINNABAR`.
- Plus `Items.MUSIC_DISC_BOUNCE`.

**Consequences — these are scope carve-outs, not adaptation work:**

| Mod | What must be dropped |
|---|---|
| **minekea** | The sulfur and cinnabar halves of `670b08f78` (Chaos Cubed variations) and all of `1e001f7a2` (small sulfur cubes in glass jars). Sulfur/cinnabar appear in **8 real Java files** (`Beams`, `Covers`, `Slabs`, `Stairs`, `CompressedBlocks`, `Bookshelves`, `GlassJarItem`, plus the demo-world generators) and **1,235 generated files**. |
| **but-what-about** | The Chiseled Sulfur entries in `block/BlockFamilies.java` (1 Java file, 28 generated files). |

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
  `(…, HolderLookup.Provider)`. **Every implementer must be updated in the same wave chain** —
  minekea alone has ~57 such classes, plus but-what-about and jdcrafte.
- New `chimericlib.accesswidener` (widens `ModelTemplate.createMap` and its `model` / `requiredSlots`
  / `suffix` fields, for `CustomBlockModel`), and the `accessWidenerPath` blocks in
  `common/build.gradle` + `fabric/build.gradle` get **uncommented**. Verify the four AW entries
  resolve against the 26.1.2 `ModelTemplate` before committing.
- New `assets/chimericlib/lang/en_us.json` (14 keys — command messages + the falling-upward entity name).
- `ChimericLib.init()` now registers `BlockPropertiesArgument` + `BlockStateCommand`.

**26.1.2 adaptations required in this wave**

1. `GameTestPlayers` — rewrite against `makeMockPlayer` / a hand-built `ServerPlayer` (§7).
2. `ArmorTrimAtlasProvider.VANILLA_TRIM_PATTERNS` is a hardcoded 18-entry list commented "MC 26.2".
   Verify against 26.1.2's `assets/minecraft/atlases/armor_trims.json` in the client jar and trim to
   whatever actually exists.
3. `TrimmedArmorItemModel` reads `ArmorTrim`/`Equippable` off the live stack at **render** time
   rather than bake time, because default data components aren't bound until a server reload. That
   reasoning **holds on 26.1.2 too** (lazy binding is confirmed present here) — keep the design.
4. `chimeric-lib/common/src/testFixtures/.../BootstrapMinecraft` already exists on `26.1.2` (round 1
   backported it). Do not re-add it; only `GameTestPlayers` is new.

**Acceptance**

```bash
./gradlew :chimeric-lib:common:build :chimeric-lib:fabric:build :chimeric-lib:neoforge:build
./gradlew :chimeric-lib:fabric:test          # JUnit incl. the new BlockFamilyTest
./gradlew :chimeric-lib:fabric:runGameTest
```

---

## 9. Wave 2 — per-mod branches

Every branch forks from `backport/26.1.2/r2-chimeric-lib`. Fourteen assignments; each is one agent.

`athenaeum`, `enchantment-numbers-fix`, `houdini-block` and `miniblock-merchants` appear nowhere
here — their entire payload is the Wave 0 sweeps.

| # | Branch suffix | Mod(s) | Payload (files / Java) | Risk | The actual work |
|---|---|---|---|---|---|
| A | `minekea` | minekea | 3751 / 81 | **High** | 24 feature commits. **3,632 files are regenerable datagen — regenerate, don't patch.** Port the 81 Java files, apply the ColorCollection + weathering + `TagAppender<T,T>` + `this::valueLookupBuilder` reversals (~350 sites, mechanical), adopt the new chimeric-lib `BlockFamily`/datagen helpers, then `./gradlew :minekea:fabric:runDatagen`. **Drop sulfur + cinnabar entirely** (§7). Zero merge conflicts — no minekea Java diverged on `26.1.2`. |
| B | `hopper-xtreme` | hopper-xtreme | 154 / 24 | **High** | Nether Star Hopper tier (16 items/transfer), Diamond Hopper Item Filter (10 slots) via a new `AbstractHopperItemFilterScreenHandler` base, and the filter-drop-on-break fix. **One real conflict: `fabric/.../XtremeHopperRecipeGenerator.java`** — `26.1.2` uses `Tuple` + `Items.GRAY_GLAZED_TERRACOTTA`; the payload adds lines in `Pair` + `Items.GLAZED_TERRACOTTA.gray()` form. Keep 26.1.2's style, splice in the new recipes. ⚠ **The anti-dupe `FilterSlot` logic (`mayPlace`/`safeInsert`/`remove`/`getMaxStackSize`) must survive the base-class extraction verbatim** — it moves into the new abstract base. Verified safe on `main`; do not regress it. |
| C | `log-all-the-things` | log-all-the-things (new) | 112 / 66 | **High** | Lava-, carpet-, snow- and window-logging. 12 shared mixins + loader-split `FireBlock`/`BucketItem` mixins. 3 new block entities with custom renderers. `LightCoordsUtil` **exists** on 26.1.2 but lacks `getLightCoords` — fix `client/FaceLighting.java` method-by-method. 3× `new BlockEntityType<>` need AW entries. GameTests in a `gametest` source set with their own `fabric.mod.json` — `Blocks.COPPER_BARS.weathering().unaffected()` → `Blocks.COPPER_BARS.unaffected()`, `Blocks.CARPET.white()` → `Blocks.WHITE_CARPET`, `makeMockServerPlayer` → chimeric-lib's adapted `GameTestPlayers`. |
| D | `camel-nostrils` | camel-nostrils (new) | 226 / 50 | **High** | Camel snout removal, zombie fish, golden crops, Livna (upside-down anvil), upside-down bed/chest/crafting table, 8 advancements, 3 new entities. 11 shared mixins **plus loader-split `CN$ServerPlayerMixin`** (NeoForge restructures the sleep method's lambdas — see `docs/NEOFORGE.md`). `BedRule` **exists** on 26.1.2. `advancements.triggers.PlayerTrigger` → `advancements.criterion.PlayerTrigger` (2 files). `new BlockEntityType<>` at `block/ModBlocks.java:63` needs an AW entry. Depends on chimeric-lib `FallingUpwardBlock(Entity)` + `ProfileUtils`. ⚠ Register entity renderers in the **NeoForge mod constructor**, not a lifecycle event. |
| E | `effective-gear` | effective-gear (new) | 103 / 49 | **High** | Preserving enchantment for shears, 16 trim-material set bonuses, ender-pearl trim calming endermen, 2 advancements, a keybinding, a network payload. **19 shared mixins**, several on AI classes (`HoglinAi`, `PiglinAi`, `PiglinBruteAi`, `VibrationSystem.Listener`) — verify each target signature against the 26.1.2 jar. Datagen on **both** loaders. Hard-depends on chimeric-lib's whole `trims` package. `chimericlib_compat` → `5.4.0`. No 26.2-only symbols found in its own source. |
| F | `stack-it-up` | stack-it-up (new) | 56 / 34 | **High** | AllStackable reborn: configurable stack sizes plus 14 shared mixins working around stack-size-sensitive vanilla systems, a loader-split `MixinItemStackDamage`, a Gson config with a migration path, and `/stackitup` (needs chimeric-lib's new `commands` package). ⚠ **Two mixins target numbered anonymous inner classes** — `DispenseItemBehavior$12` and `HorseInventoryMenu$1`. Anonymous-class numbering is not stable across MC versions: **re-derive both from the 26.1.2 jar** (`unzip -l "$J1" \| grep 'DispenseItemBehavior\$'` then `javap -p` to find the right one), do not assume they carry over. JUnit `ConfigMigrationTest` must pass. |
| G | `but-what-about` | but-what-about (new) | 1061 / 9 | **High** | Stairs/slabs/walls for vanilla blocks Mojang skipped. Only 9 Java files, but 1,028 generated — **regenerate via `runDatagen`, don't patch**. `block/BlockFamilies.java` is written almost entirely in `.weathering()` + `Blocks.CONCRETE.pick(color)` form; rewrite in 26.1.2 flat-constant form. **Drop the Chiseled Sulfur family** (§7). Depends on chimeric-lib `blocks.family`. |
| H | `jdcrafte` | jdcrafte (revived) | 387 / 22 | **High** | Feeding trough (real inventory, shift-right-click to empty), weathervane, trellis + trellis arch in every wood type. Wave 0 already replaced the stale 1.21.10-era directory wholesale, so this is a straight port. 327 generated files — regenerate. `TagAppender<Block>` → `TagAppender<Block, Block>` across 5 datagen classes. 2× `new BlockEntityType<>` need AW entries. Depends on chimeric-lib fabric `TranslationUtils`. |
| I | `next-update-now` | next-update-now (new) | 903 / 20 | **Med-High** | Poplar wood set (custom foliage/trunk placers, three sapling colours) + colored concrete/wool slabs & stairs. 870 **hand-authored** resources (no datagen) — copy verbatim. `BlockEntityTypes.SIGN` / `.HANGING_SIGN` → `BlockEntityType.*` at `block/ModBlocks.java:201,202,216,217` and `ModBlockEntityValidBlocks.java`. That sign-integration mixin was already the source of a crash fixed in 1.0.1 — **test placing poplar signs and hanging signs on both loaders**. AW widens `FoliagePlacerType.<init>` / `TrunkPlacerType.<init>`. |
| J | `worldgen-mods` | archaeology-tweaks, artificial-heart | 91+86 / 21+20 | **Medium** | Both newly gain worldgen the same way — Fabric registers `configured_feature`/`placed_feature` through a `FabricDynamicRegistryProvider`; NeoForge additionally needs a `BiomeModifier`. One agent learns the pattern once. **archaeology-tweaks**: 8 naturally-generating suspicious blocks + loot tables, suspicious rooted dirt via a mixin redirect on azalea root placement (2%), the **Gentle Touch** enchantment, 4 advancements. Conflict: `ATBrushableBlockEntity.java` (`advancements.triggers.CriteriaTriggers`, `EntityTypes.ITEM` ×2 — `26.1.2` already reversed both in this file). **artificial-heart**: pale pumpkin block/crop/seeds/carved, Pale Garden patch worldgen, passive Creaking Golem summoning, 2 advancements. `PaleCarvedPumpkinBlock.java` + 2 mixins need the `CriteriaTriggers` / `EntityTypes` reversals; no merge conflicts. |
| K | `new-small-mods` | better-target-dummies, better-portal-linking | 59+38 / 19+17 | **Medium** | **better-target-dummies**: target dummy block that takes any mob's skin, dummy spawn egg, mob-picker screen. `new BlockEntityType<>` at `block/ModBlocks.java:22` needs an AW entry. Its AW already widens `MenuScreens.register` — harmless but unnecessary on 26.1.2. **better-portal-linking**: portal-corner blocks address which Nether portal links where; YACL config; 2 mixins (`NetherPortalBlock`, `PortalForcer`). Its 3 JUnit tests use `Blocks.CONCRETE.red()` / `Blocks.DYED_TERRACOTTA.white()` ~15 times — reverse to flat constants. `BootstrapMinecraft` already exists on `26.1.2`. |
| L | `scaffold-mods` | sneaky-tweaks, toy-box, hang-from-slabs | 46+21+10 / 20+5+5 | **Low** | **sneaky-tweaks**: berry-bush immunity, timed campfire immunity with a HUD grace meter, crouch bridging, 6 advancements, YACL config. One real fix: `@Mixin(Hud.class)` → `@Mixin(net.minecraft.client.gui.Gui.class)`; `Gui.extractAirBubbles(…)` exists with the identical signature, so the injection point is unchanged. **toy-box** and **hang-from-slabs** are empty scaffolds — Wave 0's import plus a build is the whole job. |
| M | `client-and-config` | beacon-conduit-tweaks, flat-bedrock, banner-tweaks | 14+17+10 / 7+8+1 | **Medium** | **beacon-conduit-tweaks**: hide the beacon beam with a carpet on top, or tinted glass in the column (a second pane re-reveals it); 5 new render-state/section mixins. Conflict: `BCTweaksBeaconMixin.java` — `BlockEntityTypes.BEACON` → `BlockEntityType.BEACON` (26.1.2 already reversed it) and a `getRegisteredName()` call to verify. **flat-bedrock**: brand-new YACL/Mod Menu config — per-dimension thickness, replacement block, Nether no-roof; check against YACL `3.9.4`, not `3.9.5`. **banner-tweaks**: one mixin line adding "N/12 layers" to banner tooltips. |
| N | `additive-features` | sponj, villager-tweaks, shulker-stuff | 25+24+11 / 9+9+3 | **Low** | **sponj**: datagen scaffolding, a custom stat, 4 advancements (Big Gulp, Spill Response Team, Dry Heat, Space Heater). **villager-tweaks**: configurable baby growth time (two vanilla code paths — spawned and bred), a max-discount cap, leashable nitwits, datagen + 2 advancements. **shulker-stuff**: stackable-shulker dupe fix and a config gutted of ~230 lines of copy-pasted dead fields. No conflicts, no 26.2-only symbols in any of the three. |

### Procedure for a Wave 2 agent

```bash
MOD=<mod-name>
PORT2=23d614746

git checkout -b backport/26.1.2/r2-$MOD backport/26.1.2/r2-chimeric-lib

# The payload for this mod — feature work only; the 26.2 port is already behind PORT2.
git diff --no-ext-diff --binary $PORT2..origin/main -- $MOD/ > /tmp/$MOD.patch

git apply --3way /tmp/$MOD.patch
#   on failure:  git apply --reject /tmp/$MOD.patch   and hand-merge the .rej hunks
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
5. **For absence, use the jar, never `git grep`.** §7 has the command; both version jars are cached.
6. **Preserve intent, not literal text.** Where adaptation is needed, keep the behaviour the commit
   message describes and re-express it in 26.1.2 terms.
7. **Keep the explanatory comments and javadoc.** A large share of this payload is documentation of
   *why* — the `@Overwrite` justifications, the reflection rationale in `TrimmedArmorItemModel`, the
   loader-split mixin explanations. That is the durable value; do not strip it as "just comments".
8. **Regenerate datagen; never hand-edit `src/main/generated/`.** If your mod has thousands of
   generated files in the payload, port the Java and re-run `runDatagen`.
9. **Report honestly.** If something cannot be backported (26.2-only vanilla block, missing API, a
   test that can't run), finish everything else and say explicitly what you left out and why.
   Do not silently narrow scope.

---

## 10. Wave 3 — integration (one agent, serial)

Branch `backport/26.1.2/r2-integration` off `26.1.2` with every Wave 2 branch merged in.

1. `bun run datagen` repo-wide; commit the result. Confirm no mod's generated output drifts.
2. `bun run build` (full lifecycle, no scoping flags) — clean, access-wideners, Patchouli books,
   Gradle build, modpacks, teardown.
3. Verify modpack jar filenames: `<archives_name>-<platform>-26.1.2-<version>.jar`.
4. Confirm `git status` is clean after the build — `revert-fabricmodjson.ts` must have stripped the
   transient `fabric.mod.json` / access-widener edits.
5. Run every test suite (§11).
6. Final doc pass: `CLAUDE.md`'s active/inactive mod lists, `docs/backport-26.1.2/README.md`'s
   round-2 pointer, and each mod's `README.md` "Minecraft Versions" section.
7. Record the sulfur/cinnabar carve-out in `docs/` so it isn't rediscovered next round.

---

## 11. Overall acceptance criteria

The backport is done when, on the merged `26.1.2` branch:

1. `./gradlew clean build` is green for all **27** mods across both loaders.
2. `bun run build` completes and produces modpacks for every mod.
3. `bun run status` lists every mod with the §3 version and shows unreleased changes where expected.
4. `./gradlew :chimeric-lib:fabric:test` and `:chimeric-lib:fabric:runGameTest` pass.
5. `:hopper-xtreme:fabric:runGameTest`, `:minekea:…`, `:sponj:…`, `:shulker-stuff:…`,
   `:houdini-block:…`, `:villager-tweaks:…`, `:log-all-the-things:fabric:runGameTest` pass.
6. `:better-portal-linking:fabric:test` and `:stack-it-up:fabric:test` (JUnit) pass.
7. `bun run datagen` is a no-op — every mod's committed generated output is current.
8. No 26.2-only API leaked in:
   ```bash
   git grep -nE 'EntityTypes\.|BlockEntityTypes\.|\.weathering\(\)|advancements\.triggers|advancements\.predicates|makeMockServerPlayer\(|Blocks\.(WOOL|CARPET|CONCRETE|STAINED_GLASS|DYED_TERRACOTTA|BANNER|BED)\.|Items\.DYE\.' -- '*.java'
   ```
   returns nothing. Same for `SULFUR` / `CINNABAR` outside a changelog note.
9. `git ls-files --eol | grep -v 'w/lf'` returns nothing but `gradlew.bat`.
10. CI (`.github/workflows/build.yml`) is green on JDK 25 without a `GITHUB_TOKEN`.

## 12. Manual verification worth doing before release

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

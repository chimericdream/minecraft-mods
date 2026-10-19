# Automated CurseForge (and Modrinth) publishing — plan

_Drafted 2026-10-15. Status: **plan only, nothing implemented.** Items marked **VERIFY** are things I could
not confirm from docs in the research session and need a 5-minute check before building on them._

## TL;DR

Yes, this is very doable, and the repo is already ~80% of the way there: tags are already the release
signal (`sponj/26.2-6.1.0`), jar names are deterministic, the changelog has a parseable heading per tag, and
`mod_version`/`minecraft_compat` already live in each mod's `gradle.properties`.

**Recommendation:** add the **`me.modmuss50.mod-publish-plugin`** Gradle plugin to the shared
`gradle/mod-conventions.gradle`, driven by two new properties per mod (`curseforge_project_id`,
`modrinth_project_id`). Trigger it two ways from the same Gradle task:

1. **GitHub Action on tag push** (`<mod>/<mc>-<version>`) — the "normal" path. Gated by a GitHub
   *Environment* with a required reviewer so a stray tag push can't publish by itself.
2. **Local**: `bun run publish -- --mods=sponj` (wrapper around `./gradlew :sponj:publishMods`) with
   tokens in env vars — the fallback, and the way to do a `--dry-run` before trusting CI.

Adding **Modrinth** costs almost nothing extra once this exists (same plugin, one more block) and is also the
single best marketing move — see [MARKETING-IDEAS.md](MARKETING-IDEAS.md). **None of the mods currently link
to a Modrinth page.**

## What the repo looks like today (relevant facts)

| Fact | Where | Implication |
| --- | --- | --- |
| Releases = git tag `<folder>/<mc_compat>-<mod_version>` created locally | `.claude/skills/mc-cut-release/SKILL.md` | Tag name carries everything we need to select mod + version |
| CI `build.yml` runs on every push/PR; **no publish on tag** | `.github/workflows/build.yml` | Need a new workflow |
| `publish-packages.yml` publishes chimeric-lib to GitHub Packages on `release: created`, **using Java 21** | `.github/workflows/publish-packages.yml` | Looks stale: the build needs Java 25 (`options.release = 25`). Fix while here |
| 33 mods, each `enabled_platforms = fabric,neoforge` | `*/gradle.properties` | Each release = **2 jars** → 2 CurseForge files (+2 Modrinth) |
| Jar names: `<archives_name>-<loader>-<mc>-<ver>.jar` (e.g. `hopperxtreme-fabric-26.2-4.2.0.jar`); `archives_name` does **not** always equal the folder name | `gradle/mod-conventions.gradle`, `scripts/create-modpacks.ts` | Plugin must take the file from the Gradle task (`shadowJar.archiveFile`), never from a hand-built path |
| Prerequisite build steps: `update:patchoulibooks` (`bun run prepare:build`) | root `package.json` | CI publish job must run these before Gradle |
| `-beta.N` suffix in versions already distinguishes betas | versioning.md | Maps 1:1 to CurseForge `beta` vs `release` |
| Changelog heading `### <mc> - <version>` per tag | each `CHANGELOG.md` | Can extract release notes automatically |
| Dependencies are consistent: required = `chimericlib`, `architectury`, (Fabric) `fabric-api`; optional = `opus`, `modmenu` | `fabric.mod.json` / `neoforge.mods.toml` | CurseForge "relations" can be generated, same set for most mods |
| Backport branches exist as sibling worktrees (`26.1.2`, `1.21-1.21.1`) | `git worktree list` | Tag-triggered workflows run the workflow file **at the tagged commit** — backport branches need the workflow too |
| CurseForge URLs already in READMEs/`fabric.mod.json` for ~14 mods | grep | The projects exist; I just need the numeric project IDs (visible on each project's overview page) |

## Options considered

| # | Approach | Pros | Cons | Verdict |
| --- | --- | --- | --- | --- |
| A | **mod-publish-plugin** (Gradle) | One DSL for CurseForge + Modrinth + GitHub Releases + Discord; has a **dry-run** mode; runs identically locally and in CI; multi-platform helpers; actively maintained (2.2.1 as of research) | Gradle-side config in a 33-mod monorepo needs care (configure-on-demand ordering, see Gotchas) | **Recommended** |
| B | `Kir-Antipov/mc-publish` GitHub Action | Zero Gradle changes; reads metadata straight out of the jar | CI-only (no local path), needs a build step first, harder to feed per-mod IDs and changelog slices, weaker dry-run | Good runner-up |
| C | `TheMrMilchmann/gradle-curseforge-publish` | Clean CurseForge-only Gradle plugin | CurseForge only; you'd add a second plugin for Modrinth | Skip |
| D | Hand-rolled `curl`/Bun script against the upload API | Total control; no dependency | You re-implement game-version-ID lookup, relations, error handling | Only as a debugging aid |

The CurseForge Upload API itself (all options end up calling it): `POST /api/projects/{projectId}/upload-file`
(multipart: `metadata` JSON + `file`), auth with an `X-Api-Token` header; metadata has `changelog`,
`changelogType` (`text|html|markdown`), `releaseType` (`alpha|beta|release`), `gameVersions` (numeric IDs from
`/api/game/versions`) or `gameVersionNames`, `relations.projects[]` (`requiredDependency`,
`optionalDependency`, `embeddedLibrary`, …), and `isMarkedForManualRelease`. Tokens are created at
authors.curseforge.com → API Tokens. Docs: <https://support.curseforge.com/support/solutions/articles/9000197321-curseforge-api>.

## Proposed design

### 1. Per-mod properties (new, in each `<mod>/gradle.properties`)

```properties
curseforge_project_id = 123456
modrinth_project_id   = AbCdEfGh     # optional until a Modrinth page exists
# Optional overrides
# publish_dependencies_required = chimericlib,architectury-api   (defaults to the standard set)
```

Slugs are only needed for dependency relations and Discord webhooks. A one-off script
(`bun run publish:ids`) could scrape the existing CurseForge URLs from the READMEs and print a TODO list of
IDs to paste in; the IDs themselves must be copied from each project page by hand (no API-key-free lookup).

### 2. Shared publishing block in `gradle/mod-conventions.gradle`

Applied to the `fabric` and `neoforge` subprojects (that is where the jars are). Sketch — **API names are
from memory of the plugin README; check against the docs site (Options / CurseForge / Multi Platform pages)
before copying**:

```groovy
// in subprojects { if (project.name != 'common') { ... } }
apply plugin: 'me.modmuss50.mod-publish-plugin'

publishMods {
    file = tasks.shadowJar.archiveFile
    displayName = "${project.mod_name} ${project.mod_version} (${project.name == 'fabric' ? 'Fabric' : 'NeoForge'}, MC ${project.minecraft_compat})"
    version = project.version                       // 26.2-6.1.0
    type = project.mod_version.contains('-beta') ? BETA : STABLE
    modLoaders.add(project.name)                    // "fabric" / "neoforge"
    changelog = providers.provider { ChangelogExtractor.forVersion(rootDir, project) }
    dryRun = providers.environmentVariable('PUBLISH_DRY_RUN').isPresent()

    curseforge {
        projectId = project.curseforge_project_id
        accessToken = providers.environmentVariable('CURSEFORGE_TOKEN')
        minecraftVersions.add(project.minecraft_compat)
        javaVersions.add(JavaVersion.VERSION_25)      // VERIFY: CurseForge may expose Java 25 as a game version
        clientRequired = true; serverRequired = true  // property names VERIFY (plugin 2.0 made client/server mandatory)
        requires('architectury-api', 'chimericlib')
        if (project.name == 'fabric') requires('fabric-api')
        optional('opus')
        // modmenu is optional on Fabric only
    }

    modrinth {   // only when modrinth_project_id is set
        projectId = project.modrinth_project_id
        accessToken = providers.environmentVariable('MODRINTH_TOKEN')
        minecraftVersions.add(project.minecraft_compat)
        requires('architectury-api'); /* chimericlib/opus need Modrinth slugs or project IDs */
    }

    github { /* optional: attach both jars to a GitHub Release for the tag */ }
}
```

`ChangelogExtractor` is a ~20-line Groovy/Kotlin function (or a Bun script that writes
`build/release-notes.md`, which the Gradle block reads) that pulls the text under `### <mc> - <version>` from
the mod's `CHANGELOG.md` and **fails the publish if the section is missing** — which doubles as a guard
against tagging without a retitled changelog. CurseForge supports a `changelogType` of `markdown`; the
existing player-facing changelog tone is already right for it.

### 3. Tag → mod resolution (one small Bun script)

`scripts/publish.ts`:

- Parses `GITHUB_REF_NAME` / CLI arg `sponj/26.2-6.1.0` → folder `sponj`, mc `26.2`, version `6.1.0`.
- Asserts these equal the mod's `gradle.properties` (`mod_version`, `minecraft_compat`) — **refuses to
  publish on mismatch** (catches "tagged the wrong commit").
- Runs `bun run prepare:build`, then `./gradlew :<folder>:fabric:publishMods :<folder>:neoforge:publishMods`.
  (`mod_id` vs. folder: the existing `--mods` flag takes `mod_id`s; reuse `resolveSelectedProjects`.)
- `--dry-run` sets `PUBLISH_DRY_RUN=1`.
- Same script is the local entry point: `bun run publish -- sponj` (reads tokens from env).

### 4. The workflow `.github/workflows/publish-release.yml`

```yaml
name: publish-release
on:
  push:
    tags: ['*/*-*']          # sponj/26.2-6.1.0, chimericlib/26.2-6.0.0, villager-tweaks/26.2-6.2.0-beta.1
  workflow_dispatch:
    inputs:
      tag: { description: 'Existing tag to (re)publish', required: true }
      dry_run: { type: boolean, default: true }
jobs:
  publish:
    runs-on: ubuntu-latest
    environment: release            # <- required reviewer lives here; secrets scoped to it
    steps:
      - uses: actions/checkout@v5
        with: { ref: ${{ inputs.tag || github.ref }} }
      - uses: actions/setup-java@v5
        with: { distribution: temurin, java-version: 25 }
      - uses: actions/setup-node@v5
        with: { node-version: 22 }
      - run: npm i -g bun && bun i
      - uses: gradle/actions/setup-gradle@v5
      - run: chmod +x ./gradlew
      - run: bun run publish -- --tag "${{ inputs.tag || github.ref_name }}" ${{ inputs.dry_run && '--dry-run' || '' }}
        env:
          CURSEFORGE_TOKEN: ${{ secrets.CURSEFORGE_TOKEN }}
          MODRINTH_TOKEN: ${{ secrets.MODRINTH_TOKEN }}
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

Notes: `permissions: contents: write` only if attaching GitHub Releases. Run `bun run build --mods=<mod>`'s
tests first? The `build.yml` already ran on the commit — publish can rely on it, or `needs:` it by checking
the commit's status; simplest is to just run `./gradlew :<mod>:build` in the publish job (a few minutes).

### 5. Secrets & safety

- **Secrets** (repo → Settings → Environments → `release`): `CURSEFORGE_TOKEN`, `MODRINTH_TOKEN` (Modrinth
  PAT scopes: create/write versions). Locally: user-level env vars, never in the repo.
- **Required reviewer** on the `release` environment = a deliberate "yes, publish" click even after the tag
  push. Matters because *pushing a tag now publishes*; the existing rule "never push tags without explicit
  go-ahead" becomes even more load-bearing and the `mc-cut-release` skill's closing paragraph (which says
  distribution is manual) must be updated.
- Consider `isMarkedForManualRelease` for the very first automated uploads (file lands on CurseForge but
  stays unpublished until you click) — **VERIFY** that mod-publish-plugin exposes it; if not, the dry run +
  environment approval is enough.
- CurseForge files go through their moderation queue regardless; expect a delay between "uploaded" and
  "visible".

## Rollout (suggested order — each step independently shippable)

1. **Prep (manual, ~30 min):** create CurseForge API token; grab project IDs for the existing pages; decide
   whether to create Modrinth pages now (recommended). Record IDs in each `gradle.properties`.
2. **Local dry run on one low-stakes mod** (e.g. `stack-it-up` or `sponj`): add the plugin + block, run
   `PUBLISH_DRY_RUN=1 bun run publish -- sponj`. Verify the game-version/Java/loader mapping it prints.
3. **First real local publish** of a beta (`-beta.N` tag) of that one mod; confirm both files, dependencies,
   changelog, side flags look right on the CurseForge page.
4. **Add the workflow** + `release` environment; test with `workflow_dispatch` + `dry_run: true`.
5. **Switch the skill:** update `.claude/skills/mc-cut-release/SKILL.md` (step 7) and
   `docs/agents/releases/versioning.md`/`commands/build-release.md` to describe tag-push = publish.
6. **Roll out to all mods** (IDs only — the shared block does the rest). Chimeric-lib last or first? **First**
   if any dependent mod's relation needs its project to have a file; otherwise order doesn't matter.
7. **Backports:** cherry-pick the workflow + convention changes to `26.1.2` and `1.21-1.21.1` branches.
8. Fix `publish-packages.yml` (Java 21 → 25) while in the area.

## Gotchas to expect

- **Configuration ordering in the monorepo.** The repo already hit "referencing :opus:common evaluates it too
  early" (see `sponj/fabric/build.gradle`). Apply the plugin in the `fabric`/`neoforge` subprojects after
  `shadowJar` is registered and only read properties lazily (`providers.provider{}`); don't touch other
  projects' tasks at configuration time.
- **Unconfigured mods must not break the normal build.** Guard with
  `if (project.hasProperty('curseforge_project_id'))` so the other 20+ mods that have no ID yet (several are
  placeholder/planned: `blacklight`, `cobblicious`, `playgrounds`) don't fail `./gradlew build`.
- **Game version strings.** CurseForge's `26.2` and loader/Java entries may not exist the moment a new MC
  version ships; the first upload for a new MC version can fail on an unknown version name. **VERIFY**
  against `GET https://minecraft.curseforge.com/api/game/versions` (needs a token) — a tiny `bun run
  publish:versions` helper that lists 26.x/NeoForge/Java entries would answer this once and for all.
- **Dependency slugs.** `chimericlib`'s CurseForge slug is `chimericlib`; Opus/others need their slugs
  confirmed. Modrinth relations want Modrinth slugs/IDs, which differ.
- **Opus, ChimericLib are separate downloads.** Release notes for dependents should say "requires ChimericLib
  ≥ X" — the generated relation covers the metadata, not the human text.
- **Accidental double publish.** CurseForge has no "idempotent upload". Guard with the environment approval
  and never re-run the failed workflow blindly: check the project's Files tab first. (A pre-flight "does file
  `<name>` already exist?" check against the API is a nice-to-have.)
- **Draft/pre-release channel.** `-beta.N` → `beta`. Final → `release`. `-alpha` isn't used; fine.
- **`mc-cut-release` already stops before pushing tags**; keep that — now it protects a publish, not just a git ref.

## Effort estimate

| Piece | Rough size |
| --- | --- |
| Plugin block in `mod-conventions.gradle` + changelog extractor | 2–3 h incl. iteration |
| `scripts/publish.ts` + package.json entry | 1 h |
| Workflow + environment + secrets | 1 h |
| IDs for existing mods | 30 min (manual) |
| Dry runs, first real publish, fixes | 2–3 h |
| Backport branches + docs/skill updates | 1 h |

About one focused afternoon, most of it in verification rather than code.

## Open questions for you

1. Create Modrinth pages as part of this? (Strongly recommended — nearly free.)
2. GitHub Releases per tag too (attach jars, nice for GitHub-sourced "latest release" badges)? The plugin can
   do it in the same task.
3. Discord webhook announcement on publish? Only worth it if you have/make a Discord (see marketing doc).
4. Should betas auto-publish, or only finals? (My default: both, with `beta` type — they're "real releases" per
   your versioning rules.)
5. Should a human approval gate stay permanently, or only until you trust it?

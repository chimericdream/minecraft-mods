# Dependency Resolution Plan

**Goal:** eliminate `bun run publish:lib` from the inner development loop. Editing chimeric-lib source
should be immediately visible to every consumer mod's compile, test, GameTest, and dev-run — with no
publish step, no `~/.m2` involvement, and no possibility of stale bytecode.

Secondary goals: remove the structural traps that made the current setup hard to document correctly,
and cut the copy-paste in the Gradle configuration.

Status: **not started.** Nothing in this document has been implemented or verified by running Gradle;
the findings below come from reading the build files and `~/.m2`.

---

## 1. Why the current setup is awkward

### 1.1 The core problem

`build.gradle:87-107` wires every non-chimeric-lib subproject to chimeric-lib as an **external Maven
coordinate**:

```groovy
if (project.parent != null && project.parent.name !== 'chimeric-lib') {
    if (project.name == 'common') {
        implementation "com.chimericdream.lib:chimericlib-common:${rootProject.chimericlib_version}"
    }
    // ... fabric, neoforge
}
```

`mavenLocal()` (`build.gradle:80`) is in the repository list, so this resolves out of
`~/.m2/repository/com/chimericdream/lib/`. chimeric-lib is *in the same Gradle build* — Gradle can see
it as `:chimeric-lib:common` — but we deliberately route around that and go through a file copy in the
user's home directory instead. Every consequence flows from this:

- Source edits are invisible until republished.
- `clean` and `--rerun-tasks` don't help, because the stale artifact isn't in a build directory.
- Gradle's up-to-date checks can't see the relationship, so nothing warns you.
- CI never runs `publish:lib` (`.github/workflows/build.yml`), so it resolves chimericlib from GitHub
  Packages. **A PR that changes chimeric-lib and a consumer together cannot be validated by CI** — the
  consumer compiles against the last *released* library.

### 1.2 The coordinate mismatch that has been generating contradictory docs

This is worth calling out separately, because it is almost certainly the source of at least one of the
`docs/TESTING.md` reversals.

Each mod's `build.gradle` sets:

```groovy
archivesName = archives_name + "-" + project.name + "-" + project.minecraft_compat  // chimericlib-common-26.2
```

but the root `build.gradle:191` sets the publication's `artifactId` from `base.archivesName.get()`
**eagerly, inside the root `subprojects` block** — which runs *before* the mod's own `build.gradle`
overrides `archivesName`. So the publication captures the root value.

Verified in `~/.m2`:

| | value |
|---|---|
| Maven artifactId | `chimericlib-common` |
| Maven version | `26.2-6.0.0-alpha.0` |
| Jar file name | `chimericlib-common-26.2-6.0.0-alpha.0-dev.jar` |
| Project `version` property | `6.0.0-alpha.0` (no MC prefix) |

`docs/TESTING.md:98-99` documents the testFixtures coordinate as
`com.chimericdream.lib:chimericlib-common-<mc>:<ver>` — that artifactId **does not exist**. The
filename looks like it does, which is exactly the kind of thing that reads as correct until someone
tries it.

There is also a latent publishing bug hiding behind this: the project's own `version` is
`6.0.0-alpha.0`, and only the publication overrides it to `26.2-6.0.0-alpha.0`. That is fine today
because consumers reference chimericlib by a hand-written coordinate string. The moment we switch to
project dependencies, Gradle derives POM entries from the *project's* coordinates and would publish
consumer POMs pointing at `com.chimericdream.lib:chimericlib-common:6.0.0-alpha.0` — a version that was
never published. **Step 2 must land before or with Step 3.**

### 1.3 Other structural friction

- **18 near-identical mod `build.gradle` files.** `minekea/build.gradle` and `hopper-xtreme/build.gradle`
  are byte-identical; the others differ only by a `patchouli_compat` line and trailing whitespace. Every
  build change is an 18-file edit.
- **Duplicated version properties.** `architectury_compat`, `minecraft_compat`, `yacl_compat`,
  `modmenu_compat`, `fabric_compat` are repeated in all 21 `gradle.properties` files, and
  `chimericlib_compat` must be hand-synced with the root's `chimericlib_version` (four mods are already
  stranded at `4.0.0-beta.0`).
- **CI cannot be passing.** `build.yml` uses JDK 21; the build sets `options.release = 25`. There is no
  `java.toolchain` block, so Gradle uses whatever JDK it runs on and `release = 25` fails on 21.
- **`mavenLocal()` in every subproject's repositories** is a general reproducibility hazard, not just a
  chimericlib one — any stale local artifact silently wins over the real one.
- **Inactive mods** are commented out in `settings.gradle` but still on disk with their own build files,
  so they drift (they still reference chimericlib 4.x).

---

## 2. Step 1 — Normalize project coordinates (prerequisite)

Make each subproject's real Gradle coordinates equal what we publish, and delete the eager
`artifactId`/`version` overrides.

In each mod's `build.gradle`:

```groovy
allprojects {
    group   = project(":<mod>").maven_group
    version = "${rootProject.minecraft_compatibility}-${project(":<mod>").mod_version}"  // 26.2-6.0.0-alpha.0
}

subprojects {
    base {
        archivesName = project(":<mod>").archives_name + "-" + project.name   // chimericlib-common
    }
}
```

In the root `build.gradle` publishing block, drop the `artifactId = base.archivesName.get()` and
`version = ...` lines entirely — the defaults now produce the same values, without the ordering trap.

Net effect on published output:

- artifactId: `chimericlib-common` → unchanged (it was already this in Maven).
- version: `26.2-6.0.0-alpha.0` → unchanged.
- **Jar filename changes**: `chimericlib-common-26.2-6.0.0-alpha.0-dev.jar` →
  `chimericlib-common-26.2-6.0.0-alpha.0.jar` (the MC version now comes from the version string rather
  than being doubled into the base name). Check `scripts/create-modpacks.ts` for filename assumptions
  before merging — this is the one user-visible change in the step.

**Verify:** `./gradlew publishToMavenLocal`, then diff the generated `.pom`/`.module` files against the
current ones. Only the artifact filenames should differ.

---

## 3. Step 2 — Replace the Maven coordinate with project dependencies

Rewrite `build.gradle:87-107`. Keep it in one place (root), keep the Maven coordinate as a fallback for
when chimeric-lib is not part of the build:

```groovy
def libInBuild = rootProject.findProject(':chimeric-lib') != null

if (project.parent != null && project.parent.name !== 'chimeric-lib') {
    if (project.name == 'common') {
        dependencies {
            if (libInBuild) {
                implementation(project(':chimeric-lib:common')) { transitive = false }
            } else {
                implementation "com.chimericdream.lib:chimericlib-common:${rootProject.chimericlib_version}"
            }
        }
    }

    if (project.name == 'fabric') {
        dependencies {
            if (libInBuild) {
                // `common` extends compileClasspath/runtimeClasspath AND developmentFabric, so these
                // are registered as dev-runtime mods, not just classpath entries (see 3.1).
                common(project(':chimeric-lib:common'))  { transitive = false }
                common(project(':chimeric-lib:fabric'))  { transitive = false }
            } else {
                implementation "com.chimericdream.lib:chimericlib-fabric:${rootProject.chimericlib_version}"
            }
        }
    }

    if (project.name == 'neoforge') { /* same shape, developmentNeoForge; keep the fancymodloader exclude */ }
}
```

### 3.1 Two details that will bite if missed

**Dev-run mod detection.** An external chimericlib jar is a single classpath entry containing
`fabric.mod.json` + classes, so Fabric Loader finds both. A project dependency is *two* entries
(`build/classes/java/main`, `build/resources/main`) — the loader finds the metadata in one and the
classes in the other. Loom handles this via classpath groups, but only for dependencies it knows are
mods. That is what the existing `configurations { developmentFabric.extendsFrom common }` line in each
mod's `fabric/build.gradle` is for; routing chimericlib through the `common` configuration reuses the
mechanism the mods already use for their own `common` subproject. Same for `developmentNeoForge`.

**Do not let chimericlib into `shadowBundle`.** chimericlib ships as its own mod; consumers must not
bundle it. The snippet above deliberately uses `common`, not `shadowBundle`. Verify after the change
that consumer jars are the same size and that `unzip -l` shows no `com/chimericdream/lib/` entries.

**Note on the platform jars:** `:chimeric-lib:fabric`'s `jar` task is classified `raw` and `shadowJar`
holds the real bundled output. For a project dependency, Gradle wires consumers to the `jar` output —
i.e. the raw one, without common's classes shaded in. That is *fine* here only because we also depend
on `:chimeric-lib:common` directly. If we ever drop that, or if something resolves
`:chimeric-lib:fabric` alone, it will fail confusingly. Optional hardening: set the outgoing artifact
of `apiElements`/`runtimeElements` to `shadowJar` in the mods that apply shadow. The root
`build.gradle:206-215` comment shows this trap was already hit once on the publishing side.

### 3.2 What happens to `publish:lib`

It stays — it is still how the library reaches external/GitHub Packages consumers, and it stays useful
for testing a real published artifact before a release. It is simply no longer part of the edit→build
loop. Once Step 2 is verified, consider removing `mavenLocal()` from the root repositories list
(`build.gradle:80`) so a stale `~/.m2` copy can never shadow the in-build project again. That removal is
the change that makes the whole class of bug structurally impossible, and is the real deliverable of
this plan.

### 3.3 Test fixtures follow for free

`build.gradle:143-145` special-cases chimeric-lib's own tests. With project dependencies, every mod can
use the same line, so the special case and the misleading coordinate comment both go away:

```groovy
testImplementation(testFixtures(project(':chimeric-lib:common')))
gametestImplementation(testFixtures(project(':chimeric-lib:common')))
```

---

## 4. Step 3 — Verification

Run in order; each one is a specific failure mode from the current setup.

1. `./gradlew :minekea:fabric:build` on a clean `~/.m2` (temporarily rename the
   `com/chimericdream/lib` directory). Must succeed — proves nothing resolves from maven-local anymore.
2. Add a temporary throwing method to a chimeric-lib common class, call it from minekea, build
   **without** publishing. Must compile and fail at runtime with the new behavior.
3. `./gradlew :chimeric-lib:fabric:test :chimeric-lib:fabric:runGameTest :minekea:fabric:runGameTest`.
4. Launch `:minekea:fabric:runClient` and confirm chimericlib loads as a separate mod (the classpath-group
   issue in 3.1 shows up here, not at compile time).
5. `unzip -l minekea/fabric/build/libs/*.jar | grep chimericdream/lib` → no results.
6. `./gradlew publishToMavenLocal` and diff the consumer POMs against pre-change copies — the
   chimericlib dependency entry must still read `com.chimericdream.lib:chimericlib-fabric:26.2-6.0.0-alpha.0`.
7. Comment out `chimeric-lib` in `settings.gradle` and confirm a consumer still configures via the
   Maven fallback path.

---

## 5. Additional improvements (independent, ordered by value)

These are not required for the main goal. Each is separately landable.

### 5.1 Fix CI (high value, small)

`build.yml` builds on JDK 21 against a `release = 25` build. Move to JDK 25 and add an explicit
toolchain to the root `build.gradle` so the build stops depending on the ambient JDK:

```groovy
java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }
```

Then add the test tasks to CI — after Step 2, CI can finally validate a lib+consumer change in one PR:

```yaml
- run: ./gradlew build :chimeric-lib:fabric:test :chimeric-lib:fabric:runGameTest :minekea:fabric:runGameTest
```

Also drop the (now unnecessary) GitHub Packages credential requirement for chimericlib resolution,
which currently makes fork PRs unbuildable.

### 5.2 Convention plugins instead of 18 copies (high value, medium)

Move the duplicated `subprojects` block into precompiled script plugins under `build-logic/` (an
included build) or `buildSrc/`:

- `chimeric.mod-conventions.gradle` — group/version/archivesName/processResources/publishing.
- `chimeric.platform-shadow.gradle` — the `jar`-raw + `shadowJar` reclassification.
- `chimeric.fabric-conventions.gradle` / `chimeric.neoforge-conventions.gradle`.

Each mod's `build.gradle` then becomes ~5 lines. This is what makes future changes like Step 2 a
one-file edit instead of an 18-file one. Do it *after* Step 2 so the two changes stay reviewable.

### 5.3 Version catalog (medium)

Replace the loose `gradle.properties` version keys with `gradle/libs.versions.toml`. Keeps
mod-identity properties (`mod_id`, `mod_version`, …) in `gradle.properties` where they belong, and gets
dependency versions type-checked and centralized.

### 5.4 Derive `*_compat` from the root (medium)

The per-mod `architectury_compat`, `minecraft_compat`, `yacl_compat`, `modmenu_compat`, `fabric_compat`,
and `chimericlib_compat` values are copies. Default them from the root properties in the convention
plugin and let a mod override only when it genuinely pins something older. This removes an entire class
of "forgot to bump one of 21 files" bug — four inactive mods are already stale at `chimericlib 4.0.0-beta.0`.

### 5.5 Inactive mods (low)

Six mods are commented out of `settings.gradle` but still carry build files that silently rot. Either
include them in the build (so they at least compile) or move them to `_archive/` so it is obvious they
are not maintained. `project-list.json` + `settings.gradle` + the commented block are three places
encoding one fact; the `update:*` scripts should own it.

### 5.6 Build performance (low, opportunistic)

`org.gradle.parallel = false` and `org.gradle.configuration-cache = false` are both off, the latter for
a documented IDEA/Loom reason. Re-test `parallel = true` at least — with 60+ subprojects the win is
significant, and Loom's parallel-safety has improved. Keep configuration cache off until the linked
Loom issue closes.

### 5.7 Documentation (must accompany Step 2)

- `docs/TESTING.md`: delete the "publish loop" section entirely; fix the wrong testFixtures coordinate
  at lines 98-99.
- `CLAUDE.md`: delete the "chimeric-lib publish loop" section and the "#1 footgun" claim from the
  Architecture section; restate `publish:lib` as release-only tooling.
- The memory note `chimeric-lib-maven-local-test-loop` becomes wrong and should be replaced.

---

## 6. Suggested sequencing

| Phase | Contents | Risk |
|---|---|---|
| 1 | Step 1 (coordinates) + verify published metadata unchanged | Low — check modpack script for filename assumptions |
| 2 | Step 2 (project deps) + Step 3 verification + doc updates (5.7) | Medium — dev-run mod detection is the risky part |
| 3 | Remove `mavenLocal()` (3.2) | Low, but only after Phase 2 is proven |
| 4 | CI fix (5.1) | Low |
| 5 | Convention plugins (5.2), then catalog (5.3), then compat derivation (5.4) | Medium, mechanical |
| 6 | Inactive mods (5.5), build perf (5.6) | Low |

Phases 1–3 achieve the stated goal. Everything after is cleanup that becomes much cheaper once 5.2
lands.

# Architecture: monorepo structure

Multi-mod monorepo using Gradle + Architectury for cross-platform mod development. Each mod supports
both Fabric and NeoForge.

- **Multi-project Gradle build**: root `build.gradle` defines common config applied to every mod's
  `common`/`fabric`/`neoforge` subprojects.
- **Architectury pattern**: each mod has `common/` (shared), `fabric/`, and `neoforge/` subprojects.
- **ChimericLib dependency**: most mods depend on `chimeric-lib`. It is wired as an in-build
  **`project()` dependency** (see `build.gradle`), so editing chimeric-lib source recompiles straight
  into every consumer — no publish step, no `~/.m2`. A published-coordinate fallback is used only when
  chimeric-lib is not part of the build. Full wiring details:
  [chimeric-lib dependency](dependencies.md).
- **Project list**: all mods in the monorepo are built; `settings.gradle`'s `projectList` is mirrored in
  `project-list.json` (kept in sync by the `update:*` scripts — see
  [dev-workflow commands](../commands/dev-workflow.md)).
- **`tools/`**: developer tooling that is **not** part of the mod build. Each subdirectory is its own
  standalone Gradle build with its own wrapper, deliberately absent from `settings.gradle`, so
  `./gradlew build` at the repo root never sees it. See "tools/" below.

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

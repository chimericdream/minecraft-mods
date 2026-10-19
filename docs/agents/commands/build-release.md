# Build & modpack commands

- `bun run build` — full build: `clean` → prepare (copy access wideners + update Patchouli books) →
  `./gradlew build` → create modpacks → teardown (revert temp `fabric.mod.json` edits). Pass
  `--mods=<id,id,...>` (comma-separated `mod_id`s from each mod's `gradle.properties`, e.g.
  `--mods=chimericlib,minekea`) to scope every one of those steps — including the Gradle task
  selection and `clean` — to just those mods instead of the whole repo; omit it to build everything,
  as before. `--exclude=<id,id,...>` is the inverse — it removes matching mods from whatever set
  `--mods` selected (or from the full project list if `--mods` was omitted), e.g.
  `--exclude=minekea` builds everything except minekea, or `--mods=chimericlib,minekea
  --exclude=minekea` builds just chimericlib. Both flags work on `build:gradle`, `build:modpacks`,
  `clean` and `update:patchoulibooks` when run standalone.
- `bun run build:gradle` — `./gradlew build` only (or scoped `:mod:build` tasks with `--mods`/`--exclude`).
- `bun run build:modpacks` — create modpack distributions in `build/modpacks/{fabric,neoforge}/`.
- `bun run clean` — `./gradlew clean` (or scoped `:mod:clean` tasks with `--mods`/`--exclude`).
- `./gradlew build` / `./gradlew clean` — Gradle directly (skips the Bun lifecycle).

## chimeric-lib publishing

- `bun run publish:lib` — publish chimeric-lib to maven-local / GitHub Packages for **external**
  consumers (release-only; not needed to develop mods in this repo — see
  [chimeric-lib dependency](../architecture/dependencies.md)).

## Per-task logs

Every Gradle invocation (including `bun run build`, which shells out to `./gradlew`) also writes the
output of each forked-process task (`runGameTest`, `runDatagen`, `runData`, `run*`) to
`logs/<mod-dir>/<sub-project>/<yyyy-MM-dd_HH-mm-ss>.log` (gitignored), e.g.
`logs/artificial-heart/fabric/2026-10-15_20-03-49.log`. One file per sub-project per invocation, with a
`===== :task:path =====` header per task; any failed task (of any type) also gets a `>>> ... FAILED:`
line. Root-project tasks go to `logs/_root/`, a mod's container project to `<mod>/_project/`. Wired
in `gradle/task-logs.gradle` (applied from the root `build.gradle`). Compile/test-task console output
is *not* captured — only exec-style tasks are, because Gradle misattributes output between tasks when
building in parallel; compile/test failures still show up via the `FAILED` line.

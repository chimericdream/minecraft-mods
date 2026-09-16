# Datagen & project-management commands

## Data generation

- `bun run datagen` — runs `runDatagen` (Fabric) and/or `runData` (NeoForge) for every selected
  mod that declares `has_fabric_datagen = true` / `has_neoforge_datagen = true` in its
  `gradle.properties` (see "Keep the gradle.properties datagen flags in sync" below, and
  [MC 26.2 datagen gotchas](../../MC-26.2-NOTES.md) for the component-binding pitfall). Mods that
  declare neither flag are skipped. Supports the same `--mods=<id,id,...>` / `--exclude=<id,id,...>`
  scoping as the build scripts. **Run this before cutting any release** (see
  [Versioning](../releases/versioning.md)) so generated data on disk reflects the latest code, not a
  stale prior run.

### Keep the gradle.properties datagen flags in sync

Each mod's `gradle.properties` declares `has_fabric_datagen = true` and/or `has_neoforge_datagen =
true` when that platform registers a datagen entrypoint (Fabric: a `fabric-datagen` entrypoint in
`fabric.mod.json` pointing at a `ModDataGenerator`; NeoForge: a `data { ... }` run + `GatherDataEvent`
listener). `bun run datagen` reads these flags to decide what to run, so **whenever you add, remove, or
port a mod's data generation** (either platform), update that mod's `gradle.properties` in the same
change — add the flag when datagen is newly wired up, remove it if a platform's datagen is deleted. A
mod with no datagen on either platform has neither flag.

## Testing

- `bun run verify:gametests` — cross-checks every mod's `gametest` source set against its
  `fabric.mod.json` entrypoints and reports any `@GameTest`/`FabricClientGameTest` class that's missing
  from the list (it would silently never run) or listed but no longer exists. See
  [Testing](../../TESTING.md#registration-is-mandatory-and-fails-silently).

## Project management

- `bun run update:settingsgradle` — regenerate `settings.gradle` from the project list.
- `bun run update:projectlist` — regenerate `project-list.json`.
- `bun run copy:accesswideners` — copy access widener files across projects (see
  [access wideners](../gotchas/access-wideners.md) for why this is build-time-only, and never invoke it
  standalone outside `prebuild`/`bun run build`).
- `bun run update:patchoulibooks` — update Patchouli documentation books.

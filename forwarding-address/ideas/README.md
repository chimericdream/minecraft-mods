# Forwarding Address — Ideas

Feature ideas for this mod, from first thought to ready to build. The process is described in
[`docs/FEATURE-WORK.md`](../../docs/FEATURE-WORK.md).

## Identity

> **Forwarding Address makes block and item changes safe for existing worlds.** When a mod renames,
> merges, moves, or drops a block or item, the old ones keep working: placed blocks and held items
> convert to their new form with their contents intact, without DataFixerUpper. It serves mod authors,
> through a small versioned API, and modpack makers, through config files, equally. It never guesses: a
> conversion that can't be done exactly leaves the block alone, logs why, and tries again later. It is
> not a world editor or a general data fixer, and it adds no blocks of its own beyond the stand-ins that
> retired IDs need.

### Boundary with the suite

Forwarding Address owns the conversion machinery. The mods being migrated own their blocks and declare
the migrations: Minekea is the first source, and But What About...? and Cobblicious are the first
destinations. A migration names the old and new blocks by ID, so the two mods don't need to know about
each other. Only the mod that declares a migration depends on Forwarding Address, and a migration whose
target isn't installed is skipped and logged. Forwarding Address requires ChimericLib.

Not decided: entity and biome migrations, and items nested in shulker boxes, bundles, and entity
inventories.

## Active ideas

### Core

- [Block migration framework](core/block-migration-framework.md) — The whole mod: declare old → new
  block and item migrations, convert placed blocks and held items with contents intact, register stubs
  for retired IDs, and dump a mod's blocks for pack makers. Effort XL. Ready; Stage 0–1 (core) in progress.

## Inbox

_Empty._

## Archive

_Empty._

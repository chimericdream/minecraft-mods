### Unreleased changes

#### New Features

* Added Forwarding Address, a library mod that keeps existing worlds working when a mod renames, merges, moves, or retires a block or item. Placed blocks and held items convert to their new form with their contents intact.
* Mod authors declare a migration in code with `Migrations.block(old).to(new).register()` (or `Migrations.item(...)`), with optional mappers for renamed properties, block entities of a different type, hidden container slots, and item components. A migration whose target mod isn't installed is skipped and logged.
* Placed blocks are found when their chunk loads and converted a few at a time on later ticks, so a chunk full of old blocks doesn't stall the game. The limit is `maxConversionsPerTick` (default 256) in `config/forwardingaddress/settings.json`.
* Items convert in a player's inventory and ender chest on login, in containers placed in the world, and as dropped items and item frames when they load.
* A block that can't be converted exactly, such as a container whose new block has nowhere to put its contents, is left alone with an error in the log, and is tried again the next time its chunk loads.
* Retired items get a tooltip saying what they will become and no longer appear in the creative inventory.
* A migration that only renames a block or item (no mappers) also works after the old mod is removed. The old ID is renamed in saved chunks, entities, and player data as they load, so nothing is lost even though the old block or item is no longer registered.
* Modpack makers can declare migrations in JSON files in `config/forwardingaddress/migrations/`: IDs, renamed properties, and mapped property values. Each file is checked on load, and one bad entry is logged and skipped without losing the rest.
* Stub blocks stand in for the blocks of a mod that was removed from a pack. Declared in JSON with a list of properties or a vanilla block to copy them from, they keep a world loadable and convert to the replacement. A stub is only registered if the old mod is not installed.
* `/forwardingaddress dump <namespace>` (operators) writes a starter file of a mod's blocks and properties to `config/forwardingaddress/dumps/`, to fill in before removing the mod.
* Dropped items and item frames now convert when loaded from disk, not just when spawned.

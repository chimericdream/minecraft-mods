### Unreleased changes

#### New Features

* Added Forwarding Address, a library mod that keeps existing worlds working when a mod renames, merges, moves, or retires a block or item. Placed blocks and held items convert to their new form with their contents intact.
* Mod authors declare a migration in code with `Migrations.block(old).to(new).register()` (or `Migrations.item(...)`), with optional mappers for renamed properties, block entities of a different type, hidden container slots, and item components. A migration whose target mod isn't installed is skipped and logged.
* Placed blocks are found when their chunk loads and converted a few at a time on later ticks, so a chunk full of old blocks doesn't stall the game. The limit is `maxConversionsPerTick` (default 256) in `config/forwardingaddress/settings.json`.
* Items convert in a player's inventory and ender chest on login, in containers placed in the world, and as dropped items and item frames when they load.
* A block that can't be converted exactly, such as a container whose new block has nowhere to put its contents, is left alone with an error in the log, and is tried again the next time its chunk loads.
* Retired items get a tooltip saying what they will become and no longer appear in the creative inventory.

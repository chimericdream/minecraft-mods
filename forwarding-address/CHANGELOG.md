### Unreleased changes

#### New Features

* Added Forwarding Address. This library mod keeps existing worlds working when a mod renames, merges, moves, or removes a block or item. Placed blocks and held items change to their new form. Their contents stay.
* Mod authors declare a migration in code with `Migrations.block(old).to(new).register()` or `Migrations.item(...)`. Optional mappers handle renamed properties, different block entity types, hidden container slots, and item components. If the target mod is not installed, the mod skips the migration and logs it.
* Modpack makers declare migrations in JSON files in `config/forwardingaddress/migrations/`. The mod logs and skips a bad entry and keeps the rest.
* Placed blocks convert when their chunk loads, a few at a time, so a chunk full of old blocks does not stall the game. The limit is `maxConversionsPerTick` (default 256) in `config/forwardingaddress/settings.json`.
* Items convert in player inventories and ender chests on login. They also convert in placed containers, dropped items, and item frames when they load.
* If a block cannot be converted exactly, the mod leaves it alone and logs an error. It tries again the next time the chunk loads.
* Retired items show a tooltip with the item they will become. They do not appear in the creative inventory.
* A migration that only renames a block or item also works after the old mod is removed. The mod renames the old ID in saved chunks, entities, and player data as they load.
* Stub blocks replace the blocks of a mod that a pack no longer includes. Declare a stub in JSON. List its properties, or name a vanilla block to copy them from. The mod registers a stub only if the old mod is not installed.
* `/forwardingaddress dump <namespace>` (operators only) writes a starter file to `config/forwardingaddress/dumps/`. The file lists the blocks and properties of a mod. Fill it in before removing the mod.

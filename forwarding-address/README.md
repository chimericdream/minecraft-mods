# Forwarding Address (Fabric/NeoForge)

![Version: 1.0.0](https://img.shields.io/badge/version-1.0.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Keeps existing worlds working when blocks and items are renamed, merged, moved, or removed._

## Introduction

### Minecraft Versions

* 26.2: supported

### Current Features

Forwarding Address is a library. By itself it changes nothing. Install it when another mod or modpack needs it.

* Converts retired blocks and items to their replacements. Facing, waterlogging, other matching block state, and container contents stay.
* Converts items in player inventories, ender chests, placed containers, dropped items, and item frames.
* Spreads block conversion over several ticks (`config/forwardingaddress/settings.json`, `maxConversionsPerTick`).
* Works after the old mod is gone. A migration that only renames a block or item is applied to saved chunks, entities, and player data as they load.
* Modpack makers can declare migrations in JSON files. They can also register plain stand-in "stub" blocks for the blocks of a removed mod (see below).
* Never guesses. If a conversion cannot be exact, the mod leaves the block alone, logs why, and tries again when the chunk next loads.
* Install it on both the client and the server.

### For Mod Authors

Declare migrations while your mod initializes:

```java
Migrations.block(Identifier.fromNamespaceAndPath("oldmod", "oak_trim"))
    .to(Identifier.fromNamespaceAndPath("newmod", "oak_trim"))
    .register();
```

A block migration also converts the item of the block. You can remove a migration that only renames from your mod. The mod renames the old ID in saved data as it loads. If you use a mapper (properties, block entity, or item components), keep the old block and item registered. The mapper needs them as a source. See the `com.chimericdream.forwardingaddress.api` package for the mappers.

### For Modpack Makers

Put migration files in `config/forwardingaddress/migrations/`. Any `*.json` file works. The mod reads them in name order at startup.

```json
{
  "blocks": [
    {
      "from": "oldmod:oak_stairs",
      "to": "newmod:oak_stairs",
      "stub": { "template": "minecraft:oak_stairs" },
      "renameProperties": { "dir": "facing" },
      "mapValues": { "half": { "upper": "top", "lower": "bottom" } }
    }
  ],
  "items": [ { "from": "oldmod:wrench", "to": "newmod:wrench" } ]
}
```

* `from` and `to` are block (or item) IDs. The mod ignores an entry with a blank or missing `to`.
* `renameProperties` and `mapValues` are optional. The mod copies properties with the same name and value on its own.
* `stub` is optional. Add it when the old mod is **removed**. It registers a plain stand-in block and item under the old ID. A world that still has them can then load and convert. A stub is either `"template"` or `"properties"`. `"template"` is an existing block, and the stub copies its properties. `"properties"` is a map of property name to value names, for example `"facing": ["north", "south", "west", "east"]`. The mod matches booleans and consecutive whole numbers to the real property types automatically.
* The mod registers a stub only if no installed mod has that namespace. If you put the old mod back, there is no duplicate. The mod refuses stubs under `minecraft:`.
* An unconverted stub shows the missing-model look and has a "Retired block" name and tooltip. You can break it by hand. It drops its item, which also converts.
* Stubs are registry entries, so **the stub files must be identical on the client and the server**. At startup the log prints a short fingerprint (`stub config xxxxxxxx`). Use it to compare them.
* You cannot stub block entities. The game cannot save the block entity data of a removed mod.

**Before you remove a mod**, run `/forwardingaddress dump <namespace>` (operators only) while the mod is still installed. It writes `config/forwardingaddress/dumps/<namespace>.json`. The file lists every block of that mod with its properties and a blank `to`. Fill in the targets, delete the entries you do not need, and move the rest into the migrations folder. The mod never loads the dumps folder, so a half-finished file cannot change a world.

## Notes for Documentation

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs you find or suggest new features.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

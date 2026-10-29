# Forwarding Address (Fabric/NeoForge)

![Version: 1.0.0](https://img.shields.io/badge/version-1.0.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Keeps existing worlds working when blocks and items are renamed, merged, moved, or removed._

## Introduction

### Minecraft Versions

* 26.2: supported

### Current Features

Forwarding Address is a library: on its own it changes nothing. Install it when another mod or modpack asks for it.

* Converts retired blocks and items to their replacements, keeping facing, waterlogging, and other matching block state, and keeping container contents.
* Converts items in player inventories, ender chests, containers in the world, dropped items, and item frames.
* Spreads block conversion over several ticks (`config/forwardingaddress/settings.json`, `maxConversionsPerTick`).
* Works after the old mod is gone: a migration that only renames a block or item is applied to saved chunks, entities, and player data as they load.
* Modpack makers can declare migrations in JSON files, and can register plain stand-in "stub" blocks for the blocks of a mod they removed (see below).
* Never guesses: a conversion that can't be done exactly leaves the block alone, logs why, and tries again when the chunk next loads.
* Needs to be installed on both the client and the server.

### For Mod Authors

Declare migrations while your mod initializes:

```java
Migrations.block(Identifier.fromNamespaceAndPath("oldmod", "oak_trim"))
    .to(Identifier.fromNamespaceAndPath("newmod", "oak_trim"))
    .register();
```

A block migration converts the block's item too. A migration that only renames can be dropped from your mod entirely: the old ID is renamed in saved data as it loads. If you use a mapper (properties, block entity, or item components), keep the old block and item registered so there is something to map from. See the `com.chimericdream.forwardingaddress.api` package for the mappers.

### For Modpack Makers

Put migration files in `config/forwardingaddress/migrations/` (any `*.json` file; they are read in name order at startup):

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

* `from` and `to` are block (or item) IDs. An entry with a blank or missing `to` is ignored.
* `renameProperties` and `mapValues` are optional; properties with the same name and value are copied on their own.
* `stub` is optional. Add it when the old mod has been **removed**: it registers a plain stand-in block and item under the old ID, so a world that still contains them loads and converts. A stub is either `"template"` (a block that exists, whose properties are copied) or `"properties"` (a map of property name to its value names, for example `"facing": ["north", "south", "west", "east"]`). Booleans and consecutive whole numbers are matched to the real property types automatically.
* A stub is only registered if no installed mod has that namespace, so putting the mod back never causes a duplicate. Stubs under `minecraft:` are refused.
* An unconverted stub shows the missing-model look, has a "Retired block" name and tooltip, breaks by hand, and drops its item, which converts too.
* Stubs are registry entries, so **the stub files must be identical on the client and the server**. The log prints a short fingerprint at startup (`stub config xxxxxxxx`) to compare them.
* Block entities can't be stubbed: a removed mod's block entity data can't be saved.

**Before removing a mod**, run `/forwardingaddress dump <namespace>` (operators only) while it is still installed. It writes `config/forwardingaddress/dumps/<namespace>.json`: every block of that mod with its properties and a blank `to`. Fill in the targets, delete what you don't need, and move the entries into the migrations folder. The dumps folder is never loaded, so a half-finished file can't change a world.

## Notes for Documentation

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs you find or suggest new features.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

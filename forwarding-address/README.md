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
* Never guesses: a conversion that can't be done exactly leaves the block alone, logs why, and tries again when the chunk next loads.
* Needs to be installed on both the client and the server.

### For Mod Authors

Declare migrations while your mod initializes:

```java
Migrations.block(Identifier.fromNamespaceAndPath("oldmod", "oak_trim"))
    .to(Identifier.fromNamespaceAndPath("newmod", "oak_trim"))
    .register();
```

Keep the old block and item registered so old worlds still load. A block migration converts the block's item too. See the `com.chimericdream.forwardingaddress.api` package for the property, block entity, and item mappers.

## Notes for Documentation

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs you find or suggest new features.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

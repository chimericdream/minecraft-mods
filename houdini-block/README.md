# Houdini Block (Fabric/NeoForge)

![Version: 3.0.0-alpha.0](https://img.shields.io/badge/version-3.0.0--alpha.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_A magical block that doesn't trigger block updates when broken!_

## Introduction

Placing or breaking a block normally sends a "block update" to its neighbors — that's what makes redstone
re-evaluate, sand and gravel fall, water flow, observers fire, and so on. The Houdini Block can suppress those
updates, letting it appear and disappear without disturbing anything around it. It's a handy tool for redstone
contraptions, "update suppression" tricks, and clean building.

### Minecraft Versions

* 26.2: supported

### Current Features

* A block that can be placed and broken without notifying neighboring blocks of the change.
* **Configurable behavior via four modes**, shown in the item's tooltip:
  * **Prevent on break** — suppresses updates only when the block is removed.
  * **Prevent on place** — suppresses updates only when the block is placed.
  * **Prevent all** — suppresses updates on both place and break.
  * **Replace block** — suppresses updates whenever the block is placed or replaced, useful for swapping the block
    in and out of an existing structure without side effects.

## Notes for Documentation

The player-facing concept is "update suppression." When documenting in-game, the important part is explaining the
four modes and how to switch between them, since that is the block's entire feature set.

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/houdiniblock/issues) to report any bugs you
find.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to
the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

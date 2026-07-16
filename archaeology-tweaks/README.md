# Archaeology Tweaks (Fabric/NeoForge)

![Version: 4.0.0-alpha.0](https://img.shields.io/badge/version-4.0.0--alpha.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Small, vanilla-friendly tweaks to Minecraft's archaeology system._

## Introduction

Vanilla archaeology only lets you brush two blocks: suspicious sand and suspicious gravel. Archaeology Tweaks
extends that idea to a range of additional materials, so digs can be seeded across far more of the world without
feeling out of place. Every block behaves like its vanilla counterpart — brush it with a brush to slowly reveal
whatever loot has been assigned to it.

### Minecraft Versions

* 26.2: supported
* Earlier versions: see the git history for builds targeting older releases

### Current Features

* Adds "suspicious" brushable variants of common terrain blocks:
  * Suspicious Clay
  * Suspicious Dirt
  * Suspicious Mud
  * Suspicious Packed Mud
  * Suspicious Red Sand
  * Suspicious Rooted Dirt
  * Suspicious Soul Sand
  * Suspicious Soul Soil
* Each block is brushed exactly like vanilla suspicious sand/gravel, revealing its loot piece by piece
* Includes gravity-affected variants that behave like the block they are based on (e.g. sand and red sand fall)
* Loot is driven by loot tables, making it easy for datapacks to customize what each block can contain

## Notes for Documentation

This mod's blocks are intended to be placed by world generation, structures, or datapacks rather than crafted.
When writing in-game or datapack documentation, the key player-facing behavior is simply: _find a suspicious
block, equip a brush, and hold right-click to excavate it._

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/archaeology-tweaks/issues) to report any
bugs you find.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to
the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

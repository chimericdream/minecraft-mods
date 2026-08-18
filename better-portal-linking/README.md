# Better Portal Linking (Fabric/NeoForge)

![Version: 1.0.0](https://img.shields.io/badge/version-1.0.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Gives players more control over how their nether portals link between the Nether and Overworld._

## Introduction

### Minecraft Versions

* 26.2: supported

### Current Features

* Place matching blocks on the four diagonal corners of a portal frame, and portals with matching blocks will link to each other.
* Blocks matching the `betterportallinking:portal_address_blocks` block tag can be used for a portal's address. By default, this tag contains concrete, terracotta, and glazed terracotta, but this can be customized by adding to (or replacing) the tag with a datapack. 
* Corner order doesn't matter, and the same block can be used on more than one corner.
* The portal with the most matching corners wins. Ties go to whichever portal is closer, and any remaining tie is broken the same way every time, so a given portal always sends you to the same place.
* Portals with plain corners, or with no matching portal in range, behave exactly like vanilla.

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs you find or suggest new features.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

Furthermore, this mod's functionality was inspired by the [Corner Portal Linking](https://www.curseforge.com/minecraft/mc-mods/corner-portal-linking) mod, which I have used and enjoyed in the past. However, no code is shared between the two mods. This implementation is entirely separate from the original. 

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

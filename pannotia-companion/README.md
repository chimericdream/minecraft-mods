# Pannotia Companion (Fabric)

![Version: 1.0.0](https://img.shields.io/badge/version-1.0.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Companion mod for the World of Pannotia modpack_

## Introduction

This is the glue that makes World of Pannotia feel like its own world: pack-specific tweaks, loot and recipe
changes, datapack content, and the in-game guide book and lore (powered by Opus). It is written for the modpack
and isn't meant to be used or supported on its own.

### Minecraft Versions

* 26.2: Supported

### Current Features

* **Home, spawn and back commands**: `/pc back`, `/pc home [name]`, `/pc homes`, `/pc homes set [name]`,
  `/pc homes delete <name>`, `/pc homes rename <old> <new>` and `/pc spawn`. `/pc back` returns you to where you were
  before your last home or spawn teleport. The home limit (per dimension), teleport delays and cooldowns are configurable.
* **Wandering trader trades**: every trader also offers a handful of mini block heads and streamer heads. Vanilla
  trades and trades added by other mods are left alone.
* **Extra mob drops**: mob heads, sand from husks, dragon eggs/elytra/heads from the Ender Dragon, and extra shulker
  shells with Looting. These are added on top of existing loot, so other mods' loot changes keep working.
* **Ancient debris** drops netherite scrap instead of itself, unless mined with Silk Touch.
* **Recipes**: mini block stonecutting, stair/slab/dye/coral/glass/concrete conveniences, and a few other pack recipes. Some give the same result as a vanilla recipe from the same ingredients, so the pack needs a mod like Polymorph to choose between them.

## Notes for Documentation

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs you find or suggest new features.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to the developers of the Fabric mod loader and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

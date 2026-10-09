# StackItUp (Fabric/NeoForge)

![Version: 1.0.1](https://img.shields.io/badge/version-1.0.1-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-optional-ff9800?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_AllStackable, reborn for Fabric and NeoForge in the 26.x+ era._

## Introduction

Choose how many of each item fit in a single stack. Stack sizes are set per world with the `/stackitup` command
(or by editing the world's config file), and the mod takes care of the places where larger stacks would normally
cause trouble, such as furnaces, dispensers, anvils, cauldrons, jukeboxes, and horse inventories.

### Minecraft Versions

* 26.2: Supported

### Current Features

* **Per-item stack sizes** — set any item's maximum stack size, or change a whole group of items at once (for
  example, every item that normally stacks to 16).
* **Works with containers** — larger stacks behave correctly in furnaces, dispensers, anvils, cauldrons, and
  more.
* **Safe items stay safe** — tools, armor, and other items that take damage always stay unstackable.
* **Synced to players** — the server's stack sizes are sent to clients so what you see matches what the server
  allows.
* **Global config** — save your stack sizes as a global config and have new worlds start with them.

> The mod doesn't change any stack sizes until you configure them, so a fresh install plays like vanilla.

### Commands

Run `/stackitup help` in game for the full list. The most useful ones:

* `/stackitup set <item> <count>` — set an item's stack size.
* `/stackitup set hand` — set the size for the item you're holding.
* `/stackitup show all` — list every item you've changed.
* `/stackitup reset <item>` / `/stackitup reset all` — put items back to vanilla.

Commands need operator permission by default. To let everyone use them, change `permissionLevel` from 4 to 0 in
the world's config file.

## Notes for Documentation

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs you find or suggest new features.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

Furthermore, this mod began its life as a fork of the amazing [AllStackable](https://www.curseforge.com/minecraft/mc-mods/all-stackable) mod, which I previously contributed to in a small way. Props to KrisCris for such a great mod!

## License

This mod is released under the LGPLv3 license. [The full text of the license can be found here.](./LICENSE)

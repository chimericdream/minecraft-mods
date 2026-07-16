# Shulker Stuff (Fabric/NeoForge)

![Version: 4.0.0-alpha.0](https://img.shields.io/badge/version-4.0.0--alpha.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Quality of life tweaks, enhancements, and other changes for shulker boxes._

## Introduction

Shulker boxes are one of the best storage tools in the game, but they're a little bare-bones. Shulker Stuff adds
enchantments, an upgrade, a dyeing station, and rendering tweaks to make them more powerful and more pleasant to
use.

### Minecraft Versions

* 26.2: supported

## Current Features

### Shulker box enchantments

New enchantments that apply to shulker boxes (found in the enchanting table and as treasure/tradeable
enchantments):

* **Refill** *(max level I)* — helps keep a stack topped up from the box's contents.
* **Vacuum** *(max level II)* — pulls in nearby items.
* **Void** *(max level I)* — discards unwanted items.

> **Deep Storage** appears in the mod's assets as a planned enchantment but is not currently registered.

### Shulker Dyeing Station

A dedicated block for **recoloring shulker boxes** without wasting dye on a crafting-grid re-dye. Comes with its own
GUI.

### Netherite Plating

A **Plated Shulker Upgrade** smithing template lets you apply **Netherite Plating** to a shulker box, marked in the
tooltip as *"Netherite Plated."* Like other netherite gear, this is intended to make the box more durable/resistant.

### Rendering & QoL tweaks

* Custom rendering so upgraded and dyed shulker boxes display their state correctly, both as items and as placed
  blocks.
* Inventory and player-interaction tweaks supporting the enchantment behaviors above.

## ⚠️ Known Issue (functionality review)

The in-game config screen (`ShulkerStuffConfig`) currently exposes a long list of drop-chance options
(*Ancient Shell chance*, *Wagyu Beef chance*, *Budding Cactus chance*, and so on) that belong to the **Miniblock
Merchants** mod, not to shulker boxes. These appear to have been copied in by mistake and do not correspond to any
Shulker Stuff feature. They should be replaced with real options (e.g. enchantment tuning) or removed before
release.

## Notes for Documentation

For in-game documentation, the three player-facing systems are: **enchantments** (Refill, Vacuum, Void), the
**Shulker Dyeing Station**, and **Netherite Plating** via the smithing template.

## Issues & Suggestions

Please use the GitHub issue tracker to report any bugs you find.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to
the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

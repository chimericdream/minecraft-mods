# Enchanting Fixes (Fabric/NeoForge)

![Version: 1.0.0-beta.0](https://img.shields.io/badge/version-1.0.0--beta.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Fixes for enchanting and enchanted books._

## Introduction

### Minecraft Versions

* 26.2: supported

### Current Features

* **Book repair**: `/enchfix fixbooks` removes the repair cost from the enchanted books in your inventory. An anvil or a grindstone can add a repair cost to a book. Books with the same enchantments but a different repair cost do not stack. The command changes only plain books: books that store known enchantments and have no other changes, such as a name, lore, or custom data. Mods and data packs can use enchanted books for other purposes. The command does not change those books.
* **Maximum enchantment levels**: set the maximum level of each enchantment in the config. A separate setting keeps random enchantments, such as those from chest loot and villager trades, near vanilla levels. By default, each vanilla enchantment with more than one level has a maximum of X, or its hard cap if that is lower. Enchantments not in the list keep their usual maximum, or use the default maximum level. A separate setting controls whether the default also applies to single-level enchantments, such as Silk Touch and Mending.
* **No anvil limit**: the anvil does not refuse a change that costs 40 levels or more ("Too Expensive!"). You must still have the levels to pay the cost. A setting restores the vanilla limit.
* **Compatible enchantments**: a setting that lets you put enchantments that work correctly together on the same item, for example all the protection enchantments. The setting is off by default. See [Compatible enchantments](#compatible-enchantments).
* **Advancements**: a tab of advancements for very high enchantment levels.
* **Scaling Enchantments data pack**: an optional data pack. With it, high levels of some enchantments continue to give more benefit, up to a limit. The pack is off by default.
* **In-game guide**: craft a book and an enchanted book to get a guide to each feature and setting. Any enchanted book works. The recipe uses the enchanted book. You can also use `/opus give enchantingfixes:guide`. The guide is optional and needs the [Opus](../opus) mod, version 1.2.0 or newer.

### Maximum levels

The maximum level changes these parts of the game:

* The anvil: two items with the same enchantment at the same level can give a result above the usual maximum. Without the anvil limit, you can make high levels in survival.
* `/enchant` accepts levels up to the new maximum.
* The creative inventory shows an enchanted book for each level, up to the new maximum.
* Random enchantments can have higher levels. The Random Enchantment Levels setting controls how much higher (see below).

#### Random enchantment levels

Random enchantments come from chest loot, villager trades, piglin bartering, the enchanting table, and mob equipment. The Random Enchantment Levels setting sets their maximum level:

| Mode | Maximum level of random enchantments |
|---|---|
| Off | The vanilla maximum level, for example Sharpness V. |
| Balanced (default) | 2 levels above the vanilla maximum level, for example Sharpness VII. |
| Insane | The maximum level from the config. Each level has the same chance. If Looting has a maximum of L, a chest can have a Looting L book. |

The maximum level from the config applies in all modes. The enchanting table and some loot pick the level from the enchanting power. For these, the level does not go much above the vanilla maximum, even in Insane mode.

Some enchantments have a hard cap. Above the cap, a higher level gives no further benefit or breaks the enchantment. The config cannot set a maximum level above the cap:

| Enchantment | Cap | Reason |
|---|---|---|
| Protection | XX | All protection enchantments share a limit of 20 points. |
| Fire Protection, Blast Protection, Projectile Protection | X | The same limit of 20 points. |
| Feather Falling | VII | The same limit of 20 points. |
| Depth Strider | III | At III, you move in water at the same speed as on land. |
| Swift Sneak | V | At V, you sneak at the same speed as you walk. |
| Quick Charge | V | At V, the crossbow loads immediately. |
| Breach | VI | At VI, armor gives no protection. |
| Knockback | L | At L, a hit sends the target about 200 blocks. Above L, the target goes so far that it stops moving. |
| Thorns | VII | At VII, Thorns activates on each hit. |
| Frost Walker | XIV | At XIV, the radius is at its limit of 16 blocks. |
| Efficiency | XXXII | At XXXII, the mining speed is at its limit. |
| Aqua Affinity | XXV | At XXV, the mining speed in water is at its limit. |
| Luck of the Sea | LXXXV | At LXXXV, all catches are treasure. |
| Lure | V | At VI and higher, no fish bites. |

A data pack can set a higher maximum level for one of these enchantments. The cap then does not decrease that level. The Scaling Enchantments data pack removes some of these caps (see below).

Most single-level enchantments have the same effect at all levels. Aqua Affinity and Multishot get stronger at higher levels.

Minecraft has level numerals only up to X. Use [Enchantment Numbers Fix](../enchantment-numbers-fix) to show higher levels correctly.

On a server, the server config applies. When you join, the server sends its settings to your game. The tooltips and the creative inventory then show the server's levels.

### Compatible enchantments

Turn on Remove Incompatibilities to combine these enchantments on the same item:

| Enchantments | Item |
|---|---|
| Protection, Fire Protection, Blast Protection, Projectile Protection | Armor |
| Depth Strider, Frost Walker | Boots |
| Sharpness, Smite, Bane of Arthropods | Weapons |
| Smite, Bane of Arthropods, Density, Breach | Maces |
| Infinity, Mending | Bows |
| Multishot, Piercing | Crossbows |

These enchantments stay incompatible, because one stops the other from working:

* Silk Touch and Fortune: with Silk Touch, the block drops itself, so Fortune has no effect.
* Riptide with Loyalty or Channeling: you cannot throw a trident that has Riptide.

The setting changes the anvil, the enchanting table, `/enchant`, and enchanted loot. The setting removes the vanilla exclusive sets `armor`, `boots`, `bow`, `crossbow`, and `damage`. An enchantment from another mod in one of these sets also becomes compatible. Other incompatibilities from mods and data packs do not change.

All protection enchantments share a limit of 20 points. More protection enchantments on one piece of armor do not increase that limit. The Scaling Enchantments data pack changes the limit.

On a server, the server config applies.

### Scaling Enchantments data pack

The mod includes the "Enchanting Fixes: Scaling Enchantments" data pack. The pack is off by default. To use it, turn it on in the Data Packs screen when you create a world. For an existing world, use `/datapack enable`, then restart the world. The pack changes the enchantment data. Minecraft loads that data only when the world starts.

From level I to the usual maximum level, each enchantment works as in vanilla. Above that level, the pack changes the formula:

| Enchantment | With the pack | Cap |
|---|---|---|
| Protection, Fire Protection, Blast Protection, Projectile Protection, Feather Falling | Up to 20 points (80% less damage), the same as vanilla. Above 20 points, each point decreases damage less than the point before it. The decrease gets closer to the Protection Limit % setting (default 95%), but does not reach it. For example, 40 points give 87.5% less damage and 80 points give 91.25% less damage. | none |
| Lure | I to V are the same as vanilla. Above V, the wait gets closer to 29 seconds less than normal, but does not reach it. Fish always bite. | none |
| Quick Charge | Above III, the charge time is 1.5 seconds divided by the level. At XXX, the crossbow loads immediately. | XXX |
| Breach | Above IV, the part of the armor that Breach ignores gets closer to 100% of full armor, but does not reach it. | none |
| Thorns | Above III, the activation chance gets closer to 100%, but does not reach it. The damage increases with each level and has no limit. Each activation costs 1 durability, not 2. | none |
| Unbreaking (armor only) | Above III, the chance to prevent durability damage gets closer to 100%, the same as on tools. In vanilla, it stops near 40%. For example, X gives 63%. | none |

The other caps in the table above do not change.

## Notes for Documentation

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs you find or suggest new features.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

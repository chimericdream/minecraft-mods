---
title: Settings
icon: item:minecraft:comparator
tags: [settings]
summary: All the settings and where to change them.
---
Open the settings from the mods list. The mod saves them in the file `enchantingfixes.json5` in your config folder.

| Setting | Default | What it does |
| --- | --- | --- |
| Maximum Level / Enchantment | Each vanilla enchantment with more than one level: 10, or its hard cap if lower | The maximum level of each enchantment in the list. Write one entry on each line, for example `minecraft:sharpness=10`. |
| Use Default Maximum Level | Off | On: the enchantments that are not in the list use the default maximum level. |
| Default Maximum Level | 10 | The maximum level of the enchantments that are not in the list. A value lower than the usual maximum decreases the maximum. |
| Random Enchantment Levels | Balanced | The maximum level of random enchantments, such as those from chest loot, villager trades, and the enchanting table. Off: the vanilla maximum. Balanced: 2 levels above the vanilla maximum. Insane: the maximum level from the config. See [Maximum levels](02-max-levels.md). |
| Default Applies to Single-Level Enchantments | Off | On: single-level enchantments, such as Silk Touch, also use the default maximum level. |
| Remove "Too Expensive!" Limit | On | The anvil does not refuse a change that costs 40 levels or more. A stack of more than one item keeps the limit. |
| Remove Incompatibilities | Off | On: you can put enchantments that work correctly together on the same item, for example all the protection enchantments. See [Compatible enchantments](06-compatibility.md). |
| Protection Limit % | 95 | The limit of the damage decrease from protection enchantments, from 80 to 100. Applies only with the [Scaling Enchantments](03-scaling-enchantments.md) data pack. 80 keeps the vanilla limit. |

Each level must be from 1 to 255. The [hard caps](02-max-levels.md) also apply. When you save, the mod removes each
line in the list that is not in the correct form.

> [!NOTE]
> On a server, the server settings apply. When you join, the server sends its settings to your game.
> The tooltips and the creative inventory then show the server's levels.

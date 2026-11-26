---
title: Maximum levels
icon: item:minecraft:enchanting_table
tags: [features, settings]
summary: Set the maximum level of each enchantment.
---
Use the [settings](04-settings.md) to set the maximum level of each enchantment, from 1 to 255. By default, the list
sets each vanilla enchantment with more than one level to level X. If its hard cap is lower, the list uses the cap. Enchantments not in the list,
such as those from other mods, keep their usual maximum level. You can set a default maximum level for them.

## What changes

* **Anvil:** two items with the same enchantment at the same level can give a level above the usual maximum.
  The anvil has no limit of 40 levels, so you can make high levels in survival. You must still have the levels to
  pay the cost.
* **Commands:** `/enchant` accepts levels up to the new maximum.
* **Creative inventory:** it shows an enchanted book for each level, up to the new maximum.
* **Random enchantments:** they can have higher levels. The Random Enchantment Levels setting controls how much
  higher.

## Random enchantments

Random enchantments come from chest loot, villager trades, piglin bartering, the enchanting table, and mob
equipment. The Random Enchantment Levels [setting](04-settings.md) sets their maximum level:

| Mode | Maximum level |
| --- | --- |
| Off | The vanilla maximum, for example Sharpness V. |
| Balanced (default) | 2 levels above the vanilla maximum, for example Sharpness VII. |
| Insane | The maximum level from the config. If Looting has a maximum of L, a chest can have a Looting L book. |

The enchanting table picks the level from the enchanting power. At the table, the level does not go much above the
vanilla maximum, even in Insane mode.

## Single-level enchantments

Silk Touch, Mending, and other single-level enchantments are not in the default list. They stay at level 1, unless
you put them in the list or turn on the related setting. Most do the same thing at all levels. Aqua Affinity and Multishot become stronger at
higher levels.

## Hard caps

Some enchantments have a hard cap. Above the cap, a higher level gives no more benefit or breaks the enchantment.
The settings cannot set a maximum level above the cap.

| Enchantment | Cap | Reason |
| --- | --- | --- |
| Protection | XX | All protection enchantments share a limit of 20 points. |
| Fire, Blast, and Projectile Protection | X | The same limit of 20 points. |
| Feather Falling | VII | The same limit of 20 points. |
| Depth Strider | III | You move in water at the same speed as on land. |
| Swift Sneak | V | You sneak at the same speed as you walk. |
| Quick Charge | V | The crossbow loads immediately. |
| Breach | VI | Armor gives no protection. |
| Knockback | L | A hit sends the target about 200 blocks. |
| Thorns | VII | Thorns activates on each hit. |
| Frost Walker | XIV | The radius is at its limit of 16 blocks. |
| Efficiency | XXXII | The mining speed is at its limit. |
| Aqua Affinity | XXV | The mining speed in water is at its limit. |
| Luck of the Sea | LXXXV | All catches are treasure. |
| Lure | V | At VI and higher, no fish bites. |

The [Scaling Enchantments](03-scaling-enchantments.md) data pack removes some of these caps.

> [!NOTE]
> Minecraft shows level numerals only up to X. Higher levels show as a text key, such as
> `enchantment.level.11`. The Enchantment Numbers Fix mod shows them correctly.

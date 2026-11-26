---
title: Compatible Enchantments
icon: item:minecraft:diamond_chestplate
tags: [compatibility, settings]
summary: Put more enchantments on the same item.
---
In vanilla, some enchantments are incompatible. For example, a chestplate can have Protection or Fire Protection,
but not both. Turn on Remove Incompatibilities in the [settings](04-settings.md) to combine these enchantments:

| Enchantments | Item |
| --- | --- |
| Protection, Fire Protection, Blast Protection, Projectile Protection | Armor |
| Depth Strider, Frost Walker | Boots |
| Sharpness, Smite, Bane of Arthropods | Weapons |
| Smite, Bane of Arthropods, Density, Breach | Maces |
| Infinity, Mending | Bows |
| Multishot, Piercing | Crossbows |

These enchantments stay incompatible, because one stops the other from working:

* Silk Touch and Fortune: with Silk Touch, the block drops itself, so Fortune has no effect.
* Riptide with Loyalty or Channeling: you cannot throw a trident that has Riptide.

The setting changes the anvil, the enchanting table, `/enchant`, and enchanted loot. Incompatibilities from other
mods and data packs do not change.

> [!NOTE]
> All protection enchantments share a limit of 20 points. More protection enchantments on one piece of armor do not
> increase that limit. The [Scaling Enchantments](03-scaling-enchantments.md) data pack changes the limit.

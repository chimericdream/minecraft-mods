---
title: Multi-huppers
icon: item:hopperxtreme:multi_hupper
tags: [huppers, multi-hoppers, tiers]
---
A Multi-Hupper works like a [Multi-Hopper](../02-multi-hoppers/index.md), but its vertical output points up instead of
down. Items take turns between the sides you switch on with a [wrench](../06-wrench.md).

| Multi-hupper | Cooldown |
|:-------------|---------:|
| ![](item:hopperxtreme:multi_hupper) Multi-Hupper | 8 ticks (2.5 items per second) |
| ![](item:hopperxtreme:golden_multi_hupper) Golden Multi-Hupper | 4 ticks (5 items per second) |
| ![](item:hopperxtreme:diamond_multi_hupper) Diamond Multi-Hupper | 2 ticks (10 items per second) |
| ![](item:hopperxtreme:netherite_multi_hupper) Netherite Multi-Hupper | 1 tick (20 items per second) |
| ![](item:hopperxtreme:nether_star_multi_hupper) Nether Star Multi-Hupper | 1 tick (up to 320 items per second) |

Four huppers make a Multi-Hupper:

```recipe
type: crafting_shapeless
ingredients: [hopperxtreme:hupper, hopperxtreme:hupper, hopperxtreme:hupper, hopperxtreme:hupper]
result: hopperxtreme:multi_hupper
```

The higher tiers follow the same pattern: four huppers of the same tier make the matching multi-hupper. The Golden and
Diamond ones can also be made by upgrading the multi-hupper below them with four gold ingots or four diamonds.

```recipe
type: crafting_shapeless
ingredients: [hopperxtreme:multi_hupper, gold_ingot, gold_ingot, gold_ingot, gold_ingot]
result: hopperxtreme:golden_multi_hupper
```

Put a multi-hupper alone in the grid to get four huppers of its tier back:

```recipe
type: crafting_shapeless
ingredients: [hopperxtreme:multi_hupper]
result: hopperxtreme:hupper
count: 4
```

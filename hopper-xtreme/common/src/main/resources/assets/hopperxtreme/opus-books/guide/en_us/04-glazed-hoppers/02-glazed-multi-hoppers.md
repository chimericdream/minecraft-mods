---
title: Glazed multi-hoppers
icon: item:hopperxtreme:glazed_multi_hopper
tags: [glazed, multi-hoppers, tiers]
---
Glazed Multi-Hoppers are the glazed version of the [multi-hoppers](../02-multi-hoppers/index.md). They come in five
tiers, with the same speeds as the regular multi-hoppers. Like other glazed hoppers they hold one stack and let items
drop out instead of passing them into a container, taking turns between the sides you switch on with a
[wrench](../06-wrench.md).

| Glazed multi-hopper | Cooldown |
|:--------------------|---------:|
| ![](item:hopperxtreme:glazed_multi_hopper) Glazed Multi-Hopper | 8 ticks (2.5 items per second) |
| ![](item:hopperxtreme:glazed_golden_multi_hopper) Glazed Golden Multi-Hopper | 4 ticks (5 items per second) |
| ![](item:hopperxtreme:glazed_diamond_multi_hopper) Glazed Diamond Multi-Hopper | 2 ticks (10 items per second) |
| ![](item:hopperxtreme:glazed_netherite_multi_hopper) Glazed Netherite Multi-Hopper | 1 tick (20 items per second) |
| ![](item:hopperxtreme:glazed_nether_star_multi_hopper) Glazed Nether Star Multi-Hopper | 1 tick (up to 320 items per second) |

Four glazed hoppers of the same tier make the matching glazed multi-hopper:

```recipe
type: crafting_shapeless
ingredients: [hopperxtreme:glazed_hopper, hopperxtreme:glazed_hopper, hopperxtreme:glazed_hopper, hopperxtreme:glazed_hopper]
result: hopperxtreme:glazed_multi_hopper
```

The Golden and Diamond ones can also be made by upgrading the glazed multi-hopper below them with four gold ingots or
four diamonds:

```recipe
type: crafting_shapeless
ingredients: [hopperxtreme:glazed_multi_hopper, gold_ingot, gold_ingot, gold_ingot, gold_ingot]
result: hopperxtreme:glazed_golden_multi_hopper
```

Put any glazed multi-hopper alone in the crafting grid to get four glazed hoppers of its tier back:

```recipe
type: crafting_shapeless
ingredients: [hopperxtreme:glazed_multi_hopper]
result: hopperxtreme:glazed_hopper
count: 4
```

---
title: Hupper
icon: item:hopperxtreme:hupper
tags: [huppers, tiers]
---
A Hupper moves items upward. Every hopper tier has a hupper version, and each one moves items at the same speed as
the hopper it came from.

**Cooldown:** 8 game ticks (2.5 items per second)

Put a hopper in the crafting grid to turn it into a hupper:

```recipe
type: crafting_shapeless
ingredients: [hopper]
result: hopperxtreme:hupper
```

Put a hupper in the grid to get the hopper back:

```recipe
type: crafting_shapeless
ingredients: [hopperxtreme:hupper]
result: hopper
```

## Every tier

The same one-item recipe works for each upgraded hopper. Put the hopper in the grid to get its hupper, and the hupper in
the grid to get the hopper back.

| Hupper | Cooldown |
|:-------|---------:|
| ![](item:hopperxtreme:hupper) Hupper | 8 ticks (2.5 items per second) |
| ![](item:hopperxtreme:honeyed_hupper) Honeyed Hupper | 20 ticks (1 item per second) |
| ![](item:hopperxtreme:copper_hupper) Copper Hupper | 8 ticks (2.5 items per second) |
| ![](item:hopperxtreme:golden_hupper) Golden Hupper | 4 ticks (5 items per second) |
| ![](item:hopperxtreme:diamond_hupper) Diamond Hupper | 2 ticks (10 items per second) |
| ![](item:hopperxtreme:netherite_hupper) Netherite Hupper | 1 tick (20 items per second) |
| ![](item:hopperxtreme:nether_star_hupper) Nether Star Hupper | 1 tick (up to 320 items per second) |

For example, a Diamond Hupper:

```recipe
type: crafting_shapeless
ingredients: [hopperxtreme:diamond_hopper]
result: hopperxtreme:diamond_hupper
```

The Copper Hupper, like the [Copper Hopper](../01-upgraded-hoppers/02-copper-hopper.md), cannot be locked by redstone.

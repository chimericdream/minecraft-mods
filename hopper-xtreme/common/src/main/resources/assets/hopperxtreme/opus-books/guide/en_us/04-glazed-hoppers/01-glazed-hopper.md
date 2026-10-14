---
title: Glazed Hopper
icon: item:hopperxtreme:glazed_hopper
tags: [glazed, tiers]
---
The Glazed Hopper is the basic glazed hopper.

**Cooldown:** 8 game ticks (2.5 items per second)

You can build one from scratch:

```recipe
pattern: ["IGI", "ICI", " I "]
key:
  I: iron_ingot
  G: gray_glazed_terracotta
  C: chest
result: hopperxtreme:glazed_hopper
```

Or glaze an ordinary hopper:

```recipe
type: crafting_shapeless
ingredients: [hopper, gray_glazed_terracotta]
result: hopperxtreme:glazed_hopper
```

## Other tiers

Every other tier can be made two ways: glaze the matching [upgraded hopper](../01-upgraded-hoppers/index.md) with a gray
glazed terracotta, or upgrade a glazed hopper the same way you would a regular one.

```recipe
type: crafting_shapeless
ingredients: [hopperxtreme:golden_hopper, gray_glazed_terracotta]
result: hopperxtreme:glazed_golden_hopper
```

```recipe
type: crafting_shapeless
ingredients: [hopperxtreme:glazed_hopper, gold_ingot]
result: hopperxtreme:glazed_golden_hopper
```

The Diamond tier works the same way, with a Diamond Hopper or a Glazed Hopper plus a diamond.

### Honey Glazed Hopper

The slow, 20-tick glazed hopper. It may sound tasty, but it's not edible.

```recipe
type: crafting_shapeless
ingredients: [hopperxtreme:glazed_hopper, honeycomb]
result: hopperxtreme:honey_glazed_hopper
```

```recipe
type: crafting_shapeless
ingredients: [hopperxtreme:honeyed_hopper, gray_glazed_terracotta]
result: hopperxtreme:honey_glazed_hopper
```

### Glazed Netherite Hopper

Like the regular Netherite Hopper, this one is made at a smithing table and needs a netherite upgrade smithing template.
You can also glaze a Netherite Hopper.

```recipe
type: smithing
template: netherite_upgrade_smithing_template
base: hopperxtreme:glazed_diamond_hopper
addition: netherite_ingot
result: hopperxtreme:glazed_netherite_hopper
```

```recipe
type: crafting_shapeless
ingredients: [hopperxtreme:netherite_hopper, gray_glazed_terracotta]
result: hopperxtreme:glazed_netherite_hopper
```

### Glazed Nether Star Hopper

```recipe
type: crafting_shapeless
ingredients: [hopperxtreme:glazed_netherite_hopper, nether_star]
result: hopperxtreme:glazed_nether_star_hopper
```

```recipe
type: crafting_shapeless
ingredients: [hopperxtreme:nether_star_hopper, gray_glazed_terracotta]
result: hopperxtreme:glazed_nether_star_hopper
```

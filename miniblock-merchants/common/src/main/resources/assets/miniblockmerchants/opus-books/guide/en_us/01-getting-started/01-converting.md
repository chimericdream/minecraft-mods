---
title: Converting a villager
icon: item:miniblockmerchants:crystal_phial
tags: [basics, villagers]
summary: Turn an unemployed villager into a miniblock merchant.
---
Each profession has its own conversion item, such as the ![Crystal Phial](item:miniblockmerchants:crystal_phial)
Crystal Phial for the Alchemist. Hold it and right-click a villager to convert them.

```item
id: miniblockmerchants:crystal_phial
label: Crystal Phial
```

## What the villager needs

* They must be an **adult**. A baby villager just shakes their head and nothing happens.
* They must have **no profession**. A villager who already has a job, or a nitwit, cannot be converted.
* They must not be trading or asleep.

## What happens

* The villager tells you about their new job in chat, in their own words.
* The conversion item is used up (not in Creative mode).
* The villager becomes a **master** (the top level), so every trade is unlocked right away.
* They stay put: converted villagers will not despawn.

Merchants do not need a workstation. They never look for a job block, and they keep their profession.

> [!NOTE]
> Convert the villager first, then trade. You cannot change a merchant back to another profession with a
> conversion item.

If you are not sure where to get an item, see [conversion items](../02-conversion-items/index.md).

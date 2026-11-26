---
title: Scaling Enchantments
icon: item:minecraft:experience_bottle
tags: [features]
summary: An optional data pack that makes high levels of some enchantments useful.
---
The mod includes the "Enchanting Fixes: Scaling Enchantments" data pack. With it, high levels of some enchantments
continue to give more benefit. The pack is off by default.

## Turn it on

* **New world:** turn on the pack in the Data Packs screen when you create the world.
* **Existing world:** use `/datapack enable`, then restart the world. Minecraft loads the enchantment data only when
  the world starts.

## What changes

From level I to the usual maximum level, each enchantment works as in vanilla. Above that level, the pack changes
the formula.

| Enchantment | With the pack | Cap |
| --- | --- | --- |
| All protection enchantments, Feather Falling | Above 20 points, each point decreases damage less than the point before it. The decrease gets closer to the Protection Limit % setting (default 95%), but does not reach it. For example, 40 points give 87.5% less damage. | none |
| Lure | Above V, the wait gets closer to 29 seconds less than normal, but does not reach it. Fish always bite. | none |
| Quick Charge | Above III, the charge time is 1.5 seconds divided by the level. At XXX, the crossbow loads immediately. | XXX |
| Breach | Above IV, the part of the armor that Breach ignores gets closer to 100%, but does not reach it. | none |
| Thorns | Above III, the activation chance gets closer to 100%, but does not reach it. The damage increases with each level. Each activation costs 1 durability, not 2. | none |
| Unbreaking (armor only) | Above III, the chance to prevent durability damage gets closer to 100%, the same as on tools. For example, X gives 63%. | none |

The other [hard caps](02-max-levels.md) do not change.

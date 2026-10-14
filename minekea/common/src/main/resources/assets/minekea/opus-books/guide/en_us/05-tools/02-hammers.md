---
title: Hammers
icon: item:minekea:tools/hammers/diamond
tags: [tools, building]
summary: Place random blocks from your hotbar to add texture to a build.
---
Hammers are a must-have for the discerning builder. A hammer places a random block from your hotbar each time you
right-click, which is perfect for adding texture to a build. Each tier uses more slots than the one before.

| Hammer | Slots |
|:-------|------:|
| Stone | 4 |
| Copper | 4 |
| Iron | 5 |
| Gold | 6 |
| Diamond | 7 |
| Netherite | 8 |

## Using a hammer

Put the blocks you want to the **right** of the hammer in your hotbar, then right-click all over your build. Only the
slots to the right of the hammer are used, up to the hammer's limit. Using a hammer wears it down.

![Placing random blocks](texture:minekea:gui/patchouli/hammer-random-blocks){width=200}

Control the mix with stack sizes. A stack of 48 is picked three times as often as a stack of 16.

![Blocks with different stack sizes](texture:minekea:gui/patchouli/hammer-ratio-blocks){width=120}
![The same hotbar](texture:minekea:gui/patchouli/hammer-ratio-hotbar){width=120}

## Recipes

```recipe
pattern: ["ISI", " S ", " S "]
key:
  I: iron_ingot
  S: stick
result: minekea:tools/hammers/iron
```

Other hammers use the same shape with a different head: cobblestone, cobbled deepslate or blackstone for stone, a copper
ingot, a gold ingot, a diamond or a netherite ingot.

```recipe
type: smithing
template: netherite_upgrade_smithing_template
base: minekea:tools/hammers/diamond
addition: netherite_ingot
result: minekea:tools/hammers/netherite
```

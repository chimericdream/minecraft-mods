---
title: Shulker Dyeing Station
icon: item:shulkerstuff:dye_station
tags: [dyeing]
summary: Color the lid and base of a shulker box separately.
---
The Shulker Dyeing Station recolors a shulker box. The lid and the base are dyed on their own, so you can make a
two-colored box.

```recipe
pattern: ["DSD", "BCB", "BRB"]
key:
  D: [white_dye, orange_dye, magenta_dye, light_blue_dye, yellow_dye, lime_dye, pink_dye, gray_dye, light_gray_dye, cyan_dye, purple_dye, blue_dye, brown_dye, green_dye, red_dye, black_dye]
  S: shulker_shell
  B: polished_blackstone
  C: crafting_table
  R: brush
result: shulkerstuff:dye_station
```

## Using it

Open the station and place a shulker box in the middle slot. Then add dyes:

* The **three slots above** the box color the **lid**.
* The **three slots below** the box color the **base**.

The result appears on the right. Take it and one of each input is used up.

Only a plain shulker box (the purple one) goes in the middle. The box can be enchanted or
[plated](03-netherite-plating.md), and the result keeps all of that.

## Mixing colors

Several dyes in the same half are blended together. Dyeing a box that is already custom colored blends the new dyes
with the color it has now. A half with no dyes keeps its current color.

To go back to the default color, put the box in with no dyes at all.

---
title: Block painter
icon: item:minekea:tools/painter
tags: [tools, colour]
summary: Change the colour of coloured blocks without breaking them.
---
Placed some coloured blocks and changed your mind? Do not tear down your build. Load the painter with dye and repaint.

```recipe
pattern: [" NW", " SN", "S  "]
key:
  N: iron_nugget
  S: stick
  W: [white_wool, orange_wool, magenta_wool, light_blue_wool, yellow_wool, lime_wool, pink_wool, gray_wool, light_gray_wool, cyan_wool, purple_wool, blue_wool, brown_wool, green_wool, red_wool, black_wool]
result: minekea:tools/painter
```

Any colour of wool works.

## Loading dye

Sneak and right-click to open the painter. Fill it with up to a stack of each dye colour.

![The empty painter screen](texture:minekea:gui/patchouli/painter-inventory){width=120}
![The full painter screen](texture:minekea:gui/patchouli/painter-inventory-full){width=120}

## Painting

Press **=** (the default key; it can be changed in Controls) to cycle through your colours. The durability bar shows how
much of the current colour is left. Then right-click a block to repaint it. Each block uses up one dye. In creative
mode no dye is needed.

![Before painting](texture:minekea:gui/patchouli/painter-before){width=120}
![After painting](texture:minekea:gui/patchouli/painter-after){width=120}

The painter recolours vanilla wool, concrete, concrete powder, terracotta and stained glass.

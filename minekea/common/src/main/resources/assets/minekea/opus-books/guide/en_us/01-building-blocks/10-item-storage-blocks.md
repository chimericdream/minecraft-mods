---
title: Item storage blocks
icon: item:minekea:storage/compressed/ender_pearl
tags: [building, storage]
summary: Craft items into solid blocks for compact storage or decoration.
---
Some items take up a lot of space in your inventory. Craft nine of them into a solid block for better storage, or just
as a decoration. Put the block in the crafting grid on its own to get the items back.

```recipe
pattern: ["###", "###", "###"]
key:
  "#": ender_pearl
result: minekea:storage/compressed/ender_pearl
```

```recipe
pattern: ["###", "###", "###"]
key:
  "#": sugar
result: minekea:storage/compressed/sugar
```

## Dye blocks

Every dye can be packed into a gooey block that behaves much like honey. Entities are slowed down and cannot jump on it.
It cannot be used in redstone contraptions.

```recipe
pattern: ["###", "###", "###"]
key:
  "#": white_dye
result: minekea:storage/dyes/white
```

## Egg crates and nugget pouches

Nine eggs (or blue or brown eggs) make an egg crate. A 2x2 of copper, gold or iron nuggets makes a nugget pouch.

```recipe
pattern: ["###", "###", "###"]
key:
  "#": egg
result: minekea:storage/egg_crate
```

```recipe
pattern: ["##", "##"]
key:
  "#": gold_nugget
result: minekea:currency/gold_nugget_bag
```

## Bagged blocks

Some blocks are drawn inside a brown sack when placed. Right-click the block with **shears** to remove the sack, and
with **leather** to put the sack back on.

![Bagged blocks](texture:minekea:gui/patchouli/bagged-blocks){width=120}
![Unbagged blocks](texture:minekea:gui/patchouli/unbagged-blocks){width=120}
![Re-bagged blocks](texture:minekea:gui/patchouli/re-bagged-blocks){width=120}

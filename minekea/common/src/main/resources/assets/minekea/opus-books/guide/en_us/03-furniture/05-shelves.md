---
title: Shelves
icon: item:minekea:furniture/shelves/supported/oak
tags: [furniture, storage]
summary: Wall-mounted shelves that display what is on them.
---
Shelves display items and store a small number of blocks. They come in all wood types in two styles: floating and
supported. Each shelf has four spots.

## Floating shelf

```recipe
pattern: ["XXX", "# #", "XXX"]
key:
  "#": iron_ingot
  X: warped_slab
result: minekea:furniture/shelves/floating/warped
count: 3
```

## Supported shelf

```recipe
pattern: ["XXX", "# #", "X X"]
key:
  "#": iron_nugget
  X: oak_slab
result: minekea:furniture/shelves/supported/oak
count: 3
```

## Using a shelf

* Right-click a spot with an item in your hand to put it there. Aim at the part of the shelf you want to fill.
* Sneak and right-click with an empty hand to take the item from that spot.

![A dark oak floating shelf](texture:minekea:gui/patchouli/floating_shelf_example){width=200}

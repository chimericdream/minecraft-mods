---
title: Portal addresses
icon: item:minecraft:obsidian
tags: [basics]
summary: Corner blocks that tell a portal where to link.
---
A portal's **address** is the set of special blocks sitting on the four outer corners of its frame. These are the
corners that a portal does not need, so vanilla frames normally leave them empty.

## Building an address

1. Build a normal obsidian frame and light it.
2. Place address blocks on the four diagonal corners, just outside the frame (bottom left, bottom right, top left,
   and top right).
3. Do the same on a portal in the other dimension, using the same blocks.

Now the two portals link to each other.

## Which blocks work

By default, any of these can be part of an address:

```item
id: white_concrete
label: Concrete
```

```item
id: white_terracotta
label: Terracotta
```

```item
id: white_glazed_terracotta
label: Glazed terracotta
```

Every color counts, and different colors are different blocks. A red concrete corner only matches another red
concrete corner.

## Good to know

* You don't have to fill every corner. Empty corners and corners with other blocks are simply left out of the address.
* The order of the corners doesn't matter.
* The same block can be used on more than one corner.

Next: [how links are chosen](02-how-links-are-chosen.md).

---
title: Feeding trough
icon: item:jdcrafte:feeding_trough
tags: [farm]
summary: Holds feed and visibly fills up.
---
The feeding trough holds feed for your animals. It looks fuller as you add more.

```recipe
pattern: ["W W", "WWW", "S S"]
key:
  W: [oak_slab, spruce_slab, birch_slab, jungle_slab, acacia_slab, dark_oak_slab, mangrove_slab, cherry_slab, pale_oak_slab, bamboo_slab, crimson_slab, warped_slab]
  S: stick
result: jdcrafte:feeding_trough
count: 2
```

Any wooden slab works. One craft makes 2 troughs.

## Feed

A trough takes one kind of feed at a time:

| Feed |
|:-----|
| Wheat |
| Beetroot |
| Carrots |
| Potatoes |
| Wheat seeds |

```item
jdcrafte:feeding_trough
```

## Using it

* Right-click with feed in hand to fill it. It holds up to three stacks.
* Shift-right-click with an empty hand to take a stack back out.
* Hoppers can fill and empty it too.
* The trough turns to run along the way you were facing when you placed it.
* Breaking it drops whatever feed is inside.

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
| Golden carrots |
| Golden apples |

```item
jdcrafte:feeding_trough
```

## Using it

* Right-click with feed in hand to fill it. It holds up to three stacks.
* Shift-right-click with an empty hand to take a stack back out.
* Hoppers can fill and empty it too.
* The trough turns to run along the way you were facing when you placed it.
* Every 10 seconds, a stocked trough feeds one pair of nearby animals of the same kind (cows, sheep, pigs, chickens, rabbits, or horses), using one feed each, so they start breeding.
* What you stock picks who breeds: wheat for cows and sheep, carrots for pigs and rabbits, wheat seeds for chickens, golden carrots for horses and rabbits, golden apples for horses.
* In a crowded pen (24 or more animals nearby) it stops, so it can't overrun your farm.
* Breaking it drops whatever feed is inside.

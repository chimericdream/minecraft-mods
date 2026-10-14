---
title: Trellises
icon: item:jdcrafte:oak_trellis
tags: [decor, climbing]
summary: Climbable wooden lattices, plus arches.
---
A trellis is a thin wooden lattice you can climb, like a ladder. Unlike a ladder, it doesn't need a wall behind it, and it stays put if the wall it was against is removed. It can also be placed in water.

It comes in every wood type, with a plain and a stripped version of each: Acacia, Bamboo, Birch, Cherry, Crimson, Dark Oak, Jungle, Mangrove, Oak, Pale Oak, Spruce, and Warped.

## Trellis

Four logs (or stems, or bamboo blocks) and five sticks make 4 trellises. The log type picks the wood type; use a stripped log for the stripped trellis.

```recipe
pattern: ["LSL", "SSS", "LSL"]
key:
  L: oak_log
  S: stick
result: jdcrafte:oak_trellis
count: 4
```

The lattice faces you when you place it, like a sign.

## Trellis arch

Three trellises of the same kind make 3 arches.

```recipe
pattern: [" T ", "T T"]
key:
  T: jdcrafte:oak_trellis
result: jdcrafte:oak_trellis_arch
count: 3
```

An arch is three blocks long. Place it and the rest extends away from you in the direction you face, starting from the block you clicked. Breaking any one block removes the whole arch, and it gives back one item.

```item
jdcrafte:oak_trellis_arch
```

> [!TIP]
> Arches are climbable too, so a row of trellises and an arch makes a garden walkway you can scale.

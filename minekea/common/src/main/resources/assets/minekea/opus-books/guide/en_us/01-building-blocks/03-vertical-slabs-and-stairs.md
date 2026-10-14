---
title: Vertical slabs and stairs
icon: item:minekea:building/slabs/vertical/stone_bricks
tags: [building]
summary: Slabs and stairs, but sideways.
---
Just like regular slabs and stairs, but sideways! If a block has a slab or a staircase, it has a vertical one as well.

> [!NOTE] Staircases not included
> Minekea makes no promise that vertical stairs are suitable for actual staircases.

## Vertical slabs

```recipe
pattern: ["#", "#", "#"]
key:
  "#": oak_planks
result: minekea:building/slabs/vertical/oak
count: 6
```

## Vertical stairs

```recipe
pattern: ["###", " ##", "  #"]
key:
  "#": cobblestone
result: minekea:building/stairs/vertical/cobblestone
count: 8
```

## Logs and wood

Logs, wood, and their stripped versions (every wood type, plus bamboo block) also have stairs, slabs and their
vertical versions.

```recipe
pattern: ["###"]
key:
  "#": acacia_log
result: minekea:building/slabs/acacia_log
count: 6
```

Bookshelves have slabs, stairs and vertical versions too. See [Bookshelves](../03-furniture/01-bookshelves.md).

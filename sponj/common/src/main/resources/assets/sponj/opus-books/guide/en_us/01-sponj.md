---
title: Sponj
icon: item:sponj:sponj
tags: [blocks]
summary: The water sponge, its recipe, and how far it reaches.
---
A **Sponj** soaks up water. It does its work as soon as you place it next to water, or when water appears next to it.
Afterwards it turns into a **Wet Sponj**.

## Crafting

Two vanilla sponges make two sponj.

```recipe
type: crafting_shapeless
ingredients: [sponge, sponge]
result: sponj:sponj
count: 2
```

## How far it reaches

One sponj behaves like a vanilla sponge: it clears about 64 blocks of water within 6 blocks. Sponj that touch each
other work together, and every extra one makes the reach bigger:

| Sponj touching | Reach (blocks) | Water cleared (about) |
|:---------------|:---------------|:----------------------|
| 1 | 6 | 64 |
| 2 | 9 | 128 |
| 4 | 15 | 256 |
| 8 | 27 | 512 |
| 16 | 51 | 1024 |

Sixteen is the limit. Building a bigger wall does not help, but it will not hurt either.

When a group soaks up water, every dry sponj in it turns wet at once.

## Also clears

Water-plants like kelp and seagrass in the way are removed too, and drop their items.

> [!TIP]
> To drain something big, build a wall of sponj along one edge and let them work as a team. Next, see
> [drying out](03-drying-out.md) to reuse them.

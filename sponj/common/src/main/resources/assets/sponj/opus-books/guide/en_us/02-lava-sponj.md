---
title: Lava sponj
icon: item:sponj:lava_sponj
tags: [blocks]
summary: Soaks up lava instead of water.
---
A **Lava Sponj** works like a [Sponj](01-sponj.md), but it soaks up lava. Afterwards it becomes a **Wet Lava Sponj**.

## Crafting

A sponj and magma cream.

```recipe
type: crafting_shapeless
ingredients: [sponj:sponj, magma_cream]
result: sponj:lava_sponj
```

## How it works

* The reach and the amount cleared grow with the number of lava sponj touching each other, exactly like the water
  version (up to 16).
* Lava sponj only count other lava sponj. Mixing the two kinds in one group does not combine them.
* Lava sponj do not remove kelp or seagrass, since those are for water.

Wet lava sponj drip lava while they sit there. To use one again, see [drying out](03-drying-out.md).

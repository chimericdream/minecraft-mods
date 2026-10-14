---
title: Basalt bricks
icon: item:minekea:building/general/basalt_bricks
tags: [building]
summary: A new brick block made from smooth basalt, with mossy, cracked, crimson and warped versions.
---
Too much basalt? Craft it into bricks. Each brick type (except chiseled) also has a slab, vertical slab, stairs,
vertical stairs and wall.

```recipe
pattern: ["##", "##"]
key:
  "#": smooth_basalt
result: minekea:building/general/basalt_bricks
count: 4
```

## Variants

Chiseled basalt bricks are made from four basalt bricks.

```recipe
pattern: ["##", "##"]
key:
  "#": minekea:building/general/basalt_bricks
result: minekea:building/general/chiseled_basalt_bricks
count: 4
```

Add a vine to a basalt brick for the other versions: vines for mossy, weeping vines for crimson, twisting vines for warped.

```recipe
type: crafting_shapeless
ingredients: [minekea:building/general/basalt_bricks, weeping_vines]
result: minekea:building/general/crimson_basalt_bricks
```

```recipe
type: crafting_shapeless
ingredients: [minekea:building/general/basalt_bricks, vine]
result: minekea:building/general/mossy_basalt_bricks
```

```recipe
type: crafting_shapeless
ingredients: [minekea:building/general/basalt_bricks, twisting_vines]
result: minekea:building/general/warped_basalt_bricks
```

Smelt basalt bricks to crack them.

```recipe
type: smelting
ingredient: minekea:building/general/basalt_bricks
result: minekea:building/general/cracked_basalt_bricks
cooking_time: 200
experience: 0.1
```

A stonecutter also turns smooth basalt (or other basalt bricks) into the brick types.

## Example slab and wall

```recipe
pattern: ["###"]
key:
  "#": minekea:building/general/crimson_basalt_bricks
result: minekea:building/slabs/crimson_basalt_bricks
count: 6
```

```recipe
pattern: ["###", "###"]
key:
  "#": minekea:building/general/warped_basalt_bricks
result: minekea:building/walls/warped_basalt_bricks
count: 6
```

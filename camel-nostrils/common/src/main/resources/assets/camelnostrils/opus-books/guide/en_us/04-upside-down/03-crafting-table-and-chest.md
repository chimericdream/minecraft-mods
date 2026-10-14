---
title: Crafting table and chest
icon: item:camelnostrils:upside_down_crafting_table
tags: [block]
summary: Flipped for looks only; they work the same.
---
These look flipped upside-down but work exactly like the regular ones. To make one, craft the regular block by
itself. Craft the upside-down one by itself to flip it back.

```recipe
type: crafting_shapeless
ingredients: [crafting_table]
result: camelnostrils:upside_down_crafting_table
```

```recipe
type: crafting_shapeless
ingredients: [chest]
result: camelnostrils:upside_down_chest
```

And back again:

```recipe
type: crafting_shapeless
ingredients: [camelnostrils:upside_down_crafting_table]
result: crafting_table
```

```recipe
type: crafting_shapeless
ingredients: [camelnostrils:upside_down_chest]
result: chest
```

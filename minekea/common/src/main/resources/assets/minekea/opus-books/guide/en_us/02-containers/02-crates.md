---
title: Crates
icon: item:minekea:containers/crates/oak
tags: [storage]
summary: Reinforced containers that hold as much as a double chest.
---
Crates are reinforced containers that store as many items as a double chest (54 slots), in a single block. They come in
all wood types.

> Twice the storage, half the space, barely over twice the price!

```recipe
pattern: ["#X#", "XXX", "#X#"]
key:
  "#": [oak_log, oak_wood, stripped_oak_log, stripped_oak_wood]
  X: oak_planks
result: minekea:containers/crates/oak
```

## Trapped crates

Craft a crate with a tripwire hook for a trapped crate, just like a trapped chest.

```recipe
type: crafting_shapeless
ingredients: [minekea:containers/crates/dark_oak, tripwire_hook]
result: minekea:containers/crates/trapped/dark_oak
```

## Large crates

Two crates (or trapped crates) of the same type placed side by side join to form a large crate. Hoppers use both halves.

![Two crates joined](texture:minekea:gui/patchouli/double_crates){width=200}

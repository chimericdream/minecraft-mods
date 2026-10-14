---
title: Detached Creaking Heart
icon: item:artificialheart:artificial_creaking_heart
tags: [decor, creaking-heart]
summary: A creaking heart that is purely decorative.
---
The Detached Creaking Heart looks like a creaking heart but never spawns creakings. Its glowing faces are
decoration only.

```item
id: artificialheart:artificial_creaking_heart
label: Detached Creaking Heart
```

## Getting one

Use an axe on a vanilla creaking heart. It turns into a Detached Creaking Heart (the axe loses 1 durability) and
keeps its orientation. It also earns you the [Change of Heart](../04-advancements.md) advancement.

The Detached Creaking Heart is in the Building Blocks creative tab.

## Glowing faces

* **Remove a glow:** use an axe on a glowing face. Only the face you click is switched off.
* **Add a glow:** sneak and use a resin clump on a face that is not glowing.

The heart's axis follows the side you click when placing it.

## Turning it back

Surround a Detached Creaking Heart with 8 resin clumps to get a real creaking heart.

```recipe
pattern: ["XXX", "X#X", "XXX"]
key:
  X: resin_clump
  "#": artificialheart:artificial_creaking_heart
result: creaking_heart
```

> [!TIP]
> A Detached Creaking Heart also works as the body of a [Creaking golem](../03-creaking-golems.md), but the golem
> it makes is a motionless statue.

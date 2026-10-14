---
title: Livna
icon: item:camelnostrils:livna
tags: [block, anvil]
summary: An anvil that falls upward.
---
The livna is an upside-down anvil. It falls up instead of down, and hurts whatever it hits on the way.

Craft it like a regular anvil, with the iron blocks and ingots swapped:

```recipe
pattern: ["iii", " i ", "III"]
key:
  i: iron_ingot
  I: iron_block
result: camelnostrils:livna
```

It works like a regular anvil when you use it, and wears down the same way: Livna, then Chipped Livna, then
Damaged Livna, and finally it breaks.

```item
id: camelnostrils:chipped_livna
```

```item
id: camelnostrils:damaged_livna
```

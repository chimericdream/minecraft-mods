---
title: The block
icon: item:houdiniblock:houdini_block
tags: [basics]
summary: Crafting the Houdini Block and switching its mode.
---
The Houdini Block is a stone-like block that breaks instantly. What makes it special is that it can stop block
updates from reaching its neighbors when it is placed or broken.

## Recipe

```recipe
pattern: ["DED", "N#N", "DED"]
key:
  D: diamond
  E: ender_eye
  N: nether_star
  "#": stick
result: houdiniblock:houdini_block
```

You can find it in the Functional Blocks creative tab.

## Switching modes

The block has four modes. Its tooltip always shows the current one. A new block starts in **Prevent on break**.

To switch, hold the block, sneak, and right-click while not pointing at a block. The mode changes and its name
appears above your hotbar. Each switch moves to the next mode, and after the last one it starts over:

1. Prevent on break
2. Prevent on place
3. Prevent all
4. Replace block

The mode is set when you place the block, so a placed block keeps the mode it was placed with. See
[the four modes](02-modes.md) for what each one does.

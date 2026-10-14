---
title: The Target Dummy
icon: item:bettertargetdummies:target_dummy
tags: [blocks, crafting]
summary: Crafting, placing, and powering the dummy.
---
The Target Dummy is a block. Once you bind a mob to it, that mob stands on top of the block, held in place. It does
not move or wander.

## Crafting

Surround a target block with 4 mob drop items.

```recipe
pattern: [" D ", "DTD", " D "]
key:
  D: [bone, string, rotten_flesh, feather, leather, slime_ball, spider_eye, ender_pearl]
  T: target
result: bettertargetdummies:target_dummy
```

Many different mob drops work, and so do wool and mob heads. A few of them:

* ![Bone](item:minecraft:bone) Bone
* ![Rotten flesh](item:minecraft:rotten_flesh) Rotten flesh
* ![String](item:minecraft:string) String
* ![Ender pearl](item:minecraft:ender_pearl) Ender pearl
* ![Blaze rod](item:minecraft:blaze_rod) Blaze rod
* ![Slime ball](item:minecraft:slime_ball) Slime ball

The four drops do not have to match each other.

## Placing

The dummy faces you when you place it, and each of its 4 sides has its own look. It is quickest to break with a hoe,
and it drops itself when broken.

## Redstone

The dummy only shows its mob while it has a redstone signal.

* **Powered:** the bound mob appears on top of the block.
* **Unpowered:** the mob disappears. The dummy remembers which mob was bound, so turning the power back on brings
  the same mob back.

Put a lever or button next to it to switch the mob on and off whenever you like.

> [!NOTE]
> An unpowered dummy still accepts a mob. It simply waits for a signal before the mob appears.

Next: [Binding a mob](02-binding-mobs.md).

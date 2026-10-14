---
title: Compressed blocks
icon: item:minekea:building/compressed/cobblestone/9x
tags: [building, storage]
summary: 1x to 9x compressed versions of over a hundred blocks.
---
Some blocks take up a lot of room in your inventory or storage system. Minekea adds compressed versions of over a
hundred block types, from 1x up to 9x, to help you stay organized. Each step packs nine of the one before.

## Compressing

Nine of a block make the **1x** version. Nine **1x** blocks make a **2x**, and so on up to **9x**.

```recipe
pattern: ["###", "###", "###"]
key:
  "#": cobblestone
result: minekea:building/compressed/cobblestone/1x
```

```recipe
pattern: ["###", "###", "###"]
key:
  "#": minekea:building/compressed/cobblestone/1x
result: minekea:building/compressed/cobblestone/2x
```

## Uncompressing

Put a compressed block in the crafting grid on its own to get nine of the previous step.

```recipe
type: crafting_shapeless
ingredients: [minekea:building/compressed/cobblestone/2x]
result: minekea:building/compressed/cobblestone/1x
count: 9
```

> [!TIP] Fun fact
> A single 9x compressed block holds 387,420,489 of the base block.

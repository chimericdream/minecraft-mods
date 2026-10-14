---
title: Lava-logging
icon: item:minecraft:lava_bucket
tags: [lava]
summary: Fill a block with lava, just like waterlogging.
---
Right-click a block with a lava bucket to fill it with lava, the same way a water bucket waterlogs it. Use an
empty bucket on it to scoop the lava back up.

```item
id: minecraft:lava_bucket
```

## What can hold lava

* Slabs and stairs
* Walls and fences
* Iron bars and glass panes
* Trapdoors
* Ladders
* Chains

Anything in that list that can already be waterlogged can be lava-logged, with one catch: it must not be
flammable. Stone, brick, and copper blocks work. Most wooden ones, such as oak stairs, do not.

## Good to know

* A block holds water or lava, never both.
* Lava also fills these blocks by itself: flowing lava soaks into one it flows into, and placing one in a lava
  source fills it right away.
* Sneak while using the bucket to place the lava normally instead of filling the block.
* A lava-logged slab or stairs keeps its lava when you [fit a window into it](02-window-logging.md).

> [!WARNING] Still lava
> A lava-logged block is a lava source. It burns and it flows like lava, so do not lean on it.

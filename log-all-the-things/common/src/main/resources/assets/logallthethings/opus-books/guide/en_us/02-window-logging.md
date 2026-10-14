---
title: Window-logging
icon: item:minecraft:glass_pane
tags: [windows]
summary: Fit a glass pane or bars into a slab or stairs.
---
Right-click a slab or stairs while holding a glass pane (plain or stained) or bars, and the pane fits into the
block. The result is a window you can see through.

![Glass pane](item:minecraft:glass_pane) ![Iron bars](item:minecraft:iron_bars) ![Stone slab](item:minecraft:stone_slab) ![Stone stairs](item:minecraft:stone_stairs)

## How it works

* Works on slabs and stairs. Iron bars and all the copper bars count as bars.
* On stairs, the pane stands across the open step. On a slab, it runs across the way you are facing.
* Mine the glass or bars to pop just the pane back out. Aim at the pane to do this.
* Mine the solid part to break the whole thing.

## Limits

* Double slabs do not take a pane.
* Stairs must be straight, not a corner.
* Sneak while using the pane to place it as a normal block instead.
* Panes next to a window connect to it like they would to a regular pane.

## Panes and bars

```recipe
pattern: ["GGG", "GGG"]
key:
  G: glass
result: glass_pane
count: 16
```

```recipe
pattern: ["III", "III"]
key:
  I: iron_ingot
result: iron_bars
count: 16
```

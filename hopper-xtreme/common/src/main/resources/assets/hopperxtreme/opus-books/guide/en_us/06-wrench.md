---
title: Hopper Wrench
icon: item:hopperxtreme:tools/wrench
tags: [tools]
summary: Rotate blocks, switch multi-hopper sides, and copy filters.
---
The Hopper Wrench is the ultimate multi-tool. Right-click a block with it to rotate things like glazed terracotta, redstone
components, and stairs, and to flip slabs. On hoppers it turns them to face a different way.

On a [multi-hopper](02-multi-hoppers/index.md#choosing-the-sides), multi-hupper, or glazed multi-hopper, right-click one
of the hopper's arms to switch that side on or off.

```recipe
pattern: [" # ", " ##", "#  "]
key:
  "#": iron_ingot
result: hopperxtreme:tools/wrench
```

## Copying filters

Press **M** while holding the wrench to switch it between **Rotate** and **Copy / Paste**. You can change the key in
the Controls screen. A message shows the current mode. In Copy / Paste mode the wrench no longer rotates things.

* Right-click a hopper that has a filter to copy that filter.
* Right-click another hopper to paste it. The wrench can paste as many times as you like. Pasting into a hopper with
  no filter uses up a blank filter from your inventory, which is free in creative mode. A blank filter is any
  filter with nothing in it.
* A hopper that already has a filter is left alone. Sneak and right-click to replace its filter instead. The two
  filters must be the same kind, so a standard filter can't replace a diamond one.

Switching modes makes the wrench forget what it copied.

> [!NOTE] Playing with Minekea?
> Minekea has its own wrench, which also works on these hoppers, including switching multi-hopper sides. Only the Hopper
> Wrench can copy and paste filters.

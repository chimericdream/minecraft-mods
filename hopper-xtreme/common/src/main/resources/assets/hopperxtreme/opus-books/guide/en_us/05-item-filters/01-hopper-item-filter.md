---
title: Hopper Item Filter
icon: item:hopperxtreme:hopper_item_filter
tags: [filters]
---
The Hopper Item Filter gives you up to five slots to define blocks or items for a hopper to accept (or not accept).

```recipe
pattern: ["#W#", "#C#", "###"]
key:
  "#": stick
  W: cobweb
  C: iron_chain
result: hopperxtreme:hopper_item_filter
```

## Setting it up

Right-click with the filter in your hand to open it, then place the items you want to match in its slots. The slots
only hold a "ghost" copy of each item, so nothing is used up.

![An empty filter](texture:hopperxtreme:gui/guide/filter_empty){width=200}
![A filter with items defined](texture:hopperxtreme:gui/guide/filter_defined){width=200}

The filter's look changes slightly once it has items defined, so you can tell at a glance.

## Include and exclude

A new filter is in **include** mode: only the items in the filter are accepted. In **exclude** mode it is the other
way around, and everything *except* the items in the filter is accepted. Sneak and right-click (in the air) while
holding the filter to switch modes. A message shows the new mode, and the item's tooltip lists the current one.

## What counts as a match

Filters match by item type, so extra data like custom names, enchantments, or damage is ignored. As long as the item
is the same kind as one in the filter, it matches.

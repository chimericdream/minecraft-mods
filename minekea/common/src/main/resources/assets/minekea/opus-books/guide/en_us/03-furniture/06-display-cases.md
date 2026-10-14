---
title: Display cases
icon: item:minekea:furniture/display_cases/oak
tags: [furniture, redstone]
summary: Show off one item, and read its rotation with a comparator.
---
What good are fancy items if you cannot show them off? Display cases come in all wood types, in a rustic style and a
stripped style.

```recipe
pattern: [" G ", "X X", "###"]
key:
  "#": oak_log
  G: glass
  X: oak_planks
result: minekea:furniture/display_cases/oak
```

For the stripped style, use stripped logs.

## Using a display case

* Right-click an empty case with an item to put one in.
* Right-click a filled case to rotate the item.
* Sneak and right-click a filled case to take the item back.

Mob and player heads have special rendering for best results.

![An item in a display case](texture:minekea:gui/patchouli/display_case_rendering){width=200}

## Redstone

The case outputs a redstone signal to a comparator that changes with the item's rotation.

![A display case with redstone](texture:minekea:gui/patchouli/display_case_redstone){width=200}

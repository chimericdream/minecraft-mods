---
title: Luring and leashing
icon: item:minecraft:lead
tags: [misc]
---
These settings are in the **Misc.** section.

| Setting | What it does | Default |
|:--------|:-------------|:--------|
| Lure villagers | Villagers walk toward you while you hold a lure item. | Off |
| Leash nitwits | You can attach a lead to a nitwit. | Off |

## Luring

With **Lure villagers** on, hold one of these in either hand and nearby villagers (within 12 blocks) walk toward you:

```item
id: emerald_block
label: Emerald Block
```

```item
id: emerald_ore
label: Emerald Ore
```

```item
id: deepslate_emerald_ore
label: Deepslate Emerald Ore
```

Villagers who are trading, panicking, or have been hurt ignore the lure. Put the item away and they stop following.
Luring 8 villagers at once earns [Pied Piper](../04-advancements.md).

> [!NOTE] For pack makers
> The lure items come from the item tag `villagertweaks:villager_temptation_items`, so a data pack can change them.

## Leashing nitwits

With **Leash nitwits** on, a lead can be attached to a nitwit. Other villagers still can't be leashed.

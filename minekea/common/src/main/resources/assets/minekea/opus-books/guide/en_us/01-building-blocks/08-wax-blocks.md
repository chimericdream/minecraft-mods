---
title: Wax blocks
icon: item:minekea:building/general/wax/cyan
tags: [building, colour]
summary: Smelt candles into wax, then pack wax into blocks.
---
When candles melt (are smelted) they leave behind wax. With enough wax you can make blocks. Wax blocks are slightly
less slippery than ice and don't stick to slime blocks.

Smelt a candle of any colour for that colour of wax. A plain candle or a honeycomb gives plain wax.

```recipe
type: smelting
ingredient: pink_candle
result: minekea:ingredients/wax/pink
cooking_time: 200
experience: 0.1
```

Nine wax make a wax block of the same colour.

```recipe
pattern: ["###", "###", "###"]
key:
  "#": minekea:ingredients/wax/green
result: minekea:building/general/wax/green
```

Put a wax block in the crafting grid on its own to get nine wax back.

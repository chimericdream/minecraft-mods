---
title: Trading
icon: item:minecraft:emerald
tags: [trading]
---
These settings are in the **Trading** section.

| Setting | What it does | Default |
|:--------|:-------------|:--------|
| Override max trades | Changes how many times each trade can be used before the villager needs to restock. | Off |
| Max trade count | The number of uses when the override is on. Set it to -1 for unlimited trades. | -1 |
| Demand modifier | Prices rise when you trade a lot with a villager. Turn it off to stop prices from climbing. | On |
| Cap max discount | Limits how far reputation can lower a trade's price. | Off |
| Max discount percent | The biggest discount allowed, as a percent of the original price. | 99 |

## Unlimited trades

With the override on and the count at -1, a trade never runs out. Trades that sell a player head are left alone by
the max trades and demand settings.

## Capping discounts

Curing zombie villagers again and again can drop prices very low. With **Cap max discount** on, a trade never goes
below the original price minus the percent you set, and never below 1 item. For example, a cap of 20 means a trade
costing 10 will never cost less than 8.

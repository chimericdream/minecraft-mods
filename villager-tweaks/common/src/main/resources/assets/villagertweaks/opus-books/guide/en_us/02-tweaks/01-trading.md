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
| Trade preview | Shows trades for levels the villager hasn't reached yet, greyed out. | On |

## Trade preview

Below a villager's current trades you'll see the trades it will offer at higher levels, greyed out. You can't select
them, but hovering over one tells you which level unlocks it. Use this to check that a villager will sell what you want
before you level it up.

![A novice librarian's trades, with the higher level trades greyed out and a tooltip naming the level that unlocks one](texture:villagertweaks:guide/future_trades){width=400}

With this on, a villager levels up while you're still trading, a couple of seconds after its experience bar fills. The
new trades unlock on screen, so you don't need to close and reopen it.

A villager's future trades are decided the first time you open its trades, so they won't match vanilla. Villagers you
had already traded with before installing the mod get their remaining levels the next time you open them.

## Unlimited trades

With the override on and the count at -1, a trade never runs out. Trades that sell a player head are left alone by
the max trades and demand settings.

## Capping discounts

Curing zombie villagers again and again can drop prices very low. With **Cap max discount** on, a trade never goes
below the original price minus the percent you set, and never below 1 item. For example, a cap of 20 means a trade
costing 10 will never cost less than 8.

# Cider and spice advancements

> Source: requested directly by the user, 2026-10-12 · Status: **Shipped** in 26.2-1.1.0

## Description

A second branch of advancements in the mod's existing tab, rewarding the cider press, pomace, spices,
juices, and mulled drinks. Same quiet, slightly punny tone as the pumpkin ones. Only *Season's Drinkings*
awards experience.

```
It's Pumpkin Season (existing root)
├─ Pressing Matters (craft a Cider Press)
│  ├─ Fresh Pressed (get a bottle of apple cider or berry juice)
│  │  ├─ Berry Good (get a sweet berry or glow berry juice)
│  │  └─ Three Flavors (drink apple cider, sweet berry juice, and glow berry juice)
│  └─ Waste Not (feed pomace to a hurt animal to heal it)
└─ Spice Up Your Life (get cinnamon, cloves, or nutmeg)
   └─ Mulling It Over (craft mulling spices)
      └─ Big Batch (fill a cauldron with three bottles of apple cider or berry juice)
         └─ Ladle It On (ladle a serving of a mulled drink)
            └─ Hot Cider Weather (drink a mulled drink)
               └─ Season's Drinkings (hidden challenge: drink every mulled drink, 100 XP)
```

All are plain tasks except *Season's Drinkings*, which is a hidden challenge.

**Out of scope:** *Full Rack* (all three spices at once) and *Nutmeg Deal* (a farmer trade); both were
considered and cut. Nothing for the simmer finishing, since a cauldron's tick has no player to credit
(*Ladle It On* stands in for it).

## How it is built

Mostly vanilla triggers: `recipe_crafted` (the press and the mulling spices), `inventory_changed` (juices,
spices, mulled drinks), and `consume_item` (the drinking ones). Two things have no vanilla trigger, so
there are two new ones: `pomace_healed` (*Waste Not*, no parameters) and `cider_event` (*Big Batch*, with an `event`
of `cauldron_filled`, fired when the third bottle goes in). The existing `pumpkin_event` trigger is untouched.

## Decisions

- 2026-10-12 — Cut *Full Rack* and *Nutmeg Deal*; keep the other eleven.
- 2026-10-12 — Everything goes in the existing tab, under the root.
- 2026-10-12 — *Season's Drinkings* is the only new advancement that awards experience (100 XP).
- 2026-10-12 — The two custom triggers are their own new triggers, not extra values on `pumpkin_event`.
- 2026-10-12 — *Ladle It On* replaces a "batch finished simmering" advancement, which has no player to credit.

## Related

- [Cider press](cider-press.md), [Spices](spices.md), [Mulling](mulling.md) — the features these reward.
- [Advancements](../archive/advancements.md) — the shipped pumpkin branch of the same tab.

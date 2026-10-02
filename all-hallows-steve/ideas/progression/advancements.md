# Advancements

> Source: requested directly by the user, 2026-10-02 · Status: **Exploring**

## Description

A small set of advancements for the mod, in a tab of their own, rewarding the things the mod is about:
making a carving station, dyeing and carving pumpkins, wearing them, and lighting them. They are quiet
and a little funny, in line with the mod's grounded-autumn tone. No advancement gives experience.

**What counts**
- **Carved:** a decorated pumpkin with a stencil on at least one of its four faces, whichever way it is
  turned. **Zero carvings** means all four faces are bare.
- **Full set of stencils:** all 15 stencil items (Blank included) have been in the player's inventory at
  some point, so the goal grows if stencils are added later.
- **Lit Different:** lighting a decorated pumpkin with a soul, copper or redstone torch, or adding a
  candle. A regular torch doesn't count. Only a player can earn it, never a dispenser.

**Layout.** One new tab, chained:

```
It's Pumpkin Season (root, a pumpkin)
└─ Gourd Workshop (craft the carving station)
   ├─ Not Just Orange (dye a pumpkin in the station)
   │  └─ Hey! Who turned out the lights? (hidden: wear a decorated pumpkin with zero carvings)
   ├─ First Cut Is the Deepest (carve a stencil into a pumpkin in the station)
   │  ├─ Pumpkin Head (wear a carved decorated pumpkin)
   │  │  └─ Better Side (turn a worn pumpkin to another face)
   │  └─ Rare Cut (pick up a loot-only stencil: Heart, Jigsaw, Spawner or Structure Block)
   │     └─ A Face for Every Occasion (challenge: a full set of stencils)
   └─ Lit Different (light a pumpkin with something other than a regular torch)
```

Everything is a task except *A Face for Every Occasion*, which is a challenge, and the hidden one, which
only appears once earned. Names other than the hidden one are proposals.

**Out of scope:** experience or item rewards, advancements for dispenser automation, *Four Candles* and
*Staring Contest* (both considered and not wanted).

## How it would be built

Most of these have no vanilla trigger, so one new trigger, `allhallowssteve:pumpkin_event`, takes an
`event` (dyed, carved, worn_carved, worn_uncarved, lit_unusual, turned) and the code fires it for the
player who did the thing. The rest use vanilla triggers:

- *Gourd Workshop*: `recipe_crafted` for the station's recipe.
- *A Face for Every Occasion*: one `inventory_changed` criterion per stencil, all required.
- *Rare Cut*: `inventory_changed` for any of the four loot-only stencils.
- Root: `inventory_changed` for a pumpkin.

Wearing is checked server-side when the head item changes. Titles and descriptions go through datagen
like the rest of the mod's text.

## Decisions

- 2026-10-02 — Add *First Cut Is the Deepest*, *Rare Cut* and *Better Side*. Not *Four Candles* or
  *Staring Contest*.
- 2026-10-02 — The full set means all 15 stencils, Blank included.
- 2026-10-02 — Carved means a stencil on any face; zero carvings means all four faces bare.
- 2026-10-02 — One tab of our own, chained, no experience rewards.

## Brainstorm variants

_This idea did not come from a brainstorm._

## Related

- [Candle-lit pumpkins](../carving/candle-lit-pumpkins.md) — the candle half of *Lit Different*.
- [Wearable decorated pumpkins](../carving/wearable-decorated-pumpkins.md) — the wearing advancements
  and *Better Side*.
- [Stencils as exploration loot and trades](../carving/stencil-loot-and-trades.md) — *Rare Cut*, and the
  full set gets harder or easier as loot changes.
- [More stencils](../carving/more-stencils.md) — a "full set" grows when stencils are added.

## Open questions

- Confirm the proposed names, and the exact chain above.
- **Effort** is probably **M** (a custom trigger, about eleven advancements, text, tests); re-check once
  the names and chain are settled.

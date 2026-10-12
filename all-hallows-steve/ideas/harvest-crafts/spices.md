# Spices

> Effort **M** (re-estimated 2026-10-11) · Status: **Building**
> Source: split from [Cider press](cider-press.md) on 2026-10-11 (brainstorm variant "Spices" [5, 8, 9, 11] in
> [combined-ideas § 5](../brainstorms/2026-09-29/combined-ideas.md#5-orchards-food--drink))

## Description

Three new spice items, mainly for [Mulling](mulling.md): **cinnamon**, **cloves**, and **nutmeg**. None
needs a new plant or block; each comes from an existing source, so the mod stays clear of general farming.

- **Cinnamon** drops by chance when stripping cherry, mangrove, or jungle logs with an axe.
- **Cloves** come from smelting a small flower in a furnace or smoker. The flowers are a tag (dandelion,
  poppy, azure bluet, cornflower, oxeye daisy, and similar), 1 flower to 1 clove.
- **Nutmeg** comes from smelting cocoa beans in a furnace or smoker, and is also sold by farmer villagers
  at Journeyman level (1 emerald for 2 nutmeg).
- One of each is crafted (shapeless) into **mulling spices**, the item [Mulling](mulling.md) actually uses.
- Beyond mulling, spices are optional ingredients in [Harvest foods](harvest-foods.md) (e.g. soul cakes).
  Dye and scented-candle uses are tracked as separate ideas in the README inbox, not specified here.

**Out of scope:** new spice plants or trees, saffron, star anise, and a sapling-to-cinnamon recipe.

**Effort:** M. Three new items, smelting recipes, a flower tag, a stripping drop hook, and a villager trade.
Mostly data, with two small code pieces.
**Depends on:** no other idea. The stripping drop and the farmer trade need loader-specific hooks (or a
mixin) on Fabric and NeoForge. Mulling builds on this.
**Verified by:** a GameTest for the stripping drop and the smelting recipes, plus a manual check of the
trade in `TEST_PLAN.md`.

## Implementation notes

- 2026-10-11 — Built on `feat/all-hallows-steve-spices`. Cinnamon drop chance is 25% (`Spices.CINNAMON_STRIP_CHANCE`),
  hooked by `AHS$AxeItemMixin` (common, so both loaders). Smelting 200 ticks, smoking 100. Clove flower tag:
  dandelion, poppy, blue orchid, allium, azure bluet, the four tulips, oxeye daisy, cornflower, lily of the
  valley (no wither rose). The farmer trade is a data-driven `villager_trade` added to the vanilla
  `farmer/level_3` trade tag, so it is one of the pool of Journeyman offers rather than guaranteed.
  Cinnamon (recolored bark rod) and nutmeg textures come from unused-textures (credited in the README); the cloves texture is a first-pass placeholder. The mulling spices texture (a cheesecloth sachet with a red tie) is a hand-drawn first pass too. Not yet built: spices in harvest foods (soul cakes), which waits on that idea.

## Decisions

- 2026-10-11 — The set is three items: cinnamon, cloves, nutmeg.
- 2026-10-11 — Spices come from existing blocks and items, with no new plants.
- 2026-10-11 — Cinnamon comes from stripping cherry, mangrove, and jungle logs only; no sapling recipe.
- 2026-10-11 — Cloves and nutmeg are smelted in a furnace or smoker (not the campfire).
- 2026-10-11 — Nutmeg is smelted from cocoa beans and is also a farmer villager trade.
- 2026-10-11 — The nutmeg trade is at Journeyman (level 3): 1 emerald for 2 nutmeg.
- 2026-10-11 — Cloves smelt from a tag of small flowers, 1 flower to 1 clove.
- 2026-10-11 — Spices can also be used in harvest foods, not just mulling. Spice dyes and spice candles are
  separate Inbox ideas, not part of this one.
- 2026-10-11 — Build order: with the cider press (either first), before Mulling.

## Brainstorm variants

From [combined-ideas § 5](../brainstorms/2026-09-29/combined-ideas.md#5-orchards-food--drink).

- **Spices** [5, 8, 9, 11] — Cinnamon, nutmeg, cloves, star anise, and saffron from new bushes or rare
  trees. Used in food, candles, and dyed wax, and as dye sources.

## Related

- [Mulling](mulling.md) — the main consumer.
- [Cider press](cider-press.md) — makes the cider that gets mulled.
- [Harvest foods](harvest-foods.md) — possible other uses.

## Open questions

- Chance of a cinnamon drop when stripping, and whether Fortune-like effects or axe enchantments matter.
  (Can be tuned in implementation.)
- Smelting time. (Can be tuned in implementation.)
- Exact membership of the small-flower tag. (Can be decided in implementation.)

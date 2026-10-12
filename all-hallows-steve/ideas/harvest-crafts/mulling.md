# Mulling

> Effort **L** (re-estimated 2026-10-11) · Status: **Building**
> Source: split from [Cider press](cider-press.md) on 2026-10-11 (brainstorm variant "Mulled or spiced cider" [7, 11, 13] in
> [combined-ideas § 5](../brainstorms/2026-09-29/combined-ideas.md#5-orchards-food--drink))

## Description

Mulled cider is made by **simmering cider in a cauldron**. The player pours cider bottles into a cauldron,
adds spices, and heats it from below with a lit campfire. After a short simmer the cauldron holds mulled
cider, which the player ladles out into glass bottles.

- **Cauldron:** New cider and mulled-cider cauldron blocks, like vanilla's lava and powder snow ones. Each
  has 3 levels. One cider bottle poured in adds one level, and one empty bottle used removes one level
  and gives one serving.
- **Heat:** The cauldron must sit above a lit campfire or soul campfire.
- **Simmer:** Starts only on a full (3-level) cider cauldron, so a batch is always 3 servings. It takes
  about 30 seconds, with steam and bubbles. If the fire goes out, progress resets, but the added spices
  are kept. Once done, the mulled cider stays hot until emptied. Nothing is ever wasted.
- **Mulling spices** are crafted from one cinnamon, one cloves, and one nutmeg, and added by right-clicking a
  full cider cauldron with the item. They are required: the simmer never starts without them, even over a lit
  campfire. There is only one kind of mulled cider (8 hunger, no status effects).
- **Hand-only:** Hoppers and dispensers do not interact with the cauldron, like vanilla cauldrons.
- Built after both [Cider press](cider-press.md) and [Spices](spices.md). Plain [cider](cider-press.md)
  still has no effect.

**Out of scope:** status effects on any cider, barrel aging, a separate new block just for mulling,
mixed-spice servings within one batch, and automation.

**Effort:** L. Two new cauldron blocks (with 3 levels each), spice-adding and ladling interactions, a
campfire-heat check, simmer logic, particles and rendering, and spiced food values, on both loaders.
**Depends on:** [Cider press](cider-press.md) and [Spices](spices.md), which must be built first; vanilla
cauldron and campfire behaviour; loader-specific hooks for the cauldron interactions if the vanilla
cauldron needs extending.
**Verified by:** a GameTest for the fill, spice, simmer, ladle, and fire-out reset flow, plus a visual
smoke test for the cauldron rendering and a `TEST_PLAN.md` entry.

## Implementation notes

- 2026-10-11 — Built on `feat/all-hallows-steve-mulling`. Two blocks, `cider_cauldron` and
  `mulled_cider_cauldron`, both extend vanilla's `LayeredCauldronBlock` (like Minekea's honey cauldron) with
  their own interaction dispatchers; an access widener opens `CauldronInteraction.Dispatcher.put`. They have
  no items and drop a plain cauldron.
- State is all block-state, no block entity: `level` (1–3), and on the cider block a `spiced` boolean and a
  `simmer` counter (0–4). A scheduled tick every 120 ticks (6 s) checks for a lit
  `#minecraft:campfires` block below: heated adds a step, and the step after `simmer` 4 (30 s total) swaps
  in the mulled block; unheated resets `simmer` to 0. Taking a serving out also resets it.
- Mulled cider is one item (`mulled_cider`); the item carries the food values (`MulledJuice.food`). 8 hunger / 0.65 saturation modifier, the same as the old cinnamon + cloves + nutmeg
  batch.
- Cauldron models and blockstates are hand-authored (vanilla `template_cauldron_*`, amber and darker
  red-brown content textures); colours do not show whether spices went in.
- Not yet checked: how the cauldrons actually look in game (rendering, particles). See `TEST_PLAN.md` § 7.

## Decisions

- 2026-10-11 — Mulling is explored as a mechanic, not just a crafting recipe.
- 2026-10-11 — Built after the cider press and Spices.
- 2026-10-11 — Mulling happens by simmering cider in a cauldron, not at a crafting table, furnace, or new
  pot block.
- 2026-10-11 — Spices are food-value perks only (no status effects).
- 2026-10-11 — Superseded: each spice had a different mix and stacked.
- 2026-10-11 — Mulling spices are required to start the simmer, so there is no plain mulled cider and no
  tooltip on servings.
- 2026-10-11 — Replaced the mix-and-match spices with one crafted mulling spices item (one each of cinnamon,
  cloves, and nutmeg), worth what all three used to give.
- 2026-10-11 — The cauldron has 3 levels; 1 cider bottle = 1 level. Spices are added per batch and apply
  to all servings.
- 2026-10-11 — Simmering takes about 30 seconds, mulled cider stays hot, and nothing is ever wasted.
- 2026-10-11 — Heat comes from a lit campfire or soul campfire under the cauldron only.
- 2026-10-11 — Cider is shown with new cauldron blocks (3 levels each), not a block entity on the vanilla
  cauldron.
- 2026-10-11 — The cauldron is hand-only: no hoppers or dispensers.
- 2026-10-11 — Mulling spices are added by right-clicking with the item, once per batch.
- 2026-10-11 — Mulling starts only on a full cider cauldron (3 levels), so batches are always 3 servings.
- 2026-10-11 — If the fire goes out mid-simmer, progress resets and the added spices are kept.

## Brainstorm variants

From [combined-ideas § 5](../brainstorms/2026-09-29/combined-ideas.md#5-orchards-food--drink).

- Mulled or spiced cider variant [7, 11, 13].

## Related

- [Cider press](cider-press.md) — supplies cider.
- [Spices](spices.md) — supplies the spices.
- [Harvest foods](harvest-foods.md) — lists mulled cider as one of its foods.

## Open questions

- Exact hunger and saturation numbers for plain cider, mulled cider, and each spice. (Can be tuned in
  implementation.)
- Do the cauldron colours or particles show whether spices went in? (Cosmetic; can be decided in
  implementation.)

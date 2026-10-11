# Cider press

> Shortlist #17 · Tier 2 — Solid mid-size features · Effort **L** (re-estimated 2026-10-11) · Value ★★ · Votes 9 · My vote **Yes**
> Status: **Building**

## Description

A single-block **cider press** with a small GUI, working much like a brewing stand: put apples and glass
bottles in, add fuel, and take cider bottles out after a timed press (progress arrow in the GUI). It
follows the repo's container-inventory pattern, so hoppers can feed and drain it. Suggested by 9 of 13
brainstorms.

- **Pressing:** 3 apples + 1 glass bottle → 1 cider, timed. Each pressing also yields 1 pomace.
- **Fuel:** brewing-style charges. One fuel slot accepts charcoal (8 pressings), coal (8), or blaze powder
  (20). Blaze powder is not required.
- **Cider** is a pure food item in a glass bottle, like honey bottle: saturation only, no status effect.
  It returns an empty bottle when drunk.
- **Pomace** is compostable and in the common `c:fertilizers` tag. It is a breeding item for pigs, and
  feeding it to any cow, sheep, pig, or horse-like animal below max health heals a little.
- **Look:** a barrel-style block with a spout and a press lid. A more complex model is a possible later
  upgrade (see the README inbox).
- **Mulled cider** is not part of this idea. See [Spices](spices.md) and [Mulling](mulling.md).
- Uses vanilla apples; no new orchard trees.

**Out of scope:** barrel aging, multiblock presses, status-effect plain cider, the abandoned-orchard
structure.

**Effort:** L. A new block entity with a fuel-and-progress GUI (new UI), a barrel-style model, two new
items (cider, pomace), recipes, tags, and the animal-feeding behaviour, on both loaders.
**Depends on:** no other idea. The container-inventory pattern and any menu helpers from chimeric-lib.
Pomace feeding needs loader-specific hooks (or a mixin) for the heal-on-feed behaviour. Spices and Mulling
build on this.
**Verified by:** a GameTest for the press (inputs, fuel, outputs, hopper feeding, pomace feeding and
healing), plus a manual GUI and model check listed in `TEST_PLAN.md`.

## Decisions

- 2026-10-11 — The press is a single block with a GUI (container-inventory pattern), not a use-in-world
  crank or a crafting-only recipe.
- 2026-10-11 — Cider is pure food (saturation only), with no status effect.
- 2026-10-11 — Mulled cider is not produced by the press; the press idea owns plain cider.
- 2026-10-11 — Pressing yields pomace as a byproduct.
- 2026-10-11 — Pressing is timed, 3 apples per bottle.
- 2026-10-11 — The press is automatable and needs fuel: brewing-style charges from several accepted
  items. Blaze powder is allowed but not required.
- 2026-10-11 — Pomace is compostable, animal feed, and tagged `c:fertilizers`.
- 2026-10-11 — Mulled cider uses a small set (2–3) of new spice items, and mulling is explored as a
  mechanic. (Now tracked in [Spices](spices.md) and [Mulling](mulling.md).)
- 2026-10-11 — Fuel items: charcoal 8 pressings, coal 8, blaze powder 20.
- 2026-10-11 — Pomace is a pig breeding item, and heals any cow, sheep, pig, or horse-like animal below
  max health a little when fed.
- 2026-10-11 — The press is barrel-style with a spout for now; a more complex model is saved as a later
  idea.
- 2026-10-11 — Build order: Spices and the cider press first, in either order, then Mulling after both.

## Brainstorm variants

From [combined-ideas § 5](../brainstorms/2026-09-29/combined-ideas.md#5-orchards-food--drink). Bracketed numbers
are the `brainstorms/2026-09-29/agentN.md` files that suggested each variant.

- Apples (or pumpkins + sugar [5]) pressed into cider [1, 2, 5, 7, 8, 9, 10, 11, 12].
  - Mild warmth effect, cold resistance, or saturation [5, 7, 9, 11].
  - Ages in barrels: to a stronger variant [1], or sweet → dry → vinegar [10]. A cider barrel that slowly
    turns juice into cider [12]. Fermentation-bucket version [9].
  - Mulled or spiced cider variant [7, 11, 13].
  - Pomace becomes compost or animal feed [2].
  - Pressing as a multiblock structure [11].
- **Abandoned orchard** [8, 11] — A rare structure with a weathered barn or cider shed and a press.
  (Cut from the shortlist as scope creep.)

## Related

- [Spices](spices.md) — new spice items, split out of this idea.
- [Mulling](mulling.md) — turns cider + spices into mulled cider; built after this idea and Spices.
- [Harvest foods](harvest-foods.md) — mulled cider is also listed there; its wording should now point to
  [Mulling](mulling.md).

## Open questions

- Heal amount for pomace, and the exact rule for "horse-like" animals (horse, donkey, mule, camel?). Can be
  decided in implementation.
- Timing of one pressing, in ticks. Can be decided in implementation.

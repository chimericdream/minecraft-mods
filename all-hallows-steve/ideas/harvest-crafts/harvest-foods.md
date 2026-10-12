# Harvest foods

> Shortlist #8 · Tier 1 — Quick wins · Effort **M** (re-estimated 2026-10-12) · Value ★★ · Votes 7 · My vote **Maybe**
> Status: **Ready**

## Description

A handful of well-chosen autumn foods, not a cooking system. Mulled cider is its own idea (see
[Mulling](mulling.md), built on the [Cider press](cider-press.md) and [Spices](spices.md)).

- **Soul cakes** (from the real "souling" tradition of Hallowmas). The one food where spices matter:
  - A plain soul cake needs no spice and is a simple hunger/saturation food.
  - Adding spices gives it **folklore-flavored status effects**, each lasting 30 seconds (like suspicious
    stew). A single spice gives one effect; the **mulling spices sachet** makes the best cake, with all
    three:

    | Spice | Effect |
    |---|---|
    | Cloves (warding off spirits) | Absorption I |
    | Cinnamon (luck, prosperity) | Luck I |
    | Nutmeg (dreams, seeing beyond the veil) | Night Vision |
    | Mulling spices sachet | All three |

  - Each variant (plain, cloves, cinnamon, nutmeg, sachet) has a slightly different texture, like suspicious
    stew's per-flower look.
  - Five separate items. Recipe: wheat + sugar + egg, plus at most one spice item or the sachet.
- **Pumpkin soup**: a bowl food that returns the bowl. Shapeless: pumpkin, bowl, and a carrot or potato.
  Hunger/saturation only.
- **Roasted pumpkin seeds**: a snack cooked from pumpkin seeds in a campfire, smoker, or furnace. Moved here from
  [Carving yields seeds](../carving/carving-seeds.md), which now only covers carving returning raw seeds.
  Hunger/saturation only.
- **Apple pie**: a new pie to sit beside vanilla's pumpkin pie, crafted from apple, sugar, apple over wheat,
  wheat, wheat (shaped). Hunger/saturation only.
- **Spiced pumpkin pie**: a better version of vanilla's pumpkin pie. Shapeless: pumpkin pie plus a mulling spices
  sachet. Hunger/saturation only; no status effects.

Every food except spiced soul cakes is plain hunger/saturation. Nothing else gets status effects.

**Out of scope:** a general cooking system, oven or hearth blocks, multi-step recipes, a "variety" or
"feast" bonus, mulled cider (owned by Mulling), and dried apple rings (owned by the
[Drying rack](drying-rack.md)).

**Depends on:** [Spices](spices.md) for soul cakes and spiced pumpkin pie. The other foods have no
dependencies.
**Verified by:** a data/recipe GameTest for the crafting and smoking recipes plus effect application on
eating soul cakes; manual checks listed in `TEST_PLAN.md`.

## Decisions

- 2026-10-12 — The food set is soul cakes, pumpkin soup, roasted pumpkin seeds, apple pie, and spiced
  pumpkin pie.
- 2026-10-12 — Roasted pumpkin seeds move from Carving seeds into this idea.
- 2026-10-12 — Soul cakes are the only food with status effects; effects depend on which spice(s) were used.
  Every other food is plain hunger/saturation.
- 2026-10-12 — Spices are optional for soul cakes (a plain cake works). The mulling spices sachet gives the
  best soul cake.
- 2026-10-12 — Spice effects are folklore-flavored (spirit-ward themes such as absorption, luck, or slow
  fall), not just generic comfort buffs.
- 2026-10-12 — Spiced pumpkin pie only has better food values; no status effects.
- 2026-10-12 — Spice-to-effect mapping: cloves = Absorption I, cinnamon = Luck I, nutmeg = Night Vision, and
  the sachet gives all three.
- 2026-10-12 — Each spice effect lasts 30 seconds, matching suspicious stew.
- 2026-10-12 — Each soul cake variant gets its own slightly different texture, like suspicious stew.
- 2026-10-12 — Soul cakes are five separate items: plain, cloves, cinnamon, nutmeg, and sachet.
- 2026-10-12 — Soul cake recipe: wheat + sugar + egg, plus at most one spice item or the sachet.
- 2026-10-12 — Pumpkin soup is shapeless: pumpkin, bowl, and a carrot or potato.
- 2026-10-12 — Roasted pumpkin seeds cook in a campfire, smoker, or furnace.
- 2026-10-12 — Spiced pumpkin pie is shapeless: pumpkin pie plus a mulling spices sachet.
- 2026-10-12 — Apple pie is a shaped recipe: top row apple, sugar, apple; bottom row wheat, wheat, wheat.
- 2026-10-12 — Texture sources: `food_hardtack` (soul cake), `food_stew_sweetpod` (pumpkin soup),
  `seeds_generic_gourd` (roasted seeds only), and `metal_coin_fancy_plain` or `part_mould`, tweaked (apple pie).

## Brainstorm variants

From [combined-ideas § 5](../brainstorms/2026-09-29/combined-ideas.md#5-orchards-food--drink). Bracketed numbers
are the `brainstorms/2026-09-29/agentN.md` files that suggested each variant.

- **Seasonal dishes** [1, 4, 6, 9, 11, 12, 13] — Pumpkin pie, apple pie, squash or pumpkin soup,
  vegetable stew, apple crumble, apple fritters, cinnamon rolls, candied apples, roasted chestnuts, baked
  apples, and roasted pumpkin seeds.
  - **Soul cakes** [13] — Based on the real "souling" tradition of Hallowmas.
  - **Oven or hearth baking** [12] with multi-step recipes. Lattice pie patterns for craft value [7].
  - Mild effects: comfort, faster hunger regen, saturation, brief speed, less fall damage, removes mining
    fatigue [1, 4, 9, 11].

## Related

- [Cider press](cider-press.md) — makes the plain cider (#17); its apples/pomace could feed apple pie.
- [Mulling](mulling.md) — owns mulled cider; it is no longer part of this idea's scope.
- [Spices](spices.md) — supplies the spices for soul cakes and spiced pumpkin pie.
- [Carving yields seeds](../carving/carving-seeds.md) — supplies the raw pumpkin seeds.
- [Drying rack](drying-rack.md) — dried apple rings.

## Open questions

- Exact hunger and saturation values for each food. (Can be tuned in implementation.)

## Texture notes

Candidates in the `unused-textures` repo (credited in the README already for the spices):

- **Soul cake base:** `items/food_hardtack.png`, a pale square biscuit, with a small per-spice topping
  added by hand (dark studs for cloves, reddish dust for cinnamon, tan flecks for nutmeg, all three for the
  sachet).
- **Roasted pumpkin seeds:** `items/seeds_generic_gourd.png` for the roasted item only. Raw seeds keep
  vanilla's pumpkin seed texture.
- **Pumpkin soup:** `items/food_stew_sweetpod.png` (a bowl) recolored orange.
- **Apple pie:** `items/metal_coin_fancy_plain.png` or `items/part_mould.png`, tweaked into a pie.
  `food_slice_squash.png` is a color reference for the spiced pumpkin pie.

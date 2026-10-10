# Netherite Plating, concretized

> Shortlist #4 · Tier 1 — Quick wins · Effort **S** · Value ★★ · Votes — · My vote **Yes**
> Status: **Exploring**

## Description

Define exactly what "durable" means and document it. Today the README says plated boxes are protected from
explosions and fire both as an item and when placed. The agent note goes further: the item floats in lava,
doesn't burn, and doesn't despawn, the full netherite-item treatment. **Recommendation:** check what the
code does now, document it in the README and tooltip, and add only the pieces that are missing. If the
despawn protection isn't there, decide whether to add it; it affects the world, so make it a config toggle.

## Decisions

- First step is to check what the code does today and document it.

## Related

- [Soulbound enchantment](../enchantments/soulbound.md) — plated boxes only.

## Open questions

_None yet._

# Hammer rework

> Shortlist #7 · Tier 2 — Solid mid-size features · Effort **M** · Value ★★ · Votes — · My vote **Yes**
> Status: **Exploring**

## Description

From your notes: remove the trowel functionality from the hammer, make right-click convert a block to its
cracked or cobbled variant when one exists, and break a 3x3 area instead of one block. **Recommendation:**
the changes affect an item players already own, so follow N1: add the new behavior behind a config toggle
for a version, and only remove the placer behavior in a major release with a changelog note (#8 gives it a
new home first). The 3x3 break should respect tool tier and hardness, and "convert" should be a data-driven
map (block to variant) so packs can extend it. Decide whether 3x3 is always on or a sneak toggle.

## Decisions

- Follow N1: add behind a config toggle first; do the trowel replacement item first.

## Related

- [Trowel replacement item](trowel-replacement-item.md) — do that first.

## Open questions

_None yet._

# Update visualization

> Shortlist #4 · Tier 2 — Solid mid-size features · Effort **M** · Value ★★ · Votes — · My vote **Maybe**
> Status: **Exploring**

## Description

While holding the block, briefly show particles on blocks that *would* receive updates from a place or
break at the targeted position. Debugging suppression setups by eye is guesswork today. It pairs well
with #3, because masks make the preview more useful. **Recommendation:** client-side only, particles
rather than overlays, and a client config toggle plus a non-color cue (A3, A4). Keep it to the
first-order neighbor set at first.

## Decisions

_None yet._

## Related

- [Directional masks](directional-masks.md) — the preview shows which faces a mask would block.
- [The Houdini Wand](../tools/houdini-wand.md) — the preview should work while holding the wand too.

## Open questions

_None yet._

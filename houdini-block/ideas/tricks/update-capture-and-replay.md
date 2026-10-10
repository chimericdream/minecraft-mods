# Update capture and replay

> Shortlist #8 · Tier 3 — Big bets · Effort **L** · Value ★★★ · Votes — · My vote **Maybe**
> Status: **Exploring**

## Description

The showstopper: a mode where the block *records* the updates it suppressed and fires them all when
triggered by a redstone pulse or interaction. Delayed-consequence contraptions follow: prime a sand
column, then release it on cue. It is the most on-brand idea in the list ("the trick, then the
reveal"), and also the riskiest, since it needs a block entity that stores positions and a safe way to
replay them. **Recommendation:** prototype with a cap on the number of stored updates, and decide the
chunk-unload behavior before building. A server-side config for the cap (A2, A4).

## Decisions

_None yet._

## Related

- [Directional masks](../precision/directional-masks.md) — replay would need to respect the masks.

## Open questions

_None yet._

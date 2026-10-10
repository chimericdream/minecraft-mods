# Preserving: capture the exact biome tint

> Shortlist #7 · Tier 2 — Solid mid-size features · Effort **L** · Value ★★ · Votes — · My vote **Maybe**
> Status: **Exploring**

## Description

Instead of locking mined leaves to vanilla's default color, remember the exact tint they had when broken,
so builders can match a specific biome's foliage anywhere. This needs a non-ticking block entity to hold
the captured color, which the design doc argues is cheap in practice. **Recommendation:** treat it as a
builder-focused upgrade and follow [`docs/PRESERVING-PER-BIOME-TINT.md`](../../docs/PRESERVING-PER-BIOME-TINT.md).
The unresolved questions in that doc (the tooltip mechanism and the naming scheme) need your decision
before building. It deepens a feature that already exists (A1), but it is a bigger change than it looks
because it adds a block entity to a previously blockstate-only feature.

## Decisions

_None yet._

## Related

_None yet._

## Open questions

_None yet._

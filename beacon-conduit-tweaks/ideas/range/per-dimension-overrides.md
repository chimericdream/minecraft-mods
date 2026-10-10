# Per-dimension overrides

> Shortlist #12 · Tier 2 — Solid mid-size features · Effort **M** · Value ★★ · Votes — · My vote **Yes**
> Status: **Exploring**

## Description

Different range math per dimension. The agent note's reason (the Nether's 8:1 distance compression) is
weaker than it sounds, since beacon range isn't tied to portal math, but servers do want different
numbers in different dimensions. **Recommendation:** a per-dimension override table where each value
defaults to the global one (A4). Do after #1 and #7 so the overrides cover all the range settings.

## Decisions

_None yet._

## Related

- [Range shape](range-shape.md) — overrides should cover shape too.
- [Separate vertical and horizontal range](vertical-and-horizontal-range.md) — overrides should cover both numbers.

## Open questions

_None yet._

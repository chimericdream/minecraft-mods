# Comparator fidelity

> Shortlist #1 · Tier 1 — Quick wins · Effort **S** · Value ★★ · Votes — · My vote **Yes**
> Status: **Exploring**

## Description

Make the comparator output count only real contents, so a hopper's filter slots don't read as items.
This is the quiet kind of bug that bites sorting halls: a comparator on a filtered hopper reports
"full" when the filter is. **Recommendation:** first check what the filtered hoppers do today (the filter
slots share the container, so there is a good chance they leak into the signal). If they do, fix it as a
bug, not a feature. No config, since it corrects an unintended reading.

## Decisions

_None yet._

## Related

- [Per-hopper redstone modes](redstone-modes.md) — same redstone theme.

## Open questions

_None yet._

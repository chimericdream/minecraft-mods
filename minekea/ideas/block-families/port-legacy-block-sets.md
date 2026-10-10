# Port missing legacy block sets

> Shortlist #4 · Tier 1 — Quick wins · Effort **S** · Value ★★ · Votes — · My vote **Yes/Maybe**
> Status: **Exploring**

## Description

From your notes: blocks and sets the mod had in older versions that haven't been ported to 26.x, such as
stairs made from logs. **Recommendation:** start with an audit that diffs the old version's block list
against the current one (the `LogWoodFamilies` class already exists, so some are in), and then port only
what is Minekea's own and not a vanilla family. Anything that finishes a vanilla family belongs in But What
About...?.

## Decisions

- Some of these may belong in But What About...? or Cobblicious instead; decide per block during the audit, using the boundary section in the README.

## Related

- [Vertical stairs and slabs for new blocks](vertical-stairs-and-slabs.md) — same group.
- [But What About...?](../../../but-what-about/ideas/README.md) — owns anything that finishes a vanilla family.

## Open questions

_None yet._

# Address preview

> Shortlist #4 · Tier 2 — Solid mid-size features · Effort **M** · Value ★★★ · Votes — · My vote **Maybe**
> Status: **Exploring**

## Description

Shows which address blocks a portal is currently reading, so a link that isn't behaving can be diagnosed
in-world. The backlog offers two forms: sneak-looking at the corners, or a held-item tooltip.
**Recommendation:** pick a **use-interaction** instead of either: sneak and right-click an empty hand on a
portal frame block (or a corner block) to get an action-bar message listing the four corner blocks and
whether the portal is addressed. It needs no per-tick raycast and no client code, and the mod's client side
stays optional. The sneak-look form needs client rendering or per-tick server raycasts for a small gain, so
skip it. Show block names as text, not only colors (A3). Related: #1, which gives the same information for
the last transit instead of a block you're looking at.

## Decisions

_None yet._

## Brainstorm variants

From [`brainstorms/2026-08-18-legacy/potential-features.md`](../brainstorms/2026-08-18-legacy/potential-features.md)
(Addressing niceties):

- **Address preview** — sneak-look at a portal's corners (or a held item's tooltip) to show which address
  blocks it's currently reading, useful for troubleshooting a link that isn't behaving as expected.

## Related

- [`/portallink debug` command](portallink-debug-command.md) — the same information for the last transit
- [Arrival cue](arrival-cue.md) — confirms a match after the fact; this checks before travelling

## Open questions

_None yet._

# Auto-labeled new portals

> Shortlist #5 · Tier 2 — Solid mid-size features · Effort **M** · Value ★★★ · Votes — · My vote **Yes**
> Status: **Exploring**

## Description

When the game builds a brand-new exit portal because no match exists, stamp its diagonal corners with the
entry portal's address blocks, so a freshly-dug pair links itself. This is the biggest usability gain in the
backlog: today a player has to travel through, then place matching blocks by hand on the far side.
**Recommendation:** ship **off by default** with a config toggle, since it writes blocks into the world
(N1 requires an opt-out; defaulting off makes it opt-in, which is stronger). Only stamp corner positions
that the portal generation left empty or replaceable, never overwrite existing player blocks, and skip
silently if a corner is unavailable. Needs a hook where `PortalForcer` creates the portal, so it is the one
item here that touches vanilla generation. Re-estimate effort once the hook point is checked: it could
move to L if the creation path is hard to intercept on one loader.

## Decisions

_None yet._

## Brainstorm variants

From [`brainstorms/2026-08-18-legacy/potential-features.md`](../brainstorms/2026-08-18-legacy/potential-features.md)
(Addressing niceties):

- **Auto-labeled new portals** — when the game creates a brand-new portal on arrival (no existing exit
  found), optionally stamp its corners to match the entry portal's address automatically, so a
  freshly-dug pair links itself without the player placing any blocks by hand.

## Related

- [Arrival cue](../troubleshooting/arrival-cue.md) — could also fire when an auto-labeled portal is created

## Open questions

- Which hook point in `PortalForcer`'s portal creation works on both Fabric and NeoForge? (Decide during
  implementation; may change the effort estimate.)

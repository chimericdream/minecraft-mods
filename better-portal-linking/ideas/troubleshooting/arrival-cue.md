# Arrival cue when an address matched

> Shortlist #3 · Tier 1 — Quick wins · Effort **S** · Value ★★ · Votes — · My vote **Yes**
> Status: **Exploring**

## Description

A subtle particle burst and/or sound at the destination when address matching, not vanilla, picked the
exit, so the feature is noticeable without reading logs. Server-side `sendParticles` plus one sound, fired
from the same place the log decision is made.
**Recommendation:** ship with a config toggle (A4) and a way to turn off both the particle and the sound
independently (A3, since neither should be the only signal). Use an existing vanilla sound and particle
(N3 isn't a problem, since this is a new cue, not a duplicate). Defer anything fancier. Pairs with #1 for
troubleshooting and with #4 for discovery.

## Decisions

_None yet._

## Brainstorm variants

From [`brainstorms/2026-08-18-legacy/potential-features.md`](../brainstorms/2026-08-18-legacy/potential-features.md)
(Troubleshooting):

- **In-world feedback** — a subtle particle or sound cue on arrival when address matching picked the
  destination, so the feature's effect is noticeable without checking logs.

## Related

- [`/portallink debug` command](portallink-debug-command.md) — the text-based way to see the same decision
- [Address preview](address-preview.md) — helps players discover and check addresses before travelling

## Open questions

_None yet._

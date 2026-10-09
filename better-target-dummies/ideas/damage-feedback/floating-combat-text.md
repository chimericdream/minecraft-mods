# Floating combat text

> Shortlist #6 · Tier 2 — Solid mid-size features · Effort **M** · Value ★★ · Votes — · My vote **Yes**
> Status: **Exploring**

## Description

Shows the damage number above the dummy so everyone watching sees it, not only the attacker reading their
action bar.
**Recommendation:** spawn a short-lived vanilla text display entity rather than writing custom
rendering, which keeps it close to M. Make it a config toggle and keep the text readable without
relying on color (A3). Related: #1 and #7, which also expose damage data.

## Decisions

_None yet._

## Brainstorm variants

From [`brainstorms/2026-08-24-legacy/potential-features.md`](../brainstorms/2026-08-24-legacy/potential-features.md)
(Damage feedback):

- **Floating combat text** — render the damage number in-world above the dummy (like many test-dummy mods do) instead of only the action bar, so damage is visible to everyone watching, not just the attacker.

## Related

- [DPS / average damage readout](dps-average-damage-readout.md) — also exposes damage data
- [Running damage log](running-damage-log.md) — also exposes damage data

## Open questions

_None yet._

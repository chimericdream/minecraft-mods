# Status effect handling for bound mobs

> Shortlist #4 · Tier 1 — Quick wins · Effort **S** · Value ★★ · Votes — · My vote **Maybe**
> Status: **Exploring**

## Description

Bound mobs can currently receive potion effects. Absorption in particular skews the reported number, since
only armor and magic absorption are accounted for and not the Absorption effect.
**Recommendation:** a config option with three behaviors: allow all effects (today), block all effects, or
allow all but make the readout account for Absorption. Default to allowing effects, since testing against
Resistance or Weakness is a legitimate use. It changes testing behavior rather than balance, but A2/A4
both still point to a config.

## Decisions

_None yet._

## Brainstorm variants

From [`brainstorms/2026-08-24-legacy/potential-features.md`](../brainstorms/2026-08-24-legacy/potential-features.md)
(Dummy behavior):

- **Status effect immunity toggle** — decide whether a bound dummy should be able to receive potion effects (currently it can; effects like Absorption would slightly skew the reported damage number since only armor/magic absorption are accounted for, not the Absorption effect).

## Related

- [DPS / average damage readout](dps-average-damage-readout.md) — both depend on an accurate damage number

## Open questions

_None yet._

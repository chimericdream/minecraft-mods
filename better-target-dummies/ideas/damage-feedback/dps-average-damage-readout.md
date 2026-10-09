# DPS / average damage readout

> Shortlist #1 · Tier 1 — Quick wins · Effort **S** · Value ★★★ · Votes — · My vote **Yes**
> Status: **Exploring**

## Description

Aggregates hits over a short window into an average damage per hit and damage per second, so weapons and
enchantments can be compared directly instead of by eyeballing individual action-bar numbers. This is the
payoff of the mod's whole purpose, and the hit-damage readout it extends already exists.
**Recommendation:** append the average and DPS to the existing action-bar message, using a rolling window
that resets after a few seconds without a hit. Make the window length a config option (A4) and keep the
state in memory only. Skip any GUI. Related: #7, which would keep the individual hits this summarizes.

## Decisions

_None yet._

## Brainstorm variants

From [`brainstorms/2026-08-24-legacy/potential-features.md`](../brainstorms/2026-08-24-legacy/potential-features.md)
(Damage feedback):

- **DPS / average damage** — aggregate a short combat window into an average-damage-per-hit or damage-per-second readout, useful for weapon/enchantment comparisons.

## Related

- [Running damage log](running-damage-log.md) — would keep the individual hits this summarizes
- [Floating combat text](floating-combat-text.md) — another way to show damage data

## Open questions

_None yet._

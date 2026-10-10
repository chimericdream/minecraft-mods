# Amplifier control per pyramid level

> Shortlist #8 · Tier 2 — Solid mid-size features · Effort **M** · Value ★★ · Votes — · My vote **Maybe**
> Status: **Exploring**

## Description

Configure the effect level each pyramid tier grants (for example Haste II at level 2, Haste III at level
4), instead of vanilla's fixed primary/secondary split. **Recommendation:** a small table of
level → amplifier in the config, default vanilla. It's balance-affecting, so the toggle is mandatory (A2).

## Decisions

- **Direction (decided 2026-10-09):** the mod is allowed to grow new blocks. For example, an "advanced" beacon with more powerful effects could carry this, instead of changing the vanilla beacon.

## Related

- [Effect pool via datapack](effect-pool-datapack.md) — same data-driven beacon work.
- [Variable bonuses depending on payment](payment-bonuses.md) — same data-driven beacon work.
- ["Inverted" beacons](inverted-beacons.md) — sibling new-block idea.

## Open questions

- Does this change the vanilla beacon's levels, or belong to a new advanced beacon block? (A server can still run near-vanilla either way.)

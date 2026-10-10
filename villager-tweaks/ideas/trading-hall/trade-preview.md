# Trade preview on locked trades

> Shortlist #16 · Tier 3 — Big bets · Effort **L** · Value ★ · Votes — · My vote **Maybe**
> Status: **Exploring**

## Description

Show what a locked trade will restock to, or reveal all trade tiers greyed out so hall builders can plan
without leveling every villager. **Recommendation:** hard, because later trade tiers aren't generated until
the villager levels up, so showing them means predicting generation. Skip unless you find a simple way to
do it.

## Decisions

- Your note on the vote: the trade set looks deterministic after the initial roll, which would make previewing cheap. This needs to be checked in the villager code (when each tier's offers are generated) before the effort estimate of L holds.

## Related

- [Workstation checker](workstation-checker.md) — same trading-hall group.

## Open questions

- Are later-tier offers really fixed when the villager's trades are first rolled, or generated at level-up?

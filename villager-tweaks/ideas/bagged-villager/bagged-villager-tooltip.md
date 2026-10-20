# Bagged Villager tooltip

> Shortlist #1 · Tier 1 — Quick wins · Effort **S** · Value ★★★ · Votes — · My vote **Yes**
> Status: **Building** (unreleased, `6.4.0-beta.0`)

## Description

The Bagged Villager item shows what is inside it on hover, so a chest of bagged villagers isn't a lottery.

- Always: one gray line, "<Type> <Profession> · Level N (Rank)", e.g. "Swamp Librarian · Level 4 (Expert)".
- Offers present and the setting above 0: a dark-gray "Hold Shift to see trades" hint. While Shift is held,
  the trade **results** only (no costs), in the villager's own offer order, one bullet each, capped at the
  setting, then "…and N more". An enchanted book shows its enchantment instead of "Enchanted Book"; a stack
  of more than one shows "N × Item".
- Client setting **Bagged villager tooltip trades** (Misc, 0–20, default 5). 0 removes the hint and the list
  but keeps the summary line.
- Read straight from the item's stored villager data; no new data on the item.

Out of scope: trade costs, locked/future trades from trade preview, out-of-stock markers.

## Decisions

- 2026-10-19 — Default view is profession and level only; the trade list appears while holding Shift.
- 2026-10-19 — Trades list the result item only, not the cost.
- 2026-10-19 — Trades are listed in the villager's own offer order (lowest level first), not highest first.
- 2026-10-19 — The summary line also names the villager's biome type.
- 2026-10-19 — The setting is a "max trades shown" number where 0 turns the list off.

## Related

- [Reputation viewer](../reputation/reputation-viewer.md) — another way to inspect a villager.

## Open questions

_None._

Verification: manual steps in `TEST_PLAN.md` (2a). Dependencies: none.

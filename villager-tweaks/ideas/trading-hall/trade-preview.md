# Trade preview on locked trades

> Shortlist #16 · Tier 3 — Big bets · Effort **L** · Value ★ · Votes — · My vote **Maybe**
> Status: **Building**

## Description

Show every trade a villager will offer at the levels it hasn't reached yet, greyed out and unselectable, with a
tooltip line "Available at (rank) level", so hall builders can plan without leveling every villager. Previews stay
until the villager is max level.

## Decisions

- Vanilla doesn't make this cheap. A villager rolls only its current level's trades, the first time its offers are
  read, and each roll draws from a random sequence shared by every villager in the world
  (`trade_set/<profession>/level_N`). What a villager sells at level 4 depends on how many other villagers of that
  profession rolled before it, so it can't be predicted. (Checked in the 26.2 code; the shared-sequence behavior is
  from memory, since `MinecraftServer`/`RandomSequences` aren't in the `.sources` cache.)
- So the mod rolls the missing future levels itself and stores them on the villager (`VTFutureTrades` in its NBT).
  Level-up hands out the stored offers instead of rolling again. The cost is that trades no longer match vanilla for
  the same seed.
- Rolling happens when a player starts trading, not when the villager gets a job, so villagers from before the mod are
  covered too: only levels above their current one are filled in and their existing offers are left alone.
- A profession change throws the stored offers away. Previews can be switched off with the **Trade preview** option.
- The client gets the locked offers in a separate packet after vanilla's offers packet and appends them to its own
  trade list. They are never added to the server's offers.
- Locked rows show base prices, without the player's reputation or demand adjustments.

## Related

- [Workstation checker](workstation-checker.md) — same trading-hall group.

- In 26.2 a villager only counts down to a level-up while nobody is trading, so the level-up waits until the screen
  closes. With previews on, the same 40 tick countdown runs during a trade, then the villager levels up, its prices
  are recalculated, and vanilla's own resend of the offers refreshes the open screen. The locked rows are sent again
  after every resend, which also keeps them from vanishing when the villager restocks mid-trade. Later Minecraft
  versions do the level-up refresh themselves, so this part may become unnecessary after a port.

## Open questions

_None._

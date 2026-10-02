# Pumpkin contents message

> Source: raised while refining [Candle-lit pumpkins](candle-lit-pumpkins.md), 2026-10-01 · Status: **Ready**
> Must be built after Candle-lit pumpkins, never before.

## Description

Show the player what a decorated pumpkin currently holds, as an overlay message above the hotbar, the
same way [Houdini Block](../../../houdini-block/common/src/main/java/com/chimericdream/houdiniblock/items/HoudiniBlockItem.java)
announces its placement mode (`player.sendOverlayMessage(Component.translatable(...))`, sent from the
server side only).

The problem it solves: a snuffed pumpkin uses the plain unlit overlay whether it is hollow (ready for a
torch or candle) or holds one to four candles, so the player can't tell the two apart by looking.

**What the player sees.** One short message per state, giving the contents and whether they are lit.
Candle colors are not listed. For example:

- `Empty`
- `Torch`, `Soul Torch`, `Copper Torch`, `Redstone Torch` (no lit status, since torches can't be
  snuffed)
- `1 candle (lit)` … `4 candles (lit)`, and `1 candle (snuffed)` … `4 candles (snuffed)`

**When it shows.**
- **Inspect:** sneak + empty-hand click on a decorated pumpkin shows the message and changes nothing.
  (A plain empty-hand click on a candle pumpkin snuffs it, so sneaking is the safe "look" gesture.)
- **After a player's change:** adding a candle, snuffing, relighting, and removing with shears each show
  the new contents.
- **Never from a dispenser.** The dispenser behaviors change pumpkins with no player involved, so they
  send no message.

**In scope:** decorated pumpkins only, and the contents the mod itself can put inside (torches and
candles).
**Out of scope:** vanilla carved pumpkins and jack o'lanterns, listing candle colors, a crosshair or
HUD element, and any message from a dispenser.

**Dependencies:** [Candle-lit pumpkins](candle-lit-pumpkins.md), which defines the candle contents and
the snuffed state. The message needs one translation key per state in the mod's lang file. Nothing
chimeric-lib or loader-specific.
**Verification:** a JUnit test that each pumpkin state maps to the right translation key, plus manual
steps in the mod's `TEST_PLAN.md` for the sneak-click, the after-change messages, and the silent
dispenser.

## Decisions

- 2026-10-01 — Sneak + empty-hand click is the inspect gesture.
- 2026-10-01 — The message gives the count and lit state only, not candle colors.
- 2026-10-01 — The message also shows after a player's own change (add, snuff, relight, shears), and
  never when a dispenser fires.
- 2026-10-01 — Only candles show a lit status. Torches are just `Torch`, `Soul Torch`, `Copper Torch`,
  and `Redstone Torch`.
- 2026-10-01 — Effort stays **S**.
- 2026-10-01 — This must not be built before Candle-lit pumpkins.

## Brainstorm variants

_This idea did not come from a brainstorm._

## Related

- [Candle-lit pumpkins](candle-lit-pumpkins.md) — introduces the snuffed-with-candles state this idea
  makes visible.

## Open questions

_None._

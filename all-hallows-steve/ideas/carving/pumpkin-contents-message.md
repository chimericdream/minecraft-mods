# Pumpkin contents message

> Source: raised while refining [Candle-lit pumpkins](candle-lit-pumpkins.md), 2026-10-01 · Status: **Exploring**
> To be refined and built directly after Candle-lit pumpkins.

## Description

Show the player what a decorated pumpkin currently holds, as an overlay message above the hotbar, the
same way [Houdini Block](../../../houdini-block/common/src/main/java/com/chimericdream/houdiniblock/items/HoudiniBlockItem.java)
announces its placement mode (`player.sendOverlayMessage(Component.translatable(...))`, sent from the
server side only).

The problem it solves: a snuffed pumpkin uses the plain unlit overlay whether it is hollow (ready for a
torch or candle) or holds one to four candles, so the player can't tell the two apart by looking. The
message names the contents, for example:

- Empty
- Torch, Soul Torch, Copper Torch, or Redstone Torch (the lit variants already say so by their glow)
- 1–4 candles, lit or snuffed

**In scope:** decorated pumpkins only, and the contents the mod itself can put inside (torches and
candles).
**Out of scope:** vanilla carved pumpkins and jack o'lanterns, and any new HUD element beyond the
overlay message.

## Decisions

_None yet._

## Brainstorm variants

_This idea did not come from a brainstorm._

## Related

- [Candle-lit pumpkins](candle-lit-pumpkins.md) — introduces the snuffed-with-candles state this idea
  makes visible.

## Open questions

- **What triggers the message?** It has to work without changing the pumpkin, since an empty hand
  snuffs a candle pumpkin. Options: show it after every change (add, snuff, relight, shears), plus an
  inspect-only gesture; or only the inspect gesture. What is that gesture (sneak with an empty hand,
  looking at the pumpkin, another item)?
- **Wording.** Does it show the candles' colors, or just a count and whether they are lit?
- **Dispensers.** Presumably no message, since there is no player.
- **Effort.** Likely **S** (one translation key per state and one interaction hook). Re-estimate once
  the trigger is settled.
- **Verification** (to fill in): probably a GameTest for the message sent per state.

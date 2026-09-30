# Candle-lit pumpkins

> Shortlist #1 · Tier 1 — Quick wins · Effort **S** · Value ★★★ · Votes 4 · My vote **Yes**
> Status: **Exploring**

## Description

Light a decorated pumpkin with 1–4 candles instead of a torch. Light level scales with candle count, and
the glow texture takes the candle's color. Snuff it with an empty hand and relight it with flint and
steel. This is an almost direct extension of the existing lit-pumpkin variants and the
`DispenserBehaviors` helper, and vanilla players already understand candle stacking.

*Note: Minecraft light has no color, so "tinted" means the glow texture or overlay, the same as the
current soul/copper/redstone variants.*

## Decisions

_None yet._

## Brainstorm variants

From [combined-ideas § 2](../brainstorms/2026-09-29/combined-ideas.md#2-pumpkin-lighting). Bracketed numbers are
the `brainstorms/2026-09-29/agentN.md` files that suggested each variant.

- 1–4 candles. Light level scales with candle count, candle color tints the glow, and it flickers more
  softly than a torch. Snuff it with an empty hand. Reuses the lit-pumpkin and dispenser code [13].
- Candle color follows the dye of the candle used [1].
- Different candle positions or liners change how the eyes and openings look [2].
- **Insert-dependent glow color** [7] — The light source inside tints the result: amber from tallow,
  cooler from glowstone, sickly green from something fungal.

## Related

- [Wearable decorated pumpkins](wearable-decorated-pumpkins.md) — does a candle-lit pumpkin stay lit on
  your head?
- [Turnip lanterns](../harvest-crafts/turnip-lanterns.md) — another small carved light source.

## Open questions

_None yet._

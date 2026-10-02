# Candle-lit pumpkins

> Shortlist #1 · Tier 1 — Quick wins · Effort **S** · Value ★★★ · Votes 4 · My vote **Yes**
> Status: **Ready**

## Description

Light a decorated pumpkin with 1–4 candles instead of a torch. Each candle adds a step of brightness, in
four levels that match vanilla candles (light 3, 6, 9, and 12 for 1–4 candles), so a full pumpkin is
a little dimmer than one lit with a regular torch (14).
Each of the four levels has its own glow overlay texture, and candle color has no effect on it. Any of
the 17 vanilla candles can be added, one at a time with shift-click (the same shift-gated flow as
torches). A pumpkin can hold either candles or a torch, never both. Candles can be added whether the
pumpkin is lit or snuffed, up to four. Shears take all the candles back out, and each comes back in the
color it went in. An empty hand snuffs the flame, and the pumpkin keeps its candles while snuffed, so
flint and steel relights it. A dispenser can add a candle, relight, or shear, and fails (rather than
dropping the item) on a full pumpkin, as torches do. This is an almost direct extension of the existing
lit-pumpkin variants and the `DispenserBehaviors` helper, and vanilla players already understand candle
stacking.

**In scope:** decorated pumpkins only, with the pumpkin's dye color and stencils preserved through every
transition.
**Out of scope:** colored or tinted glow, vanilla carved pumpkins / jack o'lanterns, weather snuffing,
and candles burning down.

**Dependencies:** `DispenserBehaviors` in chimeric-lib (already used for torches), and the shift-click
torch mixin (`AHS$TorchBlockItemMixin`), which needs a candle counterpart. No other ideas, and nothing
loader-specific beyond that mixin.
**Verification:** a GameTest for add / snuff / relight / shears / break-drops (including candle colors
and the pumpkin's dye and stencils), plus a visual smoke test of the four overlays.

## Decisions

- 2026-10-01 — No color-based lighting. The candle's color doesn't tint the glow; the idea is four light
  levels only.
- 2026-10-01 — Shift-click adds a candle; shears remove the candles; an empty hand snuffs; flint and
  steel relights.
- 2026-10-01 — Decorated pumpkins only. Vanilla carved pumpkins and jack o'lanterns stay torch-only.
- 2026-10-01 — Breaking the pumpkin returns its candles. No weather snuffing and no burn-down.
- 2026-10-01 — Light levels match vanilla candles: 3 / 6 / 9 / 12 for 1–4 candles. This replaces the
  earlier idea that four candles should equal the torch pumpkin (14).
- 2026-10-01 — Each light level gets its own glow overlay texture (four in all). The user is creating
  the textures.
- 2026-10-01 — Any vanilla candle can be added, and removed candles keep their color (the block
  remembers each one).
- 2026-10-01 — Candles can be added while the pumpkin is lit or snuffed (up to four). A snuffed pumpkin
  keeps its candles, so flint and steel relights it. A pumpkin holds candles or a torch, never both.
- 2026-10-01 — Dispensers mirror the torch behaviors: a candle adds one, flint and steel relights, and
  shears remove. A full pumpkin fails rather than dropping the item.
- 2026-10-01 — A snuffed pumpkin uses the plain unlit overlay, even when it holds candles. Telling a
  hollow pumpkin from one with candles is handled by [Pumpkin contents message](pumpkin-contents-message.md),
  built right after this idea.
- 2026-10-02 — Candle-lit pumpkins can't be worn. Settled in the wearable-pumpkins idea (only unlit
  decorated pumpkins are wearable).
- 2026-10-01 — Overlay textures are named `{stencil}_candlelit_{count}.png` (count 1–4), with one per
  stencil per count. Underscores, to match the existing overlays (`creeper_lit_blue.png`).

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

- [Wearable decorated pumpkins](wearable-decorated-pumpkins.md) — lit pumpkins, candle-lit included, are
  not wearable.
- [Pumpkin contents message](pumpkin-contents-message.md) — follow-up that must be built after this one:
  tells the player whether a snuffed pumpkin is hollow or holds candles.
- [Turnip lanterns](../harvest-crafts/turnip-lanterns.md) — another small carved light source.

## Open questions

- **Implementation (decide while building):** one block with candle-count and lit properties versus
  separate blocks like the four torch variants.

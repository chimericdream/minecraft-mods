# Directional masks

> Shortlist #3 · Tier 2 — Solid mid-size features · Effort **M** · Value ★★★ · Votes — · My vote **Maybe**
> Status: **Exploring**

## Description

Configure *which* neighbors get notified: suppress upward updates only, skip the north face, and so on.
It turns the block from an on/off trick into a precision instrument, which is the heart of the
"tool for technical players" identity. **Recommendation:** store six per-face flags on the item and
block (blockstate or a small block entity, to be decided), with a simple way to set them in-world. The
design question is the control scheme for six faces plus four modes without a screen. A small GUI
may be worth it. It builds directly on the existing mixin (A1).

## Decisions

_None yet._

## Related

- [Update visualization](update-visualization.md) — the preview makes masks usable; build together.
- [Update capture and replay](../tricks/update-capture-and-replay.md) — replay would need to respect the masks.

## Open questions

_None yet._

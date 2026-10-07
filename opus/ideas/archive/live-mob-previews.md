# Live mob previews

> Source: initial design discussion, 2026-10-06 (split from "Live mob and multiblock previews") · Effort **M** · Value ★★★
> Status: **Shipped** in 1.0.0-beta.0

## Description

The `entity` widget draws a live mob instead of today's labelled placeholder. The props (`id`, `scale`, `label`) already exist. It needs the 26.2 entity/GUI render-state APIs, which differ from earlier versions.

What the player sees:

- The mob stands in an idle pose and turns slowly around its vertical axis. No walking, no head tracking, no dragging.
- The mob is auto-fitted to its box, so a bat and a ghast both look right with no author tuning. `scale` is a multiplier on that fit.
- The box follows the mob's shape instead of a fixed 64×80. Its width and height are each a multiple of 16 px, with a little padding on all four sides, so taller-than-wide mobs get a tall box and roughly cubic mobs (bats, ghasts) get a square one without any special-casing. The mob itself is not scaled to a multiple of 16.
- Only living mobs render live. Any other entity type (arrows, boats, item frames) keeps the labelled placeholder, and the book validator warns about it.
- If a mob can't be created on the client (typically another mod's entity), the placeholder is shown with a short note or tooltip saying why, and the problem is logged once.
- The mob always has its default spawn appearance. Baby, variant, colour, equipment and custom names are not configurable.

**Out of scope:** dragging or any other interaction beyond the auto-rotate, mob variants or NBT, and multiblock previews (see [multiblock previews](../reader/multiblock-previews.md)).

**Dependencies:** 26.2 entity/GUI render-state APIs (read the decompiled sources with the `mc-source-decompile` skill). `BookWidgets.entity` has a TODO pointing at all-hallows-steve's `CarvingStationScreen` as a reference. Dynamic box size touches `WidgetSizer` (a client-supplied lookup callback).
**Verification:** Visual smoke test (`mc-visual-smoke-test`) of a page with a tall mob, a wide mob and a cubic mob.

## Decisions

- 2026-10-06 — `scale` is a multiplier on an auto-fit, not an absolute render scale.
- 2026-10-06 — The widget box is dynamic: its aspect ratio follows the mob's bounding box (tall mob, tall box; cubic mob, square box).
- 2026-10-06 — Slow auto-rotate in an idle pose; no mouse interaction.
- 2026-10-06 — Living mobs only. Other entity types fall back to the placeholder.
- 2026-10-06 — No variant/NBT/equipment customisation in v1.
- 2026-10-06 — `WidgetSizer` gets a size-lookup callback supplied by the client; the standalone validator omits it and falls back to the old fixed box. Revisit if it proves awkward.
- 2026-10-06 — Box width and height are multiples of 16 px; aspect ratios fall out of that rather than a separate square threshold. The mob's own dimensions are not snapped to 16; the box is sized to the mob plus a little padding on all four sides.
- 2026-10-06 — A mob that fails to construct falls back to the placeholder, is logged to the client log, and shows a note (tooltip or caption, depending on length) explaining the problem.
- 2026-10-06 — Minimum and maximum box sizes, and how the mob rounds up to the box, are settled during implementation.
- 2026-10-06 — (Superseded after in-game testing; see the next entry.) Dynamic per-mob boxes and per-mob auto-fit.
- 2026-10-06 — All mobs draw at one shared scale anchored on the villager (64 GUI px per block at GUI scale 4; other GUI scales keep the same physical size), so mobs on a page keep their relative sizes. The box is the villager's box and grows in 16 px steps only when a mob won't fit; `scale` multiplies the shared scale.
- 2026-10-06 — Rotation speed and start angle are left to the implementer.

## Brainstorm variants

_None yet._

## Related

- [Multiblock previews](../reader/multiblock-previews.md) — the other half of the original idea; shares the "rotate a 3D thing in a GUI box" problem.
- [Search that finds recipes, items and mobs](../reader/search-widgets-and-code.md) — indexes entity widgets by display name.

## Open questions

_None._

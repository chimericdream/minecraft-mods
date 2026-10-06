# Live mob and multiblock previews

> Source: initial design discussion, 2026-10-06 · Effort **L** · Value ★★★
> Status: **Exploring**

## Description

The `entity` widget draws a rotating live mob instead of today's labelled placeholder, and a new `multiblock` widget shows a small rotatable structure from a list of layers. Both need the 26.2 GUI render-state APIs, which differ from earlier versions.

**Out of scope:** interacting with the preview beyond rotating it, and animating block entities.

**Dependencies:** 26.2 entity/GUI render-state APIs (read the decompiled sources with the `mc-source-decompile` skill); `WidgetSizer` already reserves the space.
**Verification:** Visual smoke test (`mc-visual-smoke-test`) of a page with each widget.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

_None yet._

## Open questions

- Do multiblocks reuse a datapack structure template, or are they described inline in the page? Recommend inline first.

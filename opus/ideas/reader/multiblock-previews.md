# Multiblock previews

> Source: initial design discussion, 2026-10-06 (split from "Live mob and multiblock previews") · Effort **L** · Value ★★★
> Status: **Exploring**

## Description

A new `multiblock` widget shows a small rotatable structure built from a list of layers. It needs the 26.2 GUI render-state APIs, which differ from earlier versions, and a new widget type wired through `WidgetTypes`, `WidgetSpecs` and `WidgetSizer`.

**Out of scope:** interacting with the preview beyond rotating it, and animating block entities.

**Dependencies:** 26.2 GUI render-state APIs (read the decompiled sources with the `mc-source-decompile` skill). Likely shares rotation/camera code with [live mob previews](live-mob-previews.md), so build that first.
**Verification:** Visual smoke test (`mc-visual-smoke-test`) of a page with the widget.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

- [Live mob previews](live-mob-previews.md) — the other half of the original idea.

## Open questions

- Do multiblocks reuse a datapack structure template, or are they described inline in the page? Recommend inline first.

# Book skins and themes

> Source: initial design discussion, 2026-10-06 · Effort **M** · Value ★★
> Status: **Exploring**

## Description

Let a book choose its look: colours, panel and sidebar textures, and an optional fixed-size page frame, from `book.yml` plus textures in the pack. The parchment theme in `BookTheme` becomes the default skin, and a resource pack can restyle every book.

**Out of scope:** a two-page spread layout (the reader is a scrolling column by design).

**Dependencies:** `BookTheme` (exists), `book.yml` schema additions, 9-slice texture drawing in the renderer.
**Verification:** JUnit for parsing theme settings; visual smoke tests of a restyled book.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

_None yet._

## Open questions

- Colours only first, or textures from the start? Recommend colours first.

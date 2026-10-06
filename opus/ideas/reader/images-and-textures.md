# Images and texture icons

> Source: initial design discussion, 2026-10-06 · Effort **M** · Value ★★
> Status: **Exploring**

## Description

Draw `![alt](namespace:textures/path.png)` images in pages (scaled to the column, with the layout reserving the height up front) and `texture:` chapter icons in the table of contents. Images are resource-pack textures, so they ship with the book.

**Out of scope:** animated or remote images.

**Dependencies:** Image size must be known at layout time, so the reader needs a small texture-size lookup; `Inline.Image` and `IconRef.Kind.TEXTURE` already parse.
**Verification:** JUnit for layout with a fake size lookup; visual smoke test.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

_None yet._

## Open questions

- Add an optional width/height syntax such as `![alt](path){width=64}`? Recommend yes, via the same attribute style as heading ids.

# Images and texture icons

> Source: initial design discussion, 2026-10-06 · Effort **L** (re-estimated 2026-10-12: new block element, texture loading, enlarge overlay) · Value ★★
> Status: **Ready**

## Description

Draw texture images in pages and `texture:` chapter icons in the table of contents. Images are resource-pack textures, so they ship with the book.

```markdown
![A hopper chain](texture:yourpack:gui/hopper_chain)
![Small icon](texture:yourpack:gui/arrow){width=32 align=right}
```

- **Address:** `texture:<namespace>:<path>`, the same form as the book's `texture` key and the `texture:` icon scheme. The path is relative to `textures/` and has no `.png` (`yourpack:gui/diagram` is `assets/yourpack/textures/gui/diagram.png`). The scheme sits beside `item:`, `block:` and `entity:` in the parser. A plain path with no known scheme keeps drawing its alt text.
- **Position decides inline or block.**
  - An image alone in its paragraph is a **block**: centred in the column, scaled down to fit and never scaled up, with the layout reserving its height up front.
  - An image inside a line of text stays **inline** at icon size, like `item:` icons.
- **Side by side.** Several images in a paragraph with no text between them form one **row**. The row is laid out as a single block with a small fixed gap, so images share a baseline, wrap to a new row when they don't fit, and are aligned as a group by the first image's `align=`. Each image keeps its own caption.
- **`{width=N}`** sets an image's width in book pixels, in the same attribute style as heading ids. Height follows the aspect ratio. Never wider than the column.
- **`align=left|center|right`** positions a block image or a row in the column. Default is center.
- **Caption:** a block image's alt text is drawn under it as a muted caption. An image with empty alt has no caption.
- **Click to enlarge:** clicking a block image opens it at full size over the page, scaled to fit the screen. Any click or Esc closes it. Inline images aren't clickable.
- **Missing texture:** when the texture can't be found, draw a labelled placeholder box (using the alt text) and have the validator warn, so a broken path is visible to authors and harmless to readers.
- **Chapter icons:** `icon: texture:ns:path` in front matter or `book.yml` draws the texture at icon size in the contents. This replaces the `TODO(icons)` in `BookScreen`.

**Out of scope:** animated textures, remote images, and images inside `:::` containers beyond what existing containers already allow.

**Dependencies:**
- A client-side texture-size lookup. Layout needs the size before drawing, so the reader reads each PNG's dimensions from the resource manager and caches them. It is injected into the layout engine as an interface so tests can supply a fake.
- The parser already produces `Inline.Image` and `IconRef.Kind.TEXTURE`. Block and row images need a new block-level element, and `Element.Icon` is the wrong shape for them (it is item-sized).
- Registering the texture for drawing, since arbitrary pack textures are not in an atlas. Compare how the book `texture` key handles this, which only covers `textures/item/`.
- The validator's file checks, for the missing-texture warning.

**Verification:** JUnit for parsing (scheme, `{width}`, `align`, row grouping) and for layout with a fake size lookup, including row wrapping and scale-down. A visual smoke test with a block image, a row of three, an inline image, a missing texture, and click-to-enlarge.

## Decisions

- 2026-10-12 — Image addresses use `texture:ns:path`, with no `textures/` prefix and no `.png`, matching the book `texture` key and the `texture:` icon scheme.
- 2026-10-12 — Position decides inline or block: alone in a paragraph is a block, inside text is inline icon size. Images can also be grouped side by side into a row that is laid out as one block, with an optional alignment.
- 2026-10-12 — Row syntax: consecutive images in one paragraph with only spaces or a single line break between them (no other text) form a row; `align=` on the first image applies to the whole row. No `:::row` container.
- 2026-10-12 — Row layout: a fixed small gap; each image keeps its own `{width}` (or natural size) and the row wraps when it overflows. Each image keeps its own caption.
- 2026-10-12 — v1 includes the `{width=N}` attribute, click to enlarge, alt-text captions, and a missing-texture placeholder with a validator warning.

## Brainstorm variants

_None yet._

## Related

- [Book item texture](../archive/book-item-texture.md) — shares the "texture id in the pack" resolution and the missing-texture handling. Its `texture` key is limited to `textures/item/` because of the item atlas; this idea is not, so it needs its own loading path.
- [Reader accessibility](accessibility.md) — alt text is the caption and the narrator text, so keep alt required in the guide's advice.
- [Book skins and themes](../authoring/book-skins.md) — skin textures may reuse the same texture loader.
- [Deep nesting and long titles in the sidebar](deep-nesting-and-long-titles.md) — texture chapter icons take the same icon slot, so they must respect the same width limits.

## Open questions

Questions that can be decided during implementation:

- **Resolution on high GUI scales.** Recommend drawing at the texture's pixel size relative to book pixels (1 texel = 1 book pixel) before scaling down, so pixel art stays crisp, with nearest-neighbour filtering.

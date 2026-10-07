# Book item texture

> Source: user request, 2026-10-07 · Effort **M** · Value ★★
> Status: **Exploring**

## Description

An optional `texture` property in `book.yml` that changes how that book's item looks in the game. Today every book is the same generic `opus:book` item with the same closed-tome icon, so a pack with several books can't tell them apart in an inventory, a chest or an item frame.

```yaml
title: Pannotia
texture: pannotia:item/guide_book
```

The value is a texture id shipped in the pack's assets. When it is missing, or the texture can't be found, the book keeps the default tome icon. Because the texture is resolved from the stack's `opus:book_id`, a book given by `/give`, a loot table or a recipe looks right with no extra setup.

**Out of scope:** per-book 3D models, animated textures, and changing the book's in-hand pose. Screen and panel styling belongs to [book skins](book-skins.md).

**Dependencies:** `BookMeta` (add `texture` to `KNOWN_KEYS`), a way to pick an item model from a component value (see open questions), and the validator's file checks.
**Verification:** JUnit for parsing and the validator warning on a missing texture; a visual smoke test with two books showing different icons.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

- [Book skins and themes](book-skins.md) — `texture` here is the item's icon only; skins restyle the reader. The `book.yml` key naming should be chosen together.
- [Images and texture icons](../reader/images-and-textures.md) — shares the "texture id in the pack" resolution and the missing-texture handling.
- [Player-usable give command](../integration/player-give-command.md) — gives players the book that carries this texture.

## Open questions

- How does the item pick a texture from the component? Vanilla item model definitions can select on some data components, but only for values listed ahead of time, and a book pack's ids aren't known to Opus's own model file. Check the 26.2 sources (`mc-source-decompile`) for what `select` and `range_dispatch` can do. If they can't, a custom item model type that looks the id up in `BookRepository` is the fallback, and it needs registering on both loaders.
- `book.yml` is a client-side asset. Is a texture id enough, or should the key point at a full item model id (`model:`) for authors who want more control? Recommend texture only for now.
- Should the texture also be the book's icon in the reader's own UI, such as the contents header, when `icon` is unset?

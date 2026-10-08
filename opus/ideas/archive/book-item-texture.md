# Book item texture

> Source: user request, 2026-10-07 · Effort **L** · Value ★★
> Status: **Shipped** in 1.1.0

## Description

Two optional properties in `book.yml`, `texture` and `model`, that change how that book's item looks in the game. Today every book is the same generic `opus:book` item with the same closed-tome icon, so a pack with several books can't tell them apart in an inventory, a chest or an item frame.

```yaml
title: Pannotia
texture: pannotia:item/guide_book
```

- `texture` is a texture id shipped in the pack's assets, under `textures/item/`. The book is drawn as a flat item with that texture.
- `model` is a model file id (`ns:item/foo` loads `models/item/foo.json`) for authors who want more control. When it is set, `texture` is ignored.
- When neither is set, or what they point at can't be found, the book keeps the default tome icon.
- The look is resolved from the stack's `opus:book_id`, so a book given by `/give`, a loot table or a recipe looks right with no extra setup.
- The same look is the book's icon everywhere Opus shows it, including the reader's own UI, by drawing the book's item stack. An `icon` set explicitly in `book.yml` still wins in the reader.

**Out of scope:** animated textures, and changing the book's in-hand pose. Screen and panel styling belongs to [book skins](../authoring/book-skins.md).

**Dependencies:** `BookMeta` (add `texture` and `model` to `KNOWN_KEYS`), a custom item model type registered on both loaders (see Decisions), a small scanner for the two keys that doesn't depend on `BookRepository` (see Decisions), and the validator's file checks.
**Verification:** JUnit for parsing and the validator warning on a missing texture or model; a visual smoke test with books using `texture`, `model` and neither.

## Decisions

- 2026-10-07 — The item gets its look from a custom item model type that reads the stack's `opus:book_id`. Vanilla `select` on `minecraft:component` was ruled out: its cases must be listed ahead of time, so another pack's books can't be covered without overriding Opus's model file.
- 2026-10-07 — The type is registered with a mixin into `ItemModels.bootstrap()` that adds it to the private `ID_MAPPER`, the same way all-hallows-steve registers its item tint source (`AHS$ItemTintSourcesMixin`). The mixin lives in common code, so one copy covers Fabric and NeoForge. Opus already has an empty `opus.mixins.json`, and an access widener file if a field needs widening.
- 2026-10-07 — `book.yml` accepts an optional `model` as well as `texture`. When `model` is supplied, `texture` is ignored.
- 2026-10-07 — The texture is the book's icon anywhere the book is displayed, including in the reader.
- 2026-10-07 — The default items atlas (`atlases/items.json`) has a `directory` source for `item` that scans every namespace, so any texture under `textures/item/` is already stitched in. `texture` is limited to that folder, and the validator warns on anything else. No atlas changes are needed.
- 2026-10-07 — The model type cannot rely on `BookRepository`. Item models are baked, and their model dependencies resolved, in the client resource reload, and `BookRepository` fills in a separate Opus reload listener with no guaranteed order against vanilla's model manager. The model type therefore gets each book's `texture` and `model` from a small scanner that reads only those two keys from every `book.yml` during baking, rather than from `BookRepository`. Quads for a `texture` are built at bake time with the same `ItemModelGenerator` machinery as `item/generated`, via `BakingContext`.

- 2026-10-07 — `model` is a model file id (`ns:item/foo` loads `models/item/foo.json`, as the vanilla `minecraft:model` item type does), not an item definition. A bake-time scan can add it to the model dependencies, while loading an item definition is private to vanilla.
- 2026-10-07 — `book.yml`'s existing `icon` key keeps winning in the reader when an author sets it explicitly. The item look is the icon when `icon` is unset.

## Brainstorm variants

_None yet._

## Related

- [Book skins and themes](../authoring/book-skins.md) — `texture` here is the item's icon only; skins restyle the reader. The `book.yml` key naming should be chosen together.
- [Images and texture icons](../reader/images-and-textures.md) — shares the "texture id in the pack" resolution and the missing-texture handling.
- [Player-usable give command](player-give-command.md) — gives players the book that carries this texture.

## Open questions

Questions that can be decided during implementation:

- Can the scanner use the resource manager safely during baking (`Minecraft.getInstance().getResourceManager()` is the one being reloaded), or does a registered-listener ordering have to be used? Decide during implementation, and check it in the visual smoke test.

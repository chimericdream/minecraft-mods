# Potential Features — Athenaeum

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are
starting points for future planning. Several items build on the README's existing "Planned Features" list
(genres, villager, catalog, advancements) and try to make them concrete.

## Making the planned features concrete

* **Genres & rarities** — every generated book carries a genre (history, poetry, field guide, ghost story,
  cookbook, treatise…) and a rarity tier. Genre shows in the tooltip; rarity tints the cover. Loot tables
  can weight by both.
* **Book Catalog block** — a card-catalog cabinet that indexes every written book in attached bookshelves
  (chiseled or variant) and offers a searchable/browsable UI. The physical answer to "which shelf did I
  put that in?"
* **Bookbinder villager** — a new profession (job site: the Binding Press below) that buys duplicate books
  for emeralds, sells rare genre books, and at master level sells one **series finale** book.
* **Advancements** — "Well Read" (read 10 unique books), "Complete Collection" (finish a series),
  "Out of Print" (find a legendary-rarity book), "Local Historian" (collect every book generated for one
  structure type).

## Authors, series & the written world

* **Procedural authors** — books are attributed to generated authors with consistent names and genres.
  Finding more works "by" the same author makes the world feel authored, literally.
* **Book series** — multi-volume sets scattered across different structure types, giving a concrete reason
  to explore *more kinds* of places (vol. 1 in a stronghold, vol. 2 in a mansion, vol. 3 held by a
  bookbinder).
* **Marginalia** — small chance a looted book contains a previous owner's notes: a hint, a joke, or
  coordinates to a nearby structure. Books as soft treasure maps.
* **Regional flavor** — biome-aware generation, so desert-temple books skew toward desert lore and ocean
  ruin books arrive water-damaged (missing pages, smudged text).

## New blocks & items

* **Binding Press** — a workstation for copying written books more cheaply than the crafting grid, and for
  binding loose **manuscript pages** (a new loot item) into complete books.
* **Writing Desk** — a cozy lectern-alternative with ink/quill slots; doubles as the aesthetic anchor for
  library builds.
* **Book Stand** — displays a single book open to a chosen page, museum-style.
* **Bookmark item** — dye-able; when kept in your inventory, reopening a long book returns to the marked
  page.
* **Dusty Tome** — a sealed loot book that must be brushed (a friendly nod to Archaeology Tweaks) or
  cleaned in a cauldron before it can be read.

## Loot & structure coverage

* **Wider loot placement** — beyond strongholds and mansions: village libraries (temple/librarian houses),
  igloo basements, ancient cities (echoes of lost civilizations write themselves), trail ruins, and end
  cities.
* **Modded-structure hook** — a structure-tag-driven injection system so packs can add Athenaeum books to
  any structure's loot with one tag entry.
* **Config: generation controls** — per-genre and per-structure toggles, plus an allowlist/denylist of
  book pools, as the README's "additional configuration" bullet suggests.

## Stretch ideas

* **Lending ledger** — a block that tracks which player borrowed which book from the catalog. Pure
  multiplayer-server flavor.
* **Patchouli bridge** — auto-generate an in-game "library index" Patchouli book listing everything the
  player has discovered so far.

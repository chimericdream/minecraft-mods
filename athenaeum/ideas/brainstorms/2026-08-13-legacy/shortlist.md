# Athenaeum — Feature Shortlist

Scored and ranked from the legacy backlog in this folder ([`ideas-md.md`](ideas-md.md), your older notes,
and [`potential-features.md`](potential-features.md), the agent brainstorm). There is no
`combined-ideas.md` for a legacy backlog, so this list was built straight from the source notes and
checked against what the mod ships today. Everything that isn't ranked is in
[What was cut and why](#what-was-cut-and-why).

The generic "Add thematic/fun advancements" bullet was dropped on purpose: it was too vague to act on. The
"Advancements" bullet in your notes is covered by the specific ones ranked below (#8).

## How items were scored

- **Effort**, for a two-loader (Fabric + NeoForge) Architectury mod, counting art, datagen and rendering,
  not just code:
  - **S**: a day or two, mostly assets and data, reuses existing code.
  - **M**: a new block or block entity, a new interaction, or a moderate rendering change.
  - **L**: a new entity, custom rendering, worldgen, or a new UI.
  - **XL**: a system that touches many other features.
- **Value**, ★ to ★★★: fit with the mod's identity statement, and how much players would notice it.
- **Votes**: how many independent brainstorm answers suggested it. Legacy items have none, so this column
  shows `—`.

Ranking is by value relative to effort. The identity is "written works worth finding": books, where they
appear, their metadata, and light supporting content like advancements. The mod's **boundary** is strict:
it owns book content, loot placement, metadata and advancements, and a future sibling library mod owns
every block, workstation and mechanic for displaying, making or organizing books. This mod is also in a
popular modpack, so nothing here may break existing worlds (N1); every addition is a new book, a new
option, or a datapack-friendly extension. So the ranked list is content, loot and metadata only. The many
block and workstation ideas in your notes are in [What was cut and why](#what-was-cut-and-why) as sibling
mod ideas.

## Already shipped

- **109 books** in four public-domain collections (Andersen, Brothers Grimm, English and Japanese fairy
  tales), driven by datapacks.
- **Loot in stronghold libraries and woodland mansions**, plus the `get_random_book` loot function, which
  can filter by author and edition. Datapacks can put books in any loot table with it (see the examples).
- **Editions** (first, second and third) with configurable chances. This already covers most of what the
  agent note called "rarity tiers".
- The book data model already has a **genre** field, but no book sets it and nothing reads it yet (#2).

## Ranked list

Linked rows have their own idea files.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | [Wider loot placement](../../loot/wider-loot-placement.md) | S | ★★★ | — | Yes |
| 2 | [Genre metadata](../../books/genre-metadata.md) | S | ★★★ | — | Yes |
| 3 | [More books](../../books/more-books.md) | S | ★★★ | — | Yes |
| 4 | [Modded-structure loot hook](../../loot/modded-structure-hook.md) | S | ★★ | — | Yes |
| | **Tier 2 — Solid mid-size features** | | | | |
| 5 | [Generation controls](../../configuration/generation-controls.md) | M | ★★ | — | Maybe |
| 6 | [Book series](../../books/book-series.md) | M | ★★ | — | Yes |
| 7 | [Longer books](../../books/longer-books.md) | M | ★★ | — | Yes |
| 8 | [Collector advancements](../../progression/collector-advancements.md) | M | ★★ | — | Yes |
| 9 | [Regional flavor](../../flavor/regional-flavor.md) | M | ★★ | — | Maybe |
| 10 | [Marginalia](../../flavor/marginalia.md) | M | ★ | — | Maybe |

## Tier 1 — Quick wins

### 1. Wider loot placement — S · ★★★

Books only appear in two places today. Add them to village libraries (the librarian house), igloo
basements, ancient cities, trail ruins and end cities, from your notes. It is the cheapest way to deliver
the mod's goal of a reason to explore. **Recommendation:** do it with datapacks and the existing loot
function. Pick different collections for different structures (see #6), and make each structure a config
toggle (#5), default on, since a new world gets more books and existing worlds only see them in newly
generated chests.

### 2. Genre metadata — S · ★★★

Use the `genre` field the data model already has: set it on every book, show it in the tooltip, and let the
loot function filter by genre as it does by author and edition. It is the first "additional metadata" item
in your notes. **Recommendation:** start with a small fixed set (fairy tale, folk tale, legend) that fits
the current collections, and add more as #3 brings in new kinds of books. The tooltip line should be
optional in the config. Genres are what make #5 and #9 possible.

### 3. More books — S · ★★★

The top line of your notes. The collections are all fairy tales, so more books of the same kind is a
modest gain, and **new genres** are the better pitch: myths and legends, poetry, short fiction,
natural-history field guides, and original in-game lore. **Recommendation:** pick public-domain sources
(check the license for each one before adding it), and add a collection at a time, each as its own
datapack folder, so any collection can be disabled. Original lore books fit best in structure-specific
chests and give you a chance to write something that belongs to Minecraft.

### 4. Modded-structure loot hook — S · ★★

A structure-tag-driven way for packs to add Athenaeum books to any structure's loot with one tag entry,
from your notes ("including modded structures"). **Recommendation:** read a tag of loot tables (for
example `athenaeum:book_loot_tables`) and add a pool to every table in it, so a pack author never has to
override a loot table by hand. Pairs with #5.

## Tier 2 — Solid mid-size features

### 5. Generation controls — M · ★★

The "additional configuration options" in your notes: toggles per structure, per genre and per collection,
plus an allowlist and a denylist of books, and the chance and roll counts for each placement.
**Recommendation:** the structure toggles come first, because #1 adds a lot of places. Put the per-book
lists in a datapack-readable form, not just the config screen, so pack authors can use them. The existing
edition chances stay as they are.

### 6. Book series — M · ★★

Multi-volume sets scattered across structure types, so volume 1 turns up in a stronghold and volume 2 in a
mansion. It is the best use of "explore more kinds of places". **Recommendation:** model a series as a
`series` and `volume` entry in the book data, tooltip it ("Volume 2 of 5"), and let the loot function
filter by volume. The vendor part of the agent note (volume 3 held by a bookbinder) is a sibling mod idea,
so the structure chests are the only source for now. Fits well with the collection advancements in #8.

### 7. Longer books — M · ★★

From your notes. Check what is limited today: a written book has a hard cap on pages and page length, so a
"long book" has to be a series of volumes or an abridged text. **Recommendation:** treat it as "books
longer than one volume fit". The practical version is splitting a long work across volumes using #6, with
a "Part N of M" title. Do not try to exceed the written-book limits.

### 8. Collector advancements — M · ★★

Specific advancements for the books: "Complete Collection" (own every book in one collection), "First
Printing" (find a first edition, which replaces the agent note's "Out of Print"), and "Local Historian"
(own every book that can generate in one structure type). They give the mod its first reason to track what
you have found. **Recommendation:** generate them with datagen from the book list, so adding a book (#3)
updates them. Leave out "Well Read" (read 10 unique books), because there is no good event for opening a
book.

### 9. Regional flavor — M · ★★

Biome-aware book choice, so desert temples hold desert lore and ocean ruins hold water-damaged books
with missing pages. **Recommendation:** do the loot half first: filter by the structure's biome using
loot conditions, with the collections from #3 and genres from #2. The "water-damaged" text effect is a
stretch and should be a separate, optional step.

### 10. Marginalia — M · ★

A small chance a looted book has a previous owner's notes in the margins: a joke, a hint, or coordinates
to a nearby structure. **Recommendation:** only jokes and hints, from a datapack-defined list. Coordinates
to a structure mean searching for structures at loot time, which is far more work than the idea is worth.
Keep the chance low and configurable (A4).

## Honorable mentions

- **Procedural authors.** Most books here are public-domain works with a real author, so a generated
  author would be wrong for them. Revisit only if #3 adds original lore books, which could carry
  in-world authors.
- **Patchouli bridge.** An auto-generated "library index" book of what you've found. It would fit as an
  optional integration, but it overlaps the catalog (a sibling mod idea).

## What was cut and why

**Belongs in the sibling library mod (boundary).** The identity says ideas for the sibling don't belong in
this folder. These are blocks, workstations or mechanics for displaying, making or organizing books:
- **Custom blocks and items** (from your notes), **Binding Press**, **Writing Desk**, **Book Stand**,
  **Bookmark item**, **Lending ledger**.
- **Book catalog** (from your notes, and the **Book Catalog block**).
- **New villager type** (from your notes) and the **Bookbinder villager**, since its job site is a
  block.
- **GUI improvements for creating books** (from your notes, with the Stendhal inspiration and moving the
  writeable book screen to the center): this is a mechanic for making books.
- **Dusty Tome**, which needs brushing or a cauldron to open, so it needs a new mechanic.

These are tracked in GitHub issues [#130–#135](https://github.com/chimericdream/minecraft-mods/issues/130), under the sibling mod issue #129.

**Too vague to act on.**
- **Thematic advancements** (dropped; see the intro).

## Suggested first arc

Do #2 (genre metadata) first, since #5, #6 and #9 all need it. Then #1 and #4 (wider and modded-structure
placement) with #5's structure toggles, and #3 (more books) in the same release so the new places have
something new to find. #6 and #8 follow as the "collect them all" layer. #7, #9 and #10 are lower-value
extras.

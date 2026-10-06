# Search that finds recipes, items and mobs

> Source: headless review of the search box, 2026-10-06 · Effort **S** to **M** (see Description) · Value ★★★
> Status: **Exploring**

## Description

Search indexes a page's title, aliases, tags, headings, summary and body text, but not the contents of widgets or code blocks. So a page whose only mention of a hopper is a recipe block cannot be found by searching "hopper": in the bundled guide, a search for "hopper" lists the pages that mention it in prose (Kitchen sink, Folder layout, Links, Markdown) and not the Recipes page that shows the hopper recipe. In a real book, where recipes and item showcases carry the information, that is the search players will most often try.

Make widgets searchable:

- **Recipes:** index the result and the ingredients, with the result ranked above the ingredients, and both below headings. Searching "hopper" finds the page that crafts one; searching "iron ingot" can find the pages that use it.
- **Item and mob widgets:** index the id and the label.
- **Names, not ids:** players type "Hopper" or "iron ingot", not `minecraft:iron_ingot`. Matching display names needs the game's item and entity names in the player's language, which core cannot know. Core would take a small name-lookup interface from the client (the same way layout takes `TextMetrics` for text width), and the index would be built or refreshed once the language is known, which the reload listener already knows about.

An optional first step that needs no game lookups: a `keywords:` frontmatter key listing extra search words, for authors to add to a page by hand. It is cheap and explicit, but it makes authors repeat what the page already says.

Effort is **S** for ids plus `keywords:`, and **M** once localized names are included.

**Out of scope:** searching the text of other books, and fuzzy matching or spelling correction.

**Dependencies:** `SearchIndex` and `Document` in core, `RecipeSpec` / `WidgetSpecs` for what a widget contains, and a client-supplied name lookup (item and entity registries plus the current language) for the M version. `BookRepository` already rebuilds books on every resource reload, including language changes.
**Verification:** JUnit for the index with a fake name lookup (a recipe page is found by its result and by an ingredient, ranked in that order), and a headless screenshot of searching "hopper" against the guide.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

- [Open-in-guide from items](../integration/open-in-guide-hooks.md) — also needs to know which items a page is about; one index could serve both.
- [Recipe viewer integration (EMI/JEI/REI)](../integration/recipe-viewer-integration.md) — the same "which pages show this item" question from the other side.
- [Tag pages and bookmarks](tag-pages-and-bookmarks.md) — another way to find a page without browsing the tree.

## Open questions

- Should code blocks be searchable? They are examples for authors, and indexing them would make the guide's own examples match almost any term. Recommend leaving them out by default, since widgets already cover the real content.
- How should "found it inside a widget" be shown in the results? Today a result shows the page title only, with a snippet kept in the index but not drawn. Recommend drawing a short second line, such as "recipe: Hopper", so players see why the page matched.
- Start with ids and `keywords:` (S), or go straight to localized names (M)? Recommend going straight to names, since ids alone would rarely match what players type.
- Should an ingredient match rank below a result match? Recommend yes, so "hopper" lists the page that crafts a hopper before the pages that merely use one.

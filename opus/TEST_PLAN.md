# Test Plan — Opus

Opus turns a folder of Markdown files into an in-game book. Almost everything that decides what a page looks
like is plain Java with no Minecraft dependency (`com.chimericdream.opus.core`), so it is unit tested. The
Minecraft-facing layer (`client/`, `item/`, `component/`) is thin and needs in-game checks.

## Automated

JUnit 5, in `fabric/src/test/java/com/chimericdream/opus/test/core/`. Run with `./gradlew :opus:fabric:test`.
No Minecraft bootstrap is needed.

| Class | Covers |
|---|---|
| `FrontmatterTest` | the `---` block, line numbers, BOM/CRLF, bad YAML, comma or list tags |
| `MarkdownParserTest` | inline styles, links, icons, lists, task items, callouts, tables, code, widgets, heading anchors |
| `RecipeSpecTest` | every recipe type and each validation error message |
| `BookLoaderTest` | folder → chapter/page tree, ids, ordering, titles, tags, language fallback, link resolution, broken links, search |
| `LayoutEngineTest` | wrapping, headings, lists, quotes, callouts, code, tables, widget boxes, link hit-testing |
| `ValidateBookTest` | the CLI, and that the bundled guide stays free of errors and warnings |

When adding syntax or a widget, add the parser test and the layout test with it.

## Manual (in game)

Written before the Minecraft-facing layer had ever been compiled, so run the whole list once on Fabric
and once on NeoForge.

Setup: new creative world, `/give @s opus:book`.

1. **Opens** — right-click the Opus Book: the guide opens. F7 opens it too. `/give @s opus:book[opus:book_id="opus:nope"]` shows a "not found" overlay, not a crash.
2. **Navigation** — click chapters and pages in the sidebar; back with Backspace; the `<` / `>` buttons step through pages; PageUp/PageDown/Home/End and the mouse wheel scroll; the sidebar scrolls separately.
3. **Search** — type `recipe`: the sidebar lists matching pages, best matches first; clearing the box restores the contents.
4. **Links** — internal links navigate (and jump to a heading when the link has `#anchor`); hovering shows the target's file name; an `https://` link asks before opening.
5. **Text** — wrapping follows the window when resized (close and reopen after resizing if live resize is not wired up), headings are larger, bold/italic/strikethrough render, links are underlined.
6. **Blocks** — lists, nested lists, task lists, quotes, callouts (note/tip/warning colours), code blocks, tables (column alignment, header fill, narrow window).
7. **Widgets** — on the Recipes and Items and mobs pages: recipe grids and results show the right items, alternatives cycle, item tooltips appear, the entity block shows its placeholder.
8. **Reload** — edit a page in a resource pack, F3+T: the change appears. A page with a broken link logs one error with file and line, and the book still opens.
9. **Language** — switch to a language with no translation: the English pages show. Add `de_de/index.md` and switch: only that page changes.
10. **Dedicated server** — the mod loads on a server without errors (no client classes touched).

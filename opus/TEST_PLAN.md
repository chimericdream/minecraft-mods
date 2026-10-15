# Test Plan â€” Opus

Opus turns a folder of Markdown files into an in-game book. Almost everything that decides what a page looks
like is plain Java with no Minecraft dependency (`com.chimericdream.opus.core`), so it is unit tested. The
Minecraft-facing layer (`client/`, `item/`, `component/`) is thin and needs in-game checks.

## Automated

JUnit 5, in `fabric/src/test/java/com/chimericdream/opus/test/core/`. Run with `./gradlew :opus:fabric:test`.
No Minecraft bootstrap is needed.

| Class | Covers |
|---|---|
| `FrontmatterTest` | the `---` block, line numbers, BOM/CRLF, bad YAML, comma or list tags |
| `MarkdownParserTest` | inline styles, links, icons, texture images (`{width}`/`align`, rows, bad attributes), lists, task items, callouts, tables, code, widgets, heading anchors |
| `RecipeSpecTest` | every recipe type, each validation error message, and the optional recipe id kept alongside an inline definition |
| `RecipePanelTest` | the vanilla-style recipe panel geometry: slot counts and positions per recipe type, nothing overlapping or outside the panel |
| `BookLoaderTest` | folder â†’ chapter/page tree, ids, ordering, titles, tags, language fallback, link resolution, broken links, search |
| `LayoutEngineTest` | wrapping, headings, lists, quotes, callouts, code, tables, widget boxes, link hit-testing, texture images (scaling, captions, rows, alignment, missing textures) |
| `ValidateBookTest` | the CLI (incl. finding books beneath a pack folder), missing `texture:` warnings, and that the bundled guide stays free of errors and warnings |

When adding syntax or a widget, add the parser test and the layout test with it.

## Manual (in game)

The Minecraft-facing layer compiles but had never been run in game when this list was written, so run the
whole list once on Fabric and once on NeoForge.

Setup: new creative world, `/give @s opus:book`.

1. **Opens** â€” right-click the Opus Book: the guide opens. The "Open the Opus Guide" key is unbound by default in Controls; once bound, it opens the guide too. `/give @s opus:book[opus:book_id="opus:nope"]` shows a "not found" screen, not a crash. Its **Open troubleshooting** button opens the guide on "A book says 'not found'", and **Close** returns to where you were.
2. **Navigation** â€” click chapters and pages in the sidebar; the **Back** button (and Backspace) returns to the previous page and is greyed out when there is nothing to go back to; the `<` / `>` buttons step through pages; PageUp/PageDown/Home/End and the mouse wheel scroll; the sidebar scrolls separately.
3. **Search** â€” type `recipe`: the sidebar lists matching pages, best matches first. Both the **x** button and a right-click inside the box clear it and restore the contents; the **x** is greyed out when the box is empty.
3a. **Breadcrumbs** â€” the path at the top stays in the muted text colour. Every part except the current page is clickable and underlines on hover; clicking one jumps there and adds to the back history. On a narrow window the front of a long path is shortened to `...`.
3c. **Collapsible chapters** â€” in a book with nested folders (for example `05-redstone/01-vanilla-changes/â€¦`), every chapter starts closed with a right-pointing arrow and opens to a down-pointing one. The arrow only opens/closes; clicking the name shows the chapter's page and opens it. Opening a page by a link, a search result, a breadcrumb or `<` / `>` opens the chapters above it, and the sidebar scrolls to it. If the page's chapter is closed afterwards, the closed chapter is softly highlighted. `<` / `>` walk through every page whether or not its chapter is open. A folder without an `index.md` shows an automatic list of its contents. A title clipped by the sidebar shows in full in a tooltip on hover. Open chapters stay open when you close and reopen the book.
3b. **Text width** â€” on a wide window the text column stops at about 100 characters and sits centred beside the sidebar; the Back/`<`/`>` buttons line up with the column, not the window edge.
4. **Links** â€” internal links navigate (and jump to a heading when the link has `#anchor`); hovering shows the target's file name; an `https://` link asks before opening.
5. **Text** â€” wrapping follows the window as it is resized live (the search text and reading position are kept), headings are larger, bold/italic/strikethrough render, links are underlined.
6. **Blocks** â€” lists, nested lists, task lists, quotes, callouts (note/tip/warning colours), code blocks, tables (column alignment, header fill, narrow window).
7. **Widgets** â€” on the Recipes and Items and mobs pages (and the Kitchen sink): each recipe type sits on a grey panel that matches its vanilla screen (crafting table, furnace/blast furnace/smoker with the fuel slot and gauge, campfire, stonecutter, smithing table); titles are translated; the result slot shows its count; cooking time and experience appear under furnace-style slots; alternatives cycle; item tooltips appear; the entity block shows its placeholder; an id-only recipe shows an empty panel titled with the id. The optional `recipe:` id changes nothing yet.
8. **Reload** â€” edit a page in a resource pack, F3+T: the change appears. A page with a broken link logs one error with file and line, and the book still opens.
9. **Language** â€” switch to a language with no translation: the English pages show. Add `de_de/index.md` and switch: only that page changes.
10. **Dedicated server** â€” the mod loads on a server without errors (no client classes touched).
11. **Images** â€” on the Images section of "Writing books" (Opus Guide): the 64-wide book image is centred with its caption under it; hovering it shows "Click to enlarge"; clicking dims the screen and shows it larger (whole-number zoom, crisp pixels) with the caption under it; any click, any key (including Esc) or changing page closes it, and scrolling is ignored while it is open. The small inline image sits in the line of text; the three-image row sits side by side, centred, and wraps to a second row in a narrow window without anything running past the column. In a book of your own: `align=left`/`right` move a row; a `texture:` address with a typo shows a labelled "missing:" box (not clickable, no tooltip) and an inline one shows `[alt]`; `icon: texture:ns:path` in a chapter's front matter draws the picture in the contents; F3+T after replacing a PNG shows the new picture and size.

## Book of Books (manual)

Setup: survival or creative world with `/give @s opus:book` a few times, plus `/give @s opus:book[opus:book_id={book_id:"mymod:x"}]`.

1. **Craft** — a book and a chest, shapeless, make `opus:book_of_books`. An "Opus" tab is already in the advancements screen (granted on join, no toast); the first craft shows a "Bookception" toast, and that advancement stays hidden under the tab until then. A second craft shows nothing.
1a. **Magnum Opus** — a Book of Books holding 10+ guides in the inventory grants the hidden "Magnum Opus" challenge within a second; 9 guides does not.
2. **Empty** — right-clicking an empty Book of Books shows "This Book of Books is empty..." on the action bar and opens no menu. The tooltip says it is empty.
3. **Combine** — Book of Books + two Opus books in the grid gives a Book of Books whose tooltip lists both by title and says "Holds 2 guides". Adding a duplicate adds a third entry. Adding anything that is not an Opus book (a vanilla book, a stick) gives no result. A book and nothing else, or two plain books without a Book of Books, give no result.
4. **Merge** — two Books of Books in the grid merge into one holding everything.
5. **Pick** — right-click the full Book of Books: a menu lists each guide by title (an uninstalled id shows as the id). Picking one turns the held item into that real guide; right-click opens it. The tooltip says it is part of a Book of Books. With more than seven guides the menu pages with `<` / `>`. Works from the off hand too.
6. **Revert** — punch the air with the guide in the main hand: it turns back into the Book of Books with the same contents. Punching a block or mob does not revert it.
7. **Remove** — turn the Book of Books into a guide, sneak and press drop: a plain guide drops, and the Book of Books (minus that guide, one fewer duplicate if there were several) is back in the same hotbar slot. Without sneaking, the guide drops as-is and keeps its Book of Books data.
8. **Crafting with a turned-out guide** — a guide turned out of a Book of Books can be used in the grid as the Book of Books (the result holds everything).
9. **Dedicated server** — the mod loads without errors; picking, reverting and removing all work for a remote client.

## `/opus give` (manual)

- On a dedicated server, as a non-op: `/opus give` gives the guide; `/opus give mymod:x` gives a book with that id; `/opus give Bad Id` errors.
- Set `"opsOnly": true` in `config/opus-guides.json` (no restart): a non-op no longer sees or can run the command; an op still can.
- Set `"defaultGuide"` to another id: `/opus give` uses it. A malformed id logs a warning and falls back to `opus:guide`.

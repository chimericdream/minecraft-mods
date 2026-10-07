---
title: For pack authors
icon: item:minecraft:bookshelf
---
## Shipping a book

Put the book folder in a resource pack at `assets/<namespace>/opus-books/<book>/`. The book's id is
`<namespace>:<book>`, so `assets/pannotia/opus-books/pannotia/` is the book `pannotia:pannotia`.

The item is `opus:book`. Give players one with the `opus:book_id` component set to your book's id:

```
/give @s opus:book[opus:book_id={book_id:"pannotia:pannotia"}]
```

The component is an object with a `book_id` field, not a bare string. The same value works in loot tables, recipe
results and functions. Players can also open the default guide from a key.

The resource pack has to be enabled on the client. If the book isn't found, the item shows
"Book '<id>' was not found": check that the pack is active in the resource pack menu and that the id matches the
folder names.

## Translating

Copy the default-language folder, rename it (`de_de`) and translate the files you want. Any page you skip is
shown in the default language.

## Checking your book

Run the validator on the book folder. It loads the book the way the game does and lists broken links, bad
frontmatter and invalid widgets, and exits with an error status when something is wrong.

### Running the validator

You only need Java 25 and the Opus jar from your mods folder (or from the download page). Point it at your book, your
resource pack, or your mod's resources folder:

```
java -jar opus-<version>.jar path/to/your/pack
```

If the path holds `assets/<namespace>/opus-books/<book>` folders, every book inside is checked. You can also point
it straight at one book folder (the one with `book.yml` in it). `--validate` is accepted before the path if you
find it reads better.

- **Language:** it checks the book's default language. Add `--lang=de_de` to check another one; missing pages fall
  back to the default language, just like in the game.
- **Output:** one line per problem, errors and warnings, followed by a summary per book with the chapter, page, tag,
  error and warning counts.
- **Exit status:** `0` when there are no errors (warnings are fine), `1` when there are errors, and `2` when the
  folder can't be found or read. That makes it safe to use as a gate in a build or CI job.

Fix the errors first, then clear the warnings. A book that finishes with `0 errors, 0 warnings` will load cleanly
in the game.

## Reloading

While the game is running, press <kbd>F3</kbd>+<kbd>T</kbd> to reload resource packs and see your edits.

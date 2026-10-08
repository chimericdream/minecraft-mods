---
title: Links
tags: [basics, reference]
---
Links use ordinary Markdown syntax, so they also work in a Markdown preview while you write. A link that points
nowhere is reported when the book loads, so you find out before your players do.

| Destination | Goes to |
|:------------|:--------|
| `other-page.md` | A page in the same folder. The `.md` is optional. |
| `../machines/hoppers.md#usage` | A page elsewhere in the book, at a heading. |
| `/machines/hoppers` | A page, counted from the book's root. |
| `#usage` | A heading on this page. |
| `item:minecraft:hopper` | Shows the item's tooltip. |
| `book:mymod:other-book/some/page` | A page in another book. |
| `https://example.com` | A web page, after asking the player. |

Hover a link to see where it goes: pages show their file name, items show their name.

> [!TIP] Numeric prefixes are optional in links
> A file named `02-frontmatter.md` can be linked as `02-frontmatter.md` or `frontmatter.md`; both reach the same
> page. Opus accepts either, but other Markdown viewers (GitHub, your editor's preview) only follow links that
> match the real file name. If your book is also read outside the game, include the prefix in your links.

`````markdown
* [The next page in this folder](02-frontmatter.md)
* [A heading on another page](03-markdown.md#callouts)
* [A page in another chapter](../02-widgets/index.md)
* [A heading on this page](#inline-icons)
* [A web page](https://example.com)
* [A game item](item:minecraft:diamond)
* [A page in another book](book:mymod:field-guide/machines/hoppers)
`````

Renders as:

* [The next page in this folder](02-frontmatter.md)
* [A heading on another page](03-markdown.md#callouts)
* [A page in another chapter](../02-widgets/index.md)
* [A heading on this page](#inline-icons)
* [A web page](https://example.com)
* [A game item](item:minecraft:diamond)
* [A page in another book](book:mymod:field-guide/machines/hoppers)

## Inline icons

An image whose address starts with `item:` is drawn as a small icon in the text.

`````markdown
A hopper ![Hopper](item:minecraft:hopper) and a chest ![Chest](item:minecraft:chest) drawn inline with the text.
`````

Renders as:

A hopper ![Hopper](item:minecraft:hopper) and a chest ![Chest](item:minecraft:chest) drawn inline with the text.

---
title: Links
tags: [basics, reference]
---

Links use ordinary Markdown syntax, so they also work in a Markdown preview while you write.

| Destination | Goes to |
|:------------|:--------|
| `other-page.md` | A page in the same folder. The `.md` is optional. |
| `../machines/hoppers.md#usage` | A page elsewhere in the book, at a heading. |
| `/machines/hoppers` | A page, counted from the book's root. |
| `#usage` | A heading on this page. |
| `item:minecraft:hopper` | Shows the item's tooltip. |
| `book:mymod:other-book/some/page` | A page in another book. |
| `https://example.com` | A web page, after asking the player. |

A link that points nowhere is reported when the book loads, so you find out before your players do.

## Inline icons

An image whose address starts with `item:` is drawn as a small icon in the text:
`![Hopper](item:minecraft:hopper)`.

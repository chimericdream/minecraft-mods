---
title: For pack authors
icon: item:minecraft:bookshelf
---
## Shipping a book

Put the book folder in a resource pack at `assets/<namespace>/opus-books/<book>/`. Give players the book item with
the `opus:book_id` component set to your book's id, or let them open it from a key.

## Translating

Copy the default-language folder, rename it (`de_de`) and translate the files you want. Any page you skip is
shown in the default language.

## Checking your book

Run the validator on the book folder. It loads the book the way the game does and lists broken links, bad
frontmatter and invalid widgets, and exits with an error status when something is wrong.

## Reloading

While the game is running, press <kbd>F3</kbd>+<kbd>T</kbd> to reload resource packs and see your edits.

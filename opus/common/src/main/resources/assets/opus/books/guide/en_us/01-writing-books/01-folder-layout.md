---
title: Folder layout
tags: [basics, structure]
---
A book lives in `assets/<namespace>/books/<book>/`:

`````text
books/field-guide/
  book.yml                 settings shared by every language
  en_us/
    index.md               the book's home page
    machines/
      index.md             introduction to the "Machines" chapter
      01-hoppers.md        a page
      02-sorters.md
    _drafts/
      ideas.md             ignored: starts with an underscore
`````

## Rules

* A **folder** is a chapter; its `index.md` is the chapter's own page.
* A **file** is a page. A folder without an `index.md` still works and gets a title made from its name.
* Files and folders starting with `_` or `.` are ignored, which is handy for drafts.
* Each language gets its own folder (`en_us`, `de_de`, ...). Missing pages fall back to the book's default
  language.

## Ordering

Siblings are sorted by the `order` frontmatter key first, then by a numeric filename prefix such as `01-`,
and finally alphabetically. Anything without a number or an `order` comes after the numbered pages.

| File | Order it gets |
|:-----|:--------------|
| `01-intro.md` | 1, from the prefix |
| `02-setup.md` | 2, from the prefix |
| `appendix.md` with `order: 1.5` | 1.5, so it sits between the two |
| `extras.md` | no number, so it comes last |

The prefix is only for ordering: it is **not** part of the page's name. A link to `01-intro.md`, `intro.md` or
just `intro` all work, and renaming `01-intro` to `05-intro` never breaks a link.

## Titles

A page's title is, in order of preference: the `title` frontmatter key, a leading `# Heading` in the file, or
the file name made readable (`hopper-sorters.md` becomes "Hopper Sorters").

## book.yml

`````yaml
title: Field Guide
icon: item:minecraft:book
description: Everything about my machines.
default_language: en_us
`````

Keys other than these produce a warning when the book loads, so typos are caught.

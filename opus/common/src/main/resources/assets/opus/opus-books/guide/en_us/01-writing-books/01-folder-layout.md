---
title: Folder layout
tags: [basics, structure]
---
A book lives in `assets/<namespace>/opus-books/<book>/`:

`````text
opus-books/field-guide/
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
* A **file** is a page. A folder without an `index.md` still works: it gets a title made from its name and an automatic page listing what is inside it.
* Files and folders starting with `_` or `.` are ignored, which is handy for drafts.
* Each language gets its own folder (`en_us`, `de_de`, ...). Missing pages fall back to the book's default
  language.

## Nesting chapters

Chapters can nest as deeply as you like: a folder inside a folder is a sub-chapter. A folder with no `index.md`
works as a plain grouping, titled from its folder name.

`````text
en_us/
  05-redstone/
    index.md                       Chapter 5: Redstone
    01-vanilla-changes/
      index.md                     5.1 Changes to vanilla mechanics
      comparators.md               a page
      observers.md
    02-new-mechanics/
      index.md                     5.2 New mechanics
      pulse-gate.md
      advanced/                    no index.md: still a sub-chapter, titled "Advanced", with an automatic contents page
        clock.md
`````

In the contents on the left, chapters have an arrow. They start **closed**, so a book with hundreds of pages
stays easy to scan: click the arrow to open or close a chapter, or click its name to read its page and open it.
Opening a page by any route (a link, a search result, **<** / **>**, a breadcrumb) opens the chapters above it so
you can see where you are. The **<** and **>** buttons always walk through every page in order, whether or not
its chapter is open.

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

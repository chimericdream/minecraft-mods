---
title: Folder layout
tags: [basics, structure]
---

A book lives in `assets/<namespace>/books/<book>/`:

````text
books/field-guide/
  book.yml                 settings shared by every language
  en_us/
    index.md               the book's home page
    machines/
      index.md             introduction to the "Machines" chapter
      01-hoppers.md        a page
      02-sorters.md
````

## Rules

* A **folder** is a chapter; its `index.md` is the chapter's own page.
* A **file** is a page. A folder without an `index.md` still works and gets a title made from its name.
* Siblings are ordered by the `order` frontmatter key, then by a numeric filename prefix such as `01-`, then
  alphabetically. The prefix is only for ordering: it is not part of the page's id, so renaming `01-hoppers`
  to `05-hoppers` never breaks a link.
* Files and folders starting with `_` or `.` are ignored, which is handy for drafts.
* Each language gets its own folder (`en_us`, `de_de`, ...). Missing pages fall back to the book's default
  language.

## book.yml

````yaml
title: Field Guide
icon: item:minecraft:book
description: Everything about my machines.
default_language: en_us
````

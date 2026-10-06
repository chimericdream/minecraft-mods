---
title: Markdown
tags: [basics, reference]
---

Opus reads **CommonMark** plus the common GitHub additions.

## Supported

* Headings, paragraphs, **bold**, *italic*, ~~strikethrough~~ and `inline code`
* Bulleted, numbered and task lists
  * including nested lists
* Block quotes and horizontal rules
* Fenced code blocks
* Tables, with column alignment

1. Numbered lists keep their start number
2. and renumber as you go

- [x] Task lists show a check box
- [ ] ...

> A plain block quote.

## Callouts

Start a block quote with `[!KIND]` to turn it into a callout. Kinds are free text; `note`, `tip`, `warning` and
`important` have their own colours.

````markdown
> [!WARNING] Hot!
> Lava is hot.
````

## Headings and anchors

Every heading gets an anchor made from its text (`## Hello World` becomes `#hello-world`). Choose your own
with `## Hello World {#greeting}`.

## Not supported

Raw HTML is ignored with a warning. Images other than item icons are not drawn yet.

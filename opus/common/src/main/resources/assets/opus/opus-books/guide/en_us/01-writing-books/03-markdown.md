---
title: Markdown
tags: [basics, reference]
---
Opus reads **CommonMark** plus the common GitHub additions: tables, strikethrough and task lists. Every
example below is shown as the text you write, then as the page it produces.

## Text styles

`````markdown
Plain, **bold**, *italic*, ***bold italic***, ~~strikethrough~~ and `inline code`.
`````

Renders as:

Plain, **bold**, *italic*, ***bold italic***, ~~strikethrough~~ and `inline code`.

## Line breaks

`````markdown
A plain newline is a soft break:
it joins the lines with a space.

A backslash at the end of a line\
starts a new line.
`````

Renders as:

A plain newline is a soft break:
it joins the lines with a space.

A backslash at the end of a line\
starts a new line.

## Headings

Use one to six `#` characters. A single `#` at the very top of the page becomes the page title, so inside a
page use `##` and below.

`````markdown
### Third-level heading
#### Fourth-level heading
##### Fifth-level heading
###### Sixth-level heading
`````

Renders as:

### Third-level heading
#### Fourth-level heading
##### Fifth-level heading
###### Sixth-level heading

Every heading gets an anchor made from its text (`## Hello World` becomes `#hello-world`), which links can jump
to. Choose your own with `{#name}` at the end of the heading:

`````markdown
### A heading with its own anchor {#my-anchor}
`````

Renders as:

### A heading with its own anchor {#my-anchor}

## Lists

`````markdown
* A bulleted list
* with a second item
  * and a nested item
  * and another
* then back to the top level

3. A numbered list keeps its start number
4. and counts up from there
`````

Renders as:

* A bulleted list
* with a second item
  * and a nested item
  * and another
* then back to the top level

3. A numbered list keeps its start number
4. and counts up from there

## Task lists

`````markdown
- [x] Write the page
- [x] Check the links
- [ ] Add a screenshot
`````

Renders as:

- [x] Write the page
- [x] Check the links
- [ ] Add a screenshot

## Quotes

`````markdown
> A plain block quote sets text apart without a label.
> It can run over several lines.
`````

Renders as:

> A plain block quote sets text apart without a label.
> It can run over several lines.

## Callouts

Start a block quote with `[!KIND]` to turn it into a callout. Kinds are free text; `note`, `tip`, `important`,
`warning` and `danger` have their own colours. Anything after the kind becomes the title.

`````markdown
> [!NOTE]
> A note, with the default title.

> [!TIP] A custom title
> Anything after the kind becomes the title.

> [!IMPORTANT]
> Something readers should not miss.

> [!WARNING] Hot!
> Lava is hot.

> [!DANGER]
> Do not do this.
`````

Renders as:

> [!NOTE]
> A note, with the default title.

> [!TIP] A custom title
> Anything after the kind becomes the title.

> [!IMPORTANT]
> Something readers should not miss.

> [!WARNING] Hot!
> Lava is hot.

> [!DANGER]
> Do not do this.

Callouts can hold any blocks, including lists and styled text:

`````markdown
> [!TIP] Callouts hold any blocks
> Including lists:
>
> 1. First
> 2. Second
>
> and **styled** text.
`````

Renders as:

> [!TIP] Callouts hold any blocks
> Including lists:
>
> 1. First
> 2. Second
>
> and **styled** text.

## Code blocks

`````markdown
```java
public static void main(String[] args) {
    System.out.println("Hello, book!");
}
```
`````

Renders as:

```java
public static void main(String[] args) {
    System.out.println("Hello, book!");
}
```

## Tables

Column alignment follows the colons in the divider row.

`````markdown
| Item      | Count | Notes             |
|:----------|------:|:-----------------:|
| Hopper    |     5 | left, right, mid  |
| Chest     |     1 | numbers align     |
| Iron bars |    16 | centred notes     |
`````

Renders as:

| Item      | Count | Notes             |
|:----------|------:|:-----------------:|
| Hopper    |     5 | left, right, mid  |
| Chest     |     1 | numbers align     |
| Iron bars |    16 | centred notes     |

## Horizontal rules

`````markdown
Above the rule.

---

Below the rule.
`````

Renders as:

Above the rule.

---

Below the rule.

## Images

A `texture:` image draws a PNG from a resource pack. The address is `texture:namespace:path`, where the path is relative to `textures/` and has no `.png`, the same form as the `texture` key in `book.yml`. `texture:yourpack:gui/diagram` is the file `assets/yourpack/textures/gui/diagram.png`.

An image on its own line is a block, centred in the column. It is drawn at the texture's own size, scaled down to fit a narrow window and never scaled up. The alt text is shown under it as a caption, and clicking the image opens it full size.

`````markdown
![The Opus book](texture:opus:item/book){width=64}
`````

Renders as:

![The Opus book](texture:opus:item/book){width=64}

An image in the middle of a line of text is drawn small, like an item icon: ![the Opus book](texture:opus:item/book) here.

### Size and alignment

Add `{width=N}` right after an image to set its width in pixels. The height follows the texture's proportions, and the image is never wider than the column. Add `align=left`, `align=center` or `align=right` to position a block image. Put both in the same braces.

`````markdown
![](texture:opus:item/book){width=32 align=right}
`````

### Side by side

Images that sit in one paragraph with only spaces or line breaks between them make a row. The row is positioned as a group by the first image's `align`, each image keeps its own caption, and the row wraps to a second line when it doesn't fit.

`````markdown
![Book](texture:opus:item/book){width=48}
![Guide](texture:opus:item/opus_guide){width=48}
![Not found](texture:opus:item/book_invalid){width=48}
`````

Renders as:

![Book](texture:opus:item/book){width=48}
![Guide](texture:opus:item/opus_guide){width=48}
![Not found](texture:opus:item/book_invalid){width=48}

### Missing textures

If the texture can't be found, the page shows a labelled box in its place instead of failing, and the validator warns about it. Check the namespace and path, and that the file really is a PNG under `textures/`.

## Not supported

Raw HTML is ignored with a warning. Animated and remote images are not supported. An image with any address other than `item:`, `block:`, `entity:` or `texture:` shows its alt text. See
[links](04-links.md#inline-icons) for the item icon syntax.

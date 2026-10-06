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

## Not supported

Raw HTML is ignored with a warning. Images other than item icons are not drawn yet; see
[links](04-links.md#inline-icons) for the icon syntax.

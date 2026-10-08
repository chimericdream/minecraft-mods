---
title: Kitchen sink
icon: item:minecraft:cauldron
tags: [reference]
summary: Every supported element, as source and as rendered.
---
Everything Opus supports, on one page. Each example is shown as the text you write, then as the page it
produces. Use it to check a skin or a pack, or to copy a snippet.

## Text

### Styles

`````markdown
Plain, **bold**, *italic*, ***bold italic***, ~~strikethrough~~ and `inline code`.
`````

Renders as:

Plain, **bold**, *italic*, ***bold italic***, ~~strikethrough~~ and `inline code`.

### Line breaks

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

### Headings

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

`````markdown
### A heading with its own anchor {#my-anchor}
`````

Renders as:

### A heading with its own anchor {#my-anchor}

## Lists

### Bulleted and numbered

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

### Task lists

`````markdown
- [x] Write the page
- [x] Check the links
- [ ] Add a screenshot
`````

Renders as:

- [x] Write the page
- [x] Check the links
- [ ] Add a screenshot

## Blocks

### Quotes

`````markdown
> A plain block quote sets text apart without a label.
> It can run over several lines.
`````

Renders as:

> A plain block quote sets text apart without a label.
> It can run over several lines.

### Callouts

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

### Code

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

### Tables

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

### Rules

`````markdown
Above the rule.

---

Below the rule.
`````

Renders as:

Above the rule.

---

Below the rule.

## Links

Paths below are written for this page, which sits in a different folder from the Links page.

`````markdown
* [The next page in this folder](../01-writing-books/02-frontmatter.md)
* [A heading on another page](../01-writing-books/03-markdown.md#callouts)
* [A page in another chapter](../02-widgets/index.md)
* [A heading on this page](#icons)
* [A web page](https://example.com)
* [A game item](item:minecraft:diamond)
* [A page in another book](book:mymod:field-guide/machines/hoppers)
`````

Renders as:

* [The next page in this folder](../01-writing-books/02-frontmatter.md)
* [A heading on another page](../01-writing-books/03-markdown.md#callouts)
* [A page in another chapter](../02-widgets/index.md)
* [A heading on this page](#icons)
* [A web page](https://example.com)
* [A game item](item:minecraft:diamond)
* [A page in another book](book:mymod:field-guide/machines/hoppers)

### Icons

`````markdown
A hopper ![Hopper](item:minecraft:hopper) and a chest ![Chest](item:minecraft:chest) drawn inline with the text.
`````

Renders as:

A hopper ![Hopper](item:minecraft:hopper) and a chest ![Chest](item:minecraft:chest) drawn inline with the text.

## Recipes

Each recipe includes the optional `recipe:` id, which is not used yet. See [recipe ids](../02-widgets/01-recipes.md#recipe-ids-planned).

### Crafting

`````markdown
```recipe
recipe: minecraft:hopper    # optional id; planned, not used yet
pattern: ["I I", "ICI", " I "]
key:
  I: iron_ingot
  C: chest
result: hopper
```
`````

Renders as:

```recipe
recipe: minecraft:hopper    # optional id; planned, not used yet
pattern: ["I I", "ICI", " I "]
key:
  I: iron_ingot
  C: chest
result: hopper
```

`````markdown
```recipe
recipe: minecraft:flint_and_steel
type: crafting_shapeless
ingredients: [iron_ingot, flint]
result: flint_and_steel
```
`````

Renders as:

```recipe
recipe: minecraft:flint_and_steel
type: crafting_shapeless
ingredients: [iron_ingot, flint]
result: flint_and_steel
```

`````markdown
```recipe
pattern: ["PP", "PP"]
key:
  P: [oak_planks, spruce_planks, birch_planks]
result: crafting_table
```
`````

Renders as:

```recipe
pattern: ["PP", "PP"]
key:
  P: [oak_planks, spruce_planks, birch_planks]
result: crafting_table
```

### Cooking

`````markdown
```recipe
recipe: minecraft:iron_ingot_from_smelting_raw_iron
type: smelting
ingredient: raw_iron
result: iron_ingot
cooking_time: 200
experience: 0.7
```
`````

Renders as:

```recipe
recipe: minecraft:iron_ingot_from_smelting_raw_iron
type: smelting
ingredient: raw_iron
result: iron_ingot
cooking_time: 200
experience: 0.7
```

`````markdown
```recipe
recipe: minecraft:iron_ingot_from_blasting_raw_iron
type: blasting
ingredient: raw_iron
result: iron_ingot
cooking_time: 100
experience: 0.7
```
`````

Renders as:

```recipe
recipe: minecraft:iron_ingot_from_blasting_raw_iron
type: blasting
ingredient: raw_iron
result: iron_ingot
cooking_time: 100
experience: 0.7
```

`````markdown
```recipe
recipe: minecraft:cooked_beef_from_smoking
type: smoking
ingredient: beef
result: cooked_beef
cooking_time: 100
```
`````

Renders as:

```recipe
recipe: minecraft:cooked_beef_from_smoking
type: smoking
ingredient: beef
result: cooked_beef
cooking_time: 100
```

`````markdown
```recipe
recipe: minecraft:cooked_beef_from_campfire_cooking
type: campfire_cooking
ingredient: beef
result: cooked_beef
cooking_time: 600
```
`````

Renders as:

```recipe
recipe: minecraft:cooked_beef_from_campfire_cooking
type: campfire_cooking
ingredient: beef
result: cooked_beef
cooking_time: 600
```

### Stonecutting

`````markdown
```recipe
recipe: minecraft:stone_bricks_from_stone_stonecutting
type: stonecutting
ingredient: stone
result: stone_bricks
```
`````

Renders as:

```recipe
recipe: minecraft:stone_bricks_from_stone_stonecutting
type: stonecutting
ingredient: stone
result: stone_bricks
```

### Smithing

`````markdown
```recipe
recipe: minecraft:netherite_sword_smithing
type: smithing
template: netherite_upgrade_smithing_template
base: diamond_sword
addition: netherite_ingot
result: netherite_sword
```
`````

Renders as:

```recipe
recipe: minecraft:netherite_sword_smithing
type: smithing
template: netherite_upgrade_smithing_template
base: diamond_sword
addition: netherite_ingot
result: netherite_sword
```

### Id only

`````markdown
```recipe
recipe: minecraft:hopper
```
`````

Renders as:

```recipe
recipe: minecraft:hopper
```

## Items and mobs

### Items

`````markdown
```item
id: diamond
count: 3
label: Shiny
```
`````

Renders as:

```item
id: diamond
count: 3
label: Shiny
```

`````markdown
```item
minecraft:golden_apple
```
`````

Renders as:

```item
minecraft:golden_apple
```

### Mobs

`````markdown
```entity
id: creeper
scale: 1.5
```
`````

Renders as:

```entity
id: creeper
scale: 1.5
```

`````markdown
```entity
minecraft:villager
```
`````

Renders as:

```entity
minecraft:villager
```

## Frontmatter

The block at the very top of a page. See [frontmatter](../01-writing-books/02-frontmatter.md) for every key.

`````markdown
---
title: Hoppers
icon: item:minecraft:hopper
order: 1
tags: [machines, logistics]
summary: Moves items between containers.
hidden: false
aliases: [sorters]
since: "1.0"
---
`````

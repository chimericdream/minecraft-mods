---
title: Recipes
tags: [widgets]
---

Describe the recipe right in the page. Spelling out an id such as `iron_ingot` means `minecraft:iron_ingot`.

````markdown
```recipe
pattern: ["I I", "ICI", " I "]
key:
  I: iron_ingot
  C: chest
result: hopper
```
````

```recipe
pattern: ["I I", "ICI", " I "]
key:
  I: iron_ingot
  C: chest
result: hopper
```

## Other recipe types

````markdown
```recipe
type: smelting
ingredient: iron_ore
result: iron_ingot
```
````

```recipe
type: smelting
ingredient: iron_ore
result: iron_ingot
```

Supported types: `crafting_shaped` (the default when there is a `pattern`), `crafting_shapeless` (give an
`ingredients` list), `smelting`, `blasting`, `smoking`, `campfire_cooking`, `stonecutting` and `smithing`.

An ingredient can be a list of alternatives, or a tag such as `"#minecraft:planks"`.

> [!NOTE]
> Looking a recipe up by its id (`recipe: minecraft:hopper`) is planned and needs the server; until then,
> describe the recipe in the page.

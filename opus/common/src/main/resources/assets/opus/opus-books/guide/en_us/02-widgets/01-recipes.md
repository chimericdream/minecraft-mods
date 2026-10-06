---
title: Recipes
tags: [widgets]
---
Describe the recipe right in the page and Opus draws it on a panel that looks like the matching vanilla screen.
Spelling out an id such as `iron_ingot` means `minecraft:iron_ingot`.

| Key | Used by | Meaning |
|:----|:--------|:--------|
| `recipe` | all | Optional recipe id. See [recipe ids](#recipe-ids-planned). |
| `type` | all | `crafting_shaped` (the default when there is a `pattern`), `crafting_shapeless`, `smelting`, `blasting`, `smoking`, `campfire_cooking`, `stonecutting` or `smithing`. |
| `pattern`, `key` | shaped | Up to 3 rows of up to 3 characters, and what each character stands for. |
| `ingredients` | shapeless | Up to 9 ingredients. |
| `ingredient` | cooking, stonecutting | The single input. |
| `template`, `base`, `addition` | smithing | The three inputs. |
| `result`, `count` | all | The output and how many (1 to 99). |
| `cooking_time`, `experience` | cooking | Ticks and experience, shown under the slots. |

An ingredient can also be a list of alternatives, which take turns on screen like the recipe book.

> [!IMPORTANT] Recipe ids are planned
> The `recipe:` key is accepted and checked today, and these examples include it, but **it is not used yet**.
> The plan:
>
> 1. If an id is present, ask the server for that recipe and its output.
> 2. If the server has it, draw what it returned.
> 3. If it does not, fall back to the inline definition on the page.

## Crafting

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

Shapeless recipes list their ingredients instead of a pattern:

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

Give a slot several alternatives with a list:

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

## Cooking

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

The same shape works for blasting, smoking and campfire cooking, each drawn on its own panel:

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

## Stonecutting

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

## Smithing

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

## Recipe ids (planned)

A block with only an id and no definition has nothing to fall back to. Until server lookup exists it shows an empty
panel titled with the id, as below, so always include the inline definition as well.

`````markdown
```recipe
recipe: minecraft:hopper
```
`````

Renders as:

```recipe
recipe: minecraft:hopper
```

See every recipe type together in the [kitchen sink](../99-appendices/kitchen-sink.md#recipes).

---
title: Items and mobs
tags: [widgets]
---
## Items

Shows one item in a slot, with an optional stack size and caption. Hover it for its name.

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

A bare id is a shortcut for `id:`:

`````markdown
```item
minecraft:golden_apple
```
`````

Renders as:

```item
minecraft:golden_apple
```

## Mobs

Shows a live mob that turns slowly in an idle pose. The box is sized to fit the mob, so a tall mob gets a tall box and a bat or ghast gets a square one. `scale` makes the mob bigger or smaller than that fit (default `1`). Hover it to see its name, or your `label`.

Only living mobs can be shown. Anything else (an arrow, a boat), or a mob that can't be created, shows a labelled placeholder with a short reason instead. Mobs always have their default look; there are no variants, colours or equipment.

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

A bare id works here too:

`````markdown
```entity
minecraft:villager
```
`````

Renders as:

```entity
minecraft:villager
```

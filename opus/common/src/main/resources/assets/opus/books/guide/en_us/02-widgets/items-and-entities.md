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

Shows a mob. For now this is a labelled placeholder; a live, rotating preview is planned.

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

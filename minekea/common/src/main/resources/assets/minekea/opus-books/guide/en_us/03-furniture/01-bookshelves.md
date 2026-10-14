---
title: Bookshelves
icon: item:minekea:furniture/bookshelves/crimson
tags: [furniture]
summary: Bookshelves in every wood and many other materials, plus hidden doors and trapdoors.
---
Tired of a single bookshelf texture? Minekea has bookshelves in every wood type, and in a number of other materials
such as stone bricks, prismarine, quartz and sulfur.

```recipe
pattern: ["###", "XXX", "###"]
key:
  "#": crimson_planks
  X: book
result: minekea:furniture/bookshelves/crimson
count: 3
```

## Hidden doors and trapdoors

Every bookshelf type can be crafted into a door or a trapdoor that looks like the bookshelf.

```recipe
pattern: ["##", "##", "##"]
key:
  "#": minekea:furniture/bookshelves/acacia
result: minekea:furniture/doors/bookshelves/acacia
count: 3
```

```recipe
pattern: ["###", "###"]
key:
  "#": minekea:furniture/bookshelves/acacia
result: minekea:furniture/trapdoors/bookshelves/acacia
count: 12
```

## Slabs and stairs

Bookshelves also have slabs, stairs and vertical versions of both.

```recipe
pattern: ["###", " ##", "  #"]
key:
  "#": minekea:furniture/bookshelves/acacia
result: minekea:building/stairs/bookshelves/vertical/acacia
count: 8
```

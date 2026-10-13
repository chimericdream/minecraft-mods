---
title: Harvest foods
icon: item:allhallowssteve:soul_cake
tags: [food]
---
A few autumn foods, all in the Food & Drinks tab.

## Soul cake

Wheat, sugar, and an egg make 2 cakes. Add one spice before crafting for a cake with a short effect (30 seconds
each):

| Spice | Effect |
|:------|:-------|
| Cloves | Absorption |
| Cinnamon | Luck |
| Nutmeg | Night Vision |

```recipe
type: crafting_shapeless
ingredients: [wheat, sugar, egg, allhallowssteve:cloves]
result: allhallowssteve:soul_cake_cloves
count: 2
```

```recipe
type: crafting_shapeless
ingredients: [wheat, sugar, egg, allhallowssteve:cinnamon]
result: allhallowssteve:soul_cake_cinnamon
count: 2
```

```recipe
type: crafting_shapeless
ingredients: [wheat, sugar, egg, allhallowssteve:nutmeg]
result: allhallowssteve:soul_cake_nutmeg
count: 2
```

Add mulling spices for a **Spiced Soul Cake** with all three.

```recipe
type: crafting_shapeless
ingredients: [wheat, sugar, egg, allhallowssteve:mulling_spices]
result: allhallowssteve:soul_cake_spiced
count: 2
```

## Pumpkin soup

A pumpkin, a bowl, and a carrot or potato. It gives the bowl back.

```recipe
type: crafting_shapeless
ingredients: [pumpkin, carrot, bowl]
result: allhallowssteve:pumpkin_soup
```

```recipe
type: crafting_shapeless
ingredients: [pumpkin, potato, bowl]
result: allhallowssteve:pumpkin_soup
```

## Roasted pumpkin seeds

Cook pumpkin seeds in a furnace, smoker, or campfire.

```recipe
recipe: allhallowssteve:roasted_pumpkin_seeds_from_smelting
type: smelting
ingredient: pumpkin_seeds
result: allhallowssteve:roasted_pumpkin_seeds
cooking_time: 200
experience: 0.1
```

## Apple pie

```recipe
pattern: ["ASA", "WWW"]
key:
  A: apple
  S: sugar
  W: wheat
result: allhallowssteve:apple_pie
```

## Spiced pumpkin pie

A pumpkin pie and mulling spices, crafted together. A bigger meal than plain pumpkin pie.

```recipe
type: crafting_shapeless
ingredients: [pumpkin_pie, allhallowssteve:mulling_spices]
result: allhallowssteve:spiced_pumpkin_pie
```

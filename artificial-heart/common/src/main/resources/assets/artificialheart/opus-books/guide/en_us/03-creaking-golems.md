---
title: Creaking golems
icon: item:artificialheart:pale_carved_pumpkin
tags: [creaking, golem]
summary: Build a creaking that will never hurt you.
---
Build a golem shape around a creaking heart, top it with a Pale Carved Pumpkin or Pale Jack o'Lantern, and a
friendly creaking appears.

## The shape

From top to bottom:

| Row | Left | Middle | Right |
|:----|:-----|:-------|:------|
| Top | air | Pale Carved Pumpkin or Pale Jack o'Lantern | air |
| Middle | Pale Oak Fence | Creaking Heart or Detached Creaking Heart | Pale Oak Fence |
| Bottom | air | Pale Oak Log (any pale oak log) | air |

Place the pumpkin last. The blocks vanish and the creaking appears in their place.

```entity
id: creaking
label: Creaking Golem
```

## What it does

* **Real Creaking Heart:** the golem acts like a wild creaking. It stalks nearby players and freezes when you look
  at it, but it never attacks you.
* **Detached Creaking Heart:** the golem is a statue. It never moves.
* It has as much health as an enderman.
* It stays around until it is defeated.
* Wearing a [Pale Carved Pumpkin](02-pale-pumpkins/01-pale-pumpkin.md) as a helmet makes any creaking, tame or wild,
  leave you alone.

## Drops

When defeated, a golem creaking drops 1 to 2 pale oak logs, and half the time 1 to 2 resin clumps.

> [!TIP]
> A dispenser aimed at the top spot of a finished frame will place the pumpkin and finish the golem for you.

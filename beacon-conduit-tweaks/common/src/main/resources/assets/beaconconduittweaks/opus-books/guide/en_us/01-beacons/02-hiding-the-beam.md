---
title: Hiding the beam
icon: item:minecraft:tinted_glass
tags: [beacon, beam]
---
Beacon beams are visible from far away. Two simple tricks let you hide them.

## Hide the whole beam

Place any **carpet** on top of the beacon. The beam stops rendering completely.

```item
id: white_carpet
label: Any carpet
```

## Hide part of the beam

Tinted glass lets the beam pass through it, and each piece marks a switch:

* Put **tinted glass** in the beam's path. The beam is hidden from that point upward.
* Put a **second tinted glass** higher up. The beam becomes visible again above it.

You can use this to show only a section of the beam, such as a short glowing stub on top of a building.

```item
id: tinted_glass
label: Tinted glass
```

```recipe
pattern: [" A ", "AGA", " A "]
key:
  A: amethyst_shard
  G: glass
result: tinted_glass
count: 2
```

> [!NOTE]
> Hiding the beam only changes how it looks. This idea comes from the mod
> [Discontinuous Beacon Beams](https://www.curseforge.com/minecraft/mc-mods/discontinuous-beacon-beams) by
> supersaiyansubtlety.

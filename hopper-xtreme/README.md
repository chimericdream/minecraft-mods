# Hopper X-Treme (Fabric/NeoForge)

![Version: 4.0.0-alpha.0](https://img.shields.io/badge/version-4.0.0--alpha.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Upgrade your hoppers!_

## Introduction

Vanilla hoppers are slow, one-directional, and unfiltered. Hopper X-Treme adds a full family of upgraded hoppers
that move items faster, pull from multiple sides, push items **upward**, and filter what they carry — all while
staying recognizably "hopper."

### Minecraft Versions

* 26.2: supported

## Current Features

### Faster hoppers (tiers)

Each tier transfers items on a different cooldown. A vanilla hopper moves an item every 8 game ticks; the tiers
range from a deliberately slow "trickle" hopper up to a hopper that moves an item **every single tick**.

| Hopper | Transfer cooldown | Notes |
| --- | --- | --- |
| Honeyed Hopper | 20 ticks | Intentionally slow — great for item clocks and rate-limited feeds. |
| Copper Hopper | 8 ticks | Same speed as vanilla, but **ignores redstone** — it can never be locked by a redstone signal. |
| Golden Hopper | 4 ticks | 2× vanilla speed. |
| Diamond Hopper | 2 ticks | 4× vanilla speed. |
| Netherite Hopper | 1 tick | 8× vanilla speed — the fastest tier. |

### Multi-Hoppers

Multi-Hoppers output items to more than one side, so a single hopper can send items to several destinations at once.
They come in the same tiers as regular hoppers (Multi-Hopper, Golden, Diamond, Netherite).

### Huppers (upward hoppers)

A **Hupper** is a hopper that pushes items **up** instead of down, making vertical item elevators trivial. Huppers
are available in every tier (Hupper, Honeyed, Copper, Golden, Diamond, Netherite) and as **Multi-Huppers** for
sending to multiple sides while moving items upward.

### Glazed variants

Most hoppers and multi-hoppers have a **Glazed** variant (and a **Honey Glazed** version of the honeyed hopper) with
a cleaner, terracotta-style look. Glazed hoppers are more than cosmetic, though: they **do not hold onto items**.
Anything in a glazed hopper's inventory is ejected in the direction the hopper is facing — like a vanilla dropper —
so a glazed hopper pulls items in from above and continuously spits them out its output side rather than passing
them into an adjacent inventory. They come in the same tiers as their non-glazed counterparts (and keep those
tiers' transfer speeds).

### Item filtering

Upgraded hoppers support **built-in item filtering** so you can control exactly what passes through. Filtering is
managed with two items:

* **Hopper Item Filter** — configure what a hopper accepts. It has **Include** and **Exclude** modes: include mode
  only lets the listed items through, exclude mode blocks the listed items.
* **Wrench** — a tool for configuring/adjusting hoppers.

### Creative tabs

The mod organizes its blocks into dedicated creative tabs: **Hoppers**, **Multi-Hoppers**, **Huppers**, and
**Multi-Huppers**.

## Deprecated blocks

Earlier versions shipped separate "Filtered" hopper blocks (e.g. *Filtered Golden Hopper*). Filtering is now built
into the standard upgraded hoppers, so those blocks are **deprecated**. Any deprecated filtered hopper you still
have will automatically convert to its filter-capable equivalent when placed, preserving its contents, custom name,
cooldown, and filter settings. You should not craft new ones.

## Notes for Documentation

For in-game documentation, the four concepts worth teaching are: **tiers** (speed), **multi-hoppers** (push to
many sides), **huppers** (push up), and **filtering** (include/exclude with the Hopper Item Filter and Wrench).

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/hopper-extreme/issues) to report any bugs
you find.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to
the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

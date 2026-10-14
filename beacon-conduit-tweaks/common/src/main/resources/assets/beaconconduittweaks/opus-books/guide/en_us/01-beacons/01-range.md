---
title: Beacon range
icon: item:minecraft:beacon
tags: [beacon, range]
---
An active beacon gives its effects to players within its range. Beacon & Conduit Tweaks lets you set that range.

## How the range is worked out

The range is made of three parts, added together:

1. A flat **base range**. The default is 10 blocks.
2. A bonus for **each pyramid level**. The default is 10 blocks per level.
3. A small bonus for the **blocks in the pyramid** (see below).

The defaults match vanilla beacons: a full four-level pyramid reaches 50 blocks. Raise the base range or the
per-level bonus in the [settings](../03-configuration/index.md) to reach much further.

```item
id: beacon
label: Beacon
```

## Pyramid blocks

The mod also counts what the pyramid is built from. Each block adds a little extra range:

| Block | Extra range per block |
|:------|:----------------------|
| Iron block | 0 |
| Gold block | 0 |
| Diamond block | 0.5 |
| Emerald block | 0.5 |
| Netherite block | 2 |

Other beacon base blocks add nothing. Since a pyramid has many blocks, a netherite pyramid reaches noticeably
further than an iron one.

> [!NOTE]
> These per-block amounts are built into the mod. They are not part of the config screen or config file.

## Tips

* Changing the settings affects beacons as they update, so you may need to re-enter the world or wait a moment
  for existing beacons to pick up new values.
* The pyramid rules are unchanged. Only the range is different.

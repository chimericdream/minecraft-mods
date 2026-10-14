---
title: Conduits
icon: item:minecraft:conduit
tags: [conduit, range]
---
A conduit gives Conduit Power to players in the water near it. The mod changes how far that reaches.

```item
id: conduit
label: Conduit
```

## How range works

In vanilla, range grows with the number of active frame blocks around the conduit: 16 more for every 7 blocks,
so a complete 42-block frame reaches 96. By default the mod keeps that exactly, so nothing changes until you ask it to.

Three settings shape it, the same way the beacon's settings do:

| Setting | Result | Default |
|:--------|:-------|:--------|
| Base Conduit Range | A flat range every conduit has. | 0 |
| Conduit Frame Blocks / Step | How many active frame blocks earn one step of range. | 7 |
| Conduit Range / Step | Range added for each full step. | 16 |

For example, 21 frame blocks with the defaults is 3 steps of 16, or 48.

> [!WARNING]
> If you set **Conduit Range / Step** to 0 and give no other source of range, the conduit effectively stops reaching players.

You can also give individual frame blocks their own range bonus on the config screen. The list is empty by default;
see [Configuration](../03-configuration/index.md#per-block-ranges).

Older configs used a single on/off option, **Conduit: Add Vanilla Range**. It is deprecated, but still works: it is only
used for the conduit settings above that your config file hasn't set.

See [Configuration](../03-configuration/index.md) for where to find the settings.

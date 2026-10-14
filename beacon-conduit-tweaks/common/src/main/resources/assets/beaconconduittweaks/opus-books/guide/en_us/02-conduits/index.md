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

In vanilla, range grows with the number of frame blocks around the conduit. By default the mod keeps that
vanilla range exactly, so nothing changes until you ask it to.

The one setting here is **Conduit: Add Vanilla Range**:

| Setting | Result |
|:--------|:-------|
| On (default) | The normal vanilla range. |
| Off | The vanilla range is removed. Because no extra range comes from anywhere else by default, the conduit effectively stops reaching players. |

> [!WARNING]
> Leave **Add Vanilla Range** on unless you know why you are turning it off.

The mod can also give individual frame blocks their own range bonus, but those amounts are built in and empty by
default, so there is nothing to tune on the config screen yet.

See [Configuration](../03-configuration/index.md) for where to find the setting.

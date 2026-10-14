---
title: Settings and tips
icon: item:minecraft:comparator
tags: [reference]
summary: Config options, customizing the block list, and building tips.
---
## Settings

Open the config screen from the mod list (on Fabric, this needs Mod Menu; on NeoForge, use the mod's Config button).

| Option | Default | What it does |
|:-------|:-------:|:-------------|
| Enable address linking | On | The master switch. When off, the mod does nothing and vanilla linking applies. |
| Log linking decisions | Off | Writes each portal's address, the scores of nearby portals, and the winner to the game log. Handy when a link doesn't go where you expected. |

## Changing which blocks count

The blocks that can be used in an address are listed in the block tag `betterportallinking:portal_address_blocks`.
A data pack can add to that tag or replace it to allow other blocks.

## Tips

* Pick a pattern you can remember, such as one color for a base and another for a farm.
* Use different corner blocks for portals that are close together so they don't mix up.
* Two portals side by side that share a single frame column can share a corner block. Editing it changes both
  addresses at once.
* If a link goes somewhere unexpected, turn on **Log linking decisions** and check the log.

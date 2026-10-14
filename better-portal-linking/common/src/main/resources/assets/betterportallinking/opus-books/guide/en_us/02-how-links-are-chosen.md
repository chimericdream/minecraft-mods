---
title: How links are chosen
icon: item:minecraft:ender_pearl
tags: [basics]
summary: Scoring, ties, and falling back to vanilla.
---
When you step into a decorated portal, the mod looks at the portals near the spot where you would arrive and
compares their addresses with the one you came from.

## Scoring

Each matching corner block scores one point. For example, if you enter a portal with two red concrete corners and one
blue concrete corner:

| Other portal's corners | Score |
|:-----------------------|:-----:|
| Two red concrete, one blue concrete | 3 |
| Two red concrete | 2 |
| One red concrete | 1 |
| Only green concrete | 0 |

The portal with the highest score wins. Four of the same block scores higher against a matching portal than a single
matching corner would.

## Ties

If two portals score the same, the closer one wins. If they are still tied, the mod breaks the tie the same way
every time, so a given portal always sends you to the same place.

## When nothing matches

> [!NOTE]
> If the portal you enter has no address, or no nearby portal shares any corner block with it, vanilla linking takes
> over. That means you may arrive at the nearest portal, or a new one may be created.

The mod only looks at portals within the same range vanilla would search, so a matching portal that is too far away
will not be found.

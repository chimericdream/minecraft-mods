---
title: The four modes
icon: item:minecraft:comparator
tags: [basics]
summary: What Prevent on break, Prevent on place, Prevent all, and Replace block do.
---
| Mode | Placing it | Breaking it |
| --- | --- | --- |
| Prevent on break | Updates neighbors as normal | No update |
| Prevent on place | No update | Updates neighbors as normal |
| Prevent all | No update | No update |
| Replace block | No update | No update |

## Prevent on break

The default. The block updates its neighbors when you place it, but not when you break it. Use it to make a block
vanish from a build without sand falling or water rushing in.

## Prevent on place

The opposite: the block slips into place quietly, and breaking it updates neighbors as usual.

## Prevent all

Quiet both ways. Neither placing nor breaking it sends an update.

## Replace block

Also quiet both ways, and it can replace blocks and be swapped for them. This gets its own page: see
[Replace mode](03-replace-mode.md).

> [!NOTE]
> The "no update" part applies when the block is placed into empty space (air or water) and when it is removed to
> leave empty space.

---
title: Customizing loot
icon: item:minecraft:writable_book
tags: [datapacks]
summary: How datapacks can change what each block holds.
---
For pack makers. Each suspicious block reads its loot from a loot table, so a datapack can replace what it holds.

| Block | Loot table |
|:------|:-----------|
| Suspicious Clay | `archtweaks:blocks/suspicious_clay` |
| Suspicious Dirt | `archtweaks:blocks/suspicious_dirt` |
| Suspicious Mud | `archtweaks:blocks/suspicious_mud` |
| Suspicious Packed Mud | `archtweaks:blocks/suspicious_packed_mud` |
| Suspicious Red Sand | `archtweaks:blocks/suspicious_red_sand` |
| Suspicious Rooted Dirt | `archtweaks:blocks/suspicious_rooted_dirt` |
| Suspicious Soul Sand | `archtweaks:blocks/suspicious_soul_sand` |
| Suspicious Soul Soil | `archtweaks:blocks/suspicious_soul_soil` |

Replace the table with the same name in your own datapack. A block gives one item per dig, so each table should roll
a single item.

---
title: Configuration
icon: item:minecraft:comparator
tags: [config]
---
Beacon & Conduit Tweaks has a small config screen built with YACL.

* **Fabric:** open it from the mod list with Mod Menu.
* **Both loaders:** the settings are saved in `config/beacon-conduit-tweaks.json5`.

## Settings

| Option | What it does | Default |
|:-------|:-------------|:--------|
| Base Beacon Range | The flat range every active beacon has, before pyramid bonuses. At least 10. | 10 |
| Beacon Range / Level | Range added for each pyramid level. At least 10. | 10 |
| Conduit: Add Vanilla Range | Keeps the vanilla conduit range. Turn it off and conduits lose their range. | On |

## Per-block ranges

Two more settings are lists further down the screen. Each line is a block id and a range, written `block_id=range`, for example `minecraft:diamond_block=0.5`. A line that isn't in that form is dropped when you save. They are also saved in the config file as `beaconRangePerBlock` and `conduitRangePerBlock`.

| Option | What it does | Default |
|:-------|:-------------|:--------|
| Beacon Range / Block | Extra range for each block of that type in the pyramid. | Iron and gold 0, diamond and emerald 0.5, netherite 2 |
| Conduit Range / Block | Range for each frame block of that type around a conduit. A listed block also counts as a valid frame block. Restart the game after changing it. | Empty |

> [!NOTE]
> The mod is driven by the server (or your single-player world), so on a server, set these on the server.

Back to [Beacons](../01-beacons/index.md) or [Conduits](../02-conduits/index.md).

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

## Per-block ranges (config file only)

Two more settings are not on the config screen. Edit them in `config/beacon-conduit-tweaks.json5`, then restart the game.

| Setting | What it does | Default |
|:--------|:-------------|:--------|
| `beaconRangePerBlock` | Extra range for each block in the pyramid, listed by block id. | Iron and gold 0, diamond and emerald 0.5, netherite 2 |
| `conduitRangePerBlock` | Range for each conduit frame block, listed by block id. A listed block also counts as a valid frame block. | Empty |

> [!NOTE]
> The mod is driven by the server (or your single-player world), so on a server, set these on the server.

Back to [Beacons](../01-beacons/index.md) or [Conduits](../02-conduits/index.md).

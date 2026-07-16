# Beacon & Conduit Tweaks (Fabric/NeoForge)

![Version: 4.0.0-alpha.0](https://img.shields.io/badge/version-4.0.0--alpha.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: optional](https://img.shields.io/badge/client-optional-ff9800?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Make beacons and conduits reach as far as you want._

## Introduction

Beacon and conduit range in vanilla is fixed and, for large bases, often too small. Beacon & Conduit Tweaks makes
that range fully configurable — both the flat base range and how much each additional pyramid level or conduit
frame block extends it.

This is a server-side tweak: the effects are driven by the server (or your single-player world), and the config
screen is available on the client for convenience.

### Minecraft Versions

* 26.2: supported

### Current Features

* **Configurable beacon range**
  * Set the flat base range every active beacon provides (default: 10 blocks).
  * Set how much range each pyramid level adds on top of the base (default: 10 blocks per level).
  * Optionally add extra range per individual block in the pyramid.
* **Configurable conduit range**
  * Optionally keep the vanilla conduit range and add to it, or replace it entirely.
  * Set how much range each conduit frame block contributes.

### Configuration

Beacon & Conduit Tweaks uses [YACL](https://github.com/isXander/YetAnotherConfigLib) for its config screen
(accessible via Mod Menu on Fabric).

| Option | Description | Default |
| --- | --- | --- |
| Base Beacon Range | Flat range provided by an active beacon before pyramid bonuses. | 10 |
| Beacon Range / Level | Additional range added per completed pyramid level. | 10 |
| Beacon Range / Block | Additional range added per block in the pyramid. | — |
| Conduit: Add Vanilla Range | When enabled, the configured conduit range is added on top of the vanilla range instead of replacing it. | — |
| Conduit Range / Block | Range contributed by each conduit frame block. | — |

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs
you find.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to
the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

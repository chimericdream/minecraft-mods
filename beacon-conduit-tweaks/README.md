# Beacon & Conduit Tweaks (Fabric/NeoForge)

![Version: 4.2.0-beta.0](https://img.shields.io/badge/version-4.2.0--beta.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: optional](https://img.shields.io/badge/client-optional-ff9800?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Make beacons and conduits reach as far as you want._

## Introduction

Beacon and conduit range in vanilla is fixed and, for large bases, often too small. Beacon & Conduit Tweaks makes
that range configurable — the flat base range, how much each pyramid level adds, and whether conduits keep the
vanilla range.

This is a server-side tweak: the effects are driven by the server (or your single-player world), and the config
screen is available on the client for convenience.

### Minecraft Versions

* 26.2: Supported
* 26.1.2: Bug fixes only

### Current Features

#### Beacons

* **Configurable range**
  * Set the flat base range every active beacon provides (default: 10 blocks).
  * Set how much range each pyramid level adds on top of the base (default: 10 blocks per level).
  * Optionally add extra range per individual block in the pyramid. By default diamond and emerald blocks add 0.5 each and netherite adds 2. Edit these on the settings screen.
* Place a carpet on top of a beacon to completely stop the beam rendering
* Show and hide the beacon beam with tinted glass
  * Credit to [Discontinuous Beacon Beams by supersaiyansubtlety](https://www.curseforge.com/minecraft/mc-mods/discontinuous-beacon-beams) for the original idea

#### Conduits

* **Configurable range**
  * Set a flat base range, how many frame blocks earn a step of range (vanilla: 7), and how much range each step gives (vanilla: 16), just like the beacon settings.
  * Optionally set how much range each conduit frame block contributes, on the settings screen (none by default).

#### Guide book

* An in-game guide book (optional; needs the Opus mod 1.2.0 or newer) explains everything above. Craft one from a book and a glass block, or use `/opus give beaconconduittweaks:guide`.

### Configuration

Beacon & Conduit Tweaks uses [YACL](https://github.com/isXander/YetAnotherConfigLib) for its config screen
(accessible via Mod Menu on Fabric).

| Option | Description | Default |
| --- | --- | --- |
| Base Beacon Range | Flat range provided by an active beacon before pyramid bonuses. | 10 |
| Beacon Range / Level | Additional range added per completed pyramid level. | 10 |
| Beacon Range / Block | Additional range added per block in the pyramid, by block id. | Iron/gold 0, diamond/emerald 0.5, netherite 2 |
| Base Conduit Range | Flat range provided by an active conduit before frame bonuses. | 0 |
| Conduit Frame Blocks / Step | How many active frame blocks it takes to earn one step of range. | 7 |
| Conduit Range / Step | Range added for each full step of frame blocks. | 16 |
| Conduit Range / Block | Range contributed by each listed conduit frame block, by block id. | None |
| Conduit: Add Vanilla Range (deprecated) | The old on/off option. Only used for any of the three conduit settings above that your config file hasn't set: on gives 16 per step, off gives none. | On |

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs
you find.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to
the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

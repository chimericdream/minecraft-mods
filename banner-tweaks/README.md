# Banner Tweaks (Fabric/NeoForge)

![Version: 5.0.0-alpha.0](https://img.shields.io/badge/version-5.0.0--alpha.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_A mod that provides some vanilla-friendly tweaks to banners._

## Introduction

Vanilla banners cap out at six pattern layers, which runs out fast if you want a detailed design. Banner Tweaks
raises that limit and smooths out the rough edges around applying, rendering, and copying elaborate banners.

### Minecraft Versions

* 26.2: supported

### Current Features

* **More banner layers.** Raises the maximum number of pattern layers a banner can have. The limit is configurable
  from **1 to 32 layers** (default: **12**).
* Loom and banner-applying logic are patched to respect the increased layer limit, so you can keep stacking
  patterns in the loom past vanilla's cutoff.
* Banner rendering and banner-on-map rendering are patched so tall stacks of patterns display correctly, both in
  the world and when a banner is marked on a map.

### Configuration

Banner Tweaks uses [YACL](https://github.com/isXander/YetAnotherConfigLib) for its config screen (accessible via
Mod Menu on Fabric). Settings are stored in `config/bannertweaks.json5`.

| Option | Description | Default |
| --- | --- | --- |
| Max. banner layers | The maximum number of pattern layers a banner may have (range 1–32). | 12 |

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/banner-tweaks/issues) to report any bugs you
find.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to
the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

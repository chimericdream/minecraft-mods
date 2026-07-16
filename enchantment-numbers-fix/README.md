# Enchantment Numbers Fix (Fabric/NeoForge)

![Version: 4.0.0-alpha.0](https://img.shields.io/badge/version-4.0.0--alpha.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: not needed](https://img.shields.io/badge/server-not%20needed-9e9e9e?style=flat-square)

_Simple mod that converts enchantment levels above 10 to Roman numerals instead of their decimal version._

## Introduction

Minecraft only defines Roman numeral names for enchantment levels 1 through 10. Any enchantment above level 10 —
common once you start stacking commands, datapacks, or other mods — falls back to a plain decimal number like
"Sharpness 15", which looks out of place next to "Sharpness IX". Enchantment Numbers Fix continues the Roman
numerals past 10 so every level reads consistently (e.g. "Sharpness XV").

This is a purely client-side, cosmetic tweak. It changes how enchantment levels are displayed and nothing else, so
it is safe to add or remove at any time and does not need to be installed on the server.

### Minecraft Versions

* 26.2: supported

### Current Features

* Renders enchantment levels above 10 as Roman numerals instead of decimal digits.
* Applies anywhere the enchantment level name is shown (item tooltips, enchanted book names, etc.).

## Notes for Documentation

Because this mod only affects text rendering and adds no items or blocks, in-game documentation is not applicable.

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/enchantment-numbers-fix/issues) to report
any bugs you find.

## Credits

Thanks go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

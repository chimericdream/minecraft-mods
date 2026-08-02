# Sneaky Tweaks (Fabric/NeoForge)

![Version: 1.0.0](https://img.shields.io/badge/version-1.0.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: optional](https://img.shields.io/badge/client-optional-ff9800?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_A loose collection of tweaks to anything that treats sneaking as special._

## Introduction

Vanilla Minecraft already gives sneaking a handful of side effects — you don't fall off ledges when building, for instance — but the list is short. Sneaky Tweaks is where the rest of those "shouldn't this also check if you're sneaking?" moments go. There's no grand unifying mechanic here, just a running list of things in the game that behave (or should behave) differently while you're crouched.

### Minecraft Versions

* 26.2: supported

### Current Features

* **Sneak through sweet berry bushes unscathed.** In vanilla, crouching slows you down around a sweet berry bush but doesn't stop it from pricking you if you brush against it. Sneaky Tweaks cancels that damage outright — sneak through the bush and it treats you like it isn't even there.

## Notes for Documentation

Every feature in this mod is gated on the player crouching (`Player#isCrouching`), so the one thing worth calling out in any in-game or datapack documentation is which specific behavior changed — the trigger ("you were sneaking") is always the same.

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs you find, or to suggest another vanilla behavior that ought to care whether you're sneaking.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

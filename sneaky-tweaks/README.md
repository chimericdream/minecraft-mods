# Sneaky Tweaks (Fabric/NeoForge)

![Version: 1.0.0](https://img.shields.io/badge/version-1.0.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: optional](https://img.shields.io/badge/client-optional-ff9800?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_A loose collection of tweaks to anything that treats sneaking as special._

## Introduction

Vanilla Minecraft already gives sneaking a handful of side effects — you don't fall off ledges when building, for instance — but the list is short. Sneaky Tweaks is where the rest of those "shouldn't this also check if you're sneaking?" moments go. There's no grand unifying mechanic here, just a running list of things in the game that behave (or should behave) differently while you're crouched.

### Minecraft Versions

* 26.2: Supported

### Current Features

* **Sneak through sweet berry bushes unscathed.** In vanilla, crouching slows you down around a sweet berry bush but doesn't stop it from pricking you if you brush against it. Sneaky Tweaks cancels that damage outright — sneak through the bush and it treats you like it isn't even there.
* **Timed campfire immunity.** Sneaking on a lit campfire (or soul campfire) grants temporary damage immunity, similar to how sneaking near ledges keeps you from falling. Unlike stepping on magma, it's not indefinite — a depleting grace meter (shown as a HUD row of flame icons, right alongside the air-bubble meter) runs out after a few seconds of continuous sneaking, and refills once you step off. Configurable duration and an on/off toggle are in the mod's config screen.
* **Crouch bridging.** Sneak off a ledge without looking down and you'll walk straight across a gap of up to a few blocks as though it were solid ground — right up until you either look down or run out of gap to cross, at which point gravity remembers you exist. Both the gap size and the look-down angle that breaks it are configurable, along with an on/off toggle.

## Notes for Documentation

Every feature in this mod is gated on the player crouching (`Player#isCrouching`), so the one thing worth calling out in any in-game or datapack documentation is which specific behavior changed — the trigger ("you were sneaking") is always the same.

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs you find, or to suggest another vanilla behavior that ought to care whether you're sneaking.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

### Textures

The following textures and icons were downloaded from
the [Unused Textures](https://github.com/malcolmriley/unused-textures)
repository on GitHub and were created by user malcomriley. They are licensed under the
[Creative Commons Attribution 4.0 International License](https://creativecommons.org/licenses/by/4.0/) and are used
according to the permissions outlined by the artist and the license.

* Flame "Bubbles" ([hud_icons_infernal.png]([https://github.com/malcolmriley/unused-textures/blob/master/items/tool_spanner_iron.png](https://github.com/malcolmriley/unused-textures/blob/master/gui/hud_icons_infernal.png)); added in 1.1.0)

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

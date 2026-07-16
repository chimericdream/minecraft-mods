# Villager Tweaks (Fabric/NeoForge)

![Version: 6.0.0-alpha.0](https://img.shields.io/badge/version-6.0.0--alpha.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Some minor adjustments to villager interactions to ease a few headaches._

## Introduction

Trading halls, villager breeders, and reputation systems all involve fiddly vanilla behavior. Villager Tweaks bundles
a set of small, configurable quality-of-life changes to smooth out the most common villager frustrations — plus a
handy item for relocating villagers.

### Minecraft Versions

* 26.2: supported

## Current Features

### Bagged Villager

**Shift-right-click a villager while holding a Bundle** to "bag" it, storing the villager (and its trades) inside a
**Bagged Villager** item. Use the item on the ground to place the villager back into the world; the item turns back
into a plain Bundle. This makes moving villagers around your base far less painful than pushing them into boats or
minecarts.

### Trading tweaks

* **Override max trades** — optionally change how many times a villager will trade a given offer before it needs to
  restock. Set to `-1` for effectively infinite trades. *(Default: off, unlimited when enabled.)*
* **Demand modifier** — toggle vanilla's price-increase-on-heavy-trading behavior. *(Default: on.)*

### Reputation tweaks

* **Global reputation** — when enabled, all players share a single reputation with each villager instead of
  tracking it per-player. *(Default: off.)*
* **Negative reputation** — toggle whether hitting or killing villagers can lower your reputation. *(Default: on.)*

### Zombie conversion tweaks

* **Zombies always convert villagers** — force zombie-on-villager kills to convert rather than kill, regardless of
  difficulty. *(Default: off.)*
* **Override cure time** — set a fixed cure time (in ticks) for zombie villagers instead of the random vanilla
  timer. *(Default: off; default fixed time 3600 ticks when enabled.)*
* **Display cure time** — show the remaining time until a zombie villager is cured. *(Default: off.)*

### Misc.

* **Lure villagers** — villagers will follow you while you hold an emerald block, emerald ore, or deepslate emerald
  ore. *(Default: off.)*

### Configuration

Villager Tweaks uses [YACL](https://github.com/isXander/YetAnotherConfigLib) for its config screen (accessible via
Mod Menu on Fabric), grouped into **Trading**, **Zombie Conversion**, and **Misc.** sections.

## Notes for Documentation

The most documentation-worthy feature is the **Bagged Villager**, since it is a discoverable item with a specific
interaction. The remaining tweaks are configuration toggles rather than in-game content.

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/villagertweaks/issues) to report any bugs you
find.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to
the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

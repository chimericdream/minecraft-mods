# Better Target Dummies (Fabric/NeoForge)

![Version: 1.0.0](https://img.shields.io/badge/version-1.0.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Craft a target dummy block that can take on the skin of any mob, so you can test how attacks and damage types perform against specific mobs and mob categories._

## Introduction

### Minecraft Versions

* 26.2: supported

### Current Features

* **Target Dummy block.** Craft one with a target block and 4 mob drop items. Right-click it with a
  spawn egg (real or Dummy) to bind that mob to the dummy — the real mob spawns on top, held in place
  (no AI, no wandering). Each of its 4 sides has its own look, and the block faces you when placed.
* Because it's the actual mob, not a lookalike, every damage modifier that cares what you're hitting
  (Smite, Bane of Arthropods, mob-specific resistances, armor class, ...) comes out correct.
* Bound mobs are immune to environmental hazards — fire, rain, drowning, suffocation, and the like
  never hurt them, and won't even make them flinch or cry out. Your hits still land normally, with
  the usual sound and hit-flash.
* Bound mobs also stay quiet otherwise — no idle ambient noise (clucking, groaning, and the like).
* **Powered by redstone.** The dummy only spawns its mob while receiving a redstone signal — flip a
  lever or toggle a button to turn it on and off at will. Losing power removes the mob (its binding is
  remembered, so restoring power brings the same mob right back); breaking the block or `/kill`ing the
  dummy also work as one-off ways to remove it.
* **Dummy Spawn Egg.** A survival-friendly alternative to needing a real spawn egg for every mob you
  want to test. Craft one from an egg and paper, then right-click in the air to pick a mob from a
  searchable list (its tooltip always shows the current pick) and right-click a dummy with it. Pick
  again any time to switch which mob it binds. An anvil rename to the mob's name (e.g. "Zombie" or
  "Cave Spider") still works too, if you'd rather type it.
* Hitting a bound dummy shows the exact damage dealt in the action bar, so you can compare an attack's
  output across different mobs without digging through health bars or the combat log.
* The bound mob starts out facing whichever cardinal direction (north/east/south/west) you were
  standing on when you bound it — stand north of the dummy and it'll face north. Right-click the dummy
  empty-handed to rotate it 90° clockwise; sneak + right-click empty-handed to clear the binding
  entirely.
* Right-click with a different spawn egg (or a re-picked Dummy Spawn Egg) at any time to swap the
  bound mob.

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs you find or suggest new features.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

This mod was inspired by the many different "target dummy" mods that already exist, but none of them were quite what I was wanting, so this one was born.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)

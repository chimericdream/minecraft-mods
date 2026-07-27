### 26.1.2 - 6.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.1.2 and requires Java 25. It is not compatible with 1.21.x.

#### Changes

* Updated to Minecraft 26.1.2 / Architectury 20.0.7, built against official Mojang mappings.
* The NeoForge loot-modifier registry now uses ChimericLib's `LootModifierHelper` instead of its own copy
  of the registration boilerplate. The villager-conversion loot modifier itself is unchanged.
* Added `TEST_PLAN.md` and `POTENTIAL_FEATURES.md`; updated the README.

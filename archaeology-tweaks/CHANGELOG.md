### Unreleased changes

#### New Features

* Suspicious blocks now generate naturally in the world as small, rare deposits — clay near rivers
  and beaches, dirt in forests, taiga, and plains, mud and packed mud in swamps, red sand in
  badlands, rooted dirt in forests, and soul sand/soul soil anywhere in the Nether. Brushing one now
  also yields real loot instead of nothing.
* Added four new advancements:
  * **First Dig** — use a brush for the first time.
  * **Preservationist** — collect every pottery sherd.
  * **Interdimensional Archaeology** — brush a suspicious block in the Nether.
  * **Lucky Block** — get a second drop from a block via the Gentle Touch enchantment.


### 26.2 - 4.1.0

#### Changes

* Added a **Gentle Touch** enchantment for brushes (levels 1–3, applied via anvil — it's not
  villager-tradeable at low levels). Each level gives a 2% chance per completed brush action to reroll
  instead of finishing: the block resets to unbrushed and drops its item without converting to the
  base terrain block, letting a second (or more) loot roll come from the same suspicious block. Works
  on both the mod's own suspicious blocks and vanilla suspicious sand/gravel.


### 26.2 - 4.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.2 and requires Java 25. It is not compatible with 26.1.x or
  1.21.x.

#### Changes

* Updated to Minecraft 26.2, Architectury 21.0.4, Fabric API 0.154.2+26.2 and NeoForge
  26.2.0.15-beta.
* No gameplay changes of its own. Everything listed under the 26.1.2 release below is included —
  the two are the same mod built for different Minecraft versions.


### 26.1.2 - 3.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.1.2 and requires Java 25. It is not compatible with 1.21.x.

#### Changes

* Updated to Minecraft 26.1.2 / Architectury 20.0.7, built against official Mojang mappings.
* Removed a dead local in `ATBrushableBlockEntity` left over from porting vanilla's
  `BrushableBlockEntity`. No behavior change.
* Added `README.md`, `TEST_PLAN.md` and `POTENTIAL_FEATURES.md`.

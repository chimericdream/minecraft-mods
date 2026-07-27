### 26.2 - 10.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.2 and requires Java 25. It is not compatible with 26.1.x or
  1.21.x.

#### Changes

* Updated to Minecraft 26.2, Architectury 21.0.4, Fabric API 0.154.2+26.2 and NeoForge
  26.2.0.15-beta.
* No gameplay changes of its own. Everything listed under the 26.1.2 release below is included —
  the two are the same mod built for different Minecraft versions.


### 26.1.2 - 9.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.1.2 and requires Java 25. It is not compatible with 1.21.x.

#### New Features

* Armoires now render the chestplates and leggings you put in them directly. Previously each armoire
  spawned four invisible marker armor stands inside itself to display the armor; those entities are gone,
  and any left over from an earlier version are discarded when the block loads. The armor appears in
  exactly the same place as before.
* The demo world showcase was redesigned: blocks are grouped by material along a rising staircase, with
  flat color-gradient regions, fixed block-family bands per wood and stone set, filled glass jars, and a
  tool showcase wall.

#### Bug Fixes

* **Glass jars no longer crash the client.** The renderer only knew how to draw honey and milk and threw
  for anything else — but a jar accepts any fluid, so a jar of water or lava hard-crashed on render.
  Water, lava, milk and honey are now drawn explicitly, and anything else falls back to water.
* **Glass jars work with hoppers.** The block entity claimed to be a container but had no backing
  storage, so a vanilla hopper finding a jar threw immediately. The container contract is now implemented
  properly.
* **Hoppers can fill a glass jar past its first stack.** A one-slot container reads as "full" at 64 items,
  and a pushing hopper gives up before it ever asks whether the jar would accept more — so automation
  could never reach the jar's compressed reserve. The jar now presents a second, virtual input slot that
  routes into the reserve. It yields nothing on extraction, so it is invisible to pulling hoppers, and a
  jar at capacity reports as full so neighboring hoppers stop probing it.
* **A jar of honey can be bottled again.** The check compared the stored fluid against the wrong object
  entirely, so it was always false, and a second, unreachable branch sat behind it. Fluids with no bottled
  form now return nothing without draining the jar.
* Fixed a client memory leak from the glass jar's item-entity cache. It was keyed on item stacks, which
  have no value-based equality, so nearly every lookup missed and inserted a new entry that was never
  evicted — growing every frame. It is now keyed on the components that actually drive the jar's
  appearance and bounded to 256 entries.
* Mobs in glass jars now all face the same direction.
* Fixed crashes on orphaned shutter halves and armoire halves. An open shutter half only exists next to
  its parent shutter; if the parent was missing — from a `/setblock`, a world edit, or a half-broken
  shutter — using or breaking the orphan operated on whatever block sat where the parent should be and
  threw. Both now no-op instead.
* Shelves play their insert sound again when an item partially merges into an existing stack.
* Glass jar interactions are now correctly threaded to the hand that was used, and jar state is mutated
  server-side only.

#### Changes

* Updated to Minecraft 26.1.2 / Architectury 20.0.7, built against official Mojang mappings.
* Crates and Minekea barrels now use ChimericLib's shared viewer-count helper, and the Wrench is a thin
  subclass of ChimericLib's shared wrench. No behavior change to either.
* `CompressedBlocks` and `DyedBlocks` modeled their block tables with tuple classes borrowed from a
  hardware-info library, read through opaque accessor chains; they now use named records. A 1:1
  transform — every value and ordering is preserved.
* Fixed the purpur pillar beam and cover textures, which used the pillar's single texture instead of its
  separate top and side textures.
* Added 3 GameTests covering the glass jar container contract, glass jar interactions, and the orphaned
  shutter half.


### 1.21.x - 7.0.0

#### BREAKING CHANGES

* This release requires Architectury and ChimericLib

#### Notable Changes

* Added support for NeoForge
* Refactored almost all of the code to use the Architectury API and ChimericLib


### 1.21.x - 6.1.0

#### New Features

* Mob in a jar
  * Glass jars can now be used to capture and display some mobs. Right-clicking a mob with an empty jar will capture it, and shift-right-clicking a jar will release the mob.
  * Mobs that can be captured include:
    * Allays
    * Bats
    * Endermites
    * Silverfish
    * Small slimes
    * Vexes
* Framed planks
  * These planks have a similar appearance to crates, but are purely decorative.


### 1.21.x - 6.0.0

#### BREAKING CHANGES

This release fixes a crash on 1.21.1 related to Minekea's variant barrels. _Before upgrading_,
make sure to break any existing variant barrels that have been placed in your world. Any items
left in a Minekea barrel will be lost upon upgrade!

#### New Features

* Bamboo furniture
  * Added bamboo variants for all furniture and other wood type items
* Double crates
  * Crates can now merge along a horizontal axis to form double crates, similar to the behavior for chests
* Trapped crates and double crates
  * Added trapped variants of crates and double crates
* Added new ancient lantern variant

#### Changes

* Beams now support wrenches from other mods. Specifically, anything in the `c:tools/wrenches` item tag will work


### 1.21.x - 5.2.0

#### New Features

* Votive candles
    * Fancy candles that behave identically to vanilla ones
* Wax
  * Wax comes in both item and block form. It can be acquired by smelting candles or honeycomb.
  * Wax is a slippery block, causing entities to slide slightly less than on ice.
  * Similar to glazed terracotta blocks, honey and slime do not stick to wax.

#### Changes

* Armor-oires no longer need to be placed on a solid block.


### 1.21.x - 5.1.0

#### New features

* Armor-oires! These beautiful furniture items can store up to 4 complete sets of armor!

#### Bug fixes

* Crash when placing shutters on a top or bottom surface ([theaceofthespade](https://github.com/theaceofthespade))


### 1.19.1-2 - 4.0.2

#### Bug Fixes
* Fix server/client desync when using hammers on a dedicated server

### 1.19.1-2 - 4.0.1

#### Bug Fixes
* Fix client crash when trying to put a sandwich from the [Sandwichable](https://www.curseforge.com/minecraft/mc-mods/sandwichable) mod into a display case


### 1.19.1-2 - 4.0.0

First release for 1.19.1-2!


### 1.19 - 3.4.0

#### Bug Fixes
* Wooden blocks from BYG and BetterEnd/Nether now break with an axe instead of pickaxe
* Block painter now works correctly on servers instead of crashing your game
* Item models for some furniture blocks now render correctly in item frames
* When cobbled end stone is disabled, Minekea will no longer spam a bunch of invalid JSON warnings and other junk in the logs

#### New Features
* Variant barrels now function as a workstation for Fisherman villagers ([#64](https://github.com/chimericdream/minekea-fabric/issues/64))


### 1.19 - 3.3.3

#### Bug Fixes
* Fix recipes for bookshelf stairs and vertical stairs for BetterEnd / BetterNether ([#62](https://github.com/chimericdream/minekea-fabric/issues/62))
* Fix conflicting recipe for wool pressure plates ([#62](https://github.com/chimericdream/minekea-fabric/issues/62))


### 1.19 - 3.3.2

#### Bug Fixes
* Fix recipes for bookshelf stairs and vertical stairs
* Fix recipes for dyed bone block storage bookshelves


### 1.19 - 3.3.1

#### Bug Fixes
* Chair placement: fix issue with placement not behaving as expected when you weren't looking perfectly straight ([#60](https://github.com/chimericdream/minekea-fabric/issues/60))


### 1.19 - 3.3.0

#### Bug Fixes
* BYG: re-add white mangrove to BYG blocks
* End stone: fix end stone texture when cobbled end stone is enabled
* Wrench: make wrenches non-stackable

#### New Features
* Waterloggable blocks: most blocks can now be waterlogged
* Hammers: hammers are a new type of tool which can place blocks randomly selected from your hotbar 
* Shutters: new block type which functions much like a trapdoor, but opens 180 degrees
* Dyed blocks: added dyed variants for many vanilla block types
* Compatibility: added support for Better End, Better Nether, and Mythic Metals!

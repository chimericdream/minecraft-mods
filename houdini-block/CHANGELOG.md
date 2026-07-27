### 26.1.2 - 2.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.1.2 and requires Java 25. It is not compatible with 1.21.x.

#### Bug Fixes

* **Breaking a Houdini Block no longer drops two of them.** The block hands itself back manually when
  broken, but it also carried a loot table — its properties were copied wholesale from stone, which pulled
  in stone's derived drops — so a player breaking it with the correct tool got the loot-table drop *on top
  of* the manual one. The loot table has been removed.
* Fixed ghost items and ghost blocks on the client. Both the right-click swap and the break path spawned
  the dropped item and changed the block without checking which side they were running on, so the client
  spawned its own copy that popped out of existence as soon as the server synced, and swapped the block
  locally before the server agreed. Particles are still fired on both sides, as vanilla does.

#### Changes

* Updated to Minecraft 26.1.2 / Architectury 20.0.7, built against official Mojang mappings.
* Added `HoudiniBlockDropGameTest` — 5 GameTests, including a survival-break test that asserts exactly one
  drop.

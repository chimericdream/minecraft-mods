### 26.2 - 6.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.2 and requires Java 25. It is not compatible with 26.1.x or
  1.21.x.

#### Changes

* Updated to Minecraft 26.2, Architectury 21.0.4, Fabric API 0.154.2+26.2 and NeoForge
  26.2.0.15-beta.
* No API changes of its own. Everything listed under the 26.1.2 release below is included — the two are
  the same library built for different Minecraft versions.
* Mods in this suite now require ChimericLib 6.0.0 or later.


### 26.1.2 - 5.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.1.2 and requires Java 25. It is not compatible with 1.21.x.
* `SimpleInventoryScreenHandler` and `DoubleWideInventoryScreenHandler` are now thin subclasses of the
  new `InventoryScreenHandler`. Their behavior is unchanged, but anything overriding their internals
  should be re-checked against the base class.
* `ColorHelpers`' per-color palette arrays are now private. Use the new `getTints(String)` accessor,
  which hands out a defensive copy — callers could previously mutate shared palette state.
* `BlockUtils` now lives here (`com.chimericdream.lib.blocks.BlockUtils`), moved out of Sponj.

#### New Features

* `screen/InventoryScreenHandler` — base class for fixed-grid container menus. Owns slot layout,
  `quickMoveStack`, and the `removed()`/`stopOpen()` pairing that keeps viewer counts balanced.
* `inventories/ContainerOpenersCounters` — factory for `ContainerOpenersCounter`, replacing hand-rolled
  anonymous implementations. It takes the menu class as a required parameter and confirms ownership
  against the block entity, so a counter can no longer be copy-pasted onto the wrong menu type.
* `item/AbstractWrenchItem` — the wrench placement/facing/axis/slab logic that Minekea and
  Hopper X-Treme each carried a byte-for-byte copy of.
* `blocks/BlockUtils` — moved here from Sponj.
* `neoforge/loot/LootModifierHelper.createRegister(modId)` — wraps the NeoForge global-loot-modifier
  `DeferredRegister` boilerplate.
* `ColorHelpers.getTints(String)`.

#### Bug Fixes

* Partial stacks now merge correctly. `ImplementedInventory.isMatchingPartialStack` compared stacks with
  `ItemStack.matches`, which also compares counts, so two otherwise-identical partial stacks only merged
  when their counts happened to be equal. Every `tryInsert` consumer — Minekea shelves, armoires and glass
  jars, the block painter, the hopper filter — silently failed to merge partial stacks.
* `ImplementedInventory.clearContent` now preserves the fixed slot count of a `NonNullList.withSize(...)`
  instead of letting the size drift.
* `BlockConfig.getTexture()` no longer throws "No default ingredient set" for a config that supplies an
  explicit texture but no ingredient. It used `Map.getOrDefault`, whose default argument Java evaluates
  unconditionally, so the fallback ran even when it was never needed.
* Menus now issue `stopOpen` when closed, so opener counts stay balanced.
* `ColorHelpers.getTint` bounds its tint index at both ends.

#### Changes

* Updated to Minecraft 26.1.2 / Architectury 20.0.7, built against official Mojang mappings.
* Added a test harness: `common`'s `testFixtures` variant publishes `BootstrapMinecraft` and the shared
  `GameTestContainers` / `GameTestEntities` / `GameTestMenus` helpers for downstream mods to reuse.
* Added 43 JUnit tests across 9 classes and 11 GameTests across 5 classes. The GameTests live in an
  isolated `gametest` source set that never ships.
* ChimericLib now resolves as an in-build `project()` dependency inside the mod monorepo, so consumers
  compile against its source directly. `publish:lib` is release-only.

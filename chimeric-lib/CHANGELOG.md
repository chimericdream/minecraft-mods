### Unreleased changes

#### New Features

* `commands/ChimericCommand`, `commands/ChimericCommands` — a small per-mod command-registration
  framework: implement `ChimericCommand#build` to return a command tree, then call
  `ChimericCommands.register(...)` during your mod's init. Wraps Architectury's
  `CommandRegistrationEvent` so consuming mods don't need to touch it directly, and multiple commands
  that share a root literal (e.g. several features all registering under `chimericlib`) merge together
  automatically via Brigadier's own node-merging.
* `commands/blockstate/BlockStateCommand` — `/chimericlib blockstate get|set|modify <pos>` (requires
  permission level 2). `get` mirrors vanilla's `/data get block`; `set` mirrors `/setblock`, replacing
  the block via `BlockStateArgument`/`BlockInput`; `modify` is new — it merges only the given
  properties (e.g. `[facing=east]`) onto whatever block is already there, via the new
  `commands/blockstate/BlockPropertiesArgument`.
* `commands/PlatformCommandArgumentTypes` — the platform hook a custom Brigadier `ArgumentType` needs
  to sync to the client (vanilla's own reverse class-to-info lookup used for that sync is private and
  only self-populated for its built-ins). Fabric and NeoForge each get their own `Provider`
  implementation; register a custom argument type through
  `PlatformCommandArgumentTypes.registerByClass(...)`.
* `blocks/model/ModelUtils` — vanilla-block-shaped datagen helpers generalized out of Minekea:
  `registerBlockWithAxis`/`registerBlockWithWallSide`/`registerBlockWithHorizontalFacing`/
  `registerBlockWithFacing` (rotation dispatch for pillar/wall-mounted/facing block shapes),
  `registerLanternBlock`, `registerCrop` (vanilla age-property crop dispatch), `registerGeneratedItem`,
  and `makeInvalidVariant` (bedrock-textured placeholder for illegal blockstate combinations).
* `blocks/model/CustomBlockModel` — a `ModelTemplate` that also emits a `render_type` field (vanilla's
  own template has no render-type support); `CustomCropModel` is the cutout-rendered crop preset built
  on it.
* `blocks/RecipeUtils#unlockedByHas` — the `.unlockedBy(RecipeProvider.getHasName(x), generator.has(x))`
  idiom as a one-line wrapper around any `RecipeBuilder`.
* `fabric/blocks/TranslationUtils#addBlockAndItem` — the block+item translation-pair idiom in one call.
* `fabric/blocks/TagUtils#applyMineableTag` — the tool-tag-application idiom (with a `Tool`/default-tool
  overload), now also used by the family generators below to remove their own copy of it.
* `fabric/data/TextureGenerator`, `fabric/data/JarAccess` — programmatic PNG datagen and safe vanilla-jar
  asset reading, generalized out of Minekea. `TextureGenerator` now takes the consuming mod's ID so its
  `<modId>.datagen.resource-path` environment variable and `assets/<modId>/textures` base path are
  mod-specific rather than hardcoded.
* chimeric-lib now ships its own access widener (`chimericlib.accesswidener`) — needed for
  `CustomBlockModel`'s use of `ModelTemplate`'s internals. Previously commented-out scaffolding in both
  `common/build.gradle` and `fabric/build.gradle` is now active.
* `blocks/family/BlockFamily` — declare a base block's `BlockConfig` once and register whichever of
  its stairs/slab/wall variants you need, each with a derived `BlockConfig` (ingredient set to the
  base block; materialName/texture/tool/flammable/translucent/renderType inherited unless overridden).
  Vanilla `StairBlock`/`SlabBlock`/`WallBlock` are used by default; a per-variant factory override
  lets a mod substitute its own subclass.
* `fabric/blocks/family/StairsBlockDataGenerator`, `SlabBlockDataGenerator`, `WallBlockDataGenerator` —
  recipes, mineable/`#walls` tags, loot tables, blockstate/item models, and translations for each
  family variant. `BlockFamilyDataGenerators.of(family)` wraps whichever variants a `BlockFamily`
  registered into the matching generators, ready to fold into a mod's own datagen aggregator.

#### Changes

* `blocks/BlockDataGenerator#configureRecipes` now takes a `RecipeProvider` parameter, and
  `#configureBlockLootTables` now takes a `HolderLookup.Provider` parameter. Not breaking in practice —
  nothing in this suite implemented the old signature.

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

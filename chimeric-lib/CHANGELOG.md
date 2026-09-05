### Unreleased changes

#### New Features

* `blocks/family/BlockFamily` — declare a base block's `BlockConfig` once and register whichever of
  its stairs/slab/wall variants you need, each with a derived `BlockConfig` (ingredient set to the
  base block; materialName/texture/tool/flammable/translucent/renderType inherited unless overridden).
  Vanilla `StairBlock`/`SlabBlock`/`WallBlock` are used by default; a per-variant factory override
  lets a mod substitute its own subclass. Derivation is deferred to registration time, so a family can
  be declared against a base block's `RegistrySupplier` before that supplier resolves.
* `fabric/blocks/family/StairsBlockDataGenerator`, `SlabBlockDataGenerator`, `WallBlockDataGenerator` —
  recipes, mineable/`#walls` tags, loot tables, blockstate/item models, and translations for each
  family variant. `BlockFamilyDataGenerators.of(family)` wraps whichever variants a `BlockFamily`
  registered into the matching generators, ready to fold into a mod's own datagen aggregator.
  `fabric/blocks/family/FamilyBlockModels` holds the shared model registration, including a
  `registerStairsBlock(BlockModelGenerators, StairBlock, TextureMapping)` overload that uses the
  default inner/straight/outer stair model templates.
* `blocks/FallingUpwardBlock`, `blocks/Risable`, `entities/FallingUpwardBlockEntity` — the inverse of
  vanilla's falling block/`Fallable`: blocks that rise toward the sky instead of falling, despawning
  past the top of the world instead of the bottom, and carrying block-entity NBT with them.
* `util/ChimericLibParticleUtils#spawnParticleAbove` — spawns a particle just above a block position,
  used by `FallingUpwardBlock`'s dust animation.
* `util/ProfileUtils#makeGameProfile` — builds a `GameProfile` with a texture property from a
  UUID-encoding int array and base64 texture payload, for custom player-head skins.
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
* `trims/TrimMaterialConfig`, `trims/TrimMaterialRegistryHelper`, and `trims/ArmorTrimAtlasProvider` —
  a loader-agnostic datagen layer for custom armor trim materials. `TrimMaterialRegistryHelper.bootstrap`
  registers a mod's `TrimMaterialConfig` list into the `trim_material` dynamic registry (usable from
  both Fabric's `buildRegistry` and NeoForge's `RegistrySetBuilder`); `ArmorTrimAtlasProvider` generates
  the mod's `assets/minecraft/atlases/armor_trims.json`/`items.json` overrides from that same list, so a
  consuming mod only has to hand-author the palette texture and its own material definitions instead of
  restating vanilla's full trim-pattern texture list per material.
* `fabric/trims/TrimmedArmorItemModel` and `fabric/trims/TrimmedArmorModelLoadingPlugin` — a Fabric port
  of NeoForge's own `neoforge:trimmed_armor` item model type, fixing a Fabric-only gap: vanilla's armor
  item icon only recognizes its own hardcoded trim materials, so a modded material renders correctly on
  the worn 3D layer (which reads the `ArmorTrim` component directly) but falls back to the untrimmed 2D
  inventory icon. `TrimmedArmorModelLoadingPlugin` registers a Fabric Model Loading API
  `modifyItemModelBeforeBake` hook that wraps every item's model with `TrimmedArmorItemModel`, which
  no-ops unless the live stack actually carries both an `ArmorTrim` and an armor-slot `Equippable`
  component, then resolves the trim overlay sprite from whatever `TrimMaterial` is on the stack — the
  same source vanilla's own entity layer reads — instead of a fixed list of cases baked into a model
  JSON. (The wrap has to be unconditional and the check deferred to render time: Minecraft doesn't bind
  items' default components until a `ReloadableServerResources` reload, which hasn't happened yet during
  the client's very first resource/model reload, so checking a default component at bake time throws
  `NullPointerException: Components not bound yet` — see `docs/TESTING.md`.) Reaching the two private
  vanilla classes needed to bake a flat icon layer (`CuboidItemModelWrapper`,
  `ItemModelGenerator.ItemLayerKey`) is done via reflection, not an access widener — widening a private
  constructor on a private *nested* class compiled fine but still threw `IllegalAccessError` at actual
  game runtime. This needs no resource pack override, runs automatically for any mod's armor and any
  mod's trim materials (not just chimeric-lib's), and is wired into chimeric-lib's own Fabric client
  init, so a consuming mod doesn't need to call anything — registering trim materials through
  `TrimMaterialRegistryHelper` is enough for their icons to render correctly on both loaders.
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
  overload), now also used by the family generators above to remove their own copy of it.
* `fabric/data/TextureGenerator`, `fabric/data/JarAccess` — programmatic PNG datagen and safe vanilla-jar
  asset reading, generalized out of Minekea. `TextureGenerator` now takes the consuming mod's ID so its
  `<modId>.datagen.resource-path` environment variable and `assets/<modId>/textures` base path are
  mod-specific rather than hardcoded.
* `BlockConfig.getTextureOrDefault()` — returns a named texture, falling back to the config's own
  default texture (or to an explicitly supplied one) instead of null.
* chimeric-lib now ships its own access widener (`chimericlib.accesswidener`) — needed for
  `CustomBlockModel`'s use of `ModelTemplate`'s internals. Previously commented-out scaffolding in both
  `common/build.gradle` and `fabric/build.gradle` is now active.
* Added `GameTestPlayers` to the `testFixtures` GameTest helpers (alongside `GameTestContainers` /
  `GameTestEntities` / `GameTestMenus`): `makeFacingPlayer` creates a mock server player positioned
  and oriented at a target block, and `useItem` calls an item's `use()` and applies the resulting
  `InteractionResult.Success#heldItemTransformedTo()` to the held item — both needed for any GameTest
  simulating an item that overrides the general `Item#use` dispatch (buckets doing their own
  reach-limited raycast, e.g.) rather than `useOn`/`useWithoutItem`, which `GameTestHelper#useBlock`
  already covers. `makeFacingPlayer` also works around a `NullPointerException` mock players hit
  calling `ServerPlayer#lookAt` directly (it tries to send a look-rotation packet over a connection
  mock players don't have). On 26.1.2 it builds its own mock `ServerPlayer`: `GameTestHelper` here has
  only `makeMockPlayer(GameType)` (a plain `Player`) and a `makeMockServerPlayerInLevel()` hardcoded to
  creative mode, so the helper constructs a `ServerPlayer` directly and overrides `gameMode()`, the
  accessor the game reads a player's game type back through.

#### Changes

* `blocks/BlockDataGenerator#configureRecipes` now takes a `RecipeProvider` parameter, and
  `#configureBlockLootTables` now takes a `HolderLookup.Provider` parameter. Nothing in this suite
  implemented the old signatures.


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

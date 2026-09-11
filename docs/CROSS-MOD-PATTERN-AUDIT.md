# Cross-mod pattern audit (2026-09-08)

**Purpose**: a catalog of places where the same kind of thing — a mixin, a datagen provider, a config
screen, a raycast, a renderer, a test — is implemented a different way in different mods (or even
within the same mod), gathered as raw material for a standardization pass and for deciding what should
move into `chimeric-lib`. This is not itself a plan or a set of recommendations to execute blindly —
it's the "longer list to trim down" the audit was asked for.

**Not committed** — this is a working document; delete or commit it deliberately once acted on.

**Scope**: originally all 27 mods active per `settings.gradle`'s `projectList` at the time of this audit
(archaeology-tweaks, artificial-heart, athenaeum, banner-tweaks, beacon-conduit-tweaks,
better-portal-linking, better-target-dummies, but-what-about, camel-nostrils, chimeric-lib,
effective-gear, enchantment-numbers-fix, flat-bedrock, hang-from-slabs, hopper-xtreme, houdini-block,
**jdcrafte**, log-all-the-things, minekea, miniblock-merchants, next-update-now, shulker-stuff,
sneaky-tweaks, sponj, stack-it-up, toy-box, villager-tweaks). Note `jdcrafte` is flagged as a
documentation-drift item — see the companion bugs file — `settings.gradle` currently had it active,
while `CLAUDE.md` and the mod's own `TEST_PLAN.md` both still described it as inactive/disabled. This
audit followed `settings.gradle` per its own "verify against settings.gradle" instruction, so jdcrafte
is included throughout.

**Scope update (2026-09-09)**: commit `22537baaf` re-enabled four previously-commented-out mods
(`blacklight`, `cobblicious`, `pannotia-companion`, `playgrounds`), bringing `settings.gradle`'s active
count to 31 (32 including `all-hallows-steve`, added separately and **not yet covered** by this audit —
it's a brand-new mod, not a re-enable, and out of scope for this pass). All four re-enabled mods are
surveyed below and folded into the relevant sections; every count/denominator elsewhere in this document
("X of 27") has been updated to "X of 31" where these four mods change the number, and left alone where
they don't (i.e. where none of the four have the feature being counted). All four are still pure
`scripts/init-mod.sh` scaffolds — Architectury boilerplate only, no blocks/items/mixins-with-logic/
datagen/tests — so most sections (§1-5) gain only a denominator bump, not new findings; §6 (registration)
and the bugs file gain real findings, called out explicitly below.

**Method**: six research passes (mixins, data generation, gameplay logic, rendering, testing,
registration/mod-lifecycle), each reading real source files across every relevant mod, not just
grepping class names. Concrete `mod:file:line` citations are given wherever possible so findings can be
re-verified quickly rather than taken on faith.

---

## Executive summary — top standardization/consolidation priorities

Roughly ordered by how much duplicated code a fix would remove and how many mods it touches:

1. **YACL config boilerplate** — 9 mods each hand-write an identical ~20-line `ConfigClassHandler`
   skeleton, plus 9 near-identical Fabric `ModMenuIntegration` classes and 8 near-identical NeoForge
   config-screen registrations (one mod, `better-portal-linking`, is missing its NeoForge one
   entirely — see bugs file). No chimeric-lib base class exists yet, despite chimeric-lib's own
   `POTENTIAL_FEATURES.md` already naming "a config sync layer... nearly every mod in the suite with
   server-side config wants this" as a backlog item. **~26 files, single biggest opportunity.**
2. **Config sync / networking** — three incompatible strategies for the same "send a payload
   cross-loader" job coexist (Architectury `NetworkManager` in one mod vs. hand-rolled
   `PayloadTypeRegistry`/`RegisterPayloadHandlersEvent` pairs in two more vs. a duplicated
   `@Mixin(PlayerList.class)` login-sync hack in a fourth). chimeric-lib's own backlog already names
   this too ("Networking wrapper... so common code never touches loader-specific channels directly").
3. **minekea's datagen interfaces have drifted from chimeric-lib's real ones.** minekea (the heaviest
   datagen consumer in the repo, ~90 files) runs its own independently-declared duplicate of
   chimeric-lib's `BlockDataGenerator`/`FabricBlockDataGenerator`/`ItemDataGenerator`, with an adapter
   shim bridging the two, and the item-level copy has already diverged in method signature.
4. **GameTest testFixtures under-adoption.** Only 2 of 8 GameTest-having mods actually consume
   chimeric-lib's shared `GameTestContainers`/`GameTestPlayers` kit, even though at least 3 more
   (minekea, houdini-block, hopper-xtreme) have hand-rolled helpers that duplicate it almost exactly —
   and chimeric-lib's own `POTENTIAL_FEATURES.md` backlog already names every one of these gaps by
   mod, unprompted.
5. **`RealNeighborMovingBlockRenderState` + its `createMovingBlock` factory** exist as one class
   duplicated verbatim across 2 mods, plus a helper method duplicated 3x inside one of them. Zero
   mod-specific logic — a clean, no-behavior-change chimeric-lib extraction.
6. **Mixin conventions** — 5 different class-naming styles, 3 different injected-method-naming styles,
   4 different mixin-config-JSON placement conventions, none documented anywhere, with several mods
   mixing more than one style *internally*.
7. Smaller, still-worth-doing: a seat-entity-occupancy check duplicated verbatim in two minekea files
   (the entity itself already lives in chimeric-lib), a particle-burst helper duplicated in two
   minekea files (chimeric-lib has an unused near-miss for it already), `hopper-xtreme`'s
   upgrade/conversion recipe-builder helpers (generically useful, currently mod-local), and
   `SimpleInventoryScreen` being reimplemented locally by 3 mods because it's hard-coded to one
   texture.

---

## 1. Mixins

Full detail (every mixin file, every convention, with citations) lives in the raw research notes this
document was built from; the summary below is complete for standardization purposes.

### 1.1 Mixin class naming — 5 distinct conventions in simultaneous use

| Style | Pattern | Mods |
|---|---|---|
| A | `<ACRONYM_OR_FULLNAME>$<Target>Mixin` | archaeology-tweaks (partial), artificial-heart, better-portal-linking, better-target-dummies, camel-nostrils, effective-gear, shulker-stuff, sneaky-tweaks, log-all-the-things |
| B | `<Target>Mixin` (no prefix) | archaeology-tweaks (partial), next-update-now, minekea, houdini-block, chimeric-lib |
| C | `<ACRONYM><Target>Mixin` (no `$`) | enchantment-numbers-fix, miniblock-merchants, villager-tweaks |
| D | `<FullModName><Target>Mixin` (no `$`) | beacon-conduit-tweaks, hopper-xtreme, flat-bedrock (partial) |
| E | `Mixin<Target>` (reversed word order) | stack-it-up (only mod using this order) |

Accessor mixins have their own, partly-independent split: `<Target>Accessor` (camel-nostrils,
shulker-stuff, minekea) vs. `Accessor<Target>` (stack-it-up, matching its style-E reversal).

**Internal inconsistency** (same mod, multiple styles — the highest-value finding since it can't be
explained by "different mod, different author"):
- `archaeology-tweaks`: 2 of 7 mixins use style A (`AT$AbstractVillagerMixin`, `AT$WanderingTraderMixin`), 5 use style B.
- `flat-bedrock`: 1 of 3 mixins (`FlatBedrockMixin`) carries a prefix, 2 don't.
- `banner-tweaks`: no `$`-style mixins, but naming still splits between "carries a mod-identifying
  word" (`BannerBlockEntityRenderStateMixin`) and "purely target-named" (`LoomMenuMixin`, `MapStateMixin`).

### 1.2 Platform-divergent mixins (one logical mixin, two loader-specific bodies) — 3 approaches

- **Same class name, sibling `neoforge.mixin` package** (the NeoForge patched-lambda gotcha from
  `docs/NEOFORGE.md`): `camel-nostrils`'s `CN$ServerPlayerMixin` and `houdini-block`'s
  `HoudiniWorldMixin` both do this correctly. camel-nostrils' pair has excellent explanatory javadoc on
  both sides (cites the lambda-renumbering rationale, cross-references docs/NEOFORGE.md, cites the
  `javap` verification); houdini-block's identical-shape pair has **zero** comment explaining the
  duplication is intentional — worth backfilling using camel-nostrils' comment as the template.
- **Platform suffix baked into the class name itself, kept in `common`**: `log-all-the-things`'s
  `LATT$FireBlockMixin` + `LATT$FireBlockFabricMixin` + `LATT$FireBlockNeoForgeMixin` (and similarly
  for `BucketItemMixin`).
- `stack-it-up`'s two `MixinItemStackDamage` classes (one per platform folder, identical name, no
  platform suffix) weren't diffed line-by-line during this pass — worth confirming they're actually
  divergent-for-a-reason rather than simple duplication.

### 1.3 Injected-method / `@Unique` naming — 3 styles, and one file mixes two of them

1. `<lowercase-acronym>$methodName` (camel-nostrils, houdini-block, archaeology-tweaks partially).
2. No prefix, verb-first camelCase describing intent (stack-it-up's `injectGetMaxCount`).
3. No prefix, identical name to the shadowed vanilla method (archaeology-tweaks' `useWithoutItem`,
   in the *same file* as style-1 `@Unique` helpers `at$canHideItems`/`at$getHiddenState`).

No repo-wide policy is documented anywhere for this — it reads as organic per-author drift.

### 1.4 Mixin config JSON naming/placement — 4 combinations for "this mod's platform-specific mixins"

Base config naming (`<mod_id>.mixins.json` in `common/src/main/resources/`, referenced identically from
`fabric.mod.json` and `neoforge.mods.toml`) is uniform across every mod — no issue there. Where a mod
needs a *platform-specific* extra config, four different placements are in simultaneous use:

- Physically split into that platform's own module, hyphen-separated: `banner-tweaks`
  (`bannertweaks-fabric.mixins.json` in `fabric/`, `bannertweaks-neoforge.mixins.json` in `neoforge/`).
- All configs physically in `common/`, dot-separated: `camel-nostrils`
  (`camelnostrils.fabric.mixins.json` sitting in `common/`), `log-all-the-things` (three files, all in
  `common/`: base + `.fabric.` + `.neoforge.`).
- Platform config in that platform's module but under `META-INF/`: `stack-it-up`'s
  `META-INF/stackitup.neoforge.mixins.json`, while its Fabric counterpart sits at the resource root —
  i.e. stack-it-up's own two platform configs aren't even placed consistently with *each other*.

### 1.5 Documentation quality on mixins — wide variance, unrelated to naming style

Best-practice examples (multi-paragraph javadoc explaining *why* the mixin exists and what vanilla
assumption it overrides, with citations): `camel-nostrils`'s `CN$ServerPlayerMixin` pair, `stack-it-up`'s
`MixinItem`. The large majority of mixins across most mods (all 19 spot-checked in effective-gear,
archaeology-tweaks' `AbstractBlockMixin`, houdini-block's NeoForge/common pair) have zero explanation.

### 1.6 Other mixin notes

- `houdini-block` pulls its actual decision logic out into a plain `HoudiniWorldMixinLogic` class,
  leaving the mixin itself a thin `@Inject` shim — this makes the logic unit-testable without a
  Mixin-aware harness, and no other mod with non-trivial mixin bodies does this (e.g.
  archaeology-tweaks' `AbstractBlockMixin` inlines everything as `@Unique` methods on the mixin class
  itself). Worth considering as the preferred shape going forward.
- MixinExtras (`@Local`) is used only where actually needed (camel-nostrils, houdini-block) — this
  looks like healthy, not-over-applied adoption rather than an inconsistency to fix.

---

## 2. Data generation

### 2.1 Which mods have datagen, and on which platforms

Only 9 of 31 mods have any datagen at all (verified `gradle.properties` flags against real
entrypoints — every flag matches real wiring, no phantom flags): archaeology-tweaks, artificial-heart,
but-what-about, effective-gear, hopper-xtreme, jdcrafte, minekea, sponj, villager-tweaks. `blacklight`,
`cobblicious`, `pannotia-companion`, and `playgrounds` correctly declare neither flag — they're
content-free scaffolds with nothing to generate. Of those, only
**3** (archaeology-tweaks, artificial-heart, effective-gear) datagen on **both** platforms — the other
6 are Fabric-only by design (flag correctly omitted for NeoForge), meaning those 6 mods' NeoForge builds
ship hand-authored or copy-forwarded data files rather than platform-generated ones. Worth confirming
that's the intended long-term state, especially for minekea given its content volume.

### 2.2 Entry-point naming — fully consistent

Every mod's Fabric and NeoForge datagen entrypoint is literally named `ModDataGenerator`, always under a
`<platform>/.../data/` package, `implements DataGeneratorEntrypoint` (Fabric) /
`@EventBusSubscriber` + `@SubscribeEvent onGatherData` (NeoForge). No deviation found. `jdcrafte` is the
one outlier in *file layout* (not naming): its per-family datagen classes live directly next to the
block class rather than under a `data/`/`block/**DataGenerator` subpackage the way minekea nests its ~90.

### 2.3 Provider organization — 3 distinct patterns

- **A — one class per category, no internal decomposition** (mods with little/no new content):
  archaeology-tweaks, artificial-heart (worldgen/trade dynamic-registry providers only).
- **B — one flat generator class per category, hand-written per block inline**: hopper-xtreme
  (`XtremeHopperRecipeGenerator` — 177 lines in one file; `XtremeHopperLootTableGenerator` — 97 lines
  in one file, both broken into private helper methods but never delegated to a reusable abstraction).
- **C — per-block-family "aggregator" classes implementing a shared interface, iterated from a flat
  list**: minekea (~90 files, the reference/heaviest user), jdcrafte (5 families), but-what-about (uses
  chimeric-lib's `BlockFamilyDataGenerators.of(family)` to build the list programmatically rather than
  listing classes by hand — the cleanest version of this pattern).

Pattern C is clearly the intended shared convention (chimeric-lib provides the interfaces for it), but
**minekea doesn't actually consume the real chimeric-lib interfaces** — see §2.7, the top datagen
consolidation finding.

### 2.4 Loot table generation — two mechanisms, cleanly separated by use case (not actually inconsistent)

- **Datagen-time loot tables** for brand-new blocks (`FabricBlockLootSubProvider` subclasses):
  hopper-xtreme, and the per-family aggregator's `configureBlockLootTables` in
  jdcrafte/but-what-about/minekea.
- **Runtime loot table *modification*** for adding drops to an *existing vanilla* table: chimeric-lib's
  shared `com.chimericdream.lib.loot.LootTableModifier` (with its own GameTest coverage), consistently
  reused by artificial-heart, athenaeum, miniblock-merchants, shulker-stuff — **no reimplementation
  found anywhere**, this one is already fully consolidated.

No mod mixes both mechanisms for the same block family, so despite looking superficially like two
"ways to do loot tables," this split is intentional and doesn't need standardizing — just documenting
somewhere so it stops being tribal knowledge.

### 2.5 Tag generation — mostly consistent, one real helper already paying off

Block/item tag providers are uniformly named `<Mod>BlockTagGenerator`/`<Mod>ItemTagGenerator`. The
per-family mods all delegate to chimeric-lib's `com.chimericdream.lib.fabric.blocks.TagUtils
.applyMineableTag(...)` helper — whose own Javadoc documents the exact repeated idiom it replaced,
i.e. this exact kind of consolidation has already happened successfully once. `hopper-xtreme` has no
tag provider at all (smaller scope, not obviously a gap).

### 2.6 Recipe generation

- **Item-component-binding workaround** (the MC 26.2 "Components not bound yet" fix documented in
  CLAUDE.md): present verbatim in minekea, jdcrafte, but-what-about's `buildRecipes()`. **Missing** in
  `hopper-xtreme` — see bugs file.
- **Shaped/shapeless helpers**: minekea/jdcrafte/but-what-about call stock `RecipeProvider` builder
  methods directly per block family. `hopper-xtreme` instead wrote its own mod-local helper family
  (`makeShapedRecipe`, `makeSmithingRecipe`, 3 overloads of `makeShapelessUpgradeRecipe`,
  `makeShapelessConversionRecipe`, `makeBiDirectionalConversionRecipe`) to express "tiered upgrade" and
  "N-to-1/1-to-N conversion" recipe shapes — generically useful to any mod with tiered materials
  (minekea's compressed-block family is a plausible second consumer), currently exists in exactly one
  mod.
- **Trim/smithing datagen**: `effective-gear` is confirmed the sole, clean, end-to-end consumer of
  chimeric-lib's `com.chimericdream.lib.trims` package (both dynamic-registry bootstrap and armor-trim
  atlas overrides, identical on both platforms) — **no duplication found here**, a genuinely
  well-consolidated area already.

### 2.7 chimeric-lib datagen interfaces vs. mod-local reimplementation — top datagen finding

chimeric-lib provides real shared interfaces (`com.chimericdream.lib.blocks.BlockDataGenerator`,
`com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator`, `com.chimericdream.lib.items.ItemDataGenerator`,
plus `BlockFamilyDataGenerators` to build a family's generator list automatically). `but-what-about` and
`jdcrafte` consume these directly and cleanly.

**`minekea` does not** — it independently declares its own near-duplicate abstract classes
(`minekea/fabric/.../data/ChimericLibBlockDataGenerator.java`, same 8 method signatures as the real
interface, just re-declared with empty bodies), plus a
`ChimericLibBlockDataGeneratorAdapter` whose entire purpose (per its own Javadoc) is bridging the two
parallel systems, plus a second, item-level copy (`ChimericLibItemDataGenerator`) whose
`configureRecipes` signature has **already diverged** from the real `ItemDataGenerator.configureRecipes`
(3 params vs. 1) — meaning even swapping the import wouldn't be a no-op today. Moving minekea onto the
real interfaces (and generalizing chimeric-lib with the "multiple families grouped together" concept
minekea's own `BlockDataGeneratorGroup`/`ItemDataGeneratorGroup` currently reimplements, since that's a
legitimate gap in chimeric-lib's current API, not pure duplication) would let 3+ minekea-local files be
deleted outright.

### 2.8 Model/blockstate generation — consistent within each mod, inconsistent across the repo as "a fact you have to know"

Two clean approaches, never mixed within one mod: fully datagen-generated (jdcrafte, but-what-about,
minekea) vs. fully hand-written despite having datagen for other things (archaeology-tweaks,
artificial-heart, hopper-xtreme — all three confirmed to have empty `generated/assets/**/models`
directories). Not a bug, but means "does this mod's models come from datagen" isn't something you can
infer from "this mod has datagen" — it's per-mod trivia.

### 2.9 Dynamic-registry datagen (Fabric `FabricDynamicRegistryProvider` requirement)

Checked every mod with `RegistrySetBuilder` content on Fabric (archaeology-tweaks, artificial-heart,
effective-gear) against its `FabricDynamicRegistryProvider` — **every registry key is correctly paired,
no incomplete pairing found**. Naming of the provider class itself isn't uniform though:
`*WorldgenProvider` (archaeology-tweaks, artificial-heart) vs. `*TrimProvider` (effective-gear) vs.
`*VillagerTradeProvider` (archaeology-tweaks) — three suffix conventions for "one
`FabricDynamicRegistryProvider` per registry domain," cosmetic but worth picking one if standardizing.

---

## 3. Shared gameplay logic

chimeric-lib inventory relevant to this section: `commands/ChimericCommand(s)` (cross-loader command
registration, well-adopted), `util/ChimericLibParticleUtils.spawnParticleAbove` (barely adopted —
1 internal caller only), `blocks/BlockUtils` (adjacency helpers, no radius/AABB helper),
`inventories/ContainerOpenersCounters` (well-adopted, 2 consumers), `util/ModConfigurable` (per-block
config wiring, not a YACL/mod-wide config abstraction — there is currently **no** YACL helper in
chimeric-lib at all), and no networking wrapper despite Architectury's `NetworkManager` being an
available dependency. chimeric-lib's own `POTENTIAL_FEATURES.md` already names a "config sync layer"
and a "networking wrapper" as unbuilt backlog items — i.e. items 3.1 and 3.2 below are *known*
gaps, just not yet built or quantified.

### 3.1 YACL config management — see executive summary #1 for the headline; detail:

9 mods (villager-tweaks, beacon-conduit-tweaks, banner-tweaks, flat-bedrock, shulker-stuff,
miniblock-merchants, athenaeum, better-portal-linking, sneaky-tweaks) each hand-write the identical
`ConfigClassHandler.createBuilder(...).id(...).serializer(...).build()` + `load()` + `configScreen(...)`
skeleton. Even the differences are minor and interchangeable (which YACL controller-builder type per
option — `TickBoxControllerBuilder`, `IntegerFieldControllerBuilder`, etc.). `miniblock-merchants`
repeats the exact same `.option(Option.<Integer>createBuilder()...)` block **26 times** for 26 nearly
identical "chance" options. Three mods (`BannerTweaksConfig`, `ShulkerStuffConfig`,
`MiniblockMerchantsConfig`) skip the `load()` wrapper method the other six expose, forcing their main
class to reach `XConfig.HANDLER.load()` directly — harmless today, but a sign the class "contract"
nobody agreed on. `AthenaeumConfig` alone does post-load validation/clamping that a shared base could
make a discoverable, opt-in hook instead of a bespoke addition.

The Fabric `ModMenuIntegration` (9 near-identical one-line `return XConfig::configScreen;` wrappers)
and NeoForge `IConfigScreenFactory` registration (8 near-identical `() -> (client, parent) ->
XConfig.configScreen(parent)` calls — see bugs file for the missing 9th) are the same story: ~17 files
that a `ChimericLibModMenu`/`ChimericLibConfigScreens` helper could collapse to one-line calls.

### 3.2 Networking / packets — see executive summary #2; detail:

- `stack-it-up` uses Architectury's `NetworkManager` from **common** code only — one call registers
  payload + receiver on both loaders, no platform-specific files at all.
- `minekea` and `effective-gear` each hand-write a `CustomPacketPayload` record plus **two full
  platform-specific registration classes** (Fabric `PayloadTypeRegistry`/`ServerPlayNetworking`;
  NeoForge `@EventBusSubscriber`/`RegisterPayloadHandlersEvent`).
- `banner-tweaks` does a third variant for a different purpose (server→client config sync on player
  join): a clientbound payload registered the "minekea way," sent from a **`@Mixin(PlayerList.class)`
  `placeNewPlayer` injection duplicated verbatim between Fabric and NeoForge**, differing only in the
  actual send call. `stack-it-up` solves the identical "sync config on join" problem with a single
  Architectury `PlayerEvent.PLAYER_JOIN` call in common code — no mixin, no per-loader duplication.

A chimeric-lib wrapper over `NetworkManager` (already scoped in `POTENTIAL_FEATURES.md`) would let
banner-tweaks/minekea/effective-gear delete 6 platform-specific files and 2 duplicated mixins between
them.

### 3.3 Command registration — the one pattern that's already working well

Only `stack-it-up` registers a real command outside chimeric-lib itself
(`StackItUpMod.java`: `ChimericCommands.register(new StackSizeCommand())`), and it does so through the
shared `ChimericCommand`/`ChimericCommands` registry exactly as intended. No mod reimplements
`CommandRegistrationEvent` wiring by hand. Good precedent for what the config/networking consolidations
above should look like once built — though the sample size (one external consumer) is small.

### 3.4 Entity/block scanning within a radius — 4 idioms, no shared helper

1. Inflate an entity's own bounding box (`villager-tweaks/.../VTVillagerEntityMixin.java:136`,
   `artificial-heart/.../PaleCarvedPumpkinBlock.java:128`).
2. Hand-built `new AABB(minX,minY,minZ,maxX,maxY,maxZ)`
   (`camel-nostrils/.../UpsideDownChestBlock.java:101`, `hopper-xtreme/.../AbstractXtremeHopperBlockEntity.java:602`).
3. `new AABB(BlockPos)` — **identical line duplicated verbatim in two minekea files**
   (`StoolBlock.java:104`, `ChairBlock.java:130`: `world.getEntitiesOfClass(SimpleSeatEntity.class, new AABB(pos), (Object) -> true)`).
   Since `SimpleSeatEntity` itself lives in chimeric-lib, a static `isOccupiedAt`/`getSeatAt` helper
   there would delete both call sites for free — the single lowest-risk win in this category.
4. Vanilla-provided AABB, just moved (`hopper-xtreme/.../AbstractXtremeHopperBlockEntity.java:565`,
   `minekea/.../ArmoireBlockEntity.java:69`).

None of these are wrong individually, but it's 4 idioms across 6 mods with zero shared helper — a gap
next to chimeric-lib's existing (adjacency-only) `BlockUtils`.

### 3.5 Raycasting — smaller finding than expected; no cross-mod inconsistency

Most mods needing "what is the player looking at" correctly lean on vanilla's own `BlockHitResult`/
`UseOnContext` plumbing rather than hand-rolling anything (houdini-block, better-portal-linking,
hang-from-slabs, sneaky-tweaks). The one mod that does manual raycasting, `log-all-the-things`, does
the *identical* `level.clip(new ClipContext(...))` call **three separate times within itself**
(`WindowLogHelper.java:180`, `SnowLogHelper.java:200`, `CarpetLogHelper.java:137`) — within-mod
duplication, not a cross-mod pattern, but a clean extraction candidate either locally or as a
`RayCastUtils.clipOutline(...)` in chimeric-lib if another mod ever needs the same shape.

### 3.6 Sound/particle helpers

chimeric-lib's `ChimericLibParticleUtils.spawnParticleAbove` has exactly one caller — itself
(`FallingUpwardBlock.java`). Meanwhile `minekea/.../DyedBlock.java:86-88` and
`.../DyedPillarBlock.java:90-92` independently implement a near-identical "spawn 5 splash particles
above a wet block" loop, byte-for-byte the same. A small generalization
(`spawnParticleBurstAbove(level, pos, random, particle, count)`) would unify the existing unused helper
and both duplicate call sites. `playSound` boilerplate recurs dozens of times across mods but each call
site is a one-line direct vanilla call with different arguments — no repeated *logic* to extract there.

### 3.7 Block-entity ticking — consistently good, no action needed

Every ticking block entity in the repo (hopper-xtreme's hoppers, minekea's crates/armoires,
shulker-stuff's dye station) uses vanilla's `createTickerHelper` idiom identically. `CrateBlockEntity`
and `DyeStationBlockEntity` additionally share the same `ContainerOpenersCounters.recheckOpeners`
idiom from chimeric-lib — a working example of the shared-abstraction adoption the config/networking
items above should eventually match. One minor, likely-harmless style split: `XtremeHopperBlock`/
`ArmoireBlock` guard `getTicker` with a client-side check that `CrateBlock`/`DyeStationBlock` skip
(see bugs file for the caveat).

### 3.8 Platform-check helpers (`Platform.isFabric()`/`isForge()`)

Minimal direct usage outside chimeric-lib's own `ModRegistryHelper`; the two external call sites found
(`stack-it-up/.../ConfigManager.java`, `hopper-xtreme/.../ModItems.java`) are both plain, uniform
Architectury calls. No inconsistency found — small sample, but nothing to fix.

---

## 4. Rendering

### 4.1 BlockEntityRenderer registration — one outlier on timing

The dominant, correct pattern: a shared `common` `register*Renderers()` method, called from NeoForge's
`EntityRenderersEvent.RegisterRenderers` and Fabric's `ClientModInitializer.onInitializeClient`
(log-all-the-things, camel-nostrils, minekea, jdcrafte). `archaeology-tweaks` is the one mod that calls
its (misleadingly-named) `registerEntityRenderers()` — which only registers a *block-entity* renderer —
from NeoForge's `FMLClientSetupEvent` instead. Not confirmed broken (block-entity renderer dispatch
isn't as registration-order-sensitive as the entity-renderer static map `docs/NEOFORGE.md` warns about,
and `effective-gear`'s `ColorHandlerRegistry` call also works fine from `FMLClientSetupEvent`), but it's
an unexplained, sole deviation from what every other mod does for this exact call — see bugs file.

**Entity** renderer registration (a genuinely timing-sensitive case per `docs/NEOFORGE.md`) is done
correctly everywhere it occurs: both `camel-nostrils` and `minekea` put `EntityRendererRegistry.register`
in the NeoForge mod class's own constructor (not a lifecycle event), each with a comment citing the
exact reason — this is the reference implementation, no violations found.

### 4.2 Overlay/decal geometry — 4 distinct techniques for related-but-different problems

- **`submitMovingBlock`**, for drawing an existing full block state somewhere vanilla wouldn't
  otherwise render it (log-all-the-things's three overlay types, jdcrafte's weathervane rotation,
  chimeric-lib's `FallingUpwardBlockEntity`).
- **Hand-cut `submitCustomGeometry` + `QuadEmitter`**, for overlays with no reusable existing block
  model (log-all-the-things only, three renderer classes) — `QuadEmitter.SURFACE_NUDGE` is baked
  unconditionally into `emitFace` itself, so no caller can forget the z-fighting fix. Zero mod-specific
  logic in `QuadEmitter` — a clean chimeric-lib candidate ready today, just has exactly one consumer mod
  so far.
- **Whole-block `poseStack` nudge**, the fallback case of the above when no hand-cut model exists for a
  given host shape — same underlying constant, different application (transform vs. per-vertex).
  Already documented in CLAUDE.md as the two forms one policy takes; not an inconsistency.
- **Mixin-based extension of a vanilla BER** (no overlay at all, just inject extra content into an
  existing renderer's `extractRenderState`/`submit`): `banner-tweaks`'s `BannerBlockEntityRendererMixin`
  and `beacon-conduit-tweaks`'s `BCTweaksBeaconRendererMixin`, both stashing extra render-state fields
  via an accessor-mixin interface. A structurally separate strategy that would need a different kind of
  chimeric-lib helper (e.g. a "stash + late-render" utility) than the `QuadEmitter`/
  `RealNeighborMovingBlockRenderState` helpers serve the other two techniques.

### 4.3 Ambient occlusion (`RealNeighborMovingBlockRenderState`) — see executive summary #5

Fixed correctly everywhere it's needed, but the fix class itself is duplicated: an independently
maintained, near-identical copy exists in both `log-all-the-things` and `jdcrafte`, plus a
`createMovingBlock(...)` factory helper duplicated 3x inside log-all-the-things's own three renderer
classes. Zero mod-specific logic anywhere in either — clean chimeric-lib extraction
(`com.chimericdream.lib.client.RealNeighborMovingBlockRenderState` + a
`MovingBlockRenderStates.create(...)` factory). No *unfixed* instance of the underlying AO bug was
found anywhere in the repo.

### 4.4 Item rendering — 3 registration strategies (2 are legitimately platform-forced, not inconsistent)

- **Common `SpecialModelRenderers.ID_MAPPER`**, for a mod's own block/item id (minekea's glass jar,
  camel-nostrils' upside-down chest) — single, platform-agnostic registration call.
- **Platform-specific registration**, only needed when overriding a *vanilla* item id (shulker-stuff's
  dye-tinted shulker box): NeoForge has a native `RegisterSpecialModelRendererEvent` hook; Fabric has no
  equivalent, so it goes through a model-loading plugin instead. This 3-way split (shared logic class +
  2 structurally different registration mechanisms) is legitimate, not a standardization target — the
  platforms genuinely expose no common hook here.
- **Dead/vestigial code**: minekea's Fabric and NeoForge `GlassJarItemRenderer.java` files are entirely
  commented out, referencing pre-Mojang-mappings Yarn types that don't compile — real rendering now
  goes through the common `SpecialModelRenderer` path. Several more dead commented blocks exist in
  `MinekeaFabricClient.java`/`MinekeaNeoForgeClient.java`. All should simply be deleted.

### 4.5 Entity rendering — small surface, no inconsistency; two working chimeric-lib examples already

Only camel-nostrils and chimeric-lib have custom entities with rendering. camel-nostrils' fish-variant
renderers use the minimal-diff pattern (extend vanilla's renderer, override only the texture lookup).
chimeric-lib's `FallingUpwardBlockEntity` (from-scratch renderer) and `SimpleSeatEntity.EmptyRenderer`
are both already shared, working examples of "reusable render logic lives in chimeric-lib" — consumed
directly by camel-nostrils and minekea respectively. Registration timing is correct in both places that
need it (see §4.1).

### 4.6 Block/item color providers — one real consumer, one false-positive to avoid re-finding

`effective-gear`'s `PreservingBlockColors` is the only genuine `BlockTintSource` registration in the
repo, registered via Architectury's `ColorHandlerRegistry` from `FMLClientSetupEvent`. minekea's
`ColoredBlocksRegistry`/`BlockColor` is a same-named but functionally unrelated data enum (dye-group
lookup table for a tool), not a real tint provider — noting this explicitly so a future grep-based pass
doesn't mistake it for a second color-provider mod.

### 4.7 GUI/Screen rendering — a working chimeric-lib base, and 3 mods that don't use it

chimeric-lib ships `SimpleInventoryScreen` and `DoubleWideInventoryScreen`, both consumed cleanly by
minekea's crate screens. But three *other* screens (hopper-xtreme's `AbstractHopperItemFilterScreen`,
shulker-stuff's `DyeStationScreen`, minekea's own `BlockPainterScreen`) each reimplement the same
6-line `extractBackground` centering-and-blit boilerplate locally instead — likely because
`SimpleInventoryScreen` is hard-coded to vanilla's `generic_54.png` texture rather than accepting an
arbitrary texture identifier the way hopper-xtreme's own base class does. Generalizing
`SimpleInventoryScreen` to take a texture identifier would let all three converge on it.

### 4.8 Client-only registration pattern — consistent structure, one necessary asymmetry

Both platforms' client entrypoints are consistently thin wrappers delegating into a shared `common`
client class. The one structural asymmetry — NeoForge entity-renderer registration needing to live in
the `@Mod` class's constructor rather than the `@EventBusSubscriber` client class — is handled correctly
by both mods that need it (camel-nostrils, minekea both correctly split their NeoForge client code
across two classes for exactly this reason). Not an inconsistency, a necessary and correctly-applied
distinction.

---

## 5. Testing

### 5.1 JUnit — adoption has quietly grown; the docs haven't kept up

`docs/TESTING.md`/`CLAUDE.md` say chimeric-lib is the only mod with unit tests. Two more mods now have
`fabric/src/test` JUnit suites — `better-portal-linking` (3 classes) and `stack-it-up` (1 class) — and
both are internally consistent with chimeric-lib's own convention: registry-touching tests extend the
shared `BootstrapMinecraft` fixture, and stack-it-up's pure-JSON test correctly omits it (with a comment
saying why). This is a documentation-drift finding, not a code-quality one — see bugs file.

### 5.2 GameTest — fully correct registration, consistent naming; the interesting finding is fixture adoption

Every `@GameTest` class in every mod that has one (chimeric-lib, minekea, log-all-the-things,
houdini-block, shulker-stuff, sponj, villager-tweaks, plus hopper-xtreme's main-src exception) has a
matching `fabric.mod.json` entrypoint entry — **zero silently-orphaned tests found**, which was the
single highest-priority thing this audit checked given the documented past failure mode. Class naming
is uniform (`<Feature>GameTest`, except hopper-xtreme's main-src `<Feature>Test`), package structure is
uniform (`com.chimericdream.<mod>.fabric.test`, except chimeric-lib's own GameTest package
(`com.chimericdream.lib.fabric.test`) disagreeing with its own JUnit package
(`com.chimericdream.chimericlib.test`) — see bugs file), and structure conventions (default empty-air
`structure()`, occasional `maxTicks` override, no `.snbt`/`batch`/`required` usage anywhere) are
consistent across the board.

Where it isn't consistent: **only 2 of 8 GameTest mods** (chimeric-lib itself, and log-all-the-things)
wire up `gametestImplementation(testFixtures(project(":chimeric-lib:common")))` and actually import the
shared `GameTestContainers`/`GameTestPlayers`/`GameTestMenus` helpers. The other 5 hand-roll their own:
minekea (`placeJar`/`redstone` helpers + inline slot-assertion checks that duplicate
`GameTestContainers.assertSlot`), houdini-block (`mockPlayer` wrapper duplicating
`GameTestPlayers.makeFacingPlayer`'s underlying call), shulker-stuff (a private `Station` record/factory
for player+block-entity setup), villager-tweaks (a hand-rolled config-save/restore `withConfig` helper),
and hopper-xtreme (5 separate hand-rolled patterns, already self-documented in its own `TEST_PLAN.md`
under "ChimericLib helper opportunities"). **chimeric-lib's own `POTENTIAL_FEATURES.md` "Developer &
testing tools" backlog already names every one of these gaps by consumer mod, unprompted** — a
"Config override fixture" tagged for villager-tweaks, "Mock-player interaction wrappers" (partially
done) with an open `useOn` gap matching houdini-block's need, a "Menu/screen-handler test harness"
tagged for shulker-stuff by name, and container fill/assert helpers tagged for
hopper-xtreme/minekea/shulker-stuff/jdcrafte. None of these backlog items have been built yet.

### 5.3 Assertion style — split roughly in half, one mod mixes both

Style A (`context.assertTrue(cond, msg)` + `succeedWhen` for multi-tick waits): chimeric-lib, minekea.
Style B (manual `if (!cond) { context.fail(...); }`): shulker-stuff, sponj, villager-tweaks,
houdini-block, log-all-the-things (its whole 3-class suite), and mostly hopper-xtreme.
**hopper-xtreme is the only mod that mixes both styles internally** (some classes use `fail()` with a
`Component`, one uses plain-string `assertTrue`). Neither style is wrong; consolidating on one — Style
A reads shorter and pairs naturally with a future `assertContainerExactly`-style shared helper — would
remove one more axis of divergence.

### 5.4 Coverage vs. plans (`TEST_PLAN.md`) — drift runs in both directions

20 of 31 in-scope mods have a `TEST_PLAN.md`; of those, 7 describe coverage that was never actually
written (pure aspiration: athenaeum, banner-tweaks, beacon-conduit-tweaks, enchantment-numbers-fix,
flat-bedrock, miniblock-merchants, archaeology-tweaks, but-what-about, artificial-heart — 9 mods, not 7,
correcting the raw count), while `jdcrafte`'s plan is explicitly self-aware about being scaffold-only
(though its claim that the mod "is disabled in settings.gradle" is itself now false — see bugs file).
The four newly re-enabled mods (`blacklight`, `cobblicious`, `pannotia-companion`, `playgrounds`) each
have a genuinely well-written, self-aware "scaffold only, nothing to test yet" `TEST_PLAN.md` that
correctly points forward to `hopper-xtreme`'s plan as the convention to follow and to chimeric-lib's
GameTest helpers — good future-facing docs, but all four repeat `jdcrafte`'s exact drift bug: each one's
`TEST_PLAN.md` still asserts "This project is disabled in `settings.gradle`," which commit `22537baaf`
made false for all four at once — see bugs file. Conversely, `better-portal-linking`, `stack-it-up`, and
`log-all-the-things` all have **real, implemented test code with no `TEST_PLAN.md` acknowledging it at
all** — more test infrastructure than documented, the inverse gap. `hopper-xtreme`'s plan is the most
detailed and self-aware in the repo, explicitly naming its own gaps and helper-duplication.

---

## 6. Registration & mod-lifecycle

### 6.1 Registry object declaration — the one thing that's already fully consistent (worth protecting)

Every active mod with actual content to register funnels block/item/entity/etc. registration through
chimeric-lib's `com.chimericdream.lib.registries.ModRegistryHelper` — confirmed for all 27 mods from the
original audit. This is the single most consistently-adopted shared pattern found across the entire
audit; worth explicitly holding up as the model the config/networking/datagen consolidations above
should aim to replicate, not just one more thing to check off. The four newly re-enabled mods
(`blacklight`, `cobblicious`, `pannotia-companion`, `playgrounds`) have no blocks/items/entities at all
yet, so there's nothing to register and no `ModRegistryHelper` usage to confirm one way or the other —
worth re-checking this claim once any of the four gains real content.

### 6.2 Registry holder class naming/organization — 3 patterns

- **Single `ModBlocks`/`ModItems`/`ModEntities` class** — the majority (archaeology-tweaks,
  artificial-heart, better-target-dummies, camel-nostrils, hopper-xtreme, houdini-block, jdcrafte,
  shulker-stuff, sponj, next-update-now, villager-tweaks, miniblock-merchants).
- **Acronym-prefixed holder class breaking the `Mod*` convention**: `effective-gear`'s `EGBlocks`
  instead of `ModBlocks`.
- **Many small per-category classes instead of one central class**: `minekea` (~20+ classes,
  `BuildingBlocks`/`ContainerBlocks`/`Armoires`/`Doors`/etc.) and `log-all-the-things`
  (`CarpetLogBlocks`/`SnowLogBlocks`/`WindowLogBlocks`) — mirrors each mod's own datagen-class
  fragmentation, i.e. internally consistent per mod, just different from everyone else's approach.

### 6.3 Common-init structure & Fabric/NeoForge entrypoint wiring — consistent, no deviation found

Every sampled mod's shared `<Mod>Mod` class is `public final class ... { public static void init() }`,
called once from a Fabric `ModInitializer` and once from a NeoForge `@Mod`-annotated class's
**constructor** (never from `FMLCommonSetupEvent`) — good, matches the "register early" discipline the
NeoForge lambda/renderer-timing gotchas depend on. The four newly re-enabled scaffold mods
(`BlacklightMod`/`CobbliciousMod`/`PannotiaCompanionMod`/`PlaygroundsMod`, each an empty `init(){}` body
today) already follow this exact shape correctly on both platforms — a good sign the `init-mod.sh`
template itself encodes the right convention, not just that existing mods happen to match it.

### 6.4 Config-screen registration — confirmed asymmetry, see bugs file

9 mods have a Fabric `ModMenuIntegration`; only 8 have the NeoForge counterpart. `better-portal-linking`
is the one missing it, despite having a real config and a working Fabric integration — see bugs file
for the concrete fix.

### 6.5 `gradle.properties` naming — 2 mods with opposite-direction `mod_id`/`maven_group` mismatches

The dominant convention is `mod_id` = full mod name with hyphens stripped, `maven_group` =
`com.chimericdream.<same stem>` (clean in effective-gear, minekea, stack-it-up, better-target-dummies,
and all four newly re-enabled scaffolds — `blacklight`, `cobblicious`, `pannotia-companion`/
`pannotiacompanion`, `playgrounds`).
`archaeology-tweaks` breaks this with a short `mod_id` (`archtweaks`) but a full-length `maven_group`
(`com.chimericdream.archaeologytweaks`); `beacon-conduit-tweaks` breaks it the *opposite* way — full
`mod_id` (`beaconconduittweaks`) but abbreviated `maven_group` (`com.chimericdream.bctweaks`).
`chimeric-lib`'s divergence (`maven_group = com.chimericdream.lib`) looks deliberate, since every class
already lives under `com.chimericdream.lib.*`. Both mixin-config filenames and the mixin-config-JSON
placement audit (§1.4) inherit whichever choice was made here, so a future naming-convention pass should
treat `mod_id`/`maven_group`/mixin-config-filename as one linked decision, not three separate ones.

---

*Companion document*: `docs/CROSS-MOD-PATTERN-AUDIT-BUGS.md` collects every concrete bug, discrepancy,
and doc-drift finding surfaced while producing this catalog, separated out since those are
independently actionable regardless of any standardization decisions made from the list above.

# Cross-mod pattern audit — bugs & discrepancies (2026-09-08)

Companion to `docs/CROSS-MOD-PATTERN-AUDIT.md`. Everything here was found incidentally while cataloging
style/pattern differences across the monorepo (see that doc for scope/method) — these are concrete,
independently-actionable issues rather than style preferences. Grouped by how confident the finding is,
most-actionable first. **Not committed** — working document, delete or commit deliberately once triaged.

---

## A. Confirmed real bugs (behavior is wrong today)

### A1. `BCTweaksConfig`: two config options can never persist (`@SerialEntry` + `transient` together)
`beacon-conduit-tweaks/common/src/main/java/com/chimericdream/bctweaks/config/BCTweaksConfig.java:28-30`
— `beaconRangePerBlock` and `conduitRangePerBlock` are marked both `@SerialEntry` (meant to persist) and
`transient` (excluded from Gson serialization). Any in-game edit to these two YACL options is silently
discarded on the next config load/restart, even though the config screen presents them as editable.
Already flagged as an open, unresolved question in the mod's own `beacon-conduit-tweaks/TEST_PLAN.md:105-108`
("is per-block tuning meant to be user-facing yet, or code-only defaults? ... serialization test will
fail until this is resolved"). **Fix**: either drop `transient` and add a proper map-editing controller,
or drop these two from the config screen and keep them code-only constants — either is fine, but pick
one, since right now the UI lies about what's actually configurable.

### A2. `better-portal-linking` has no NeoForge config-screen registration
`better-portal-linking/neoforge/src/main/java/com/chimericdream/betterportallinking/neoforge/BetterPortalLinkingNeoForge.java` —
its constructor only calls `BetterPortalLinkingMod.init()`; there is no `IConfigScreenFactory`/
`ConfigScreenHandler` registration at all. The mod has a real config
(`common/.../config/BetterPortalLinkingConfig.java`) and a working Fabric `ModMenuIntegration`, so
**NeoForge players of this mod currently cannot reach its config screen in-game** — they'd have to
hand-edit the JSON5 file on disk. Every other one of the 9 mods with a YACL config (8 of 9) has this
NeoForge registration; this is the sole gap. **Fix**: add the registration to the constructor, following
e.g. `shulker-stuff`'s or `beacon-conduit-tweaks`'s NeoForge class as a template.
**Resolved 2026-09-17**: config-screen registration moved into chimeric-lib's `YaclConfig` helper
(both loaders), so the mod gets its NeoForge screen automatically; changelog entry added.

### A3. Broken `fabric.mod.json`/`neoforge.mods.toml` icon references in `blacklight` and `cobblicious`
`blacklight/fabric/src/main/resources/fabric.mod.json:15` declares `"icon": "assets/blacklight/icon.png"`
and `blacklight/neoforge/src/main/resources/META-INF/neoforge.mods.toml`'s `logoFile` points at the same
path, but the actual file on disk is `blacklight/common/src/main/resources/assets/blacklight/logo.png`
— `icon.png` doesn't exist. Identical mismatch in `cobblicious`
(`cobblicious/fabric/src/main/resources/fabric.mod.json:15` references `assets/cobblicious/icon.png`,
actual file is `cobblicious/common/src/main/resources/assets/cobblicious/logo.png`). Both mods will show
a missing/placeholder icon in any mod list (Fabric's mod menu, NeoForge's mod list screen) until fixed.
`pannotia-companion` and `playgrounds` don't have this bug — both correctly ship `icon.png` matching
their manifest references. **Fix**: rename each mod's `logo.png` to `icon.png` (or update the two
manifest references to `logo.png`), whichever the repo's `init-mod.sh` template is supposed to produce
— worth checking why two of four scaffolds generated from the same template diverged on this filename.

### A4. `blacklight`/`cobblicious`/`pannotia-companion`/`playgrounds` all declare `minecraft_compat = 1.21.10`, not `26.2`
Every other active mod's `gradle.properties` sets `minecraft_compat = 26.2`, matching root
`gradle.properties`' `minecraft_version`/`minecraft_compatibility` (see `archaeology-tweaks/gradle.properties:13`,
`effective-gear/gradle.properties` for reference values). All four newly re-enabled scaffolds instead
carry `minecraft_compat = 1.21.10` — a stale pre-port value baked into `.bun-create/mod/` template output
at some point before the Yarn→Mojang/MC 26.2 migration and never refreshed when these four were
scaffolded or re-enabled. This value is substituted directly into `fabric.mod.json`'s
`"minecraft": "~${minecraft_compat}"` and `neoforge.mods.toml`'s
`versionRange = "[${minecraft_compat},)"` dependency declarations — i.e. each of these four mods
currently *declares* itself compatible with Minecraft `1.21.10`, not the `26.2` it actually builds
against, which risks a loader rejecting/warning on the actual installed 26.2 once these mods ship real
content. **Fix**: update all four `gradle.properties` to `minecraft_compat = 26.2`, and check whether the
`init-mod.sh`/`.bun-create/mod/` template itself has the stale value (in which case every *future*
scaffolded mod would inherit the same bug).

### A5. `sponj` and `villager-tweaks` claim `has_fabric_datagen = true` but generate nothing
Both mods' Fabric `ModDataGenerator` bodies are byte-for-byte the same commented-out stub:
```java
public void onInitializeDataGenerator(FabricDataGenerator generator) {
    FabricDataGenerator.Pack pack = generator.createPack();
    // pack.addProvider(ModRecipeProvider::new);
    // pack.addProvider(ModBlockLootTables::new);
    // pack.addProvider(ModEnglishLangProvider::new);
}
```
(`sponj/fabric/src/main/java/com/chimericdream/sponj/fabric/data/ModDataGenerator.java`,
`villager-tweaks/fabric/src/main/java/com/chimericdream/villagertweaks/fabric/data/ModDataGenerator.java`).
Both mods' actual recipes/loot/tags/advancements are hand-authored JSON directly under
`common/src/main/resources/data/<modid>/...` (confirmed by 4-space indentation and hand-formatting
tells, e.g. `"count":  2` with a doubled space in `sponj/common/src/main/resources/data/sponj/recipe/sponj.json`).
Per `CLAUDE.md`'s own gradle.properties-flags rule, this is a live discrepancy: either the flag should
be removed (if datagen is genuinely not used) or the migration should be finished. **Decide one way or
the other** rather than leaving the flag misrepresenting reality.

---

## B. Live landmines (not crashing today, but violate an established, documented convention)

### B1. `hopper-xtreme`'s recipe datagen is missing the MC 26.2 component-binding workaround
`hopper-xtreme/fabric/src/main/java/com/chimericdream/hopperxtreme/fabric/block/XtremeHopperRecipeGenerator.java`'s
`buildRecipes()` does not call
`BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registryLookup).forEach(pending -> pending.apply())`,
unlike minekea/jdcrafte/but-what-about, which all apply it defensively per `CLAUDE.md`'s "Datagen
gotcha" section. Current recipes don't appear to read `Item.components()`, so this likely isn't crashing
today — but it's the established repo-wide convention specifically to guard against a *future* recipe
addition reintroducing the `NullPointerException: Components not bound yet` crash. **Fix**: add the
same one-line guard at the top of `buildRecipes()` for consistency and future-proofing.

### B2. `archaeology-tweaks` registers its block-entity renderer from the "wrong" NeoForge event
`archaeology-tweaks/neoforge/src/main/java/com/chimericdream/archaeologytweaks/neoforge/client/ArchaeologyTweaksNeoForgeClient.java:12-15`
subscribes to `FMLClientSetupEvent` for a `BlockEntityRendererRegistry.register(...)` call, via
`ArchaeologyTweaksClient.java:7-18`'s misleadingly-named `registerEntityRenderers()` (it only registers a
block-entity renderer, no actual entity renderer exists in this mod). Every other mod doing the
equivalent call (log-all-the-things, camel-nostrils, minekea, jdcrafte) uses
`EntityRenderersEvent.RegisterRenderers`, which `docs/NEOFORGE.md` explicitly confirms as the safe event
for this registry (unlike the *entity*-renderer registry, which does need the constructor). Not
confirmed broken — `effective-gear`'s `ColorHandlerRegistry` call also works fine from
`FMLClientSetupEvent` — but it's the sole, unexplained deviation from what every sibling mod does for
this exact call, and the method name doesn't match what it does. **Fix**: either rename the method to
`registerBlockEntityRenderers()` for honesty, or align it to `EntityRenderersEvent.RegisterRenderers`
like everyone else — mainly so a future developer copying this file as a template for a *real* entity
renderer doesn't put `EntityRendererRegistry.register` in the wrong place.

---

## C. Dead/vestigial code (safe to delete, no functional risk)

### C1. `minekea`'s `PointOfInterestTypesAccessor.java` — entirely commented out, wrong location
`minekea/common/src/main/java/PointOfInterestTypesAccessor.java` sits directly under `src/main/java`
with **no package declaration at all** (even the `package` line is commented out), importing
pre-Mojang-mappings Yarn types (`net.minecraft.block.BlockState`, `net.minecraft.registry.entry.RegistryEntry`,
etc.) that don't exist under this repo's current mappings. Leftover from an abandoned pre-port
experiment ("Shamelessly stolen from Reinforced Barrels"). **Delete it** — it compiles to nothing and is
the only file in the repo not living under `com.chimericdream.*`.

### C2. `artificial-heart`'s `ModDataGenerator` has ~140 lines of dead code copy-pasted from minekea
`artificial-heart/fabric/src/main/java/com/chimericdream/artificialheart/fabric/data/ModDataGenerator.java`
lines ~18-180 are commented out, referencing minekea-specific types that don't exist in this mod at all
(`MinekeaModelGenerator`, `MinekeaBlockLootTables`, `BlockDataGeneratorGroup`, `JarAccess`,
`TextureGenerator`, etc.). Clearly copied as a starting template and never cleaned up. **Delete it.**

### C3. minekea's item-rendering dead code (3 spots, pre-Mojang-mappings)
- `minekea/fabric/.../client/render/item/GlassJarItemRenderer.java` — entirely commented out,
  Yarn-mapped `BuiltinItemRendererRegistry.DynamicItemRenderer` code that doesn't compile if uncommented.
- `minekea/neoforge/.../client/render/item/GlassJarItemRenderer.java` — same situation
  (`BuiltinModelItemRenderer`/`IClientItemExtensions`).
- `minekea/fabric/.../client/MinekeaFabricClient.java:34-39,89-111` and
  `minekea/neoforge/.../client/MinekeaNeoForgeClient.java:41-47` — more dead references to the above,
  including a live `@SubscribeEvent registerClientExtensions(...)` method whose entire body is
  commented out.

Real glass-jar item rendering now happens through the `common` `SpecialModelRenderer` path
(`GlassJarItemRenderer` under `client/render/item/` in `common`) — all of the above is pure dead weight.
**Delete.**

### C4. Commented-out legacy `render(...)` methods left beside live MC 26.2 implementations
`archaeology-tweaks/.../ATBrushableBlockEntityRenderer.java:62-88` and
`minekea/.../DisplayCaseBlockEntityRenderer.java:170-224` each carry a full pre-port `render(...)`
method as a comment directly next to the live, correct `extractRenderState`/`submit` implementation.
Not incorrect, just clutter that makes the real logic harder to read. **Delete both.**

### C5. Dead commented-out `render`/`extractRenderState` overrides in chimeric-lib's screen base classes
`chimeric-lib/common/.../screen/SimpleInventoryScreen.java:27-32` and
`.../DoubleWideInventoryScreen.java:63-68` — minor cleanup, leftover from an older MC API shape.

---

## D. Documentation drift (the code is fine; the docs describing it are stale)

### D1. `jdcrafte`'s active/inactive status disagrees across three sources
`settings.gradle` (the documented source of truth) has `jdcrafte` **uncommented/active**. `CLAUDE.md`'s
"Inactive (5)" list names it as inactive. `jdcrafte/TEST_PLAN.md` itself asserts "This project is
disabled in `settings.gradle`." All three cannot be right. **Fix**: reconcile `CLAUDE.md` and
`jdcrafte/TEST_PLAN.md` with the actual, current `settings.gradle` state (or, if jdcrafte was
re-enabled deliberately and the docs just haven't caught up, that's the simplest explanation — either
way the docs need a pass).

### D1a. Same drift, now hitting four more mods at once: `blacklight`/`cobblicious`/`pannotia-companion`/`playgrounds`
Commit `22537baaf` ("chore: re-enable blacklight, cobblicious, pannotia-companion, playgrounds")
uncommented all four in `settings.gradle`, but every one of their `TEST_PLAN.md` files still opens with
"**Status: scaffold only — nothing to test yet.** This project is disabled in `settings.gradle`..."
(`blacklight/TEST_PLAN.md:3`, `cobblicious/TEST_PLAN.md:3`, `pannotia-companion/TEST_PLAN.md:3`,
`playgrounds/TEST_PLAN.md:3` — `playgrounds`' phrasing is "disabled ... and probably exempt"). This repo
already saw this exact failure mode with `jdcrafte` (D1) — a re-enabling commit that updates
`settings.gradle` but not the per-mod docs describing that state — so this is now a recurring pattern,
not a one-off: any future "re-enable mod X" commit should grep for "disabled in `settings.gradle`"
across the repo (not just in `X`'s own files) as part of the same change, since the phrase gets copied
into new scaffolds' `TEST_PLAN.md` by the `init-mod.sh` template while a mod is still commented out.
**Fix**: drop or rephrase the "disabled in settings.gradle" sentence in all four files (the rest of each
plan — "scaffold only, nothing to test yet" — is still accurate and worth keeping).

### D2. `docs/TESTING.md` / `CLAUDE.md` are stale on JUnit adoption
Both currently assert "chimeric-lib is currently the only mod with unit tests." `better-portal-linking`
(3 classes) and `stack-it-up` (1 class) both now have `fabric/src/test` JUnit suites, both correctly
following chimeric-lib's `BootstrapMinecraft` convention. Not a code problem — just needs the claim
(and chimeric-lib's own test count/package list, which is also slightly out of date re: its `blocks`/
`blocks.family` packages) updated in `docs/TESTING.md` line 25-26 and wherever `CLAUDE.md` repeats it.

---

## E. Maintenance hazards (correct today, but duplicated in a way that will drift)

These overlap with the consolidation opportunities in the main audit doc — listed here specifically
because the *risk* (silent divergence between copies) is itself worth flagging as a standalone concern,
independent of whether/when the consolidation happens.

### E1. `RealNeighborMovingBlockRenderState` — two independently-maintained copies
`log-all-the-things/common/.../client/RealNeighborMovingBlockRenderState.java` and
`jdcrafte/common/.../client/RealNeighborMovingBlockRenderState.java` are identical in logic (differ only
in Javadoc wording). A future fix to one has no mechanism to reach the other. The `createMovingBlock(...)`
factory helper is *also* duplicated three times inside log-all-the-things itself
(`CarpetedBlockEntityRenderer`, `SnowedBlockEntityRenderer`, `WindowedBlockEntityRenderer`), plus a
fourth inlined copy in jdcrafte's `WeathervaneBlockEntityRenderer`.

### E2. `FabricGlassJarBlockEntityRenderer`/`NeoForgeGlassJarBlockEntityRenderer` — byte-identical platform split with no platform-specific content
Both minekea classes read from the same cross-platform `ArchitecturyFluidAttributes`; nothing in either
body is actually platform-specific. Could be one class in `common` with no split at all.

### E3. Seat-occupancy check duplicated verbatim in two minekea files
`minekea/common/.../block/furniture/seats/StoolBlock.java:104` and
`.../ChairBlock.java:130` both contain the identical line
`world.getEntitiesOfClass(SimpleSeatEntity.class, new AABB(pos), (Object) -> true)`. Since
`SimpleSeatEntity` already lives in chimeric-lib, a static helper there would remove both copies with no
behavior change and no risk of the two call sites drifting apart.

### E4. Particle-burst helper duplicated in two minekea files, next to an unused near-miss in chimeric-lib
`minekea/.../DyedBlock.java:86-88` and `.../DyedPillarBlock.java:90-92` contain the identical 3-line
"spawn 5 splash particles above a wet block" loop, while chimeric-lib's own
`ChimericLibParticleUtils.spawnParticleAbove` (a close but not exact match — single particle, different
API, different Y offset) sits unused by anything outside chimeric-lib itself.

### E5. `houdini-block`'s platform-divergent mixin pair has no explanatory comment
`HoudiniWorldMixin` exists once in `common` and once in `neoforge/.../mixin/` for the documented
NeoForge patched-lambda reason (correct code, matches the pattern `docs/NEOFORGE.md` describes) — but
unlike `camel-nostrils`'s otherwise-identical `CN$ServerPlayerMixin` pair (which has an extensive
javadoc on both classes explaining the split, cross-referencing docs/NEOFORGE.md, and citing the
`javap` verification), houdini-block's pair has **zero** comment. A future maintainer has no way to know
the duplication is intentional rather than copy-paste drift without independently rediscovering the
NeoForge gotcha. Low-effort fix: copy camel-nostrils' comment style over.

### E6. `chimeric-lib`'s own JUnit and GameTest suites disagree on base package
JUnit tests live under `com.chimericdream.chimericlib.test.*`; GameTest classes live under
`com.chimericdream.lib.fabric.test.*` — inside the same mod, whose base package is `com.chimericdream.lib`.
Not a functional bug, but since chimeric-lib is meant to be the reference layout other mods copy, this
internal inconsistency risks propagating to whichever mod copies it next.

---

## F. Worth a second look, not yet confirmed either way

### F1. `stack-it-up`'s two `MixinItemStackDamage` classes weren't diffed line-by-line
One lives in `fabric/`, one in `neoforge/`, both with the identical class name and no platform suffix
(unlike log-all-the-things's convention of baking the platform into the class name for this exact
situation). Given the repo has a documented, legitimate reason for platform-divergent same-named mixin
pairs (the NeoForge lambda-renumbering gotcha), this pair is plausibly fine — but nothing in either
file's name signals *why* two copies exist, unlike camel-nostrils'/houdini-block's pairs. Worth a quick
diff to confirm before assuming either way.

### F2. `getTicker` client-side-guard inconsistency
`XtremeHopperBlock.getTicker`/`ArmoireBlock.getTicker` guard with `world.isClientSide() ? null : ...`;
`CrateBlock.getTicker`/`DyeStationBlock.getTicker` don't guard at all. Their `tick()` bodies currently
only touch `ContainerOpenersCounters` bookkeeping, which looks side-safe either way — flagged only
because if `recheckOpeners` ever grows a client-only or server-only side effect, two of its four call
sites would then run on the wrong side silently.

### F3. `archaeology-tweaks`'s and `flat-bedrock`'s internal mixin-naming splits — intentional or historical accident?
`archaeology-tweaks` has 2 of 7 mixins using an `AT$` prefix and 5 using none;
`flat-bedrock` has 1 of 3 using a `FlatBedrock` prefix and 2 using none. Worth checking git history
before picking a target convention for either mod, since it's not obvious from the code alone whether
the prefixed ones were added later (suggesting that's the newer/intended style) or are themselves the
legacy holdovers.

### F4. Only 3 of 9 datagen-enabled mods datagen on both platforms
`hopper-xtreme`, `jdcrafte`, `minekea`, `sponj` (setup aside, see A3), `villager-tweaks` have Fabric-only
datagen by design (the flag is correctly omitted for NeoForge, so this isn't a flag/reality mismatch) —
meaning these mods' NeoForge builds ship hand-authored or copy-forwarded data files. Likely intentional,
but worth explicitly confirming that's the long-term plan, especially for minekea given its content
volume (hand-syncing that much data between platforms is error-prone if it's ever expected to match
exactly).

### F5. `FabricDynamicRegistryProvider` naming isn't uniform
`*WorldgenProvider` (archaeology-tweaks, artificial-heart) vs. `*TrimProvider` (effective-gear) vs.
`*VillagerTradeProvider` (archaeology-tweaks) — three suffix conventions for the same kind of class.
Purely cosmetic.

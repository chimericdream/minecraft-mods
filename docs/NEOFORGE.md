# NeoForge gotchas

Runtime/build behaviors that are specific to the NeoForge platform in this repo — things that work
fine on Fabric (or work fine in general) but break, silently misbehave, or crash only under NeoForge.
Add new entries here as they're found; `CLAUDE.md` just points here so day-to-day context doesn't carry
the full write-ups.

## Mixin-added `SynchedEntityData` fields crash on NeoForge only

Adding a new synced-data field to a **vanilla** entity via `@Mixin` — a `@Unique static final
EntityDataAccessor<T> FOO = SynchedEntityData.defineId(TargetEntity.class, ...)` merged into the
target class by Mixin — compiles and works fine on Fabric, but crashes NeoForge at bootstrap with an
opaque `net.neoforged.fml.ModLoadingException: ... ExceptionInInitializerError: null` during "Registry
initialization." The crash report and logs give **no stack trace or cause** for this one — FML's
`ModLoader.waitForFuture` only formats the caught exception's class + message, never its `getCause()`
chain, so the real reason never surfaces anywhere on disk. Confirmed by attaching a debugger and by
bisecting the mixin file line-by-line.

**Root cause**: NeoForge patches `SynchedEntityData.defineId` to verify the calling code's declaring
class actually *is* the entity class being registered against, and hard-rejects mismatches as "attempt
to add synced data to a foreign entity." Mixin's bytecode merge makes the field's initializer *execute*
as part of the target class's own `<clinit>`, but the merged code still isn't recognized as declared by
the entity class, so NeoForge (and only NeoForge — Fabric has no equivalent check) blocks it
unconditionally. This is deliberate on NeoForge's part (preventing synced-data id collisions between
mods), not a bug to work around with mixin priority/ordering tricks.

**Fix**: don't use `SynchedEntityData` for mixin-added fields on vanilla entities. Use each platform's
native attachment API instead (NeoForge's `AttachmentType`, Fabric's
`AttachmentType`/`AttachmentRegistry`/`AttachmentTarget`), which isn't drawn from a fixed id space and
supports both persistence (`.serialize(MapCodec)` / `.persistent(Codec)`) and client sync
(`.sync(StreamCodec)` / `.syncWith(StreamCodec, AttachmentSyncPredicate)`). Put the platform split
behind a common `Provider` interface set via `setProvider(...)` at platform mod-init — **not**
`@ExpectPlatform`, since NeoForge's dev run resolves `common` and the `neoforge` source set as separate
JPMS modules, and an `@ExpectPlatform`-generated `Impl` class in the same package as its interface
causes two modules to export that package, which fails FML startup ("Modules ... export package ...").
Reference implementations: `sneaky-tweaks`'s `CampfireGraceHolder` (the original instance of this
pattern) and `camel-nostrils`'s `CN$CamelSnoutState`.

## Custom entity renderers must register in the NeoForge mod constructor, not a lifecycle event

Registering a **custom entity's** renderer via Architectury's
`dev.architectury.registry.client.level.entity.EntityRendererRegistry.register(...)` works fine on
Fabric but silently no-ops on NeoForge if the call happens from any `@SubscribeEvent` lifecycle
handler — including `EntityRenderersEvent.RegisterRenderers`, the event that looks like the obviously
correct place. The entity then has no renderer, and the game crashes the next time it's anywhere in
view: `NullPointerException: Cannot invoke "EntityRenderer.shouldRender(...)" because "renderer" is
null` in `EntityRenderDispatcher.shouldRender`, reached via `LevelExtractor.extractVisibleEntities` on
the render thread. Confirmed via `camel-nostrils`'s Livna block (spawns a
`FallingUpwardBlockEntity`) — placing the block summoned the entity and crashed the client
immediately.

**Root cause**: `EntityRendererRegistry.register(...)` just stores the factory in a static map
(`EntityRendererRegistryImpl.RENDERERS`, in architectury-neoforge). That map is only drained into the
real game renderer dispatch by **Architectury's own** `EntityRenderersEvent.RegisterRenderers`
listener, which is subscribed on *architectury's own mod event bus* — not yours. NeoForge fires this
event separately per mod bus, in mod-load order, and every mod here declares `architectury` as an
`ordering = "AFTER"` dependency, so architectury's bus fires (and drains the map) *before* your mod's
bus gets the same event. By the time your own `RegisterRenderers` handler runs and populates the map,
architectury has already read it and moved on — the entry is added too late and never consumed.

**Fix**: call `EntityRendererRegistry.register(...)` directly and unconditionally in the platform mod
class's **constructor** (guarded by `FMLEnvironment.getDist() == Dist.CLIENT`), not from any
`@SubscribeEvent` hook. Mod construction for every mod completes before any lifecycle or client event
fires for any of them, so this is early enough regardless of bus-firing order. This does **not** apply
on Fabric — `ClientModInitializer.onInitializeClient()` already runs early enough relative to Fabric's
own entity-renderer map construction — and it does **not** apply to Architectury's separate
`BlockEntityRendererRegistry` (block-entity renderers), which registers fine from inside
`EntityRenderersEvent.RegisterRenderers`. Reference implementations:
`minekea/neoforge/.../MinekeaNeoForge.java` constructor (the original instance of this pattern) and
`camel-nostrils/neoforge/.../CamelNostrilsNeoForge.java` constructor.

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

## NeoForge's event-hook patches restructure vanilla method bodies into differently-numbered lambdas

A `@Redirect`/`@Inject` mixin whose `method =` and `@At(target = ...)` were derived from vanilla/Fabric
bytecode can fail purely on NeoForge with `InjectionError: ... failed injection check, (0/1)
succeeded. Scanned 0 target(s)` — even though the named method still exists in the patched class (so
it's not the usual "method renamed/removed" case) and even though `required`/`defaultRequire` are
otherwise satisfied. Confirmed via `camel-nostrils`'s `CN$ServerPlayerMixin`, which redirects
`BedRule.canSleep`/`canSetSpawn` and a `PlayerTrigger.trigger` call inside
`ServerPlayer#startSleepInBed`.

**Root cause**: NeoForge patches many vanilla methods to wrap their body in an event hook (here,
`EventHooks.canPlayerStartSleeping`). To do this without duplicating logic, the patcher moves the
method's *entire original body* into a synthetic lambda (`invokedynamic` + `lambda$originalMethod$N`),
computes its result once for the event to see/override, then falls through to the real state change
(often via `super.methodName(...)`, unpatched on the superclass) only if the event didn't cancel.
Confirmed via `javap` against `~/.gradle/caches/fabric-loom/{mc_version}/neoforge/{neoforge_version}/minecraft-merged-official-at-patched.jar`
(the actual patched jar Loom builds against — **not** the `neoforge-*-sources.jar` from Maven, which is
NeoForge's own mod source, not patched vanilla source): on NeoForge,
`ServerPlayer#startSleepInBed` no longer contains the `BedRule` checks or the `bedBlocked` call at
all — they moved into `lambda$startSleepInBed$0`, and the advancement-trigger consumer that's
`lambda$startSleepInBed$1` on vanilla/Fabric shifted to `lambda$startSleepInBed$2` (NeoForge's patch
adds an extra lambda earlier in the method, shifting every later lambda's synthetic index). Mixin's
injector still finds the *named* target method fine; it just scans zero matching instructions inside
it, hence "Scanned 0 target(s)" rather than a "target not found" error.

**Fix**: don't assume a mixin targeting vanilla method/lambda names works unmodified on NeoForge if
that method is patched. Diff the two independently with `javap -p -c` (extract just the class you need
from the patched jar above, and from the unpatched
`~/.gradle/caches/fabric-loom/{mc_version}/net/minecraft/minecraft-merged-deobf/**.jar` or equivalent,
rather than trusting a decompile of one side) to find the real target method/lambda names and confirm
which `INVOKE`s actually live where on each platform. If they differ, split into a common mixin (used
by Fabric only) plus a NeoForge-only mixin class + its own mixin config, each targeting the correct
lambda numbering for that platform — register the common config only from `fabric.mod.json` (add a
`camelnostrils.fabric.mixins.json`-style second config there for the fabric-only pieces) and the
NeoForge one only from `neoforge.mods.toml`'s own `[[mixins]]` block, so mixins unaffected by the
patch can stay in one shared config while the affected one gets platform-specific implementations.
Redirects that only consume the `@At(INVOKE)`'s own call arguments (not surrounding locals) are
unaffected by which method contains them and don't need MixinExtras `@Local` sugar at all — only the
ones capturing an enclosing-method local (e.g. via `@Local(argsOnly = true)`) need their target
updated to whichever method/lambda now contains that local in scope. Reference implementation:
`camel-nostrils`'s `CN$ServerPlayerMixin` — compare
`common/.../mixin/CN$ServerPlayerMixin.java` (Fabric, registered via `camelnostrils.fabric.mixins.json`)
against `neoforge/.../neoforge/mixin/CN$ServerPlayerMixin.java` (registered via
`META-INF/camelnostrils.neoforge.mixins.json`).

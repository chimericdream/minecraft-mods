# Block migration framework

> Source: legacy backlog, [§ Extract & generalize existing suite patterns](../../../chimeric-lib/ideas/brainstorms/2026-08-27-legacy/potential-features.md#extract--generalize-existing-suite-patterns) · Effort **XL** · Status: **Ready**

## Identity

Approved 2026-10-26; now lives in the mod's [`ideas/README.md`](../README.md#identity).

## Description

A **standalone mod** (its own mod, not part of ChimericLib, though it requires it) that lets mod authors
and modpack makers retire, rename, merge, or move blocks and items without DataFixerUpper and without
breaking existing worlds or inventories. A migration says "old block → new block", and the mod converts placed blocks and held items
to the new one with their contents intact. It generalizes the pattern Hopper X-Treme built for its
`filtered_*` hoppers ([`docs/BLOCK-MIGRATION.md`](../../../docs/BLOCK-MIGRATION.md)), and the suite's own mods
(Minekea, Hopper X-Treme) become its first consumers.

**Who it's for**

- **Mod authors**, who get a small API instead of rebuilding the snapshot, swap, and restore logic per
  mod.
- **Modpack makers**, who need to swap one mod's blocks for another's, or clean up after a removed or
  renamed mod, without writing Java. See the open question on data-driven declaration.

**What it does**

- **Declaration.** Two ways into one registry. Mod authors use a code builder, e.g.
  `Migrations.block(old).to(new)`, with options for property mapping and block-entity handling. Modpack
  makers use JSON files in the config directory, read at startup, covering IDs, property remaps, stub
  declarations, and same-type block-entity re-attach. The target can be any registered block in any mod,
  so renames, merges, and cross-mod moves (such as Minekea → But What About...? / Cobblicious) are all
  supported. The mod checks the target is present and logs each conversion.
- **Finding placed blocks.** A chunk-load scan. Per loader, hook chunk load, skip any section whose
  palette has no registered deprecated block, and queue the rest. A server-tick handler drains the queue
  at a configurable cap of conversions per tick, so a chunk full of old blocks can't spike the tick. This
  works for blocks with no block entity (stairs, slabs, walls), which the current block-entity-tick
  approach cannot reach.
- **Block-entity preservation.** The snapshot / clear / swap / re-attach / restore sequence from the
  block-entity trap, with a hook for hidden slots (Hopper X-Treme's trailing filter slot) and for
  per-block fields that must survive. When the old and new blocks have different `BlockEntityType`s, a
  per-migration mapper moves the data across, and a migration without one is skipped and logged.
- **Stub registration.** For simple blocks (stairs, slabs, walls), the builder can register a stub block
  and item under the retired ID with an explicit property list (or one copied from a template block), so
  the owning mod can delete its old classes. Blocks with a block entity keep their real registration and
  the mod passes the framework a supplier for it. An unconverted stub is deliberately plain: the
  missing-model look, a readable name and tooltip, and full-cube collision that breaks by hand.
- **Uninstalled mods.** The same stubs, declared in JSON, can stand in for the blocks of a mod that has
  been removed from a pack, registered under that mod's namespace and converted to a block that still
  exists. A stub is registered only if no installed mod has that namespace as its mod ID, so reinstalling
  the mod never causes a duplicate registration, whatever the load order. Simple blocks only: a removed
  mod's block entities can't be saved.
- **Property dump command.** `/forwardingaddress dump <namespace>`, for operators, writes a starter JSON
  file of that mod's blocks and their properties while the mod is still installed, so a pack maker has the
  property lists the stubs need before removing it. Targets are left blank for the pack maker to fill in,
  and block-entity blocks are flagged as unsupported. The file goes to
  `config/forwardingaddress/dumps/<namespace>.json`, never into the live migrations folder.
- **Property mapping.** Matching properties are copied by default. An optional per-migration mapper
  handles renamed or merged property values.
- **Item conversion.** Deprecated items convert too, not just on placement: in the player inventory and
  ender chest (on login), in block-entity containers (via the chunk scan), and as item entities and in
  item frames (on entity load). The old items stay registered, so a stack that isn't reached still loads
  and converts when placed.
- **Deprecated tooltip and creative-tab hiding.** A standard tooltip and tab omission, so each mod stops
  redoing them (Hopper X-Treme currently has its own tooltip hook per loader).

**Out of scope (so far):** entity or biome migrations, items nested in shulker boxes and bundles, entity
inventories (chest minecarts, chest boats, mobs), JSON block-entity mappers, datapack-reloadable rules,
block entities of an uninstalled mod, and rewriting save data outside loaded chunks.

## Decisions

- 2026-10-26 — Placed deprecated blocks are found by a chunk-load scan using the section palette, not by
  block-entity ticks or random ticks.
- 2026-10-26 — The framework covers block-entity preservation, item conversion, property mapping, and
  the deprecated tooltip with creative-tab hiding.
- 2026-10-26 — Cross-mod migration (old block in one mod, target in another) is first-class in v1.
- 2026-10-26 — Migrations are declared with a code builder; no JSON. **Superseded** by the data-driven
  decision below.
- 2026-10-26 — Block-entity handling depends on the types. If old and new share a `BlockEntityType`, the
  original block entity is re-attached. If they differ, the migration must supply a mapper, which receives
  the old block entity's saved tag and the new block entity. With no mapper, the block is left
  unconverted, an error naming the position and both IDs is logged, and conversion is retried on the next
  chunk load. Nothing is copied on a best-effort basis.
- 2026-10-26 — Chunk load only detects: it checks section palettes and enqueues the chunk per level, and
  never calls `setBlock` itself. A server-tick handler drains the queue, converting up to a configurable
  number of blocks per tick (default about 256). The cap is a block count, not a time budget, so tests
  are deterministic. Every conversion counts as 1, block-entity ones included. The queue is not
  persisted: a chunk that unloads mid-drain is dropped from the queue and rescanned on its next load.
- 2026-10-26 — v1 item conversion covers the player inventory (including armor and offhand) and ender
  chest on login, block-entity containers via the chunk scan under the same queue and cap, and item
  entities and item frames on entity load. Items nested in shulker boxes and bundles, and entity
  inventories (chest minecarts, chest boats, mobs), are left for a later version. Conversion keeps the
  stack count and copies data components, and the migration's mapper handles components that differ.
- 2026-10-26 — The builder can register a stub block and item under a retired ID, with a declared property
  set and no block entity, for simple blocks such as stairs, slabs, and walls. Blocks with a block entity
  keep their real registration in the owning mod, which passes the framework a supplier for it, as Hopper
  X-Treme does today. A stub for a block-entity block is not supported.
- 2026-10-26 — A stub's property set is declared as an explicit list of `Property` objects, with a
  convenience that copies the set from a template block that already exists at registration time (such
  as a vanilla stair). It is never derived from the migration target, because the target may not be
  registered yet in a cross-mod move and because a migration can rename or merge properties. Because a
  block builds its state definition in its constructor, the library must pass the list in through a
  holder or factory, not a subclass field.
- 2026-10-26 — When a block moves between mods, the old mod registers the stub under its own namespace
  (Minekea registers `minekea:` stubs and depends on But What About...? for the target). A new mod
  adopting a legacy ID is not in v1. **Superseded** by the uninstalled-mods decision below.
- 2026-10-26 — This is built as an **independent mod, not part of ChimericLib**, because mod authors and
  modpack makers would want it on its own. It moved out of ChimericLib's ideas, since ChimericLib's
  identity is shared plumbing that adds nothing a player installs it for.
- 2026-10-26 — The mod is named **Forwarding Address** (`mod_id` `forwardingaddress`), and its identity
  statement and suite boundary note are approved (see Identity above). Only the mod that declares a
  migration depends on it; source and destination mods don't depend on each other, and a migration whose
  target isn't installed is skipped and logged.
- 2026-10-26 — Migrations can also be declared in **JSON files in the config directory**, read at startup,
  for modpack makers. The JSON loader and the code builder write into one migration registry, so the
  chunk scan, queue, and item conversion have a single path. Config is used, not datapacks, because stubs
  register blocks and the block registry freezes before datapacks load. JSON can declare old → new IDs,
  property renames and value maps, stub declarations (an explicit property list, or a vanilla template to
  copy), and same-type block-entity re-attach. Block-entity mappers stay code-only: a JSON migration
  between different block-entity types is skipped and logged. Datapack-reloadable conversion rules are
  not in v1.
- 2026-10-26 — Converting the blocks of an **uninstalled mod** is in scope for v1. Stubs, declared in
  JSON, are registered under the removed mod's namespace and convert to a block that still exists. A stub
  is registered only if the ID is free, so no one gets a duplicate registration. This covers simple
  blocks only; a removed mod's block entities are out of scope. A **property dump command**, which writes
  a starter JSON file of a mod's blocks and properties while the mod is still installed, is also in v1.
  This supersedes the rule that the old mod registers the stub.
- 2026-10-26 — The **property dump command** is `/forwardingaddress dump <namespace>`, restricted to
  operators, one namespace per run. It writes the namespace's blocks with their properties and possible
  values, in the same shape as the migration JSON's stub declarations, and flags block-entity blocks as
  unsupported. Targets are left blank and are never guessed or suggested, so entries with no target are
  ignored by the loader with a warning. The file is written to `config/forwardingaddress/dumps/
  <namespace>.json`, a separate folder that is never loaded as live migrations, so a half-finished file
  can't change a world. Non-block items are not dumped in v1. It only works while the mod is installed,
  so the docs must say to run it before removing the mod.
- 2026-10-26 — The **duplicate guard** is an installed-mod check, not registration priority. A stub under
  `foo:` is registered only if no installed mod has the mod ID `foo`, using each loader's mod list, which
  is known before any registration. It is order-independent on Fabric and NeoForge. Where the loader
  allows it, the guard also checks that the key isn't already registered, a conflict stops with a clear
  message naming the ID and the migration file, and stubs under `minecraft:` are refused. Late
  registration was rejected: NeoForge has only a handful of event priority levels, and Fabric has no
  priority at all.
- 2026-10-26 — The mod **requires ChimericLib**, so a pack that uses migrations installs both. This was
  chosen over an Architectury-only mod, to reuse ChimericLib's helpers. Consuming mods (Minekea and future
  mods) take a **required dependency** on this mod. Hopper X-Treme keeps its own `filtered_*` code for
  now; moving it onto this mod is a separate Hopper X-Treme backlog item. The public API is a **separate,
  semver-versioned API package** that consumers compile against, published on release like ChimericLib's
  `publish:lib` and used as an in-build `project()` dependency inside this repo.
- 2026-10-26 — **ChimericLib surface.** Forwarding Address uses ChimericLib's `ChimericCommands` (the dump
  command), `TextHelpers` (the deprecated tooltip), `ModRegistryHelper` (registration, with stub
  registration under a foreign namespace, which the existing `Identifier` overloads now keep on both
  loaders), and its GameTest test fixtures for the
  conversion tests. It gets the loader hooks, installed-mod list, and config folder from Architectury
  API. ChimericLib needs no new helpers first. ChimericLib's only runtime requirement is Architectury
  API, so requiring it costs a pack one extra jar. Forwarding Address does **not** use `YaclConfig`,
  because that would require YACL: it keeps its own settings (such as the per-tick cap) in a small Gson
  JSON file beside the migration JSON, and an optional config screen can come later. The dump command
  stays separate from ChimericLib's `/chimericlib dump` registry-report idea; the two are cross-linked.
- 2026-10-26 — **Namespace-preserving registration.** `ModRegistryHelper`'s `Identifier` overloads for
  blocks and items stripped the namespace on NeoForge (a branch added 2024-11-09/10, with no recorded
  reason), so a plan was made to add new explicit methods that preserve any namespace.
  **Superseded 2026-10-27:** the NeoForge branches were removed (`7cbe38ca6`), and Forwarding Address
  registers stubs through the existing `registerBlock`/`registerItem`/`registerWithItem` overloads. No new
  ChimericLib methods are needed, which also reverts the earlier amendment to "ChimericLib needs no new
  helpers first". A ChimericLib GameTest (`registerWithItemKeepsForeignNamespace`, Fabric only) guards the
  foreign-namespace behavior.
- 2026-10-26 — **Mismatched stub configs** are handled by the loaders' own registry-mismatch disconnect,
  which names the differing entries (the stub IDs), with no custom join-time check in v1. The docs and mod
  page say the stub config in `config/forwardingaddress/` must be identical on client and server, and the
  mod logs the stub count and a short config hash at startup so the two logs can be compared. A
  handshake-based check is left for a time when ChimericLib's networking wrapper exists.
- 2026-10-26 — Forwarding Address is **required on both client and server** (Fabric `environment: "*"`,
  NeoForge default). Stubs are registry entries that clients need in order to read chunk data and join,
  and mod metadata is fixed at build time, so a server-only mode for packs without stubs isn't possible.
  The mod page states this prominently.
- 2026-10-26 — An unconverted **stub looks plain** in v1. The block and the item use the standard
  missing-model look, with no runtime-generated resources. The item gets a readable name and a tooltip
  naming the original ID and the intended target (or saying no target is configured). A stub block has
  full-cube collision, low hardness, can be broken by hand, and drops the stub item, which converts later
  through item conversion (the drop mechanism is an implementation detail, unverified for 26.2). A shared
  placeholder model and a borrowed target model are not in v1.
- 2026-10-26 — Marked **Ready**. The stub-registration prototype stays an implementation-time check, and
  delivery staging is decided when the build is planned. Starting the build is a separate request.
- 2026-10-26 — Effort re-estimated at **XL** against the rubric, now that scope is fixed. It is a system
  touching many features: per-loader hooks (chunk load, entity load, login, tick), a queue and budget,
  block-entity handling with mappers, item conversion, stub registration, a JSON loader, a dump command,
  and an API package. It also carries the overhead of a whole new mod: scaffolding, mod page, docs, and
  publishing.
- 2026-10-26 — Verification: JUnit for JSON parsing, property mapping, and the queue budget; per-loader
  GameTests for conversion (contents kept, nothing dropped, facing and waterlogging preserved, item
  conversion, the per-tick cap, and skipped-and-logged cases); a visual smoke test for stub rendering; and
  manual steps in the mod's `TEST_PLAN.md` for the removed-mod flow (dump, remove the mod, load the
  world), which a GameTest can't easily cover.
- 2026-10-27 — **Build plan decided.** Delivery is staged: **Stage 1 (core)** is the code builder, chunk scan
  and queue, block swap with block-entity re-attach, property mapping, item conversion, and the deprecated
  tooltip; **Stage 2 (pack-maker layer)** is the JSON loader, stubs, duplicate guard, and dump command.
  The **public API is a package inside `common`** (`com.chimericdream.forwardingaddress.api`), not a
  separate Gradle module, so splitting it out later stays cheap. Rules: `api` never imports from the rest of
  the mod; the builder reaches the implementation through a backend interface installed at init; API
  types are plain data and functional interfaces; a JUnit test fails if `api` imports from outside
  itself. This supersedes the "separate, semver-versioned API package" wording in the ChimericLib decision.
  The repo's GameTest source set is Fabric-only, so conversion logic is GameTested on Fabric with thin
  per-loader hooks, and NeoForge is covered by a manual pass in `TEST_PLAN.md`. The stub-registration
  prototype spike runs at the start of Stage 2.
- 2026-10-27 — **Stage 1 (core) built.** Differences from the plan, all small: (1) Placed blocks are
  detected by each loader's *post-load* chunk event (Fabric `ServerChunkEvents.CHUNK_LOAD`, NeoForge
  `ChunkEvent.Load`), not Architectury's `ChunkEvent.LOAD_DATA`, which fires before the chunk is usable.
  The event only reports the chunk position; the palette check happens in the drain, on the server thread,
  so the event is safe off-thread. (2) The cap is per level per server tick (default 256), and at most 32
  chunks are palette-scanned per drain; scanning costs no budget. (3) A block migration also migrates the
  block's item automatically, and an explicit item migration for the same ID wins. (4) Duplicate
  declarations are logged and ignored, not thrown, so one conflict can't stop a pack from loading. (5) If
  a block-entity mapper throws after the swap, the original block and its contents are put back and the
  block is skipped. (6) The tooltip key is `item.forwardingaddress.retired.tooltip`; retired items are
  removed from creative tabs by Fabric's `CreativeModeTabEvents.MODIFY_OUTPUT_ALL` and NeoForge's
  `BuildCreativeModeTabContentsEvent`. An item whose target isn't installed is *not* hidden or tooltipped.
  (7) The settings file is `config/forwardingaddress/settings.json` with one key,
  `maxConversionsPerTick`; an unreadable file keeps the defaults and is never overwritten.
  (8) **Declaring is order-independent.** Mod initialization order isn't guaranteed on either loader (a
  GameTest failed intermittently on exactly this), so `Migrations` holds declarations made before
  Forwarding Address starts and hands them over, in order, when its backend installs. Nothing throws.

## Related

- [`docs/BLOCK-MIGRATION.md`](../../../docs/BLOCK-MIGRATION.md) — the current per-mod approach this would generalize. Update it to point at the mod once built.
- [But What About...? ideas](../../../but-what-about/ideas/README.md) — the Minekea boundary plan is the first cross-mod consumer. Its plan has Minekea depend on the destination mod; with Forwarding Address, only the mod that declares the migration depends on this one, and the two mods don't need to know about each other.
- [Namespace-preserving registration](../../../chimeric-lib/ideas/archive/namespace-preserving-registration.md)
  — dropped; the existing `ModRegistryHelper` overloads already do what stub registration needs.
- [ChimericLib ideas](../../../chimeric-lib/ideas/README.md) — this idea used to live there. The related
  [Component/NBT compatibility helpers](../../../chimeric-lib/ideas/infrastructure/component-nbt-compat-helpers.md)
  and [GameTest harness helpers](../../../chimeric-lib/ideas/testing/gametest-harness-helpers.md) remain
  ChimericLib ideas; the conversion GameTest (contents kept, nothing dropped) is a candidate shared fixture.
  The [Registry dump command](../../../chimeric-lib/ideas/dev-tools/registry-dump-command.md) is a separate
  ChimericLib idea with a similar "write a report to disk" step; this mod's dump command stays separate.

## Open questions

- **Runnable prototype of the stub registration** (implementation-time; the user marked the idea Ready
  with this still open). A source-reading spike on 2026-10-26 (a subagent
  reading Architectury 21.0.4, NeoForge 26.2.0.15-beta, and the 26.2 jars with `javap`; not run, and not
  independently re-checked) found that the design holds, with the caveats below. A prototype on both
  loaders still has to prove: client and server sync with matching and mismatched stub sets; loading an
  old world with stubs registered; the missing-model look for stubs; and Architectury `Platform` calls
  inside the NeoForge mod constructor.
  - **Holds:** `Platform.isModLoaded` works before any registration on both loaders, and the config
    folder and a JSON read are available in the Fabric initializer and the NeoForge constructor.
    Architectury's `DeferredRegister.register(Identifier, ...)` keeps a foreign namespace on both.
  - **Backstop only:** a `containsKey` check sees only entries from mods processed earlier on both
    loaders, so it is not an order guarantee. The installed-mod rule is what makes the guard
    order-independent. `register()` itself does no check, so do it before calling `register(...)`. A
    duplicate throws `IllegalStateException("Adding duplicate key ...")`.
  - **Constraints:** call `register()` once, after every stub is added (a second call throws, and entries
    added later on NeoForge are missed); sort the stub list so registration order, and so network IDs, is
    deterministic; fail loudly if the stub config can't be read, because a partly registered set causes
    registry-sync mismatches.

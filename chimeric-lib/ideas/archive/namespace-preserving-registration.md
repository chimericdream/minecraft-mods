# Namespace-preserving registration

> Source: ChimericLib inbox, raised while fleshing out the [Block migration framework](../../../forwarding-address/ideas/core/block-migration-framework.md) · Effort **S** · Status: **Dropped** (not needed: the NeoForge `getPath()` branches were removed in `7cbe38ca6`, so the existing overloads already keep any namespace)

## Description

New methods on `ModRegistryHelper` that register under **any** namespace on both Fabric and NeoForge, so a
mod can register an ID that isn't its own.

**The problem.** Six `Identifier` overloads behave differently by loader: `registerBlock`, `registerItem`,
`registerEntityType`, `registerScreenHandler`, `registerVillagerProfession`, and `registerCustomStat`. On
Fabric they pass the `Identifier` through. On NeoForge they call `register(id.getPath(), ...)`, which
silently replaces a foreign namespace with the helper's own mod ID. `registerWithItem(Identifier, ...)` is
affected too, because it calls `registerBlock` and `registerItem`. `registerFluid(Identifier, ...)` and
`registerBlockEntity(Identifier, ...)` already pass the `Identifier` through on both loaders. The branch has
been there since the helper was added in November 2024 (`9e302c131`, `087c602c4`), and no commit message
or comment says why.

**The new methods.** Each calls the underlying `DeferredRegister.register(Identifier, ...)` directly, with
no `getPath()` branch. A source reading of Architectury 21.0.4 found that this keeps the namespace on both
loaders; it has not been run. Each takes an `Identifier` (the `String` overloads always use the helper's own
mod ID, so they have no variant), and those that have a `Item.Properties` overload get a matching one:

| Existing method | New method |
|---|---|
| `registerBlock` | `registerBlockWithNamespace` |
| `registerItem` | `registerItemWithNamespace` |
| `registerWithItem` | `registerWithItemAndNamespace` |
| `registerEntityType` | `registerEntityTypeWithNamespace` |
| `registerScreenHandler` | `registerScreenHandlerWithNamespace` |
| `registerVillagerProfession` | `registerVillagerProfessionWithNamespace` |
| `registerCustomStat` | `registerCustomStatWithNamespace` |

**Alongside the methods:** a Javadoc note on each of the six existing overloads saying it drops a foreign
namespace on NeoForge and pointing at the new method.

**First consumer:** the Forwarding Address mod, which registers stub blocks and items under a removed
mod's namespace. It needs only the block and item methods; the other four are added so the helper is
consistent.

**Out of scope:** changing the behavior of the existing overloads (about 900 call sites across 28 files, mostly
Minekea, all in their own namespace and so unaffected), and any `String`-name variants.

## Decisions

- 2026-10-26 — Add new explicit methods, not a change to the existing overloads. The existing overloads'
  NeoForge behavior has an unrecorded reason and is relied on by the mods in this repo, so it is left
  alone. The other options considered were bypassing the helper with its public `DeferredRegister` fields
  and fixing the existing overloads.
- 2026-10-26 — The new methods cover **all six** affected registrations (plus `registerWithItem`), not only
  blocks and items, so the helper is consistent.
- 2026-10-26 — Naming: a `WithNamespace` suffix, and `AndNamespace` where the existing name already
  contains `With` (`registerWithItemAndNamespace`).
- 2026-10-26 — Tested with a **probe registration plus a GameTest**: a probe block and item under a test
  namespace in ChimericLib's GameTest source set, with a GameTest asserting both IDs exist in the
  registries, on each loader. A matching step goes in ChimericLib's `TEST_PLAN.md`.
- 2026-10-26 — Marked **Ready**. Starting the build is a separate request.
- 2026-10-26 — Effort estimated at **S**: seven small methods, Javadoc on six overloads, a probe
  registration, one GameTest, and a test-plan step.

- 2026-10-27 — **Dropped.** Commit `7cbe38ca6` removed the `Platform.isNeoForge()` branches from the six
  `Identifier` overloads, after booting archaeology-tweaks through NeoForge `runData` showed Architectury
  21.0.4's `DeferredRegister` registers the full `Identifier` there. The existing overloads now keep a
  foreign namespace on both loaders, so none of the `WithNamespace` methods are needed and none were
  built. The Javadoc notes are moot too. The first open question below is answered by that commit. A
  regression GameTest, `RegisterableBlockGameTest.registerWithItemKeepsForeignNamespace`, registers a
  block and item under a foreign namespace through `registerWithItem` and asserts neither lands under the
  helper's own namespace. It runs on Fabric only, because ChimericLib's GameTest source set is
  Fabric-only.

## Brainstorm variants

_None; this did not come from a brainstorm._

## Related

- [Block migration framework](../../../forwarding-address/ideas/core/block-migration-framework.md) — the Forwarding Address mod
  needs these methods before its stub registration can be built.
- [Creative tab builder](../datagen/creative-tab-builder.md) and
  [Block family generator](../datagen/block-family-generator.md) — other registration helpers in the same area.
- [GameTest harness helpers](../testing/gametest-harness-helpers.md) — the probe GameTest lives in the
  same source set.

## Open questions

- ~~Should the NeoForge `getPath()` branch be investigated, and possibly removed from the existing overloads
  later, once every consumer mod has been tested? Not needed for this idea, so it can stay open.~~ Answered: removed in `7cbe38ca6`.
- Can the probe registration be added to ChimericLib's GameTest source set at initialization time on both
  loaders? Can be worked out during implementation.

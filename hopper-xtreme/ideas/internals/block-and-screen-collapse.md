# Block and screen collapse (refactor 3.1, step 4)

> Source: step 4 of [`REFACTOR-3.1-PLAN.md`](../../REFACTOR-3.1-PLAN.md), code-review plan item 3.1 · Effort **M** · Status: **Exploring**

## Description

Finish the hopper de-duplication started by the block-entity extraction (steps 1–3, done: see
`AbstractXtremeHopperBlockEntity`). The six block classes and four screen handlers/screens still repeat
the same wiring. This is internal cleanup with no player-visible change.

**In scope:** the block-entity plumbing shared by the block classes, the two filtered screen handlers,
and the client screens.
**Out of scope:** block geometry, which stays split per root, and any gameplay change.

**Verification:** `:hopper-xtreme:fabric:runGameTest` covers the server-side block behavior. The
`*Screen` classes are client-only and not covered by gametests, so check slot layout and rendering with
the `mc-visual-smoke-test` skill (ask first in a live session).

### Why it isn't one base class

The six block classes do **not** share geometry, so they can't collapse the way the BEs did:

| block | root | geometry / state |
|-------|------|------------------|
| `XtremeHopperBlock`, `GlazedHopperBlock`      | `AbstractHopperBlock`      | down-facing hopper shapes; `FACING` excludes UP |
| `XtremeMultiHopperBlock`, `GlazedMultiHopperBlock` | `AbstractMultiHopperBlock` | 4 horizontal + `DOWN_CONNECTED` |
| `XtremeHupperBlock`                            | `BaseEntityBlock` (own)    | **inverted** up-facing shapes; `FACING` excludes DOWN |
| `XtremeMultiHupperBlock`                       | `BaseEntityBlock` (own)    | own shapes; 4 horizontal + `UP_CONNECTED` |

The geometry split is correct and should stay. What's actually duplicated 6× is the **block-entity
plumbing**, independent of geometry:

- `cooldownInTicks` / `baseKey` / `withFilter` fields + getters (already the `HopperVariantBlock` contract),
- `newBlockEntity` and `getTicker` (`createTickerHelper(type, <VARIANT>_BLOCK_ENTITY.get(), <BE>::serverTick)`),
- `useWithoutItem` → `player.openMenu(be)` + `Stats.INSPECT_HOPPER`,
- `entityInside` → `<BE>.onEntityCollided(...)`,
- for the single-facing blocks: `onPlace` / `neighborChanged` / `updateEnabled` (incl. the
  `copper_hopper` opt-out).

### How to collapse it

1. Give every variant BE a shared way to be constructed and ticked generically. Two hooks are enough:
   `BlockEntityType<?> beType()` and `BlockEntity newBlockEntity(BlockPos, BlockState)` (or hand the
   block a `BiFunction<BlockPos, BlockState, ? extends AbstractXtremeHopperBlockEntity>` at
   construction). `serverTick` / `onEntityCollided` are already generic on the base, so `getTicker`
   and `entityInside` can call them through `beType()` without knowing the concrete leaf.
2. Because `AbstractHopperBlock` and `AbstractMultiHopperBlock` are separate roots (and the huppers
   extend `BaseEntityBlock` directly), put the plumbing in a **`HopperBlockPlumbing` interface with
   `default` methods** that call those two hooks, and have all block roots implement it. That dedups
   the wiring 6 → 1 without touching geometry. (If a `default`-method interface gets awkward around
   `protected` block methods, the fallback is to duplicate the ~4 plumbing methods once per root,
   i.e. 6 → 2, which is still most of the win.)
3. Leaves shrink to: `CODEC`, the three field values, and the two hooks.

### Screen handlers / screens

- The two **filtered** handlers are structurally identical — `FilteredHopperScreenHandler`
  (`STORAGE_SLOT_COUNT = 5`) and `FilteredGlazedHopperScreenHandler` (`= 1`): N `NonFilterSlot`s + one
  `FilterSlot`, the standard player-inventory block, and a `quickMoveStack` that respects the hidden
  filter slot. Extract `AbstractFilteredHopperScreenHandler` parameterized by the storage-slot count
  and the storage-slot X positions (`int[]`) + the menu type; each leaf becomes a constructor plus
  those constants.
- `GlazedHopperScreenHandler` (one plain slot, no filter) is the odd one out — the *unfiltered*
  hopper reuses vanilla `HopperMenu`, so only the unfiltered glazed case needs a bespoke 1-slot menu.
  Leave it, or fold its `quickMoveStack` into the shared base.
- The client `*Screen` classes differ only in background texture + label positions; a shared base
  taking those as constructor args collapses them identically.

### Gate

`./gradlew :hopper-xtreme:fabric:runGameTest` still covers the server-side block behavior. The
`*Screen` classes are client-only and **not** exercised by gametests — verify slot layout/rendering
with the `mc-visual-smoke-test` skill.

## Decisions

_None yet._

## Brainstorm variants

_None. This came from a build plan, not a brainstorm._

## Related

- Code-review plan item 3.1 (block-entity extraction, already applied): [`REFACTOR-3.1-PLAN.md`](../../REFACTOR-3.1-PLAN.md).

## Open questions

- Plumbing as a `HopperBlockPlumbing` interface with `default` methods (6 → 1), or the fallback of one
  copy per block root (6 → 2) if `protected` block methods make the interface awkward?
- Fold `GlazedHopperScreenHandler`'s `quickMoveStack` into the shared base, or leave it alone?

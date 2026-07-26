# Hopper X-Treme 3.1 — block-entity base-class extraction (design / WIP)

Code-review plan item **3.1**: the six `*BlockEntity` classes in
`hopper-xtreme/common/.../entity/` (~3,700 lines) are ~80% identical. This doc captures the exact
variation surface (diff-verified) and the proposed `AbstractXtremeHopperBlockEntity` design, so the
refactor can be executed as a focused, gametest-gated pass.

**Status:** the block-entity extraction (steps 1–3 below) is **applied** — see
`AbstractXtremeHopperBlockEntity` and the single-facing / multi intermediates. The six leaves are now
thin subclasses (~3,700 → ~1,430 lines). Gametests are green before and after. Step 4 (block classes
+ screen handlers/screens) is **not** done yet — the blocks turned out to be less uniform than
"diff-identical modulo names" (the hupper/multi-hupper blocks extend `BaseEntityBlock` directly with
their own inverted geometry and `FACING` predicate), so that collapse is a separate, meatier pass.

One latent quirk was surfaced and **removed** for consistency: `XtremeMultiHupperBlockEntity` used to
be the only variant whose `canExtract` gated on `isFilter == (slot == 5)`, so a *non-filtered*
multi-hupper refused to pull anything but the filter item out of a source's slot 5 (and refused the
filter item from every other slot) — dead code from an older filter design. Since the non-filtered
hoppers/huppers are deprecated, the multi-hupper now inherits the standard `passesExtractFilter`
(unfiltered → extract everything), matching the other five variants. Gametests stay green.

## The six variants and how they differ

| BE | pull dir | output | storage (no filter / filter) | multi-dir? |
|----|----------|--------|------------------------------|-----------|
| `XtremeHopperBlockEntity`      | DOWN | insert into facing container | 5 / 6 | no |
| `XtremeHupperBlockEntity`      | UP   | insert into facing container | 5 / 6 | no |
| `XtremeMultiHopperBlockEntity` | DOWN | insert, round-robin over connected sides | 5 / 6 | yes |
| `XtremeMultiHupperBlockEntity` | UP   | insert, round-robin over connected sides | 5 / 6 | yes |
| `GlazedHopperBlockEntity`      | DOWN | **drop** in front (no container needed) | 1 / 2 | no |
| `GlazedMultiHopperBlockEntity` | DOWN | **drop**, round-robin | 1 / 2 | yes |

Everything else — the vanilla-hopper-fork `tick`, `insert`/`extract`/`transfer`, `getInventoryAt`,
cooldown, `isFull`/`isEmpty`, filter handling (`withFilter`, filter-slot index), deprecated-block
NBT upgrade on load — is shared, modulo the class/type/screen-handler/block names.

### The axes of variation (all diff-verified against `XtremeHopperBlockEntity`)

1. **Pull direction (hopper=DOWN vs hupper=UP).** Affects: `SUCK_AABB` / `getSuckAabb()`, the pull
   `Direction` constant, `getInventoryAt` y offset (`+0.5` vs `-0.5`), `getLevelY()` (`±0.5`), and a
   `canBlockFromBelow()` override on the hupper.
2. **Output strategy (insert vs drop).** Insert path: `insertAndExtract` → `insert` into the facing
   `Container` (null-guarded, `isInventoryFull` check). Drop path: `dropAndExtract` → `drop` an item
   in front when the facing face isn't sturdy. This is the largest single divergence (~30–40 lines).
3. **Output targeting (single vs multi).** Multi variants add `north/south/east/west/downConnected`
   flags + a `getNextDirection()` round-robin (`lastDirection`) and persist it; the transfer loop
   asks `getNextDirection()` instead of the fixed `facing`.
4. **Storage size:** 5 (Xtreme) or 1 (Glazed), `+1` when `withFilter`.
5. **Identity:** BE type supplier, menu/screen-handler factory, block class, container display name.

## Proposed shape

`AbstractXtremeHopperBlockEntity extends RandomizableContainerBlockEntity` (keep the existing base)
holding **all** shared logic, parameterized by abstract hooks:

```java
protected abstract BlockEntityType<?> type();
protected abstract int storageSize();                 // 5 or 1
protected abstract Direction pullDirection();          // UP or DOWN
protected abstract AABB suckAabb();
protected abstract boolean canBlockFromBelow();        // default false
protected abstract AbstractContainerMenu createMenu(...); // per screen handler
// output strategy — one of two shared implementations, selected per variant:
protected abstract boolean pushOutput(Level, BlockPos, XtremeHopperBE); // insert OR drop
protected abstract Direction nextOutputDirection();    // fixed facing OR round-robin
```

Two small strategy helpers rather than booleans read best:
- **output**: `InsertOutput` (into facing container) vs `DropOutput` (item in front).
- **targeting**: `SingleFacing` vs `RoundRobinConnected` (owns the connected-side flags + `lastDirection`,
  including their NBT save/load).

The six concrete BEs then shrink to: constructor (type + size + filter), the four/five hook overrides,
and their identity constants. Estimated net deletion **~2,500 lines**.

Fold in while here (all currently duplicated 6×, minimal fixes already merged):
- **1.2** filter-slot index — pass it explicitly (already fixed per-copy; centralize).
- **2.11** `canExtract` `instanceof` guard (already fixed per-copy; centralize).
- **2.12** `isFull()` iterate `getContainerSize()` not the backing list (already fixed per-copy; centralize).

The block classes and the four screen handlers + four screens get the same treatment in a **deferred**
follow-up — see "Step 4 (deferred)" below. (They are *not* "diff-identical modulo names" the way the
BEs were: the hupper/multi-hupper blocks carry their own inverted geometry, so the collapse is a
plumbing dedup rather than a single base class.)

## Gametest gate (run before AND after — this is the safety net)

```
./gradlew :hopper-xtreme:fabric:runGameTest
```

Covers: `SixSlotTransferTest`, `TransferSpeedTest`, `PreventFilterExtractionTest`,
`DeprecatedBlockConversionTest`, `FilterSlotGeometryTest`, `ExtractGuardTest` — 19 tests total, all
green on `main`. They exercise transfer, speed/cooldown, filter geometry (1.2/1.3), the extract guard
(2.11), and deprecated-block NBT conversion — i.e. exactly the shared logic this refactor moves.

## Sequencing

1. Extract `AbstractXtremeHopperBlockEntity` + the two strategy pairs; migrate the two simple Xtreme
   variants (hopper/hupper) first; run gametests.
2. Migrate the two Glazed (drop) variants; run gametests.
3. Migrate the two Multi variants (round-robin targeting); run gametests.
4. Collapse the block classes, then the screen handlers/screens; run gametests. **Deferred** — see below.

Keep each step a separate commit so a regression bisects cleanly.

## Step 4 (deferred): block + screen collapse — how to do it

Steps 1–3 (the block-entity extraction) are done and merged on this branch. Step 4 was intentionally
left for a future refactor. This is the plan for when it's picked up.

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

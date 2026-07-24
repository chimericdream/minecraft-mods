# Hopper X-Treme 3.1 — block-entity base-class extraction (design / WIP)

Code-review plan item **3.1**: the six `*BlockEntity` classes in
`hopper-xtreme/common/.../entity/` (~3,700 lines) are ~80% identical. This doc captures the exact
variation surface (diff-verified) and the proposed `AbstractXtremeHopperBlockEntity` design, so the
refactor can be executed as a focused, gametest-gated pass.

**Status:** analysis complete, extraction NOT yet applied. This branch currently contains only this
plan. Do the extraction, then run the gametest gate (below) before/after.

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

The block classes (`XtremeHopperBlock`/`GlazedHopperBlock`/… diff-identical modulo names) and the four
screen handlers + four screens (differ only in slot-layout constants) get the same treatment in a
follow-up commit on this branch.

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
4. Collapse the block classes, then the screen handlers/screens; run gametests.

Keep each step a separate commit so a regression bisects cleanly.

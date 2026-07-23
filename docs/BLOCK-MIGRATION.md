# Non-breaking block/item migration (deprecation & rename)

How to deprecate or rename a block/item in these Architectury mods without breaking existing worlds and
without vanilla DataFixerUpper.

## Why not DataFixerUpper

DFU is not portable here: Fabric API ships no data-fixer registration API (migration there means mixins
on NBT/registry lookups), and NeoForge uses a separate path. So migrate **in-code in the `common`
module**, which runs on both loaders.

## Approach

1. **Keep the old block/item registered indefinitely** so old worlds and inventories still load. Just
   drop it from creative tabs (omit `arch$tab` in its item settings) and stop generating its recipes.
2. **Upgrade placed blocks on load** by deriving state from the block itself (not NBT), and **convert
   deprecated blocks on their first server tick** by swapping the blockstate to the canonical block via
   `level.setBlock(...)`.

## The block-entity trap (do not skip)

`level.setBlock` does **not** preserve the block entity — **even when the old and new block share the
same `BlockEntityType`**. Replacing the block installs a fresh empty BE, and the old block's removal
drops/clears its visible container slots. A GameTest proved this; do **not** assume `isValid(newState)`
keeps the BE.

To migrate a block-with-inventory in place, losing nothing and duplicating nothing:

1. **Snapshot** the full inventory into a `List<ItemStack>` of copies. Include any hidden trailing
   slots — e.g. hopper-xtreme's hidden filter slot at index `getContainerSize()`.
2. **Clear** the container first, so the block removal has nothing to drop (prevents duplication).
3. `level.setBlock(pos, newState, UPDATE_ALL)`.
4. **Re-attach the original BE object**: `clearRemoved()`, `setBlockState(newState)`,
   `level.setBlockEntity(be)` — this keeps custom name / cooldown / flags.
5. **Restore** the snapshot with `setItem`.

## Verify

Write a GameTest that asserts both:
- container contents are preserved after conversion, **and**
- nothing dropped: `assertItemEntityCountIs(item, pos, radius, 0)`.

## Reference implementation

Hopper X-Treme's deprecation, which made every hopper filter-capable and retired the `filtered_*`
blocks:
- `hopper-xtreme/common/.../block/HopperDeprecation.java` — the snapshot/clear/swap/re-attach/restore logic.
- `hopper-xtreme/common/.../block/ModBlocks.java` — the `getCanonicalForDeprecated` deprecated→canonical map.
- Per-loader tick wiring: `hopper-xtreme/neoforge/.../event/HopperXtremeEventHandler.java`,
  `hopper-xtreme/fabric/.../client/HopperXtremeFabricClient.java`.
- `hopper-xtreme/fabric/src/main/.../test/DeprecatedBlockConversionTest.java` — the conversion GameTest.

The scaffold (alias registration, canonical-map entry, per-loader tick hook, a conversion GameTest) is
mechanical and touches ~5 files across `common`/`fabric`/`neoforge`. The judgment is per-block: which BE
fields must survive the swap, and where hidden slots live.

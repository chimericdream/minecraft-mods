package com.chimericdream.camelnostrils.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Vanilla's {@code BlockEntityType#validBlocks} (see {@code BlockEntityType.BED}) is an immutable
 * {@code Set.of(...)} baked from the vanilla block list at class-init time. The upside-down beds
 * reuse vanilla's {@code BedBlockEntity} (via {@code BedBlock#newBlockEntity}), so they can never be
 * in that baked-in set, and {@code BlockEntityType#isValid} always returns false for them - block
 * placement crashes with "Invalid block entity ... state". {@code CN$BlockEntityTypeMixin} extends
 * {@code isValid} to also consult this side-table, which mod blocks register themselves into.
 */
public class ModBlockEntityValidBlocks {
    private static final Map<BlockEntityType<?>, Set<Supplier<Block>>> EXTRA_VALID_BLOCKS = new IdentityHashMap<>();

    public static void register(BlockEntityType<?> type, Supplier<Block> block) {
        EXTRA_VALID_BLOCKS.computeIfAbsent(type, ignored -> new HashSet<>()).add(block);
    }

    public static boolean isExtraValid(BlockEntityType<?> type, Block block) {
        Set<Supplier<Block>> extras = EXTRA_VALID_BLOCKS.get(type);
        if (extras == null) {
            return false;
        }

        for (Supplier<Block> supplier : extras) {
            if (supplier.get() == block) {
                return true;
            }
        }

        return false;
    }
}

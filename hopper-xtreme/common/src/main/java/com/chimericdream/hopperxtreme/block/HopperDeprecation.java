package com.chimericdream.hopperxtreme.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Handles the deprecation of the standalone {@code filtered_*} blocks. Every hopper is now
 * filter-capable, so the old {@code filtered_*} variants are redundant aliases kept only so that
 * existing worlds and inventories keep loading. Placed {@code filtered_*} blocks are converted to
 * their canonical (non-prefixed) equivalent on their first server tick.
 */
public final class HopperDeprecation {
    private static final String FILTERED_PREFIX = "filtered_";

    public static final String DEPRECATED_TOOLTIP_KEY = "item.hopperxtreme.deprecated.tooltip";

    private HopperDeprecation() {
    }

    public static boolean isDeprecated(String baseKey) {
        return baseKey.startsWith(FILTERED_PREFIX);
    }

    /**
     * @return {@code true} if the stack is one of the deprecated {@code filtered_*} hopper items,
     * which convert to their canonical equivalent when placed.
     */
    public static boolean isDeprecatedItem(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem
            && blockItem.getBlock() instanceof HopperVariantBlock variant
            && isDeprecated(variant.getBaseKey());
    }

    /**
     * If the block at {@code pos} is a deprecated {@code filtered_*} variant, replace it in place
     * with its canonical equivalent, preserving the block entity (inventory, installed filter, and
     * custom name) because both blocks share the same {@link net.minecraft.world.level.block.entity.BlockEntityType}.
     *
     * @return {@code true} if a conversion happened this tick.
     */
    public static boolean convertIfDeprecated(Level world, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof HopperVariantBlock variant) || !isDeprecated(variant.getBaseKey())) {
            return false;
        }

        Block canonical = ModBlocks.getCanonicalForDeprecated(variant.getBaseKey());
        if (canonical == null) {
            return false;
        }

        // Preserve the block entity across the swap. Two things are needed:
        //  1) Re-attach the original block entity (it carries the custom name, cooldown, and filter
        //     flag) since replacing the block installs a fresh, empty one. This is safe because the
        //     deprecated and canonical blocks share the same BlockEntityType.
        //  2) Snapshot and restore the inventory, because replacing the old block clears/drops its
        //     visible container slots before we re-attach it.
        BlockEntity blockEntity = world.getBlockEntity(pos);
        List<ItemStack> savedItems = snapshotInventory(blockEntity);

        // Empty the container before the swap so replacing the old block has nothing to drop (which
        // would otherwise duplicate the contents). We restore the snapshot onto the re-attached
        // block entity afterwards.
        clearInventory(blockEntity, savedItems.size());

        BlockState newState = copyProperties(state, canonical.defaultBlockState());
        world.setBlock(pos, newState, Block.UPDATE_ALL);

        if (blockEntity != null) {
            blockEntity.clearRemoved();
            blockEntity.setBlockState(newState);
            world.setBlockEntity(blockEntity);
            restoreInventory(blockEntity, savedItems);
        }

        return true;
    }

    private static List<ItemStack> snapshotInventory(BlockEntity blockEntity) {
        List<ItemStack> saved = new ArrayList<>();
        if (blockEntity instanceof Container container) {
            // getContainerSize() hides the trailing filter slot, so include one extra slot for it.
            int slots = container.getContainerSize() + 1;
            for (int i = 0; i < slots; i++) {
                saved.add(container.getItem(i).copy());
            }
        }

        return saved;
    }

    private static void clearInventory(BlockEntity blockEntity, int slots) {
        if (blockEntity instanceof Container container) {
            for (int i = 0; i < slots; i++) {
                container.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    private static void restoreInventory(BlockEntity blockEntity, List<ItemStack> savedItems) {
        if (blockEntity instanceof Container container) {
            for (int i = 0; i < savedItems.size(); i++) {
                container.setItem(i, savedItems.get(i));
            }
        }
    }

    private static BlockState copyProperties(BlockState from, BlockState to) {
        for (Property<?> property : from.getProperties()) {
            to = copyProperty(from, to, property);
        }

        return to;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, Property<T> property) {
        if (to.hasProperty(property)) {
            return to.setValue(property, from.getValue(property));
        }

        return to;
    }
}

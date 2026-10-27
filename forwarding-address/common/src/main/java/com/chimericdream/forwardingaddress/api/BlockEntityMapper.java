package com.chimericdream.forwardingaddress.api;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Moves block-entity data across when the old and new blocks use different block-entity types.
 */
@FunctionalInterface
public interface BlockEntityMapper {
    /**
     * @param oldData    the old block entity's saved data, including its metadata
     * @param target     the new block's block entity, freshly created and already placed
     * @param registries the registries to use when reading components from {@code oldData}
     */
    void map(CompoundTag oldData, BlockEntity target, HolderLookup.Provider registries);
}

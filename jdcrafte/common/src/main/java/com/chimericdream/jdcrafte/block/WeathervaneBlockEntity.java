package com.chimericdream.jdcrafte.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Carries no data of its own - the weathervane's only variable state is
 * {@link WeathervaneBlock#ROTATION}, which lives on the blockstate. This block entity exists purely so
 * {@code WeathervaneBlockEntityRenderer} has a per-instance hook to rotate the model at render time
 * (see {@link WeathervaneBlock#getRenderShape}).
 */
public class WeathervaneBlockEntity extends BlockEntity {
    public WeathervaneBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlocks.WEATHERVANE_BLOCK_ENTITY.get(), pos, state);
    }

    public WeathervaneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}

package com.chimericdream.camelnostrils.block.entity;

import com.chimericdream.camelnostrils.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class UpsideDownChestBlockEntity extends ChestBlockEntity {
    public UpsideDownChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.UPSIDE_DOWN_CHEST_BLOCK_ENTITY.get(), pos, state);
    }
}

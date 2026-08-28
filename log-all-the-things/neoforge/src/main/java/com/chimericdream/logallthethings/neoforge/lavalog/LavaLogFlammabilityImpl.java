package com.chimericdream.logallthethings.neoforge.lavalog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import com.chimericdream.logallthethings.lavalog.LavaLogFlammability;

public final class LavaLogFlammabilityImpl implements LavaLogFlammability.Provider {
    @Override
    public boolean isFlammable(BlockGetter level, BlockPos pos, BlockState state) {
        // Direction is irrelevant here: vanilla's ignite/burn odds are keyed purely by block, not by
        // which face fire would touch, so any fixed direction returns the same result.
        return state.isFlammable(level, pos, Direction.UP);
    }
}

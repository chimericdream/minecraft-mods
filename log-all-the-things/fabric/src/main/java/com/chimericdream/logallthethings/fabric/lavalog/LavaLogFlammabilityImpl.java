package com.chimericdream.logallthethings.fabric.lavalog;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import com.chimericdream.logallthethings.lavalog.LavaLogFlammability;

public final class LavaLogFlammabilityImpl implements LavaLogFlammability.Provider {
    @Override
    public boolean isFlammable(BlockGetter level, BlockPos pos, BlockState state) {
        FlammableBlockRegistry.Entry entry = FlammableBlockRegistry.getDefaultInstance().get(state.getBlock());
        return entry != null && entry.getIgniteOdds() > 0;
    }
}

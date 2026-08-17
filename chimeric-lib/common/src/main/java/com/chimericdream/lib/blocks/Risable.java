package com.chimericdream.lib.blocks;

import com.chimericdream.lib.entities.FallingUpwardBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The inverse of vanilla's {@code Fallable}: implemented by blocks that rise via
 * {@link FallingUpwardBlock} instead of falling like sand or gravel.
 */
public interface Risable {
    default void onLand(Level level, BlockPos pos, BlockState state, BlockState replacedBlock, FallingUpwardBlockEntity entity) {
    }

    default void onBrokenAfterRise(Level level, BlockPos pos, FallingUpwardBlockEntity entity) {
    }

    default DamageSource getRiseDamageSource(Entity entity) {
        return entity.damageSources().fallingBlock(entity);
    }
}

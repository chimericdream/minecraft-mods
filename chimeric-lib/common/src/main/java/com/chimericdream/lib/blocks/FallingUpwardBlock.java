package com.chimericdream.lib.blocks;

import com.chimericdream.lib.entities.FallingUpwardBlockEntity;
import com.chimericdream.lib.util.ChimericLibParticleUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * The inverse of vanilla's {@link FallingBlock}: instead of falling toward the void when the
 * block below it is free, it rises toward the sky when the block above it is free, and despawns
 * once it passes the top of the world instead of the bottom.
 * <p>
 * ChimericLib doesn't own a shared {@link EntityType} the way vanilla owns
 * {@code EntityTypes.FALLING_BLOCK} for every vanilla falling block, since entity types are
 * registered per-mod. Each consuming mod registers its own single {@code EntityType<FallingUpwardBlockEntity>}
 * (see {@code SimpleSeatEntity}/{@code Seats} for the same pattern) and returns it from
 * {@link #getFallingUpwardEntityType()} — every {@code FallingUpwardBlock} in that mod can share
 * the one entity type, since the block being carried is stored on the entity, not the type.
 */
public abstract class FallingUpwardBlock extends Block implements Risable {
    public FallingUpwardBlock(Properties properties) {
        super(properties);
    }

    protected abstract MapCodec<? extends FallingUpwardBlock> codec();

    protected abstract EntityType<? extends FallingUpwardBlockEntity> getFallingUpwardEntityType();

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, getDelayAfterPlace());
    }

    @Override
    protected BlockState updateShape(
        BlockState state,
        LevelReader level,
        ScheduledTickAccess ticks,
        BlockPos pos,
        Direction directionToNeighbour,
        BlockPos neighbourPos,
        BlockState neighbourState,
        RandomSource random
    ) {
        ticks.scheduleTick(pos, this, getDelayAfterPlace());
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (FallingBlock.isFree(level.getBlockState(pos.above())) && pos.getY() <= level.getMaxY()) {
            FallingUpwardBlockEntity entity = FallingUpwardBlockEntity.rise(getFallingUpwardEntityType(), level, pos, state);
            this.rising(entity);
        }
    }

    protected void rising(FallingUpwardBlockEntity entity) {
    }

    protected int getDelayAfterPlace() {
        return 2;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(16) == 0) {
            BlockPos above = pos.above();
            if (FallingBlock.isFree(level.getBlockState(above))) {
                ChimericLibParticleUtils.spawnParticleAbove(level, pos, random, new BlockParticleOption(ParticleTypes.FALLING_DUST, state));
            }
        }
    }

    public abstract int getDustColor(BlockState blockState, BlockGetter level, BlockPos pos);
}

package com.chimericdream.sponj.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

/**
 * Shared wet-sponge behavior. A wet sponj dries back out (turning into its dry block) when placed in
 * an environment that boils off its fluid, and drips particles while wet. The only per-fluid
 * variation is the dry block, the drip particle, and the "should dry out here" condition.
 */
public abstract class AbstractWetSponjBlock extends Block {
    protected AbstractWetSponjBlock(ResourceKey<Block> blockKey) {
        super(Properties.ofFullCopy(Blocks.SPONGE).setId(blockKey));
    }

    /** The dry block this sponj reverts to when it dries out. */
    protected abstract Block getDryBlock();

    /** The particle emitted while wet. */
    protected abstract ParticleOptions getDripParticle();

    /** Whether this sponj should dry out in the dimension it was just placed in. */
    protected abstract boolean shouldDryOut(Level world, BlockPos pos);

    @Override
    public void onPlace(@NonNull BlockState state, Level world, @NonNull BlockPos pos, @NonNull BlockState oldState, boolean notify) {
        if (shouldDryOut(world, pos)) {
            world.setBlock(pos, getDryBlock().defaultBlockState(), 3);
            world.levelEvent(2009, pos, 0);
            world.playSound((Player) null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, (1.0F + world.getRandom().nextFloat() * 0.2F) * 0.7F);
        }
    }

    @Override
    public void animateTick(@NonNull BlockState state, @NonNull Level world, @NonNull BlockPos pos, @NonNull RandomSource random) {
        Direction direction = Direction.getRandom(random);
        if (direction != Direction.UP) {
            BlockPos blockPos = pos.relative(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (!state.canOcclude() || !blockState.isFaceSturdy(world, blockPos, direction.getOpposite())) {
                double d = (double) pos.getX();
                double e = (double) pos.getY();
                double f = (double) pos.getZ();
                if (direction == Direction.DOWN) {
                    e -= 0.05;
                    d += random.nextDouble();
                    f += random.nextDouble();
                } else {
                    e += random.nextDouble() * 0.8;
                    if (direction.getAxis() == Direction.Axis.X) {
                        f += random.nextDouble();
                        if (direction == Direction.EAST) {
                            ++d;
                        } else {
                            d += 0.05;
                        }
                    } else {
                        d += random.nextDouble();
                        if (direction == Direction.SOUTH) {
                            ++f;
                        } else {
                            f += 0.05;
                        }
                    }
                }

                world.addAlwaysVisibleParticle(getDripParticle(), d, e, f, 0.0, 0.0, 0.0);
            }
        }
    }
}

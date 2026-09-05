package com.chimericdream.logallthethings.lavalog;

import java.util.Optional;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * Shared lava-logging logic used by every per-block mixin. Mirrors
 * {@link net.minecraft.world.level.block.SimpleWaterloggedBlock}'s water-flavored defaults, but for
 * {@link Fluids#LAVA}, plus the non-flammable and mutual-exclusion-with-water gates that make it safe.
 * Every lava-loggable block's {@code canPlaceLiquid}/{@code placeLiquid} override routes its LAVA
 * branch through {@link #canLavaLog}/{@link #placeLava} - the same two interface methods vanilla's
 * {@code FlowingFluid} spread/tick logic calls on any adjacent {@code LiquidBlockContainer} to
 * auto-fill it, exactly the way flowing water auto-waterlogs a fence it flows into. Freshly-placed
 * blocks are covered separately by {@link #tryLavaLogOnPlace}, called from each mixin's
 * {@code getStateForPlacement} injection.
 */
public final class LavaLogHelper {
    private LavaLogHelper() {
    }

    public static Pair<Boolean, BlockState> tryLavaLogOnPlace(BlockGetter level, BlockPos pos, BlockState state) {
        BlockState currentState = level.getBlockState(pos);
        if (currentState.getFluidState().isSourceOfType(Fluids.LAVA) && state.hasProperty(LavaLogProperties.LAVALOGGED)) {
            return Pair.of(true, state.setValue(LavaLogProperties.LAVALOGGED, true));
        }

        return Pair.of(false, state);
    }

    public static boolean canLavaLog(BlockGetter level, BlockPos pos, BlockState state) {
        return canLavaLog(level, pos, state, state);
    }

    /**
     * Same gating as {@link #canLavaLog(BlockGetter, BlockPos, BlockState)}, but with the flammability
     * check split onto a separate {@code flammabilityState} - {@code WindowedBlock} needs this, since
     * its own carrier state (the one that actually holds {@link LavaLogProperties#LAVALOGGED}) has no
     * {@code WATERLOGGED} property and isn't itself the block whose flammability matters; the real
     * answer comes from its block entity's host state instead. {@code carrierState} is checked with
     * {@code hasProperty} rather than an unconditional {@code getValue} for the same reason -
     * {@code WindowedBlock}'s state never has {@code WATERLOGGED} at all.
     */
    public static boolean canLavaLog(BlockGetter level, BlockPos pos, BlockState carrierState, BlockState flammabilityState) {
        if (carrierState.hasProperty(BlockStateProperties.WATERLOGGED) && carrierState.getValue(BlockStateProperties.WATERLOGGED)) {
            return false;
        }
        if (carrierState.getValue(LavaLogProperties.LAVALOGGED)) {
            return false;
        }

        return !LavaLogFlammability.isFlammable(level, pos, flammabilityState);
    }

    public static boolean placeLava(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.getValue(LavaLogProperties.LAVALOGGED) || !fluidState.isSourceOfType(Fluids.LAVA)) {
            return false;
        }

        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(LavaLogProperties.LAVALOGGED, true), 3);
            level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
        }

        return true;
    }

    public static ItemStack pickupLava(LevelAccessor level, BlockPos pos, BlockState state) {
        if (!state.getValue(LavaLogProperties.LAVALOGGED)) {
            return ItemStack.EMPTY;
        }

        level.setBlock(pos, state.setValue(LavaLogProperties.LAVALOGGED, false), 3);
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }

        return new ItemStack(Items.LAVA_BUCKET);
    }

    public static Optional<SoundEvent> getPickupSound() {
        return Fluids.LAVA.getPickupSound();
    }
}

package com.chimericdream.logallthethings.lavalog;

import java.util.Optional;

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
 * {@link Fluids#LAVA}, plus the non-flammable and mutual-exclusion-with-water gates that make it safe -
 * and, deliberately unlike water, only ever via an explicit lava bucket (see
 * {@link #explicitBucketAction}), never by real lava flowing into a container on its own.
 */
public final class LavaLogHelper {
    /**
     * Every lava-loggable block's {@code canPlaceLiquid}/{@code placeLiquid} override routes its LAVA
     * branch through {@link #canLavaLog}/{@link #placeLava} - the same two interface methods vanilla's
     * {@code FlowingFluid} spread/tick logic calls on any adjacent {@code LiquidBlockContainer} to
     * auto-fill it, exactly the way flowing water auto-waterlogs a fence it flows into. Unlike water,
     * lava-logging is meant to be bucket-only (deliberately not mirroring that part of water's behavior),
     * so both methods refuse unless this flag says the call is happening inside an explicit bucket
     * action - set by {@code LATT$BucketItemMixin} around {@code BucketItem#use}, which covers a player
     * emptying a lava bucket (both loaders) but not a dispenser doing the same (a narrower, accepted gap
     * rather than chasing NeoForge's differently-patched {@code emptyContents} overload).
     */
    private static boolean explicitBucketAction = false;

    private LavaLogHelper() {
    }

    public static void beginExplicitBucketAction() {
        explicitBucketAction = true;
    }

    public static void endExplicitBucketAction() {
        explicitBucketAction = false;
    }

    public static boolean canLavaLog(BlockGetter level, BlockPos pos, BlockState state) {
        if (!explicitBucketAction) {
            return false;
        }
        if (state.getValue(BlockStateProperties.WATERLOGGED) || state.getValue(LavaLogProperties.LAVALOGGED)) {
            return false;
        }

        return !LavaLogFlammability.isFlammable(level, pos, state);
    }

    public static boolean placeLava(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!explicitBucketAction || state.getValue(LavaLogProperties.LAVALOGGED) || !fluidState.is(Fluids.LAVA)) {
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

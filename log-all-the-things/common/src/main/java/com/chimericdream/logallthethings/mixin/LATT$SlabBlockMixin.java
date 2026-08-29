package com.chimericdream.logallthethings.mixin;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.chimericdream.logallthethings.lavalog.LavaLogHelper;
import com.chimericdream.logallthethings.lavalog.LavaLogProperties;

/**
 * Unlike the other lava-loggable targets, {@link SlabBlock} already overrides
 * {@code canPlaceLiquid}/{@code placeLiquid} itself (to refuse double slabs), so those two are
 * {@code @Inject}ed rather than declared as plain interface overrides — declaring a same-signature
 * method here would conflict with SlabBlock's own body instead of extending it. It does not override
 * {@code pickupBlock}/{@code getPickupSound}, so those stay plain overrides like every other target.
 */
@Mixin(SlabBlock.class)
public abstract class LATT$SlabBlockMixin implements SimpleWaterloggedBlock {
    @Unique
    private boolean latt$lastPickupWasLava = false;

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void latt$addLavaLoggedProperty(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(LavaLogProperties.LAVALOGGED);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void latt$setDefaultLavaLogged(Properties properties, CallbackInfo ci) {
        Block self = (Block) (Object) this;
        self.registerDefaultState(self.defaultBlockState().setValue(LavaLogProperties.LAVALOGGED, false));
    }

    @Inject(method = "canPlaceLiquid", at = @At("HEAD"), cancellable = true)
    private void latt$canPlaceLiquid(
        @Nullable LivingEntity user, BlockGetter level, BlockPos pos, BlockState state, Fluid type, CallbackInfoReturnable<Boolean> cir
    ) {
        if (type == Fluids.LAVA) {
            cir.setReturnValue(state.getValue(SlabBlock.TYPE) != SlabType.DOUBLE && LavaLogHelper.canLavaLog(level, pos, state));
        } else if (state.getValue(LavaLogProperties.LAVALOGGED)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "placeLiquid", at = @At("HEAD"), cancellable = true)
    private void latt$placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
        if (fluidState.is(Fluids.LAVA)) {
            cir.setReturnValue(state.getValue(SlabBlock.TYPE) != SlabType.DOUBLE && LavaLogHelper.placeLava(level, pos, state, fluidState));
        } else if (state.getValue(LavaLogProperties.LAVALOGGED)) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public @NonNull ItemStack pickupBlock(@Nullable LivingEntity user, @NonNull LevelAccessor level, @NonNull BlockPos pos, BlockState state) {
        latt$lastPickupWasLava = state.getValue(LavaLogProperties.LAVALOGGED);
        if (latt$lastPickupWasLava) {
            return LavaLogHelper.pickupLava(level, pos, state);
        }

        return SimpleWaterloggedBlock.super.pickupBlock(user, level, pos, state);
    }

    @Override
    public @NonNull Optional<SoundEvent> getPickupSound() {
        return latt$lastPickupWasLava ? LavaLogHelper.getPickupSound() : SimpleWaterloggedBlock.super.getPickupSound();
    }

    @Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
    private void latt$getFluidState(BlockState state, CallbackInfoReturnable<FluidState> cir) {
        if (state.getValue(LavaLogProperties.LAVALOGGED)) {
            cir.setReturnValue(Fluids.LAVA.getSource(false));
        }
    }

    @Inject(method = "updateShape", at = @At("HEAD"))
    private void latt$scheduleLavaTick(
        BlockState state,
        LevelReader level,
        ScheduledTickAccess ticks,
        BlockPos pos,
        Direction directionToNeighbour,
        BlockPos neighbourPos,
        BlockState neighbourState,
        RandomSource random,
        CallbackInfoReturnable<BlockState> cir
    ) {
        if (state.getValue(LavaLogProperties.LAVALOGGED)) {
            ticks.scheduleTick(pos, Fluids.LAVA, Fluids.LAVA.getTickDelay(level));
        }
    }
}

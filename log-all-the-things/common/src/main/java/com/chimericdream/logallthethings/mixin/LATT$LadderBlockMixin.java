package com.chimericdream.logallthethings.mixin;

import java.util.Optional;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.chimericdream.logallthethings.lavalog.LavaLogHelper;
import com.chimericdream.logallthethings.lavalog.LavaLogProperties;

/**
 * Vanilla's only ladder ({@code Blocks.LADDER}) is wood, which fails the non-flammable gate, so no
 * vanilla ladder can actually be lava-logged today — this mixin just makes it possible on the class,
 * for modded non-flammable ladders reusing {@code LadderBlock}.
 */
@Mixin(LadderBlock.class)
public abstract class LATT$LadderBlockMixin implements SimpleWaterloggedBlock {
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

    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity user, BlockGetter level, BlockPos pos, BlockState state, Fluid type) {
        if (type == Fluids.LAVA) {
            return LavaLogHelper.canLavaLog(level, pos, state);
        }

        return !state.getValue(LavaLogProperties.LAVALOGGED) && SimpleWaterloggedBlock.super.canPlaceLiquid(user, level, pos, state, type);
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (fluidState.is(Fluids.LAVA)) {
            return LavaLogHelper.placeLava(level, pos, state, fluidState);
        }

        return !state.getValue(LavaLogProperties.LAVALOGGED) && SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluidState);
    }

    @Override
    public ItemStack pickupBlock(@Nullable LivingEntity user, LevelAccessor level, BlockPos pos, BlockState state) {
        latt$lastPickupWasLava = state.getValue(LavaLogProperties.LAVALOGGED);
        if (latt$lastPickupWasLava) {
            return LavaLogHelper.pickupLava(level, pos, state);
        }

        return SimpleWaterloggedBlock.super.pickupBlock(user, level, pos, state);
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
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

    /** Automatically lava-logs a freshly-placed block when it's being placed into a lava source. */
    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void latt$tryLavaLogOnPlace(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState result = cir.getReturnValue();
        if (result == null) {
            return;
        }

        Pair<Boolean, BlockState> lavaLogResult = LavaLogHelper.tryLavaLogOnPlace(context.getLevel(), context.getClickedPos(), result);
        if (lavaLogResult.getFirst()) {
            cir.setReturnValue(lavaLogResult.getSecond());
        }
    }
}

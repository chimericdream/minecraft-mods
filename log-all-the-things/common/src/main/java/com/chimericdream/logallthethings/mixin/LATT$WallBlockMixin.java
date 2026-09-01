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
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WallBlock;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.chimericdream.logallthethings.carpetlog.CarpetLogHelper;
import com.chimericdream.logallthethings.lavalog.LavaLogHelper;
import com.chimericdream.logallthethings.lavalog.LavaLogProperties;

@Mixin(WallBlock.class)
public abstract class LATT$WallBlockMixin implements SimpleWaterloggedBlock {
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

    /**
     * Substitutes a carpet-logged neighbour's stored host state in place of its real (connection-
     * property-less) {@code CarpetedBlock} carrier state before vanilla's own {@code connectsTo} check
     * runs, so a real wall connects to a carpet-logged wall/fence/bars exactly as it would to the live
     * block. {@code WallBlock#updateShape} uses only this parameter (never re-queries the level for the
     * neighbour), so substituting it here is enough - see
     * {@link CarpetLogHelper#effectiveNeighborState}. {@code ordinal = 1} picks the second
     * {@code BlockState}-typed argument ({@code neighbourState}), since {@code state} (the block's own
     * state) is ordinal 0.
     */
    @ModifyVariable(method = "updateShape", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private BlockState latt$substituteCarpetedNeighbor(
        BlockState neighbourState,
        BlockState state,
        LevelReader level,
        ScheduledTickAccess ticks,
        BlockPos pos,
        Direction directionToNeighbour,
        BlockPos neighbourPos
    ) {
        return CarpetLogHelper.effectiveNeighborState(level, neighbourPos, neighbourState);
    }

    /**
     * Same substitution as {@link #latt$substituteCarpetedNeighbor}, for the five
     * {@code level.getBlockState(...)} calls {@code WallBlock#getStateForPlacement} makes directly (one
     * per horizontal neighbour, plus the block above) instead of receiving them as parameters - lets a
     * wall placed directly against a carpet-logged wall/fence/bars connect to it immediately, not just
     * after a later neighbour update. Unlike {@code FenceBlock}/{@code IronBarsBlock}, {@code WallBlock}
     * declares its local {@code level} variable as {@code LevelReader} rather than {@code BlockGetter},
     * so the compiled call site targets {@code LevelReader#getBlockState} instead.
     */
    @Redirect(
        method = "getStateForPlacement",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelReader;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState latt$substituteCarpetedNeighborOnPlace(LevelReader level, BlockPos pos) {
        return CarpetLogHelper.effectiveNeighborState(level, pos, level.getBlockState(pos));
    }
}

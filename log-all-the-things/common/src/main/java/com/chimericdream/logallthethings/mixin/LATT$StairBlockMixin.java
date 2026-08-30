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
import net.minecraft.world.level.block.StairBlock;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.chimericdream.logallthethings.lavalog.LavaLogHelper;
import com.chimericdream.logallthethings.lavalog.LavaLogProperties;
import com.chimericdream.logallthethings.windowlog.WindowedBlock;
import com.chimericdream.logallthethings.windowlog.WindowedBlockEntity;

/**
 * Adds a {@code lavalogged} blockstate property to stairs, mirroring vanilla waterlogging but for
 * lava. {@code registerDefaultState} is widened to public for this in
 * {@code logallthethings.accesswidener} (same approach as {@code effective-gear}'s
 * {@code EG$LeavesBlockMixin}).
 */
@Mixin(StairBlock.class)
public abstract class LATT$StairBlockMixin implements SimpleWaterloggedBlock {
    @Unique
    private boolean latt$lastPickupWasLava = false;

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void latt$addLavaLoggedProperty(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(LavaLogProperties.LAVALOGGED);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void latt$setDefaultLavaLogged(BlockState baseState, Properties properties, CallbackInfo ci) {
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

    // BucketPickup#getPickupSound() takes no BlockState, so it can't tell water/lava pickup apart on
    // its own; latt$lastPickupWasLava carries that from the pickupBlock() call immediately before it
    // in BucketItem#use()'s synchronous pickup path.
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

    /**
     * A window-logged stair neighbour is always a {@link WindowedBlock} instance, not a
     * {@code StairBlock}, so vanilla's own {@code isStairs}/{@code FACING} corner-shape checks inside
     * the private {@code getStairsShape}/{@code canTakeShape} helpers never recognise it as a stair to
     * connect to - a plain stair placed next to one stays permanently {@code STRAIGHT} instead of
     * forming the normal inner/outer corner. Substituting the window-logged neighbour's host state
     * (only when that host actually is a stair) at every {@code getBlockState} call site inside those
     * two helpers - the same "see through WindowedBlock to its host" trick
     * {@code LATT$FireBlockMixin} already uses for flammability - lets the corner form normally. This
     * can never force the window-logged neighbour itself to change shape: it has no {@code SHAPE}
     * property of its own and isn't a {@code StairBlock} instance, so only the plain stair on the other
     * side of the connection is ever the one whose state gets updated.
     */
    @Unique
    private static BlockState latt$effectiveStairNeighborState(BlockGetter level, BlockPos pos) {
        BlockState real = level.getBlockState(pos);
        if (real.getBlock() instanceof WindowedBlock && level.getBlockEntity(pos) instanceof WindowedBlockEntity be) {
            BlockState host = be.getHostState();
            if (host.getBlock() instanceof StairBlock) {
                return host;
            }
        }

        return real;
    }

    @Redirect(
        method = "getStairsShape",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private static BlockState latt$getStairsShapeSeesWindowLoggedHost(BlockGetter level, BlockPos pos) {
        return latt$effectiveStairNeighborState(level, pos);
    }

    @Redirect(
        method = "canTakeShape",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private static BlockState latt$canTakeShapeSeesWindowLoggedHost(BlockGetter level, BlockPos pos) {
        return latt$effectiveStairNeighborState(level, pos);
    }
}

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
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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
import com.chimericdream.logallthethings.windowlog.WindowLogHelper;

/**
 * Covers both iron bars and glass panes: {@code IronBarsBlock} is the vanilla class used for both.
 */
@Mixin(IronBarsBlock.class)
public abstract class LATT$IronBarsBlockMixin implements SimpleWaterloggedBlock {
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

    // IronBarsBlock doesn't declare its own getFluidState() (it's inherited from the abstract
    // CrossCollisionBlock, which DOES declare it) — see LATT$CrossCollisionBlockMixin instead, which
    // covers both FenceBlock and IronBarsBlock for this one method.

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
     * A window-logged neighbour's exposed {@code WindowedBlock} state has no properties of its own and a
     * notch-shaped, non-full-face collision shape, so vanilla's own {@code attachsTo} check (not full,
     * not an {@code IronBarsBlock}, not a wall) never connects to it — even when the pane/bars embedded
     * inside it is aligned to face this exact direction. Overriding just that one direction's property
     * after vanilla computes the rest keeps every other neighbour's normal connection logic untouched.
     */
    @Inject(method = "updateShape", at = @At("RETURN"), cancellable = true)
    private void latt$connectToWindowLoggedNeighbor(
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
        if (!directionToNeighbour.getAxis().isHorizontal()) {
            return;
        }

        BooleanProperty property = CrossCollisionBlock.PROPERTY_BY_DIRECTION.get(directionToNeighbour);
        BlockState result = cir.getReturnValue();
        if (property == null || result == null || !result.hasProperty(property) || result.getValue(property)) {
            return;
        }

        if (WindowLogHelper.hasAlignedWindow(level, neighbourPos, directionToNeighbour.getOpposite())) {
            cir.setReturnValue(result.setValue(property, true));
        }
    }

    /** Same connection as {@link #latt$connectToWindowLoggedNeighbor}, for freshly-placed panes/bars. */
    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void latt$connectToWindowLoggedNeighborsOnPlace(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState result = cir.getReturnValue();
        if (result == null) {
            return;
        }

        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        boolean changed = false;

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BooleanProperty property = CrossCollisionBlock.PROPERTY_BY_DIRECTION.get(direction);
            if (property == null || !result.hasProperty(property) || result.getValue(property)) {
                continue;
            }

            if (WindowLogHelper.hasAlignedWindow(level, pos.relative(direction), direction.getOpposite())) {
                result = result.setValue(property, true);
                changed = true;
            }
        }

        Pair<Boolean, BlockState> lavaLogResult = LavaLogHelper.tryLavaLogOnPlace(level, pos, result);

        if (changed || lavaLogResult.getFirst()) {
            cir.setReturnValue(lavaLogResult.getSecond());
        }
    }
}

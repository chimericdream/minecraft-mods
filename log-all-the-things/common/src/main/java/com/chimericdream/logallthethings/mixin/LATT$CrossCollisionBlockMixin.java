package com.chimericdream.logallthethings.mixin;

import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.chimericdream.logallthethings.lavalog.LavaLogProperties;

/**
 * {@code getFluidState} is declared once on the abstract {@link CrossCollisionBlock} and inherited
 * unchanged by both {@code FenceBlock} and {@code IronBarsBlock} (neither overrides it), so one
 * {@code @Inject} here covers both — unlike {@code createBlockStateDefinition}, the constructor, and
 * {@code updateShape}, which each concrete subclass declares (and thus mixes into) separately.
 */
@Mixin(CrossCollisionBlock.class)
public abstract class LATT$CrossCollisionBlockMixin {
    @Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
    private void latt$getFluidState(BlockState state, CallbackInfoReturnable<FluidState> cir) {
        if (state.getValue(LavaLogProperties.LAVALOGGED)) {
            cir.setReturnValue(Fluids.LAVA.getSource(false));
        }
    }
}

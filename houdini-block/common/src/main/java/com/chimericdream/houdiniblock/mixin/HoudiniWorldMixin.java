package com.chimericdream.houdiniblock.mixin;

import com.chimericdream.houdiniblock.mixinlogic.HoudiniWorldMixinLogic;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
abstract public class HoudiniWorldMixin {
    @Inject(
        method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;scheduleBlockRerenderIfNeeded(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private void houdini$preventBlockUpdates(
        BlockPos pos,
        BlockState newState,
        int flags,
        int maxUpdateDepth,
        CallbackInfoReturnable<Boolean> cir,
        @Local(ordinal = 0) Block newBlock,
        @Local(ordinal = 1) BlockState previousState
    ) {
        if (HoudiniWorldMixinLogic.preventBlockUpdates(previousState, newState, newBlock)) {
            cir.setReturnValue(false);
        }
    }
}

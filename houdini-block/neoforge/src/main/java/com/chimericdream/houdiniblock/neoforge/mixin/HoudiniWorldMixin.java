package com.chimericdream.houdiniblock.neoforge.mixin;

import com.chimericdream.houdiniblock.mixinlogic.HoudiniWorldMixinLogic;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
abstract public class HoudiniWorldMixin {
    @Shadow
    abstract public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState oldState, BlockState newState);

    @Inject(
        method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;markAndNotifyBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;II)V"
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
            this.scheduleBlockRerenderIfNeeded(pos, newState, previousState);
            cir.setReturnValue(false);
        }
    }

    @Inject(
        method = "markAndNotifyBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;II)V",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private void houdini$preventBlockUpdates2(
        BlockPos pos,
        @Nullable WorldChunk chunk,
        BlockState previousState,
        BlockState newState,
        int flags,
        int maxUpdateDepth,
        CallbackInfo ci
    ) {
        Block newBlock = newState.getBlock();

        if (HoudiniWorldMixinLogic.preventBlockUpdates(previousState, newState, newBlock)) {
            ci.cancel();
        }
    }
}

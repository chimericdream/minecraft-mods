package com.chimericdream.houdiniblock.neoforge.mixin;

import com.chimericdream.houdiniblock.mixinlogic.HoudiniWorldMixinLogic;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
abstract public class HoudiniWorldMixin {
    @Shadow
    abstract public void setBlocksDirty(BlockPos pos, BlockState oldState, BlockState newState);

    @Inject(
        method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;markAndNotifyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;II)V"
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
            this.setBlocksDirty(pos, newState, previousState);
            cir.setReturnValue(false);
        }
    }

    @Inject(
        method = "markAndNotifyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;II)V",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private void houdini$preventBlockUpdates2(
        BlockPos pos,
        @Nullable LevelChunk chunk,
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

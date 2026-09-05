package com.chimericdream.flatbedrock.mixin;

import com.chimericdream.flatbedrock.FlatBedrockContext;
import com.chimericdream.flatbedrock.config.FlatBedrockConfig;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// SurfaceRules$StateRule backs every SurfaceRules.state(...) result, not just bedrock's, so this
// only acts when the shared bedrock instance (SurfaceRuleData.BEDROCK) is the one being placed.
@Mixin(targets = "net/minecraft/world/level/levelgen/SurfaceRules$StateRule")
abstract public class BedrockStateRuleMixin {
    @Shadow
    @Final
    private BlockState state;

    @Inject(method = "tryApply", at = @At("HEAD"), cancellable = true)
    private void fb$replaceBedrock(int blockX, int blockY, int blockZ, CallbackInfoReturnable<BlockState> cir) {
        if (state.is(Blocks.BEDROCK)) {
            BlockState replacement = FlatBedrockConfig.resolveReplacementBlockState(FlatBedrockContext.get());
            if (replacement != state) {
                cir.setReturnValue(replacement);
            }
        }
    }
}

package com.chimericdream.flatbedrock.mixin;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/world/level/levelgen/SurfaceRules$VerticalGradientConditionSource")
abstract public class FlatBedrockMixin {
    @Shadow
    @Final
    private Identifier randomName;
    @Unique
    private static final Identifier fb$bedrockFloor = Identifier.withDefaultNamespace("bedrock_floor");
    @Unique
    private static final Identifier fb$bedrockRoof = Identifier.withDefaultNamespace("bedrock_roof");
    @Unique
    private static final VerticalAnchor fb$aboveBottom = VerticalAnchor.aboveBottom(1);
    @Unique
    private static final VerticalAnchor fb$belowTop = VerticalAnchor.belowTop(1);

    @Inject(method = "falseAtAndAbove", at = @At("HEAD"), cancellable = true)
    private void falseAtAndAbove(CallbackInfoReturnable<VerticalAnchor> cir) {
        if(randomName.equals(fb$bedrockFloor)) {
            cir.setReturnValue(fb$aboveBottom);
        }
    }

    @Inject(method = "trueAtAndBelow", at = @At("HEAD"), cancellable = true)
    private void trueAtAndBelow(CallbackInfoReturnable<VerticalAnchor> cir) {
        if(randomName.equals(fb$bedrockRoof)) {
            cir.setReturnValue(fb$belowTop);
        }
    }
}

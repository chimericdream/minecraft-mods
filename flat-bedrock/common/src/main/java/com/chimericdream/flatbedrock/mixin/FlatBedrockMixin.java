package com.chimericdream.flatbedrock.mixin;

import com.chimericdream.flatbedrock.FlatBedrockContext;
import com.chimericdream.flatbedrock.config.FlatBedrockConfig;
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

    // Vanilla's gradient is probabilistic between trueAtAndBelow and falseAtAndAbove. Pinning both
    // bounds to the same T-block window (instead of leaving one at its vanilla default) removes that
    // randomness entirely and gives an exact, deterministic thickness - including 0, which disables
    // the layer altogether (used for the nether's "no roof" option).
    @Inject(method = "falseAtAndAbove", at = @At("HEAD"), cancellable = true)
    private void fb$falseAtAndAbove(CallbackInfoReturnable<VerticalAnchor> cir) {
        if (randomName.equals(fb$bedrockFloor)) {
            int thickness = FlatBedrockConfig.resolveFloorThickness(FlatBedrockContext.get());
            cir.setReturnValue(VerticalAnchor.aboveBottom(thickness));
        } else if (randomName.equals(fb$bedrockRoof)) {
            int thickness = FlatBedrockConfig.resolveRoofThickness();
            cir.setReturnValue(VerticalAnchor.belowTop(thickness - 1));
        }
    }

    @Inject(method = "trueAtAndBelow", at = @At("HEAD"), cancellable = true)
    private void fb$trueAtAndBelow(CallbackInfoReturnable<VerticalAnchor> cir) {
        if (randomName.equals(fb$bedrockFloor)) {
            int thickness = FlatBedrockConfig.resolveFloorThickness(FlatBedrockContext.get());
            cir.setReturnValue(VerticalAnchor.aboveBottom(thickness - 1));
        } else if (randomName.equals(fb$bedrockRoof)) {
            int thickness = FlatBedrockConfig.resolveRoofThickness();
            cir.setReturnValue(VerticalAnchor.belowTop(thickness));
        }
    }
}

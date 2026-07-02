package com.chimericdream.flatbedrock.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/world/gen/surfacebuilder/MaterialRules$VerticalGradientMaterialCondition")
abstract public class FlatBedrockMixin {
    @Shadow
    @Final
    private ResourceLocation randomName;
    private static final ResourceLocation bedrockFloor = ResourceLocation.withDefaultNamespace("bedrock_floor");
    private static final ResourceLocation bedrockRoof = ResourceLocation.withDefaultNamespace("bedrock_roof");
    private static final VerticalAnchor aboveBottom = VerticalAnchor.aboveBottom(1);
    private static final VerticalAnchor belowTop = VerticalAnchor.belowTop(1);

    @Inject(method = "falseAtAndAbove", at = @At("HEAD"), cancellable = true)
    private void falseAtAndAbove(CallbackInfoReturnable<VerticalAnchor> cir) {
        if(randomName.equals(bedrockFloor)) {
            cir.setReturnValue(aboveBottom);
        }
    }

    @Inject(method = "trueAtAndBelow", at = @At("HEAD"), cancellable = true)
    private void trueAtAndBelow(CallbackInfoReturnable<VerticalAnchor> cir) {
        if(randomName.equals(bedrockRoof)) {
            cir.setReturnValue(belowTop);
        }
    }

//    @Redirect(
//        method = "createDefaultRule(ZZZ)Lnet/minecraft/world/gen/surfacebuilder/MaterialRules$MaterialRule;",
//        at = @At(
//            value = "INVOKE",
//            target = "net/minecraft/world/gen/surfacebuilder/MaterialRules.verticalGradient (Ljava/lang/String;Lnet/minecraft/world/gen/YOffset;Lnet/minecraft/world/gen/YOffset;)Lnet/minecraft/world/gen/surfacebuilder/MaterialRules$MaterialCondition;",
//            ordinal = 0
//        )
//    )
//    private static MaterialRules.MaterialCondition flattenBedrockRoof(String id, YOffset oldOffset, YOffset roofLayer) {
//        return MaterialRules.verticalGradient(id, roofLayer, roofLayer);
//    }
//
//    @Redirect(
//        method = "createDefaultRule(ZZZ)Lnet/minecraft/world/gen/surfacebuilder/MaterialRules$MaterialRule;",
//        at = @At(
//            value = "INVOKE",
//            target = "net/minecraft/world/gen/surfacebuilder/MaterialRules.verticalGradient (Ljava/lang/String;Lnet/minecraft/world/gen/YOffset;Lnet/minecraft/world/gen/YOffset;)Lnet/minecraft/world/gen/surfacebuilder/MaterialRules$MaterialCondition;",
//            ordinal = 1
//        )
//    )
//    private static MaterialRules.MaterialCondition flattenBedrockFloor(String id, YOffset floorLayer, YOffset oldOffset) {
//        return MaterialRules.verticalGradient(id, floorLayer, floorLayer);
//    }
//
//    @Redirect(
//        method = "createNetherSurfaceRule()Lnet/minecraft/world/gen/surfacebuilder/MaterialRules$MaterialRule;",
//        at = @At(
//            value = "INVOKE",
//            target = "net/minecraft/world/gen/surfacebuilder/MaterialRules.verticalGradient (Ljava/lang/String;Lnet/minecraft/world/gen/YOffset;Lnet/minecraft/world/gen/YOffset;)Lnet/minecraft/world/gen/surfacebuilder/MaterialRules$MaterialCondition;",
//            ordinal = 0
//        )
//    )
//    private static MaterialRules.MaterialCondition flattenNetherFloor(String id, YOffset floorLayer, YOffset oldOffset) {
//        return MaterialRules.verticalGradient(id, floorLayer, floorLayer);
//    }
//
//    @Redirect(
//        method = "createNetherSurfaceRule()Lnet/minecraft/world/gen/surfacebuilder/MaterialRules$MaterialRule;",
//        at = @At(
//            value = "INVOKE",
//            target = "net/minecraft/world/gen/surfacebuilder/MaterialRules.verticalGradient (Ljava/lang/String;Lnet/minecraft/world/gen/YOffset;Lnet/minecraft/world/gen/YOffset;)Lnet/minecraft/world/gen/surfacebuilder/MaterialRules$MaterialCondition;",
//            ordinal = 1
//        )
//    )
//    private static MaterialRules.MaterialCondition flattenNetherRoof(String id, YOffset oldOffset, YOffset roofLayer) {
//        return MaterialRules.verticalGradient(id, YOffset.belowTop(1), roofLayer);
//    }
}

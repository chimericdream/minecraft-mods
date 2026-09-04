package com.chimericdream.camelnostrils.mixin;

import com.chimericdream.camelnostrils.client.model.CN$CamelModels;
import com.chimericdream.camelnostrils.client.render.entity.state.CN$CamelRenderStateAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.CamelRenderer;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * AgeableMobRenderer#submit() unconditionally resets {@code this.model} to its adult/baby model every
 * call before delegating to MobRenderer#submit() - the same swap point vanilla itself uses to switch
 * between the two. Hooking in right before that delegating call lets a no-snout camel's model swap
 * happen after vanilla's own reset, without having to reimplement submit() from scratch.
 *
 * {@code model} is declared on the grandparent LivingEntityRenderer, not on AgeableMobRenderer
 * itself, so a plain {@code @Shadow} here can't locate it (Mixin only shadows fields declared
 * directly on the mixin's target class) - {@link CN$LivingEntityRendererAccessor} sets it instead.
 */
@SuppressWarnings("deprecation")
@Mixin(AgeableMobRenderer.class)
public abstract class CN$AgeableMobRendererMixin {
    @Inject(
        method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/MobRenderer;submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"
        )
    )
    private void cn$swapSnoutlessCamelModel(
        final LivingEntityRenderState state,
        final PoseStack poseStack,
        final SubmitNodeCollector submitNodeCollector,
        final CameraRenderState camera,
        final CallbackInfo ci
    ) {
        //noinspection ConstantValue
        if (
            (Object) this instanceof CamelRenderer
                && state instanceof CamelRenderState camelState
                && !((CN$CamelRenderStateAccessor) camelState).cn$hasSnout()
        ) {
            EntityModel<?> noSnoutModel = state.isBaby ? CN$CamelModels.noSnoutBabyModel() : CN$CamelModels.noSnoutAdultModel();
            ((CN$LivingEntityRendererAccessor) this).cn$setModel(noSnoutModel);
        }
    }
}

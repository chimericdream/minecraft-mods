package com.chimericdream.bannertweaks.mixin;

import com.chimericdream.bannertweaks.client.render.block.entity.state.BannerBlockEntityRenderStateAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BannerRenderer.class)
abstract public class BannerBlockEntityRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/level/block/entity/BannerBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/BannerRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", at = @At("TAIL"))
    public void bt$updateRenderState(BannerBlockEntity entity, BannerRenderState renderState, float f, Vec3 vec3d, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlayCommand, CallbackInfo ci) {
        ((BannerBlockEntityRenderStateAccessor) renderState).bt$setCustomName(entity.getCustomName());
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/BannerRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("TAIL"))
    private void bt$renderBannerName(BannerRenderState renderState, PoseStack matrices, SubmitNodeCollector orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        bt$renderLabelIfPresent(renderState, matrices, orderedRenderCommandQueue, cameraRenderState, renderState.lightCoords);
    }

    @Unique
    protected void bt$renderLabelIfPresent(BannerRenderState renderState, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraRenderState, int light) {
        double squaredDistanceToCamera = cameraRenderState.blockPos.distToCenterSqr(cameraRenderState.pos);

        if (squaredDistanceToCamera > 1024.0) {
            return;
        }

        Component text = ((BannerBlockEntityRenderStateAccessor) renderState).bt$getCustomName();

        if (Minecraft.getInstance().gui.hud.isHidden() || text == null) {
            return;
        }

        float verticalOffset = 0f;
        if (renderState.attachmentType == BannerBlock.AttachmentType.WALL) {
            verticalOffset = -1f;
        }

        Vec3 nameLabelPos = new Vec3(0.5f, 1.625f + verticalOffset, 0.5f);
        queue.submitNameTag(matrices, nameLabelPos, 0, text, true, light, cameraRenderState);
    }
}

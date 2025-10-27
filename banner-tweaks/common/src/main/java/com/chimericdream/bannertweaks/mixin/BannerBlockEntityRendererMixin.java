package com.chimericdream.bannertweaks.mixin;

import com.chimericdream.bannertweaks.client.render.block.entity.state.BannerBlockEntityRenderStateAccessor;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BannerBlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BannerBlockEntityRenderer.class)
abstract public class BannerBlockEntityRendererMixin {
    @Inject(method = "updateRenderState(Lnet/minecraft/block/entity/BannerBlockEntity;Lnet/minecraft/client/render/block/entity/state/BannerBlockEntityRenderState;FLnet/minecraft/util/math/Vec3d;Lnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V", at = @At("TAIL"))
    public void bt$updateRenderState(BannerBlockEntity entity, BannerBlockEntityRenderState renderState, float f, Vec3d vec3d, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand, CallbackInfo ci) {
        ((BannerBlockEntityRenderStateAccessor) renderState).bt$setCustomName(entity.getCustomName());
    }

    @Inject(method = "render(Lnet/minecraft/client/render/block/entity/state/BannerBlockEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("TAIL"))
    private void bt$renderBannerName(BannerBlockEntityRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        bt$renderLabelIfPresent(renderState, matrices, orderedRenderCommandQueue, cameraRenderState, renderState.lightmapCoordinates);
    }

    @Unique
    protected void bt$renderLabelIfPresent(BannerBlockEntityRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState, int light) {
        double squaredDistanceToCamera = cameraRenderState.blockPos.getSquaredDistance(cameraRenderState.pos);

        if (squaredDistanceToCamera > 1024.0) {
            return;
        }

        Text text = ((BannerBlockEntityRenderStateAccessor) renderState).bt$getCustomName();

        if (!MinecraftClient.isHudEnabled() || text == null) {
            return;
        }

        float verticalOffset = 0f;
        if (!renderState.standing) {
            verticalOffset = -1f;
        }

        Vec3d nameLabelPos = new Vec3d(0.5f, 1.625f + verticalOffset, 0.5f);
        queue.submitLabel(matrices, nameLabelPos, 0, text, true, light, squaredDistanceToCamera, cameraRenderState);
    }
}

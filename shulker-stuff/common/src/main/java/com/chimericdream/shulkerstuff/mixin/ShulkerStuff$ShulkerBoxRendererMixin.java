package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.client.render.block.entity.state.ShulkerBoxRenderStateAccessor;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffDyedColorComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.blockentity.state.ShulkerBoxRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxRenderer.class)
abstract public class ShulkerStuff$ShulkerBoxRendererMixin {
    @Shadow
    @Final
    public ShulkerBoxRenderer.ShulkerBoxModel model;

    @Shadow
    @Final
    public MaterialSet materials;

    @Inject(
        method = "Lnet/minecraft/client/renderer/blockentity/ShulkerBoxRenderer;extractRenderState(Lnet/minecraft/world/level/block/entity/ShulkerBoxBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/ShulkerBoxRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
        at = @At("TAIL")
    )
    private void ss$extractRenderState(ShulkerBoxBlockEntity blockEntity, ShulkerBoxRenderState renderState, float partialTick, Vec3 vec3, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, CallbackInfo ci) {
        ShulkerBoxRenderStateAccessor accessor = (ShulkerBoxRenderStateAccessor) renderState;

        boolean useVanillaColor = blockEntity.getColor() != null;
        accessor.ss$setUseVanillaColor(useVanillaColor);

        if (useVanillaColor) {
            return;
        }

        ShulkerStuffDyedColorComponent ssDyedColorComponent = blockEntity.components().get(ShulkerStuffComponentTypes.SHULKER_STUFF_DYED_COLOR_COMPONENT.get());
        if (ssDyedColorComponent == null) {
            // This is roughly the same as the default color for shulker boxes
            accessor.ss$setLidColor(9922455);
            accessor.ss$setBaseColor(9922455);
        } else {
            accessor.ss$setLidColor(ssDyedColorComponent.lidColor());
            accessor.ss$setBaseColor(ssDyedColorComponent.baseColor());
        }
    }

    @Inject(
        method = "Lnet/minecraft/client/renderer/blockentity/ShulkerBoxRenderer;submit(Lnet/minecraft/client/renderer/blockentity/state/ShulkerBoxRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void ss$submit(ShulkerBoxRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState, CallbackInfo ci) {
        ShulkerBoxRenderStateAccessor accessor = (ShulkerBoxRenderStateAccessor) renderState;
        if (accessor.ss$useVanillaColor()) {
            return;
        }

        DyeColor dyeColor = renderState.color;
        Material material = dyeColor == null ? Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION : Sheets.getShulkerBoxMaterial(dyeColor);
        TextureAtlasSprite sprite = this.materials.get(material);
        RenderType renderType = Sheets.shulkerBoxSheet();

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(0.9995F, 0.9995F, 0.9995F);
        poseStack.mulPose(renderState.direction.getRotation());
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, -1.0F, 0.0F);

        this.model.setupAnim(renderState.progress);

        ModelPart root = this.model.root();
        ModelPart lid = root.getChild("lid");

        lid.visible = false;
        collector.submitModelPart(root, poseStack, renderType, renderState.lightCoords, OverlayTexture.NO_OVERLAY, sprite, false, false, accessor.ss$getBaseColor(), renderState.breakProgress, 0);
        lid.visible = true;
        collector.submitModelPart(lid, poseStack, renderType, renderState.lightCoords, OverlayTexture.NO_OVERLAY, sprite, false, false, accessor.ss$getLidColor(), renderState.breakProgress, 0);

        poseStack.popPose();

        ci.cancel();
    }
}

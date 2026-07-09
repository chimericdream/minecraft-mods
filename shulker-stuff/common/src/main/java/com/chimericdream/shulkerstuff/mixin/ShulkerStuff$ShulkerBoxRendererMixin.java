package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.client.render.block.entity.state.ShulkerBoxRenderStateAccessor;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffDyedColorComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
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
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxRenderer.class)
abstract public class ShulkerStuff$ShulkerBoxRendererMixin {
    @Shadow
    @Final
    public MaterialSet materials;

    // ShulkerBoxRenderer's own "model" field is a single instance shared by every shulker box block
    // entity, vanilla-colored ones included. Vanilla-colored boxes never get cancelled below, so
    // vanilla's own (unmodified) submit() still runs for them and calls model.setupAnim(...), mutating
    // the very same "lid"/"base" ModelPart objects a mixin reading that field would see - which
    // corrupted our boxes' lid position whenever a vanilla-colored box shared a frame with one of ours.
    // Bake a completely separate part tree that only this mixin ever touches, so vanilla's rendering
    // can't cross-contaminate it.
    @Unique
    private ModelPart ss$ownRoot;

    @Unique
    private ModelPart ss$ownRoot() {
        if (this.ss$ownRoot == null) {
            this.ss$ownRoot = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SHULKER_BOX);
        }

        return this.ss$ownRoot;
    }

    @Inject(
        method = "extractRenderState(Lnet/minecraft/world/level/block/entity/ShulkerBoxBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/ShulkerBoxRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
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
            accessor.ss$setLidColor(-1);
            accessor.ss$setBaseColor(-1);
//            // This is roughly the same as the default color for shulker boxes
//            accessor.ss$setLidColor(9922455);
//            accessor.ss$setBaseColor(9922455);
        } else {
            accessor.ss$setLidColor(ssDyedColorComponent.lidColor());
            accessor.ss$setBaseColor(ssDyedColorComponent.baseColor());
        }
    }

    @Inject(
        method = "submit(Lnet/minecraft/client/renderer/blockentity/state/ShulkerBoxRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void ss$submit(ShulkerBoxRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState, CallbackInfo ci) {
        ShulkerBoxRenderStateAccessor accessor = (ShulkerBoxRenderStateAccessor) renderState;
        if (accessor.ss$useVanillaColor()) {
            return;
        }

        // -1 means that part was never dyed by this mod, so it keeps the plain undyed (purple-hued)
        // texture; only a genuinely dyed part switches to the near-grayscale white texture so its tint
        // comes out as the intended hue instead of shifting within the undyed texture's own purple.
        int baseColor = accessor.ss$getBaseColor();
        int lidColor = accessor.ss$getLidColor();
        boolean baseDyed = baseColor != -1;
        boolean lidDyed = lidColor != -1;

        TextureAtlasSprite defaultSprite = this.materials.get(Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION);
        TextureAtlasSprite dyedSprite = this.materials.get(Sheets.getShulkerBoxMaterial(DyeColor.WHITE));
        RenderType renderType = Sheets.shulkerBoxSheet();

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(0.9995F, 0.9995F, 0.9995F);
        poseStack.mulPose(renderState.direction.getRotation());
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, -1.0F, 0.0F);

        // submitModelPart doesn't bake vertices immediately, it queues the ModelPart reference for a
        // later batched draw pass, so mutating shared pose/visibility here (as the old
        // ModelPart#setupAnim/visible-toggle approach did) leaks whichever entity rendered last into
        // every other queued shulker box - see ss$ownRoot(). Pin the lid's own pose to a constant
        // neutral rest (harmless no matter which entity's call sets it last, since every entity sets
        // it to the same value) and drive the open animation entirely through the PoseStack instead,
        // which is copied per submission.
        // "lid" and "base" are both direct children of root (siblings, not parent/child of each
        // other) per ShulkerModel's shared mesh definition, so fetch them by name rather than by
        // Model#allParts() iteration order (which puts lid before base, opposite of what you'd guess).
        ModelPart root = ss$ownRoot();
        ModelPart base = root.getChild("base");
        ModelPart lid = root.getChild("lid");

        lid.setPos(0.0F, 0.0F, 0.0F);
        lid.xRot = 0.0F;
        lid.yRot = 0.0F;
        lid.zRot = 0.0F;

        collector.submitModelPart(base, poseStack, renderType, renderState.lightCoords, OverlayTexture.NO_OVERLAY, baseDyed ? dyedSprite : defaultSprite, false, false, baseColor, renderState.breakProgress, 0);

        poseStack.pushPose();
        poseStack.translate(0.0F, 1.5F - renderState.progress * 0.5F, 0.0F);
        poseStack.mulPose(new Quaternionf().rotationY(270.0F * renderState.progress * ((float) Math.PI / 180F)));
        collector.submitModelPart(lid, poseStack, renderType, renderState.lightCoords, OverlayTexture.NO_OVERLAY, lidDyed ? dyedSprite : defaultSprite, false, false, lidColor, renderState.breakProgress, 0);
        poseStack.popPose();

        poseStack.popPose();

        ci.cancel();
    }
}

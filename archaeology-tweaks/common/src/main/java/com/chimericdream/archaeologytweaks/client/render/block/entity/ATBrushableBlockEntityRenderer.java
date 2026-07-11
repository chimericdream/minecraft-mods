package com.chimericdream.archaeologytweaks.client.render.block.entity;

import com.chimericdream.archaeologytweaks.block.entity.ATBrushableBlockEntity;
import com.chimericdream.archaeologytweaks.client.render.block.entity.state.ATBrushableBlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelRenderer.BrightnessGetter;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ATBrushableBlockEntityRenderer implements BlockEntityRenderer<ATBrushableBlockEntity, ATBrushableBlockEntityRenderState> {
    private final ItemModelResolver itemModelManager;

    public ATBrushableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelManager = context.itemModelResolver();
    }

    public @NotNull ATBrushableBlockEntityRenderState createRenderState() {
        return new ATBrushableBlockEntityRenderState();
    }

    public void extractRenderState(ATBrushableBlockEntity blockEntity, ATBrushableBlockEntityRenderState renderState, float f, Vec3 vec3d, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlayCommand) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, f, vec3d, crumblingOverlayCommand);
        renderState.face = blockEntity.getHitDirection();
        renderState.dusted = (Integer) blockEntity.getBlockState().getValue(BlockStateProperties.DUSTED);
        if (blockEntity.getLevel() != null && blockEntity.getHitDirection() != null) {
            renderState.lightCoords = LevelRenderer.getLightCoords(BrightnessGetter.DEFAULT, blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos().relative(blockEntity.getHitDirection()));
        }

        this.itemModelManager.updateForTopItem(renderState.itemRenderState, blockEntity.getItem(), ItemDisplayContext.FIXED, blockEntity.getLevel(), (ItemOwner) null, 0);
    }

    @Override
    public void submit(ATBrushableBlockEntityRenderState renderState, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (renderState.dusted > 0 && renderState.face != null && !renderState.itemRenderState.isEmpty()) {
            matrixStack.pushPose();
            matrixStack.translate(0.0F, 0.5F, 0.0F);
            float[] fs = this.getTranslation(renderState.face, renderState.dusted);
            matrixStack.translate(fs[0], fs[1], fs[2]);
            matrixStack.mulPose(Axis.YP.rotationDegrees(75.0F));
            boolean bl = renderState.face == Direction.EAST || renderState.face == Direction.WEST;
            matrixStack.mulPose(Axis.YP.rotationDegrees((float) ((bl ? 90 : 0) + 11)));
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            renderState.itemRenderState.submit(matrixStack, orderedRenderCommandQueue, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            matrixStack.popPose();
        }
    }

//    public void render(ATBrushableBlockEntity brushableBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
//        if (brushableBlockEntity.getWorld() != null) {
//            int k = brushableBlockEntity.getCachedState().get(Properties.DUSTED);
//
//            if (k > 0) {
//                Direction direction = brushableBlockEntity.getHitDirection();
//
//                if (direction != null) {
//                    ItemStack itemStack = brushableBlockEntity.getItem();
//
//                    if (!itemStack.isEmpty()) {
//                        matrixStack.push();
//                        matrixStack.translate(0.0F, 0.5F, 0.0F);
//                        float[] fs = this.getTranslation(direction, k);
//                        matrixStack.translate(fs[0], fs[1], fs[2]);
//                        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(75.0F));
//                        boolean bl = direction == Direction.EAST || direction == Direction.WEST;
//                        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) ((bl ? 90 : 0) + 11)));
//                        matrixStack.scale(0.5F, 0.5F, 0.5F);
//                        int l = WorldRenderer.getLightmapCoordinates(WorldRenderer.BrightnessGetter.DEFAULT, brushableBlockEntity.getWorld(), brushableBlockEntity.getCachedState(), brushableBlockEntity.getPos().offset(direction));
//                        this.itemRenderer.renderItem(itemStack, ItemDisplayContext.FIXED, l, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, brushableBlockEntity.getWorld(), 0);
//                        matrixStack.pop();
//                    }
//                }
//            }
//        }
//    }

    private float[] getTranslation(Direction direction, int dustedLevel) {
        float[] fs = new float[]{0.5F, 0.0F, 0.5F};
        float f = (float) dustedLevel / 10.0F * 0.75F;

        switch (direction) {
            case EAST -> fs[0] = 0.73F + f;
            case WEST -> fs[0] = 0.25F - f;
            case UP -> fs[1] = 0.25F + f;
            case DOWN -> fs[1] = -0.23F - f;
            case NORTH -> fs[2] = 0.25F - f;
            case SOUTH -> fs[2] = 0.73F + f;
        }

        return fs;
    }
}

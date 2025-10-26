package com.chimericdream.archaeologytweaks.client.render.block.entity;

import com.chimericdream.archaeologytweaks.block.entity.ATBrushableBlockEntity;
import com.chimericdream.archaeologytweaks.client.render.block.entity.state.ATBrushableBlockEntityRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.WorldRenderer.BrightnessGetter;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ATBrushableBlockEntityRenderer implements BlockEntityRenderer<ATBrushableBlockEntity, ATBrushableBlockEntityRenderState> {
    private final ItemModelManager itemModelManager;

    public ATBrushableBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemModelManager = context.itemModelManager();
    }

    public ATBrushableBlockEntityRenderState createRenderState() {
        return new ATBrushableBlockEntityRenderState();
    }

    public void updateRenderState(ATBrushableBlockEntity blockEntity, ATBrushableBlockEntityRenderState renderState, float f, Vec3d vec3d, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, renderState, f, vec3d, crumblingOverlayCommand);
        renderState.face = blockEntity.getHitDirection();
        renderState.dusted = (Integer) blockEntity.getCachedState().get(Properties.DUSTED);
        if (blockEntity.getWorld() != null && blockEntity.getHitDirection() != null) {
            renderState.lightmapCoordinates = WorldRenderer.getLightmapCoordinates(BrightnessGetter.DEFAULT, blockEntity.getWorld(), blockEntity.getCachedState(), blockEntity.getPos().offset(blockEntity.getHitDirection()));
        }

        this.itemModelManager.clearAndUpdate(renderState.itemRenderState, blockEntity.getItem(), ItemDisplayContext.FIXED, blockEntity.getWorld(), (HeldItemContext) null, 0);
    }

    public void render(ATBrushableBlockEntityRenderState renderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (renderState.dusted > 0 && renderState.face != null && !renderState.itemRenderState.isEmpty()) {
            matrixStack.push();
            matrixStack.translate(0.0F, 0.5F, 0.0F);
            float[] fs = this.getTranslation(renderState.face, renderState.dusted);
            matrixStack.translate(fs[0], fs[1], fs[2]);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(75.0F));
            boolean bl = renderState.face == Direction.EAST || renderState.face == Direction.WEST;
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) ((bl ? 90 : 0) + 11)));
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            renderState.itemRenderState.render(matrixStack, orderedRenderCommandQueue, renderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);
            matrixStack.pop();
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

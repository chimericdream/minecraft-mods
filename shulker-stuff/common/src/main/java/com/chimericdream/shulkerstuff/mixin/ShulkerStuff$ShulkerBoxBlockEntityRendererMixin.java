package com.chimericdream.shulkerstuff.mixin;

import com.chimericdream.shulkerstuff.client.util.ShulkerBoxSprites;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffDataComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(ShulkerBoxBlockEntityRenderer.class)
abstract public class ShulkerStuff$ShulkerBoxBlockEntityRendererMixin {
    @Shadow
    @Final
    public ShulkerEntityModel<?> model;

    @Inject(method = "render*", at = @At("HEAD"), cancellable = true)
    private void ss$render(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        Direction direction = Direction.UP;
        if (shulkerBoxBlockEntity.hasWorld()) {
            @SuppressWarnings("DataFlowIssue")
            BlockState blockState = shulkerBoxBlockEntity.getWorld().getBlockState(shulkerBoxBlockEntity.getPos());
            if (blockState.getBlock() instanceof ShulkerBoxBlock) {
                direction = blockState.get(ShulkerBoxBlock.FACING);
            }
        }

        boolean useVanillaColor = false;

        DyeColor dyeColor = shulkerBoxBlockEntity.getColor();
        SpriteIdentifier spriteIdentifier = ShulkerBoxSprites.GRAYSCALE_SHULKER;
        if (dyeColor != null) {
            spriteIdentifier = TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.get(dyeColor.getId());
            useVanillaColor = true;
        }

        int rgb1 = -1, rgb2 = -1;

        ShulkerStuffDataComponent ssData = shulkerBoxBlockEntity.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_DATA.get());
        if (!useVanillaColor) {
            if (ssData == null) {
                // This is roughly the same as the default color for shulker boxes
                rgb1 = 9922455;
                rgb2 = 9922455;
            } else {
                rgb1 = ssData.lidColor();
                rgb2 = ssData.baseColor();
            }
        }

        matrixStack.push();
        matrixStack.translate(0.5F, 0.5F, 0.5F);
        matrixStack.scale(0.9995F, 0.9995F, 0.9995F);
        matrixStack.multiply(direction.getRotationQuaternion());
        matrixStack.scale(1.0F, -1.0F, -1.0F);
        matrixStack.translate(0.0F, -1.0F, 0.0F);

        Iterator<ModelPart> modelParts = this.model.getParts().iterator();
        ModelPart base = modelParts.next();
        ModelPart lid = modelParts.next();

        lid.setPivot(0.0F, 24.0F - shulkerBoxBlockEntity.getAnimationProgress(f) * 0.5F * 16.0F, 0.0F);
        lid.yaw = 270.0F * shulkerBoxBlockEntity.getAnimationProgress(f) * ((float) Math.PI / 180F);

        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull);

        lid.render(matrixStack, vertexConsumer, i, j, rgb1);
        base.render(matrixStack, vertexConsumer, i, j, rgb2);
        matrixStack.pop();

        ci.cancel();
    }
}

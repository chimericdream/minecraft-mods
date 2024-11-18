package com.chimericdream.shulkerstuff.fabric.client.render.item;

import com.chimericdream.shulkerstuff.client.render.item.ShulkerBoxItemRendererLogic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ShulkerBoxItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState defaultState = Blocks.SHULKER_BOX.getDefaultState();

        JarModel model = new JarModel();
        model.setModel(MinecraftClient.getInstance().getBlockRenderManager().getModel(defaultState));

        ShulkerBoxItemRendererLogic.render(stack, matrices, vertexConsumers, light, overlay, model);
    }

    private static class JarModel extends ForwardingBakedModel {
        public void setModel(BakedModel model) {
            this.wrapped = model;
        }
    }
}

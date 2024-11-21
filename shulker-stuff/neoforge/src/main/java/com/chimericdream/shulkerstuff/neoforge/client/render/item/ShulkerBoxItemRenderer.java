package com.chimericdream.shulkerstuff.neoforge.client.render.item;

import com.chimericdream.shulkerstuff.client.render.item.ShulkerBoxItemRendererLogic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class ShulkerBoxItemRenderer extends BuiltinModelItemRenderer {
    public ShulkerBoxItemRenderer() {
        super(MinecraftClient.getInstance().getBlockEntityRenderDispatcher(), MinecraftClient.getInstance().getEntityModelLoader());
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ShulkerBoxItemRendererLogic.render(stack, matrices, vertexConsumers, light, overlay);
    }

    public static class ShulkerBoxItemRendererExtension implements IClientItemExtensions {
        private final ShulkerBoxItemRenderer renderer = new ShulkerBoxItemRenderer();

        @Override
        public @NotNull BuiltinModelItemRenderer getCustomRenderer() {
            return renderer;
        }
    }
}

package com.chimericdream.miniblockmerchants.neoforge.client.render.item;

import com.chimericdream.miniblockmerchants.client.render.item.VillagerConversionItemRendererLogic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class VillagerConversionItemRenderer extends BuiltinModelItemRenderer {
    public VillagerConversionItemRenderer() {
        super(MinecraftClient.getInstance().getBlockEntityRenderDispatcher(), MinecraftClient.getInstance().getEntityModelLoader());
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        VillagerConversionItemRendererLogic.render(stack, mode, matrices, vertexConsumers, light, overlay);
    }

    public static class VillagerConversionItemRendererExtension implements IClientItemExtensions {
        private final VillagerConversionItemRenderer renderer = new VillagerConversionItemRenderer();

        @Override
        public @NotNull BuiltinModelItemRenderer getCustomRenderer() {
            return renderer;
        }
    }
}

package com.chimericdream.shulkerstuff.client.render.item;

import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffDyedColorComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ShulkerBoxItemRendererLogic {
    public static void render(
        ItemStack stack,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        int overlay
    ) {
        BlockState defaultState = Blocks.SHULKER_BOX.getDefaultState();
        BakedModel model = MinecraftClient.getInstance().getBlockRenderManager().getModel(defaultState);

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GUI, false, matrices, vertexConsumers, light, overlay, model);
        matrices.pop();

        ShulkerBoxBlockEntity entity = new ShulkerBoxBlockEntity(BlockPos.ORIGIN, defaultState);

        ShulkerStuffDyedColorComponent ssDyedColorComponent = stack.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_DYED_COLOR_COMPONENT.get());
        if (ssDyedColorComponent != null) {
            entity.setComponents(ComponentMap.builder().add(ShulkerStuffComponentTypes.SHULKER_STUFF_DYED_COLOR_COMPONENT.get(), ssDyedColorComponent).build());
        }
        MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(entity, matrices, vertexConsumers, light, overlay);
    }
}

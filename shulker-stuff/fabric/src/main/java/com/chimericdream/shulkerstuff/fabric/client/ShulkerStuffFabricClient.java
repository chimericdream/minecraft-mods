package com.chimericdream.shulkerstuff.fabric.client;

import com.chimericdream.shulkerstuff.fabric.client.render.item.ShulkerBoxItemRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.item.Items;

public final class ShulkerStuffFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        initializeBlockRenderLayers();
    }

    private void initializeBlockRenderLayers() {
        BuiltinItemRendererRegistry.INSTANCE.register(Items.SHULKER_BOX, new ShulkerBoxItemRenderer());
    }
}

package com.chimericdream.shulkerstuff.fabric.client;

import com.chimericdream.shulkerstuff.client.ShulkerStuffClient;
import com.chimericdream.shulkerstuff.fabric.client.render.item.ShulkerBoxModelLoadingPlugin;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

public final class ShulkerStuffFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ShulkerStuffClient.onInitializeClient();
        ModelLoadingPlugin.register(new ShulkerBoxModelLoadingPlugin());
    }
}

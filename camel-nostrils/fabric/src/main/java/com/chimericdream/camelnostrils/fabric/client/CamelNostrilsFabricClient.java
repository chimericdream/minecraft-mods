package com.chimericdream.camelnostrils.fabric.client;

import com.chimericdream.camelnostrils.client.CamelNostrilsClient;
import net.fabricmc.api.ClientModInitializer;

public final class CamelNostrilsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CamelNostrilsClient.registerEntityRenderers();
    }
}

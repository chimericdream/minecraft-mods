package com.chimericdream.jdcrafte.fabric.client;

import com.chimericdream.jdcrafte.client.JDCrafteClient;
import net.fabricmc.api.ClientModInitializer;

public final class JDCrafteFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        JDCrafteClient.registerBlockEntityRenderers();
    }
}

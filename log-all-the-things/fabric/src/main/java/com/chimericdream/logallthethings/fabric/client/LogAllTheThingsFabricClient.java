package com.chimericdream.logallthethings.fabric.client;

import net.fabricmc.api.ClientModInitializer;

import com.chimericdream.logallthethings.client.LogAllTheThingsClient;

public final class LogAllTheThingsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LogAllTheThingsClient.registerBlockEntityRenderers();
    }
}

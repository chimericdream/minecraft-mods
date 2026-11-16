package com.chimericdream.nextupdatenow.fabric.client;

import com.chimericdream.nextupdatenow.client.NextUpdateNowClient;
import net.fabricmc.api.ClientModInitializer;

public final class NextUpdateNowFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NextUpdateNowClient.registerEntityRenderers();
    }
}

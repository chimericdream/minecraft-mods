package com.chimericdream.polycraft.fabric.client;

import com.chimericdream.polycraft.client.PolyCraftClient;
import net.fabricmc.api.ClientModInitializer;

public final class PolyCraftFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PolyCraftClient.init();
    }
}

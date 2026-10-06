package com.chimericdream.opus.fabric.client;

import com.chimericdream.opus.client.OpusClient;
import net.fabricmc.api.ClientModInitializer;

public final class OpusFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OpusClient.init();
    }
}

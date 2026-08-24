package com.chimericdream.bettertargetdummies.fabric.client;

import com.chimericdream.bettertargetdummies.client.BetterTargetDummiesClient;
import net.fabricmc.api.ClientModInitializer;

public final class BetterTargetDummiesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BetterTargetDummiesClient.onInitializeClient();
    }
}

package com.chimericdream.hopperxtreme.fabric.client;

import com.chimericdream.hopperxtreme.client.HopperXtremeClient;
import net.fabricmc.api.ClientModInitializer;

public final class HopperXtremeFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HopperXtremeClient.onInitializeClient();
    }
}

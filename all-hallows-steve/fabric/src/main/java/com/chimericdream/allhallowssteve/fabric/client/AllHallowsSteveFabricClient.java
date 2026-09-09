package com.chimericdream.allhallowssteve.fabric.client;

import com.chimericdream.allhallowssteve.client.AllHallowsSteveClient;
import net.fabricmc.api.ClientModInitializer;

public final class AllHallowsSteveFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AllHallowsSteveClient.onInitializeClient();
    }
}

package com.chimericdream.archaeologytweaks.fabric.client;

import com.chimericdream.archaeologytweaks.client.ArchaeologyTweaksClient;
import net.fabricmc.api.ClientModInitializer;

public class ArchaeologyTweaksFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ArchaeologyTweaksClient.onInitializeClient();
    }
}

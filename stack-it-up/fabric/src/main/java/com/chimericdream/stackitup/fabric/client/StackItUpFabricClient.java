package com.chimericdream.stackitup.fabric.client;

import net.fabricmc.api.ClientModInitializer;

public final class StackItUpFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Network receiver registration happens in StackItUpMod.init() (common init), which
        // Architectury's NetworkManager handles correctly on both client and dedicated server -
        // nothing client-only needed here currently.
    }
}

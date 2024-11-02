package com.chimericdream.lib.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.lib.ChimericLib;

public final class ChimericLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        ChimericLib.init();
    }
}

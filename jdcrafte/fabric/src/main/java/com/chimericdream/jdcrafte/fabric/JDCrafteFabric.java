package com.chimericdream.jdcrafte.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.jdcrafte.JDCrafteMod;

public final class JDCrafteFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        JDCrafteMod.init();
    }
}

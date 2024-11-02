package com.chimericdream.houdiniblock.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.houdiniblock.HoudiniBlockMod;

public final class HoudiniBlockFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        HoudiniBlockMod.init();
    }
}

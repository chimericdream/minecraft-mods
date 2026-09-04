package com.chimericdream.lib.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.lib.ChimericLib;
import com.chimericdream.lib.commands.PlatformCommandArgumentTypes;
import com.chimericdream.lib.fabric.commands.PlatformCommandArgumentTypesImpl;

public final class ChimericLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Platform providers must be wired up before ChimericLib.init() runs, since it registers
        // things (like custom command argument types) that need them.
        PlatformCommandArgumentTypes.setProvider(new PlatformCommandArgumentTypesImpl());

        // Run our common setup.
        ChimericLib.init();
    }
}

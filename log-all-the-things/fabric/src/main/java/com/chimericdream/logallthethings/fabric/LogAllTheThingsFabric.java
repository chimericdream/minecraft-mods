package com.chimericdream.logallthethings.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.logallthethings.LogAllTheThingsMod;

public final class LogAllTheThingsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LogAllTheThingsMod.init();
    }
}

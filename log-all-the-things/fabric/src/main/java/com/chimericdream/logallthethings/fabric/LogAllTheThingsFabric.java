package com.chimericdream.logallthethings.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.logallthethings.LogAllTheThingsMod;
import com.chimericdream.logallthethings.fabric.lavalog.LavaLogFlammabilityImpl;
import com.chimericdream.logallthethings.lavalog.LavaLogFlammability;

public final class LogAllTheThingsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LogAllTheThingsMod.init();

        LavaLogFlammability.setProvider(new LavaLogFlammabilityImpl());
    }
}

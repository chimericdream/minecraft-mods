package com.chimericdream.athenaeum.fabric;

import com.chimericdream.athenaeum.AthenaeumMod;
import com.chimericdream.athenaeum.fabric.config.ConfigManager;
import net.fabricmc.api.ModInitializer;

public final class AthenaeumModFabric implements ModInitializer {
    static {
        ConfigManager.registerAutoConfig();
        AthenaeumMod.CONFIG = ConfigManager::getConfig;
    }

    @Override
    public void onInitialize() {
        AthenaeumMod.init();
    }
}

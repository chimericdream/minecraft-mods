package com.chimericdream.athenaeum.neoforge;

import com.chimericdream.athenaeum.AthenaeumMod;
import com.chimericdream.athenaeum.AthenaeumModInfo;
import com.chimericdream.athenaeum.neoforge.config.ConfigManager;
import net.neoforged.fml.common.Mod;

@Mod(AthenaeumModInfo.MOD_ID)
public final class AthenaeumModNeoForge {
    static {
        ConfigManager.registerAutoConfig();
        AthenaeumMod.CONFIG = ConfigManager::getConfig;
    }

    public AthenaeumModNeoForge() {
        // Run our common setup.
        AthenaeumMod.init();
    }
}

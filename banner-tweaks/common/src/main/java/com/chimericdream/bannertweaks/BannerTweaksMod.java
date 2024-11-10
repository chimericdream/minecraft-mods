package com.chimericdream.bannertweaks;

import com.chimericdream.bannertweaks.config.BannerTweaksConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BannerTweaksMod {
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static void init() {
        BannerTweaksConfig.HANDLER.load();
    }
}

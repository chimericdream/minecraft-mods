package com.chimericdream.bctweaks;

import com.chimericdream.bctweaks.config.BCTweaksConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BeaconConduitTweaksMod {
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static void init() {
        BCTweaksConfig.load();
    }
}

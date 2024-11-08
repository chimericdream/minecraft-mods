package com.chimericdream.bctweaks;

import com.chimericdream.bctweaks.config.BCTweaksConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BeaconConduitTweaksMod {
    public static final Logger LOGGER = LoggerFactory.getLogger(ModInfo.MOD_ID);

    public static void init() {
        BCTweaksConfig.load();
    }
}

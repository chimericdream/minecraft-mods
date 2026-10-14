package com.chimericdream.bctweaks;

import com.chimericdream.bctweaks.config.BCTweaksConfig;
import dev.architectury.event.events.common.LifecycleEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BeaconConduitTweaksMod {
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static void init() {
        BCTweaksConfig.CONFIG.init();

        // Other mods' blocks are registered by now, so anything still unknown really is missing.
        LifecycleEvent.SERVER_STARTED.register(server -> BCTweaksConfig.warnAboutUnknownBlocks(BCTweaksConfig.CONFIG.instance()));
    }
}

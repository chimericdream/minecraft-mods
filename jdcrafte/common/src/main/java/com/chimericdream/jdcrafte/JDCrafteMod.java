package com.chimericdream.jdcrafte;

import com.chimericdream.jdcrafte.block.ModBlocks;
import com.chimericdream.lib.registries.ModRegistryHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class JDCrafteMod {
    public static final String MOD_ID = "jdcrafte";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(MOD_ID, LOGGER);

    public static void init() {
        ModBlocks.init();

        REGISTRY_HELPER.init();
    }
}

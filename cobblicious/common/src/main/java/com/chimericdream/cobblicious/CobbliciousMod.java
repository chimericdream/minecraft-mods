package com.chimericdream.cobblicious;

import com.chimericdream.cobblicious.block.BlockFamilies;
import com.chimericdream.cobblicious.block.ModBlocks;
import com.chimericdream.cobblicious.crop.ModCrops;
import com.chimericdream.lib.registries.ModRegistryHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CobbliciousMod {
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        ModCrops.init();
        ModBlocks.init();
        BlockFamilies.init();

        REGISTRY_HELPER.init();
    }
}

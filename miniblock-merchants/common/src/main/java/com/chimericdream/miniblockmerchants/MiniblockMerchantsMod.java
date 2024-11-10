package com.chimericdream.miniblockmerchants;

import com.chimericdream.lib.registries.ModRegistryHelper;
import com.chimericdream.miniblockmerchants.config.MiniblockMerchantsConfig;
import com.chimericdream.miniblockmerchants.item.ModItems;
import com.chimericdream.miniblockmerchants.registry.ModProfessions;
import com.chimericdream.miniblockmerchants.registry.ModStats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MiniblockMerchantsMod {
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MiniblockMerchantsConfig.HANDLER.load();

        ModItems.init();
        ModProfessions.init();
        ModStats.init();
        REGISTRY_HELPER.init();
    }
}

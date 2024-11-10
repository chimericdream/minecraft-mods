package com.chimericdream.miniblockmerchants;

import com.chimericdream.miniblockmerchants.config.MiniblockMerchantsConfig;
import com.chimericdream.miniblockmerchants.registry.MMProfessions;
import com.chimericdream.miniblockmerchants.registry.MMStats;
import com.chimericdream.miniblockmerchants.registry.ModRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MiniblockMerchantsMod {
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static void init() {
        MiniblockMerchantsConfig.HANDLER.load();

        MMProfessions.init();

        ModRegistries.init();

        MMStats.init();
    }
}

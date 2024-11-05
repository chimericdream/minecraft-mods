package com.chimericdream.miniblockmerchants;

import com.chimericdream.miniblockmerchants.config.MiniblockMerchantsConfig;
import com.chimericdream.miniblockmerchants.registry.MMProfessions;
import com.chimericdream.miniblockmerchants.registry.MMStats;
import com.chimericdream.miniblockmerchants.registry.ModRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MiniblockMerchantsMod {
    public static final Logger LOGGER = LoggerFactory.getLogger(ModInfo.MOD_ID);

    public static void init() {
        MiniblockMerchantsConfig.HANDLER.load();

        MMProfessions.init();

        ModRegistries.init();

        MMStats.init();
    }
}

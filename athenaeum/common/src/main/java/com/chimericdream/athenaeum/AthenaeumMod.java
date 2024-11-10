package com.chimericdream.athenaeum;

import com.chimericdream.athenaeum.config.AthenaeumConfig;
import com.chimericdream.athenaeum.loot.AthenaeumLootFunctionTypes;
import com.chimericdream.athenaeum.loot.AthenaeumLootTables;
import com.chimericdream.athenaeum.registries.AthenaeumRegistries;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AthenaeumMod {
    public static final Logger LOGGER = LogManager.getLogger("athenaeum");

    public static Supplier<AthenaeumConfig> CONFIG = AthenaeumConfig::new;

    public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(AthenaeumModInfo.MOD_ID));

    public static void init() {
        AthenaeumRegistries.init();
        AthenaeumLootFunctionTypes.register();
        AthenaeumLootTables.init();
        AthenaeumReloadListener.register();
    }
}

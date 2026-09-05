package com.chimericdream.camelnostrils;

import com.chimericdream.camelnostrils.block.ModBlocks;
import com.chimericdream.camelnostrils.entity.ModEntities;
import com.chimericdream.camelnostrils.item.ModItems;
import com.chimericdream.camelnostrils.stats.ModStats;
import com.chimericdream.lib.registries.ModRegistryHelper;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public final class CamelNostrilsMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        ModEntities.init();
        ModBlocks.init();
        ModItems.init();
        ModStats.init();

        REGISTRY_HELPER.init();

        // Entity attributes can only be registered once the entity types above actually exist in the
        // registry, which REGISTRY_HELPER.init() just did.
        ModEntities.registerAttributes();
    }
}

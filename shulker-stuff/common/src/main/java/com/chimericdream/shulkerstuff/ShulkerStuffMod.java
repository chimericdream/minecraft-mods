package com.chimericdream.shulkerstuff;

import com.chimericdream.lib.registries.ModRegistryHelper;
import com.chimericdream.shulkerstuff.block.ModBlocks;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.config.ShulkerStuffConfig;
import com.chimericdream.shulkerstuff.item.ModItems;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public final class ShulkerStuffMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));
        ShulkerStuffConfig.HANDLER.load();

        REGISTRY_HELPER.init();
        ModBlocks.init();
        ModItems.init();
        ShulkerStuffComponentTypes.init();
    }
}

package com.chimericdream.villagertweaks;

import com.chimericdream.lib.registries.ModRegistryHelper;
import com.chimericdream.villagertweaks.item.ModItems;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class VillagerTweaksMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LoggerFactory.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        REGISTRY_HELPER.init();
        ModItems.init();
    }
}

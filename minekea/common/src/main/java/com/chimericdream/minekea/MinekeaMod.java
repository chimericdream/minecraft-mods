package com.chimericdream.minekea;

import com.chimericdream.lib.registries.ModRegistryHelper;
import com.chimericdream.minekea.block.ModBlocks;
import com.chimericdream.minekea.fluid.ModFluids;
import com.chimericdream.minekea.item.ModItems;
import com.chimericdream.minekea.network.ServerNetworking;
import com.chimericdream.minekea.registry.ColoredBlocksRegistry;
import com.chimericdream.minekea.registry.ModItemGroups;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public final class MinekeaMod {
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);
    public static Supplier<RegistrarManager> MANAGER;

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        LOGGER.info("Initializing server networking");
        ServerNetworking.init();

        ModFluids.init();
        ModBlocks.init();
//        ModCrops.init();
        ModItems.init();
        ModItemGroups.init();

        ColoredBlocksRegistry.init();

        REGISTRY_HELPER.init();
    }

    public static void initVillagerPois() {
        LOGGER.info("Registering villager points of interest");
//        MinekeaPointOfInterestTypes.init();
    }
}

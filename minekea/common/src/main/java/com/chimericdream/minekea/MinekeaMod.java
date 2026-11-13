package com.chimericdream.minekea;

import com.chimericdream.lib.registries.ModRegistryHelper;
import com.chimericdream.minekea.block.ModBlocks;
import com.chimericdream.minekea.crop.ModCrops;
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
        ModCrops.init();
        ModItems.init();
        ModItemGroups.init();

        ColoredBlocksRegistry.init();

        REGISTRY_HELPER.init();
    }

    /**
     * Runs logic that depends on registry objects actually being resolvable via {@code .get()}.
     * On NeoForge, DeferredRegister entries aren't available until RegisterEvent fires, which
     * happens after all mods finish construction - so this must run from a post-registration
     * lifecycle hook (e.g. FMLCommonSetupEvent), not from {@link #init()} itself. On Fabric,
     * registration is synchronous, so calling this immediately after {@link #init()} is safe.
     */
    public static void postInit() {
        ModFluids.postInit();
        ModBlocks.postInit();
        ModCrops.postInit();
        ModItems.postInit();
        ModItemGroups.postInit();
    }

    public static void initVillagerPois() {
        LOGGER.info("Registering villager points of interest");
//        MinekeaPointOfInterestTypes.init();
    }
}

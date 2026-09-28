package com.chimericdream.allhallowssteve;

import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.block.ModDispenserBehaviors;
import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.item.ModItems;
import com.chimericdream.allhallowssteve.stats.ModStats;
import com.chimericdream.lib.registries.ModRegistryHelper;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public final class AllHallowsSteveMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        REGISTRY_HELPER.init();
        ModBlocks.init();
        ModItems.init();
        ModStats.init();
        AllHallowsSteveComponentTypes.init();
    }

    /**
     * Runs logic that depends on registry objects actually being resolvable via {@code .get()}, and
     * that writes to vanilla's non-thread-safe dispenser registry. On NeoForge this must run from
     * {@code FMLCommonSetupEvent#enqueueWork}, not from {@link #init()}; on Fabric, registration is
     * synchronous, so calling this immediately after {@link #init()} is safe.
     */
    public static void postInit() {
        ModDispenserBehaviors.init();
    }
}

package com.chimericdream.hopperxtreme;

import com.chimericdream.hopperxtreme.block.ModBlocks;
import com.chimericdream.hopperxtreme.compat.PatchouliFlags;
import com.chimericdream.hopperxtreme.component.HopperXtremeComponentTypes;
import com.chimericdream.hopperxtreme.item.ModItems;
import com.chimericdream.hopperxtreme.registry.ModItemGroups;
import com.chimericdream.lib.registries.ModRegistryHelper;
import com.google.common.base.Suppliers;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public final class HopperXtremeMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        if (Platform.isModLoaded("patchouli")) {
            PatchouliFlags.init();
        }

        ModBlocks.init();
        ModItems.init();
        ModItemGroups.init();
        HopperXtremeComponentTypes.init();

        REGISTRY_HELPER.init();
    }
}

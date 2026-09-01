package com.chimericdream.logallthethings;

import com.chimericdream.lib.registries.ModRegistryHelper;
import com.google.common.base.Suppliers;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

import com.chimericdream.logallthethings.carpetlog.CarpetLogBlocks;
import com.chimericdream.logallthethings.carpetlog.CarpetLogHelper;
import com.chimericdream.logallthethings.snowlog.SnowLogBlocks;
import com.chimericdream.logallthethings.snowlog.SnowLogHelper;
import com.chimericdream.logallthethings.windowlog.WindowLogBlocks;
import com.chimericdream.logallthethings.windowlog.WindowLogHelper;

public final class LogAllTheThingsMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        WindowLogBlocks.init();
        CarpetLogBlocks.init();
        SnowLogBlocks.init();

        REGISTRY_HELPER.init();

        InteractionEvent.RIGHT_CLICK_BLOCK.register(WindowLogHelper::tryWindowLog);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(CarpetLogHelper::tryCarpetLog);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(SnowLogHelper::tryPlaceSnow);
        BlockEvent.BREAK.register(WindowLogHelper::tryPartialBreak);
        BlockEvent.BREAK.register(CarpetLogHelper::tryPartialBreak);
        BlockEvent.BREAK.register(SnowLogHelper::tryPartialBreak);
    }
}

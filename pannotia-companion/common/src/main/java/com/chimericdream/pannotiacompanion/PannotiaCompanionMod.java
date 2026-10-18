package com.chimericdream.pannotiacompanion;

import com.chimericdream.lib.commands.ChimericCommands;
import com.chimericdream.lib.registries.ModRegistryHelper;
import com.chimericdream.pannotiacompanion.config.PannotiaCompanionConfig;
import com.chimericdream.pannotiacompanion.home.PcHomeCommand;
import com.chimericdream.pannotiacompanion.home.TeleportManager;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public final class PannotiaCompanionMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        PannotiaCompanionConfig.CONFIG.init();
        TeleportManager.init();
        ChimericCommands.register(new PcHomeCommand());

        REGISTRY_HELPER.init();
    }
}

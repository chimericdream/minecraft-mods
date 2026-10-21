package com.chimericdream.polycraft;

import com.chimericdream.lib.registries.ModRegistryHelper;
import com.chimericdream.polycraft.command.PolyCraftCommand;
import com.chimericdream.polycraft.config.PolyCraftConfig;
import com.chimericdream.polycraft.rules.PolyCraftRules;
import com.google.common.base.Suppliers;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public final class PolyCraftMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        PolyCraftConfig.CONFIG.init();
        PolyCraftRules.init();

        CommandRegistrationEvent.EVENT.register((dispatcher, context, selection) -> PolyCraftCommand.register(dispatcher));

        REGISTRY_HELPER.init();
    }
}

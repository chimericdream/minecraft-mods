package com.chimericdream.forwardingaddress;

import com.chimericdream.forwardingaddress.api.Migrations;
import com.chimericdream.forwardingaddress.config.ConfigLoader;
import com.chimericdream.forwardingaddress.config.DumpCommand;
import com.chimericdream.forwardingaddress.migration.MigrationEvents;
import com.chimericdream.forwardingaddress.migration.MigrationRegistry;
import com.chimericdream.lib.commands.ChimericCommands;
import com.chimericdream.lib.registries.ModRegistryHelper;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public final class ForwardingAddressMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        ForwardingSettings.load();
        Migrations.installBackend(MigrationRegistry.INSTANCE);
        MigrationEvents.init();

        // Stubs are registry entries, so the JSON files are read before the registries are filled.
        ConfigLoader.load(REGISTRY_HELPER);
        ChimericCommands.register(new DumpCommand());

        REGISTRY_HELPER.init();
    }
}

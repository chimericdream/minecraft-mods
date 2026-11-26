package com.chimericdream.enchantingfixes;

import com.chimericdream.enchantingfixes.book.FixBooksCommand;
import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import com.chimericdream.enchantingfixes.enchantment.ScalingEnchantments;
import com.chimericdream.enchantingfixes.network.SettingsSync;
import com.chimericdream.lib.commands.ChimericCommands;
import com.chimericdream.lib.registries.ModRegistryHelper;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public final class EnchantingFixesMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        EnchantingFixesConfig.CONFIG.init();
        ScalingEnchantments.init();
        SettingsSync.init();

        ChimericCommands.register(new FixBooksCommand());

        REGISTRY_HELPER.init();
    }
}

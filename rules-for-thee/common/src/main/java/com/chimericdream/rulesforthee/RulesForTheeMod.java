package com.chimericdream.rulesforthee;

import com.chimericdream.lib.commands.ChimericCommands;
import com.chimericdream.lib.registries.ModRegistryHelper;
import com.chimericdream.rulesforthee.command.RulesForTheeCommand;
import com.chimericdream.rulesforthee.notify.RuleNotifier;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public final class RulesForTheeMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        REGISTRY_HELPER.init();

        // `rft` is a short alias for the same command tree.
        ChimericCommands.register(new RulesForTheeCommand("rulesforthee"));
        ChimericCommands.register(new RulesForTheeCommand("rft"));
        RuleNotifier.init();
    }
}

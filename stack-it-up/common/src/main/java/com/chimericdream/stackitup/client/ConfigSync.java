package com.chimericdream.stackitup.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.chimericdream.stackitup.config.ConfigManager;
import com.chimericdream.stackitup.util.ItemsHelper;

import static com.chimericdream.stackitup.StackItUpMod.LOGGER;

public class ConfigSync {
    private static final ItemsHelper itemsHelper = ItemsHelper.getItemsHelper();

    /**
     * Items' stack size is directly modified, no config is held for that on the client side. In
     * contrast, rules are kept client-side as some client mixins need them.
     */
    public static void syncConfig(ArrayList<LinkedHashMap<String, Integer>> configList) {
        LOGGER.info("[StackItUp] [Client] Sync config from server side!");
        itemsHelper.setCountByConfig(configList.get(0).entrySet(), false);
        ConfigManager.getConfigManager().setRulesMap(configList.get(1));
        LOGGER.info("[StackItUp] [Client] Sync rules:");
        for (Map.Entry<String, Integer> rule : configList.get(1).entrySet()) {
            String tag = switch (rule.getValue()) {
                case 0 -> "false";
                case 1 -> "true";
                default -> rule.getValue().toString();
            };
            LOGGER.info("\t[{}] = {}", rule.getKey(), tag);
        }
        LOGGER.info("[StackItUp] [Client] Sync finished.");
    }

    public static void resetConfig() {
        itemsHelper.resetAll(false);
        ConfigManager.getConfigManager().setRulesMap(ConfigManager.getConfigManager().defaultRules(false));
    }
}

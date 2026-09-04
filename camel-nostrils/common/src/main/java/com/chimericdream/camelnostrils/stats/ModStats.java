package com.chimericdream.camelnostrils.stats;

import com.chimericdream.camelnostrils.ModInfo;
import net.minecraft.resources.Identifier;

import static com.chimericdream.camelnostrils.CamelNostrilsMod.REGISTRY_HELPER;

public class ModStats {
    public static final Identifier INTERACT_WITH_LIVNA = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "interact_with_livna");
    public static final Identifier INTERACT_WITH_UD_CRAFTING_TABLE = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "interact_with_upside_down_crafting_table");

    public static void init() {
        REGISTRY_HELPER.registerCustomStat(INTERACT_WITH_LIVNA);
        REGISTRY_HELPER.registerCustomStat(INTERACT_WITH_UD_CRAFTING_TABLE);
    }
}

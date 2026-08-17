package com.chimericdream.camelnostrils.stats;

import com.chimericdream.camelnostrils.ModInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;

import static net.minecraft.stats.Stats.CUSTOM;

public class ModStats {
    public static final Identifier INTERACT_WITH_LIVNA;

    private static Identifier makeCustomStat(final String id, final StatFormatter formatter) {
        Identifier location = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, id);

        Registry.register(BuiltInRegistries.CUSTOM_STAT, id, location);
        CUSTOM.get(location, formatter);

        return location;
    }

    static {
        INTERACT_WITH_LIVNA = makeCustomStat("interact_with_livna", StatFormatter.DEFAULT);
    }
}

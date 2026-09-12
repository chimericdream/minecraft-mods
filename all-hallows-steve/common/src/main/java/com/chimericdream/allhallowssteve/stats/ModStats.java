package com.chimericdream.allhallowssteve.stats;

import com.chimericdream.allhallowssteve.ModInfo;
import net.minecraft.resources.Identifier;

import static com.chimericdream.allhallowssteve.AllHallowsSteveMod.REGISTRY_HELPER;

public class ModStats {
    public static final Identifier LIGHT_DECORATED_PUMPKIN = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "light_decorated_pumpkin");

    public static void init() {
        REGISTRY_HELPER.registerCustomStat(LIGHT_DECORATED_PUMPKIN);
    }
}

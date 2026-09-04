package com.chimericdream.sponj.registry;

import com.chimericdream.sponj.ModInfo;
import net.minecraft.resources.Identifier;

import static com.chimericdream.sponj.SponjMod.REGISTRY_HELPER;

public class ModStats {
    public static final Identifier WATER_ABSORBED = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "water_absorbed");

    public static void init() {
        REGISTRY_HELPER.registerCustomStat(WATER_ABSORBED);
    }
}

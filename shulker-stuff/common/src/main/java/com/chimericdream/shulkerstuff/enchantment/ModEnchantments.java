package com.chimericdream.shulkerstuff.enchantment;

import com.chimericdream.shulkerstuff.ModInfo;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    public static final RegistryKey<Enchantment> DEEP_STORAGE = of("deep_storage");
    public static final RegistryKey<Enchantment> REFILLING = of("refill");
    public static final RegistryKey<Enchantment> VACUUM = of("vacuum");
    public static final RegistryKey<Enchantment> VOID = of("void");

    private static RegistryKey<Enchantment> of(String id) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(ModInfo.MOD_ID, id));
    }

    public static void init() {
        // NO-OP
    }
}

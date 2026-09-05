package com.chimericdream.archaeologytweaks.enchantment;

import com.chimericdream.archaeologytweaks.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> GENTLE_TOUCH = of("gentle_touch");

    private static ResourceKey<Enchantment> of(String id) {
        return ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, id));
    }

    public static void init() {
        // NO-OP
    }
}

package com.chimericdream.effectivegear.enchantment;

import com.chimericdream.effectivegear.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> PRESERVING = of("preserving");

    private static ResourceKey<Enchantment> of(String id) {
        return ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, id));
    }

    public static void init() {
        // NO-OP
    }
}

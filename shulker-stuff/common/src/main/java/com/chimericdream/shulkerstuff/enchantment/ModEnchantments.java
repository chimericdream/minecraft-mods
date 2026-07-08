package com.chimericdream.shulkerstuff.enchantment;

import com.chimericdream.shulkerstuff.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> REFILLING = of("refill");
    public static final ResourceKey<Enchantment> VACUUM = of("vacuum");
    public static final ResourceKey<Enchantment> VOID = of("void");

    private static ResourceKey<Enchantment> of(String id) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, id));
    }

    public static void init() {
        // NO-OP
    }
}

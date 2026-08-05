package com.chimericdream.archaeologytweaks.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class GentleTouchHelper {
    private static final double CHANCE_PER_LEVEL = 0.02;

    private GentleTouchHelper() {
    }

    public static int getLevel(ServerLevel world, ItemStack brush) {
        Registry<Enchantment> registry = world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> gentleTouch = registry.getOrThrow(ModEnchantments.GENTLE_TOUCH);
        return EnchantmentHelper.getItemEnchantmentLevel(gentleTouch, brush);
    }

    public static boolean rolls(ServerLevel world, int level) {
        return level > 0 && world.getRandom().nextDouble() < level * CHANCE_PER_LEVEL;
    }
}

package com.chimericdream.enchantingfixes.mixin;

import com.chimericdream.enchantingfixes.enchantment.RandomLevels;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * {@code getAvailableEnchantmentResults} starts at the maximum level and picks the first level whose cost range
 * contains the enchanting power. The enchanting table, {@code enchant_with_levels} loot, and mob equipment use
 * it. The target is the lambda in its {@code forEach}: {@code $1} in vanilla, and {@code $0} with NeoForge, which
 * removes the other lambda. Only one of them calls {@code getMaxLevel}. Check the names when Minecraft or NeoForge
 * changes this method.
 */
@Mixin(EnchantmentHelper.class)
public abstract class EFEnchantmentHelperMixin {
    @WrapOperation(
        method = {"lambda$getAvailableEnchantmentResults$0", "lambda$getAvailableEnchantmentResults$1"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I")
    )
    private static int enchantingfixes$capRandomLevel(Enchantment enchantment, Operation<Integer> original) {
        return RandomLevels.cap(enchantment, original.call(enchantment));
    }
}

package com.chimericdream.enchantingfixes.mixin;

import com.chimericdream.enchantingfixes.enchantment.RandomLevels;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * {@code enchant_randomly} picks a level from 1 to the maximum level, with the same chance for each level. Chest
 * loot, villager trades for enchanted books, and piglin bartering use it.
 */
@Mixin(EnchantRandomlyFunction.class)
public abstract class EFEnchantRandomlyFunctionMixin {
    @WrapOperation(
        method = "enchantItem",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I")
    )
    private int enchantingfixes$capRandomLevel(Enchantment enchantment, Operation<Integer> original) {
        return RandomLevels.cap(enchantment, original.call(enchantment));
    }
}

package com.chimericdream.enchantingfixes.mixin;

import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import com.chimericdream.enchantingfixes.enchantment.EnchantmentIds;
import com.chimericdream.enchantingfixes.enchantment.MaxLevels;
import com.chimericdream.enchantingfixes.enchantment.ScalingEnchantments;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Vanilla callers of {@code getMaxLevel}: the anvil (the cap when it combines levels), the enchanting
 * table, {@code /enchant}, loot functions that enchant randomly, the creative tab of enchanted books,
 * and the tooltip (it hides the level numeral when the maximum level is 1).
 */
@Mixin(Enchantment.class)
public abstract class EFEnchantmentMixin {
    @ModifyReturnValue(method = "getMaxLevel()I", at = @At("RETURN"))
    private int enchantingfixes$configuredMaxLevel(int original) {
        String id = EnchantmentIds.get((Enchantment) (Object) this);
        return MaxLevels.resolve(original, id, EnchantingFixesConfig.CONFIG.instance(), ScalingEnchantments.isActive());
    }
}

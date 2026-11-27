package com.chimericdream.enchantingfixes.mixin;

import com.chimericdream.enchantingfixes.config.SyncedConfig;
import com.chimericdream.enchantingfixes.enchantment.XpCosts;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * The enchanting table cost with the Fixed XP Costs setting. {@code onEnchantmentPerformed} removes the levels
 * and changes the enchantment seed. This mixin takes the XP points of the cost first, then lets the method
 * remove 0 levels, so the seed still changes.
 */
@Mixin(Player.class)
public abstract class EFPlayerMixin {
    @ModifyVariable(method = "onEnchantmentPerformed", at = @At("HEAD"), argsOnly = true)
    private int enchantingfixes$payInXpPoints(int enchantmentCost) {
        if (enchantmentCost <= 0 || !SyncedConfig.effective().fixedXpCosts) {
            return enchantmentCost;
        }

        XpCosts.pay((Player) (Object) this, enchantmentCost);
        return 0;
    }
}

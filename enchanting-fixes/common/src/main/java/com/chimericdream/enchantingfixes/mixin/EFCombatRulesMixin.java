package com.chimericdream.enchantingfixes.mixin;

import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import com.chimericdream.enchantingfixes.enchantment.ScalingEnchantments;
import net.minecraft.world.damagesource.CombatRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Vanilla clamps the protection points from all protection enchantments to 20. The data pack cannot
 * change this limit, so this mixin does it while the Scaling Enchantments pack is on.
 */
@Mixin(CombatRules.class)
public abstract class EFCombatRulesMixin {
    @Inject(method = "getDamageAfterMagicAbsorb(FF)F", at = @At("HEAD"), cancellable = true)
    private static void enchantingfixes$scaleProtection(float damage, float totalMagicArmor, CallbackInfoReturnable<Float> cir) {
        if (!ScalingEnchantments.isActive() || totalMagicArmor <= ScalingEnchantments.VANILLA_MAX_POINTS) {
            return;
        }

        float limit = EnchantingFixesConfig.CONFIG.instance().protectionLimitPercent / 100.0F;
        cir.setReturnValue(damage * (1.0F - ScalingEnchantments.protectionReduction(totalMagicArmor, limit)));
    }
}

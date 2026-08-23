package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A full lapis lazuli trim armor set gives enchanting a small chance to bump one of the applied
 * enchantments up an extra level, capped one above that enchantment's normal maximum. This runs after
 * the vanilla enchant/pay/trigger logic in {@code clickMenuButton} has already fully completed, so it
 * only ever adjusts the final result rather than needing to hook the private enchantment-selection
 * lambda.
 */
@Mixin(EnchantmentMenu.class)
public class EG$EnchantmentMenuMixin {
    @Shadow
    @Final
    private Container enchantSlots;

    @Inject(method = "clickMenuButton", at = @At("RETURN"))
    private void eg$lapisTrimBonusLevel(Player player, int buttonId, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || !TrimSetUtils.isWearingFullTrim(player, TrimMaterials.LAPIS)) {
            return;
        }

        if (player.getRandom().nextFloat() >= 0.15F) {
            return;
        }

        ItemStack result = this.enchantSlots.getItem(0);
        if (result.isEmpty()) {
            return;
        }

        ItemEnchantments enchantments = result.getEnchantments();
        List<Holder<Enchantment>> candidates = new ArrayList<>();
        for (Holder<Enchantment> enchantment : enchantments.keySet()) {
            if (enchantment.value().getMaxLevel() > 1) {
                candidates.add(enchantment);
            }
        }

        if (candidates.isEmpty()) {
            return;
        }

        Holder<Enchantment> chosen = candidates.get(player.getRandom().nextInt(candidates.size()));
        result.enchant(chosen, chosen.value().getMaxLevel() + 1);
    }
}

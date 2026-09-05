package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.item.equipment.trim.TrimPatterns;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public class EG$VillagerMixin {
    @Inject(method = "updateSpecialPrices", at = @At("TAIL"))
    private void eg$applyTrimDiscounts(Player player, CallbackInfo ci) {
        Villager self = (Villager) (Object) this;

        boolean hasEmeraldDiscount = TrimSetUtils.isWearingFullTrim(player, TrimMaterials.EMERALD);
        boolean hasHostDiscount = TrimSetUtils.isWearingFullPattern(player, TrimPatterns.HOST);
        if (!hasEmeraldDiscount && !hasHostDiscount) {
            return;
        }

        float discount = (hasEmeraldDiscount ? 0.15F : 0.0F) + (hasHostDiscount ? 0.1F : 0.0F);
        for (MerchantOffer offer : self.getOffers()) {
            int costReduction = Math.max(1, Mth.ceil(offer.getBaseCostA().getCount() * discount));
            offer.addToSpecialPriceDiff(-costReduction);
        }
    }
}

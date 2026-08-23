package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public class EG$VillagerMixin {
    @Inject(method = "updateSpecialPrices", at = @At("TAIL"))
    private void eg$applyEmeraldTrimDiscount(Player player, CallbackInfo ci) {
        Villager self = (Villager) (Object) this;

        if (TrimSetUtils.isWearingFullTrim(player, TrimMaterials.EMERALD)) {
            for (MerchantOffer offer : self.getOffers()) {
                int costReduction = Math.max(1, Mth.ceil(offer.getBaseCostA().getCount() * 0.15F));
                offer.addToSpecialPriceDiff(-costReduction);
            }
        }
    }
}

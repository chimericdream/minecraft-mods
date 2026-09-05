package com.chimericdream.archaeologytweaks.mixin;

import com.chimericdream.archaeologytweaks.villager.ModTradeSets;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTrader.class)
abstract public class AT$WanderingTraderMixin extends AT$AbstractVillagerMixin {
    @Inject(method = "updateTrades(Lnet/minecraft/server/level/ServerLevel;)V", at = @At("TAIL"))
    private void at$addTrimTrades(
        ServerLevel level,
        CallbackInfo ci,
        @Local(name = "offers") MerchantOffers offers
    ) {
        this.addOffersFromTradeSet(level, offers, ModTradeSets.WANDERING_TRADER_TRIMS);
    }
}

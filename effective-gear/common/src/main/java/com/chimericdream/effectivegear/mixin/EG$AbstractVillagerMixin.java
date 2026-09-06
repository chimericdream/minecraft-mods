package com.chimericdream.effectivegear.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.TradeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Shares the {@code addOffersFromTradeSet} shadow with {@link EG$WanderingTraderMixin}, which
 * extends this class so its target's mixin hierarchy mirrors the real {@code WanderingTrader
 * extends AbstractVillager} hierarchy (letting it call an inherited protected method without an
 * access widener).
 */
@Mixin(AbstractVillager.class)
abstract public class EG$AbstractVillagerMixin {
    @Shadow
    abstract protected void addOffersFromTradeSet(ServerLevel level, MerchantOffers offers, ResourceKey<TradeSet> resourceKey);
}

package com.chimericdream.pannotiacompanion.fabric.test;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.List;

/**
 * Our trades must be added on top of the vanilla wandering trader offers, never instead of them.
 */
@SuppressWarnings("unused")
public class PannotiaTradeGameTest {
    @GameTest
    public void wanderingTraderKeepsVanillaTradesAndGainsOurs(GameTestHelper context) {
        WanderingTrader trader = context.spawn(EntityTypes.WANDERING_TRADER, new BlockPos(1, 2, 1));
        List<MerchantOffer> offers = trader.getOffers().stream().toList();

        long headTrades = offers.stream()
            .filter(offer -> offer.getResult().is(Items.PLAYER_HEAD))
            .count();
        long vanillaTrades = offers.stream()
            .filter(offer -> !offer.getResult().is(Items.PLAYER_HEAD))
            .count();

        // 15-21 mini block trades + 2-5 streamer head trades
        if (headTrades < 17 || headTrades > 26) {
            throw context.assertionException("Expected 17-26 head trades, found " + headTrades);
        }
        if (vanillaTrades < 1) {
            throw context.assertionException("Vanilla trades were removed");
        }

        context.succeed();
    }
}

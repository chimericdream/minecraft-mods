package com.chimericdream.villagertweaks.trade;

import dev.architectury.networking.NetworkManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public final class TradePreviewNetworking {
    private TradePreviewNetworking() {
    }

    /** Registers the locked-trades packet. Called once from common init, which registers the payload type on both sides. */
    public static void init() {
        NetworkManager.registerReceiver(
            NetworkManager.s2c(),
            LockedTradesPayload.ID,
            LockedTradesPayload.CODEC,
            (payload, context) -> context.queue(() -> apply(context.getPlayer(), payload))
        );
    }

    /** Appends the locked trades to the open trading screen's offers, unless the screen they were meant for has already closed. */
    private static void apply(Player player, LockedTradesPayload payload) {
        if (!(player.containerMenu instanceof MerchantMenu menu) || menu.containerId != payload.containerId()) {
            return;
        }

        MerchantOffers offers = menu.getOffers();
        offers.removeIf(offer -> offer instanceof LockableOffer lockable && lockable.vt$isLocked());

        for (LockedTradesPayload.LockedTrade trade : payload.trades()) {
            MerchantOffer offer = trade.offer();
            ((LockableOffer) offer).vt$setUnlockLevel(trade.level());
            offers.add(offer);
        }
    }
}

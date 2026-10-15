package com.chimericdream.hopperxtreme.network;

import com.chimericdream.hopperxtreme.item.WrenchItem;
import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;

public final class WrenchNetworking {
    private WrenchNetworking() {
    }

    /** Registers the mode-switch packet. Called once from common init, which registers the payload type on both sides. */
    public static void init() {
        NetworkManager.registerReceiver(
            NetworkManager.c2s(),
            CycleWrenchModePayload.ID,
            CycleWrenchModePayload.CODEC,
            (payload, context) -> {
                if (context.getPlayer() instanceof ServerPlayer player) {
                    context.queue(() -> WrenchItem.cycleMode(player));
                }
            }
        );
    }
}

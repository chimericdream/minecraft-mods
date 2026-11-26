package com.chimericdream.enchantingfixes.network;

import com.chimericdream.enchantingfixes.client.EnchantingFixesClient;
import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import com.chimericdream.enchantingfixes.enchantment.ScalingEnchantments;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;

/**
 * Sends the server settings to each player when they join. A dedicated server reads its config only when
 * it starts, and the pack state also changes only then, so one packet on join is enough.
 */
public final class SettingsSync {
    private SettingsSync() {
    }

    public static void init() {
        // One registerReceiver call in common init registers the payload type on both sides. On a dedicated
        // server, the receiver is never used, so EnchantingFixesClient never loads. See the note in StackItUpMod.
        NetworkManager.registerReceiver(
            NetworkManager.s2c(),
            ServerSettingsPayload.ID,
            ServerSettingsPayload.CODEC,
            (payload, context) -> context.queue(() -> EnchantingFixesClient.applyServerSettings(payload))
        );
        PlayerEvent.PLAYER_JOIN.register(SettingsSync::sendTo);
    }

    private static void sendTo(ServerPlayer player) {
        if (NetworkManager.canPlayerReceive(player, ServerSettingsPayload.ID)) {
            NetworkManager.sendToPlayer(player, ServerSettingsPayload.of(EnchantingFixesConfig.CONFIG.instance(), ScalingEnchantments.isActive()));
        }
    }
}

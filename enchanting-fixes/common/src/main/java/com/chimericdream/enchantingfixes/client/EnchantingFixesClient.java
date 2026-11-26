package com.chimericdream.enchantingfixes.client;

import com.chimericdream.enchantingfixes.config.SyncedConfig;
import com.chimericdream.enchantingfixes.enchantment.ScalingEnchantments;
import com.chimericdream.enchantingfixes.network.ServerSettingsPayload;
import dev.architectury.event.events.client.ClientPlayerEvent;
import net.minecraft.client.Minecraft;

public final class EnchantingFixesClient {
    private EnchantingFixesClient() {
    }

    public static void init() {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
            SyncedConfig.setFromServer(null);
            ScalingEnchantments.setActiveFromServer(false);
        });
    }

    /**
     * In single player, and on the client of a LAN host, the client and the integrated server already share
     * the config and the pack state. A copy would not show later changes in the config screen, so this
     * keeps the local values.
     */
    public static void applyServerSettings(ServerSettingsPayload payload) {
        if (Minecraft.getInstance().isLocalServer()) {
            return;
        }

        SyncedConfig.setFromServer(payload.toConfig());
        ScalingEnchantments.setActiveFromServer(payload.scalingPack());
    }
}

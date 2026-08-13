package com.chimericdream.stackitup.network;

import java.util.List;

import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;

import com.chimericdream.stackitup.StackItUpMod;
import com.chimericdream.stackitup.config.ConfigManager;

import static com.chimericdream.stackitup.StackItUpMod.LOGGER;

public class NetworkHelper {
    public static void sentConfigToAll() {
        if (StackItUpMod.minecraftServer != null) {
            List<ServerPlayer> players = StackItUpMod.minecraftServer.getPlayerList().getPlayers();
            for (ServerPlayer player : players) {
                sentConfigToPlayer(player, ConfigManager.getConfigManager().getSerializedConfig());
            }
        } else {
            LOGGER.warn("[StackItUp] Server hasn't been loaded.");
        }
    }

    public static void sentConfigToPlayer(ServerPlayer player, byte[] data) {
        ByteArrayPayload payload = new ByteArrayPayload(data);
        NetworkManager.sendToPlayer(player, payload);
    }
}

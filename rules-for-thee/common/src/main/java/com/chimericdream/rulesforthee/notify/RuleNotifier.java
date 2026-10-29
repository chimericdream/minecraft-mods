package com.chimericdream.rulesforthee.notify;

import com.chimericdream.rulesforthee.data.RulesData;
import com.chimericdream.rulesforthee.rule.ClientSync;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * Tells players when their rules change. The wording is literal text, not translation keys: this mod is
 * server-side only, and a vanilla client has no language entries for keys it doesn't know.
 */
public final class RuleNotifier {
    private RuleNotifier() {
    }

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(RuleNotifier::notifyIfChanged);
    }

    /** Call after changing a player's profile, profile contents, or overrides. Offline players are told on login. */
    public static void refresh(MinecraftServer server, UUID player) {
        ServerPlayer online = server.getPlayerList().getPlayer(player);
        if (online != null) {
            // The client caches a few rules, so it has to be told even when the player needs no message.
            ClientSync.sync(online);
            notifyIfChanged(online);
        }
    }

    public static void notifyIfChanged(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        RuleNotices.takePending(RulesData.get(server), player.getUUID(), player.getGameProfile().name())
            .ifPresent(lines -> lines.forEach(line -> player.sendSystemMessage(
                Component.literal(line).withStyle(line.startsWith("  ") ? ChatFormatting.YELLOW : ChatFormatting.GOLD)
            )));
    }
}

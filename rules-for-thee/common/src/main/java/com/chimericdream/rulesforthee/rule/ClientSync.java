package com.chimericdream.rulesforthee.rule;

import com.chimericdream.rulesforthee.data.RulesData;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.List;

/**
 * A few game rules are cached by the client rather than read from the server each time: whether to show the
 * death screen ({@code doImmediateRespawn}) and whether to show reduced debug info. Vanilla sends the world's
 * value in the login packet and again to every player when {@code /gamerule} changes it.
 *
 * <p>A player with an override has to be told their own value instead. This sends it when they join (after
 * the login packet) and whenever their effective rules change, and again after a {@code /gamerule} change has
 * been broadcast, since that broadcast resets everyone to the world's value. The client keeps both flags
 * across a respawn, so nothing is needed on death.
 */
public final class ClientSync {
    private ClientSync() {
    }

    public static void init() {
        PlayerEvent.PLAYER_JOIN.register(ClientSync::sync);
    }

    /** The packets that set this player's client-cached rules to their effective values. */
    public static List<Packet<?>> packets(ServerPlayer player) {
        GameRules world = player.level().getGameRules();
        boolean immediateRespawn = PlayerRules.resolve(player, GameRules.IMMEDIATE_RESPAWN, world.get(GameRules.IMMEDIATE_RESPAWN));
        boolean reducedDebugInfo = PlayerRules.resolve(player, GameRules.REDUCED_DEBUG_INFO, world.get(GameRules.REDUCED_DEBUG_INFO));

        return List.of(
            new ClientboundGameEventPacket(ClientboundGameEventPacket.IMMEDIATE_RESPAWN, immediateRespawn ? 1.0F : 0.0F),
            new ClientboundEntityEventPacket(player, reducedDebugInfo ? EntityEvent.REDUCED_DEBUG_INFO : EntityEvent.FULL_DEBUG_INFO)
        );
    }

    public static void sync(ServerPlayer player) {
        if (player.connection == null) {
            return;
        }

        packets(player).forEach(player.connection::send);
    }

    /**
     * Called after vanilla has broadcast a changed world rule. Players with their own value for that rule
     * were just sent the world's, so send theirs again.
     */
    public static void afterWorldRuleChanged(MinecraftServer server, GameRule<?> rule) {
        if (rule != GameRules.IMMEDIATE_RESPAWN && rule != GameRules.REDUCED_DEBUG_INFO) {
            return;
        }

        RulesData data = RulesData.get(server);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (EffectiveRules.override(data, player.getUUID(), rule).isPresent()) {
                sync(player);
            }
        }
    }
}

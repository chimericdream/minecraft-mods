package com.chimericdream.rulesforthee.fabric.test;

import com.chimericdream.rulesforthee.data.RulesData;
import com.chimericdream.rulesforthee.rule.ClientSync;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.List;

/**
 * The rules the client caches (death screen, reduced debug info) are sent as packets. A mock player has no
 * connection to send them on, so these check the packets that would be sent.
 */
@SuppressWarnings("unused")
public class ClientSyncGameTest {
    private static ServerPlayer mockPlayer(GameTestHelper context) {
        return (ServerPlayer) context.makeMockServerPlayer(GameType.SURVIVAL);
    }

    private static <T> void withWorld(MinecraftServer server, GameRule<T> rule, T value, Runnable body) {
        GameRules rules = server.overworld().getGameRules();
        T previous = rules.get(rule);
        rules.set(rule, value, server);
        try {
            body.run();
        } finally {
            rules.set(rule, previous, server);
        }
    }

    private static float immediateRespawn(List<Packet<?>> packets) {
        return packets.stream()
            .filter(ClientboundGameEventPacket.class::isInstance)
            .map(ClientboundGameEventPacket.class::cast)
            .filter(packet -> packet.getEvent() == ClientboundGameEventPacket.IMMEDIATE_RESPAWN)
            .findFirst().orElseThrow().getParam();
    }

    private static byte debugInfo(List<Packet<?>> packets) {
        return packets.stream()
            .filter(ClientboundEntityEventPacket.class::isInstance)
            .map(ClientboundEntityEventPacket.class::cast)
            .findFirst().orElseThrow().getEventId();
    }

    @GameTest
    public void packetsCarryTheWorldValueWithoutAnOverride(GameTestHelper context) {
        MinecraftServer server = context.getLevel().getServer();
        ServerPlayer player = mockPlayer(context);

        withWorld(server, GameRules.IMMEDIATE_RESPAWN, false, () ->
            context.assertTrue(immediateRespawn(ClientSync.packets(player)) == 0.0F, "world off: show the death screen"));
        withWorld(server, GameRules.IMMEDIATE_RESPAWN, true, () ->
            context.assertTrue(immediateRespawn(ClientSync.packets(player)) == 1.0F, "world on: respawn immediately"));
        withWorld(server, GameRules.REDUCED_DEBUG_INFO, true, () ->
            context.assertTrue(debugInfo(ClientSync.packets(player)) == EntityEvent.REDUCED_DEBUG_INFO, "world on: reduced"));
        withWorld(server, GameRules.REDUCED_DEBUG_INFO, false, () ->
            context.assertTrue(debugInfo(ClientSync.packets(player)) == EntityEvent.FULL_DEBUG_INFO, "world off: full"));
        context.succeed();
    }

    @GameTest
    public void packetsCarryThePlayersOverride(GameTestHelper context) {
        MinecraftServer server = context.getLevel().getServer();
        ServerPlayer player = mockPlayer(context);
        RulesData data = RulesData.get(server);

        try {
            data.entry(player.getUUID()).overrides().set(GameRules.IMMEDIATE_RESPAWN, true);
            data.entry(player.getUUID()).overrides().set(GameRules.REDUCED_DEBUG_INFO, true);

            withWorld(server, GameRules.IMMEDIATE_RESPAWN, false, () ->
                context.assertTrue(immediateRespawn(ClientSync.packets(player)) == 1.0F, "override on, world off: respawn immediately"));
            withWorld(server, GameRules.REDUCED_DEBUG_INFO, false, () ->
                context.assertTrue(debugInfo(ClientSync.packets(player)) == EntityEvent.REDUCED_DEBUG_INFO, "override on, world off: reduced"));

            data.entry(player.getUUID()).overrides().set(GameRules.IMMEDIATE_RESPAWN, false);
            withWorld(server, GameRules.IMMEDIATE_RESPAWN, true, () ->
                context.assertTrue(immediateRespawn(ClientSync.packets(player)) == 0.0F, "override off, world on: show the death screen"));
            context.succeed();
        } finally {
            data.removePlayer(player.getUUID());
        }
    }

    @GameTest
    public void aWorldRuleChangeWithNoOnlinePlayersIsHarmless(GameTestHelper context) {
        // Exercises the onGameRuleChanged hook: changing the world rule goes through the vanilla broadcast and
        // then our re-send, which must cope with no online players and with rules it doesn't handle.
        MinecraftServer server = context.getLevel().getServer();

        withWorld(server, GameRules.IMMEDIATE_RESPAWN, true, () -> { });
        withWorld(server, GameRules.REDUCED_DEBUG_INFO, true, () -> { });
        withWorld(server, GameRules.FALL_DAMAGE, false, () -> { });
        context.succeed();
    }
}

package com.chimericdream.rulesforthee.fabric.test;

import com.chimericdream.rulesforthee.data.RulesData;
import com.chimericdream.rulesforthee.rule.PlayerRules;
import com.chimericdream.rulesforthee.rule.SubjectScope;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;

/** The integer rules and the smaller hooks: portal delays, command feedback, and the scoped respawn radius. */
@SuppressWarnings("unused")
public class EasyRulesGameTest {
    private static ServerPlayer mockPlayer(GameTestHelper context, GameType type) {
        return (ServerPlayer) context.makeMockServerPlayer(type);
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

    @GameTest
    public void portalDelayFollowsTheOverrideForSurvivalAndCreative(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        MinecraftServer server = level.getServer();
        RulesData data = RulesData.get(server);
        NetherPortalBlock portal = (NetherPortalBlock) Blocks.NETHER_PORTAL;
        ServerPlayer survival = mockPlayer(context, GameType.SURVIVAL);
        ServerPlayer creative = mockPlayer(context, GameType.CREATIVE);

        try {
            withWorld(server, GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY, 80, () -> {
                context.assertTrue(portal.getPortalTransitionTime(level, survival) == 80, "no override: the world's delay");

                data.entry(survival.getUUID()).overrides().set(GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY, 7);
                context.assertTrue(portal.getPortalTransitionTime(level, survival) == 7, "override: the player's delay");
                // The override is per player.
                context.assertTrue(portal.getPortalTransitionTime(level, creative) != 7, "someone else keeps the world's delay");
            });

            // A creative player uses the other rule.
            data.entry(creative.getUUID()).overrides().set(GameRules.PLAYERS_NETHER_PORTAL_CREATIVE_DELAY, 3);
            context.assertTrue(portal.getPortalTransitionTime(level, creative) == 3, "creative override: the creative delay");

            // The rule's own codec rejects negative values, so an out-of-range stored value is ignored and the
            // player gets the world's delay rather than a broken one.
            withWorld(server, GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY, 80, () -> {
                data.entry(survival.getUUID()).overrides().set(GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY, -5);
                context.assertTrue(
                    portal.getPortalTransitionTime(level, survival) == 80,
                    "an invalid stored value falls back to the world's: " + portal.getPortalTransitionTime(level, survival)
                );
            });
            context.succeed();
        } finally {
            data.removePlayer(survival.getUUID());
            data.removePlayer(creative.getUUID());
        }
    }

    @GameTest
    public void commandFeedbackFollowsTheOverride(GameTestHelper context) {
        MinecraftServer server = context.getLevel().getServer();
        RulesData data = RulesData.get(server);
        ServerPlayer player = mockPlayer(context, GameType.SURVIVAL);

        try {
            withWorld(server, GameRules.SEND_COMMAND_FEEDBACK, true, () -> {
                context.assertTrue(player.commandSource().acceptsSuccess(), "no override: feedback is sent");

                data.entry(player.getUUID()).overrides().set(GameRules.SEND_COMMAND_FEEDBACK, false);
                context.assertTrue(!player.commandSource().acceptsSuccess(), "override off: no feedback");
            });

            withWorld(server, GameRules.SEND_COMMAND_FEEDBACK, false, () -> {
                data.entry(player.getUUID()).overrides().set(GameRules.SEND_COMMAND_FEEDBACK, true);
                context.assertTrue(player.commandSource().acceptsSuccess(), "override on, world off: feedback is sent");
            });
            context.succeed();
        } finally {
            data.removePlayer(player.getUUID());
        }
    }

    @GameTest
    public void respawnRadiusIsReadForThePlayerInTheScope(GameTestHelper context) {
        MinecraftServer server = context.getLevel().getServer();
        RulesData data = RulesData.get(server);
        ServerPlayer player = mockPlayer(context, GameType.SURVIVAL);

        try {
            data.entry(player.getUUID()).overrides().set(GameRules.RESPAWN_RADIUS, 2);

            context.assertTrue(SubjectScope.current().isEmpty(), "no scope is open");

            try (SubjectScope ignored = SubjectScope.enter(player)) {
                context.assertTrue(SubjectScope.current().orElseThrow() == player, "the scope names the player");
                int radius = (int) PlayerRules.resolveUnchecked(SubjectScope.current().orElseThrow(), GameRules.RESPAWN_RADIUS, 10);
                context.assertTrue(radius == 2, "the scoped player's radius is used: " + radius);

                // Scopes nest and unwind.
                ServerPlayer other = mockPlayer(context, GameType.SURVIVAL);
                try (SubjectScope nested = SubjectScope.enter(other)) {
                    context.assertTrue(SubjectScope.current().orElseThrow() == other, "the innermost scope wins");
                }
                context.assertTrue(SubjectScope.current().orElseThrow() == player, "the outer scope is restored");
            }

            context.assertTrue(SubjectScope.current().isEmpty(), "the scope closed");
            context.succeed();
        } finally {
            data.removePlayer(player.getUUID());
        }
    }

    @GameTest
    public void integerRulesCanBeSetThroughTheirCommandArgument(GameTestHelper context) {
        MinecraftServer server = context.getLevel().getServer();
        // The value argument is each rule's own vanilla argument type, so an integer rule needs a number.
        var dispatcher = server.getCommands().getDispatcher();
        var parsed = dispatcher.parse("rulesforthee profile edit x set respawn_radius 4", server.createCommandSourceStack());
        var bad = dispatcher.parse("rulesforthee profile edit x set respawn_radius yes", server.createCommandSourceStack());

        context.assertTrue(parsed.getReader().canRead() == false, "an integer is accepted");
        context.assertTrue(bad.getReader().canRead(), "a non-number is not");
        context.succeed();
    }
}

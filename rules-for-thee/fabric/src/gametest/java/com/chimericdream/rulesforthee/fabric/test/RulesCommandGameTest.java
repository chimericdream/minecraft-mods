package com.chimericdream.rulesforthee.fabric.test;

import com.chimericdream.rulesforthee.data.PlayerEntry;
import com.chimericdream.rulesforthee.data.RulesData;
import com.chimericdream.rulesforthee.rule.EffectiveRules;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Runs the real command tree against a live server and checks the saved data and the lookups.
 *
 * <p>Commands target players by name. The test server runs in offline mode, so a name resolves to the
 * offline UUID derived from it, and every mock player is named alike, so every test targets the same UUID.
 * Tests run one at a time on the server thread and each cleans up after itself.
 */
@SuppressWarnings("unused")
public class RulesCommandGameTest {
    /** What a command did: its result (0 means failure) and anything it said, so a failing check explains itself. */
    private record Outcome(String command, int result, List<String> output) {
        boolean ok() {
            return result > 0;
        }

        @Override
        public String toString() {
            return "'" + command + "' -> " + result + " " + output;
        }
    }

    /** The player the commands will affect: the one the server resolves the mock player's name to. */
    private record Target(String name, UUID id) {
        static Target of(ServerPlayer player) {
            String name = player.getGameProfile().name();

            return new Target(name, NameAndId.createOffline(name).id());
        }
    }

    /**
     * Mock players have no network connection, so output goes to a collector instead of the player. A
     * parse failure surfaces as a failure message.
     */
    private static Outcome run(MinecraftServer server, CommandSourceStack source, String command) {
        AtomicInteger result = new AtomicInteger(-1);
        List<String> output = new ArrayList<>();
        CommandSource collector = new CommandSource() {
            @Override
            public void sendSystemMessage(Component message) {
                output.add(message.getString());
            }

            @Override
            public boolean acceptsSuccess() {
                return true;
            }

            @Override
            public boolean acceptsFailure() {
                return true;
            }

            @Override
            public boolean shouldInformAdmins() {
                return false;
            }
        };

        server.getCommands().performPrefixedCommand(
            source.withSource(collector).withCallback((success, value) -> result.set(success ? value : 0)),
            command
        );

        return new Outcome(command, result.get(), output);
    }

    private static ServerPlayer mockPlayer(GameTestHelper context) {
        return (ServerPlayer) context.makeMockServerPlayer(GameType.SURVIVAL);
    }

    private static boolean keepsInventory(RulesData data, Target target, boolean worldValue) {
        return EffectiveRules.resolve(data, target.id(), GameRules.KEEP_INVENTORY, worldValue);
    }

    private static void cleanUp(RulesData data, Target target, String... profiles) {
        data.removePlayer(target.id());
        for (String profile : profiles) {
            data.deleteProfile(profile);
        }
    }

    @GameTest
    public void profileCommandsChangeThePlayersEffectiveRules(GameTestHelper context) {
        MinecraftServer server = context.getLevel().getServer();
        Target target = Target.of(mockPlayer(context));
        RulesData data = RulesData.get(server);
        CommandSourceStack console = server.createCommandSourceStack();

        try {
            Outcome create = run(server, console, "rulesforthee profile create gt_kids");
            context.assertTrue(create.ok(), create.toString());
            Outcome set = run(server, console, "rulesforthee profile edit gt_kids set keep_inventory true");
            context.assertTrue(set.ok(), set.toString());
            Outcome assign = run(server, console, "rulesforthee player " + target.name() + " profile set gt_kids");
            context.assertTrue(assign.ok(), assign.toString());

            context.assertTrue(keepsInventory(data, target, false), "the profile's override should apply");
            context.assertTrue(data.entry(target.id()).profile().equals(Optional.of("gt_kids")), "profile recorded");

            // The player's own override beats the profile.
            run(server, console, "rulesforthee player " + target.name() + " set keep_inventory false");
            context.assertTrue(!keepsInventory(data, target, true), "the player's own value should beat the profile");

            run(server, console, "rulesforthee player " + target.name() + " clear keep_inventory");
            context.assertTrue(keepsInventory(data, target, false), "clearing the override falls back to the profile");
            context.succeed();
        } finally {
            cleanUp(data, target, "gt_kids");
        }
    }

    @GameTest
    public void aBadProfileOrNameFails(GameTestHelper context) {
        MinecraftServer server = context.getLevel().getServer();
        Target target = Target.of(mockPlayer(context));
        RulesData data = RulesData.get(server);
        CommandSourceStack console = server.createCommandSourceStack();

        try {
            Outcome unknown = run(server, console, "rulesforthee player " + target.name() + " profile set gt_missing");
            context.assertTrue(!unknown.ok(), "unknown profile: " + unknown);
            Outcome badName = run(server, console, "rulesforthee profile create Bad_Name");
            context.assertTrue(!badName.ok(), "invalid name: " + badName);
            Outcome first = run(server, console, "rulesforthee profile create gt_dupe");
            context.assertTrue(first.ok(), "first create: " + first);
            Outcome dupe = run(server, console, "rulesforthee profile create gt_dupe");
            context.assertTrue(!dupe.ok(), "duplicate: " + dupe);
            Outcome del = run(server, console, "rulesforthee profile delete gt_missing");
            context.assertTrue(!del.ok(), "delete unknown: " + del);
            context.assertTrue(data.playerEntry(target.id()).flatMap(PlayerEntry::profile).isEmpty(), "no profile was assigned");
            context.succeed();
        } finally {
            cleanUp(data, target, "gt_dupe");
        }
    }

    @GameTest
    public void nonOpsCanOnlyUseMine(GameTestHelper context) {
        MinecraftServer server = context.getLevel().getServer();
        ServerPlayer player = mockPlayer(context);
        Target target = Target.of(player);
        RulesData data = RulesData.get(server);
        // A mock player isn't an operator, so a source built from the player has ordinary permissions.
        CommandSourceStack asPlayer = player.createCommandSourceStack();

        try {
            Outcome create = run(server, asPlayer, "rulesforthee profile create gt_nope");
            context.assertTrue(!create.ok(), "profile create needs ops: " + create);
            context.assertTrue(data.profile("gt_nope").isEmpty(), "profile was not created");
            Outcome mine = run(server, asPlayer, "rulesforthee mine");
            context.assertTrue(mine.ok(), "mine is open to everyone: " + mine);
            context.succeed();
        } finally {
            cleanUp(data, target, "gt_nope");
        }
    }

    @GameTest
    public void deletingAProfileRemovesItFromMembers(GameTestHelper context) {
        MinecraftServer server = context.getLevel().getServer();
        Target target = Target.of(mockPlayer(context));
        RulesData data = RulesData.get(server);
        CommandSourceStack console = server.createCommandSourceStack();

        try {
            run(server, console, "rulesforthee profile create gt_gone");
            run(server, console, "rulesforthee profile edit gt_gone set keep_inventory true");
            Outcome assign = run(server, console, "rulesforthee player " + target.name() + " profile set gt_gone");
            context.assertTrue(assign.ok(), assign.toString());
            context.assertTrue(keepsInventory(data, target, false), "applies before delete");

            run(server, console, "rulesforthee profile delete gt_gone");

            context.assertTrue(!keepsInventory(data, target, false), "no longer applies");
            context.assertTrue(data.entry(target.id()).profile().isEmpty(), "membership removed");
            context.succeed();
        } finally {
            cleanUp(data, target, "gt_gone");
        }
    }
}

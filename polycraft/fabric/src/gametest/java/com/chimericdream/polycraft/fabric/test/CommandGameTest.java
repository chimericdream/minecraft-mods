package com.chimericdream.polycraft.fabric.test;

import com.chimericdream.polycraft.config.PolyCraftConfig;
import com.chimericdream.polycraft.data.PolyCraftChoices;
import com.chimericdream.polycraft.rules.PolyCraftRules;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.nbt.Tag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * M2: the {@code /polycraft} commands, the rules files, and saved player choices. The fixture datapack under
 * {@code gametest/resources/data/polycraft_test} adds four conflicting recipes for two cactus (a and the copy make
 * 3 green dye, b makes 5 green dye, c makes lime dye) and two sweet-berry recipes, eight amethyst-shard recipes (one conflict with many options), two smelting recipes for gunpowder, plus a rules file that
 * prefers recipe b and excludes recipe c.
 */
@SuppressWarnings("unused")
public class CommandGameTest {
    private static final String GROUPS_IN_FIXTURE = "polycraft conflicts polycraft_test";

    private static ServerPlayer player(GameTestHelper context) {
        return (ServerPlayer) context.makeMockServerPlayerInLevel();
    }

    private static CommandSourceStack op(ServerPlayer player) {
        return player.createCommandSourceStack().withPermission(LevelBasedPermissionSet.GAMEMASTER);
    }

    private static int run(GameTestHelper context, CommandSourceStack source, String command) throws CommandSyntaxException {
        return context.getLevel().getServer().getCommands().getDispatcher().execute(command, source);
    }

    private static boolean refused(GameTestHelper context, CommandSourceStack source, String command) {
        try {
            run(context, source, command);
            return false;
        } catch (CommandSyntaxException e) {
            return true;
        }
    }

    private static Path exportFile() {
        return PolyCraftRules.configFile().resolveSibling("conflicts.json");
    }

    @GameTest
    public void datapackRulesLoad(GameTestHelper context) {
        context.assertTrue(PolyCraftRules.current().priority().contains("polycraft_test:two_cactus_b"), "the datapack's priority entry should load");
        context.assertTrue(PolyCraftRules.current().isExcluded("polycraft_test:two_cactus_c"), "the datapack's excluded recipe should load");
        context.succeed();
    }

    @GameTest
    public void conflictsListsTheFixtureGroups(GameTestHelper context) throws CommandSyntaxException {
        ServerPlayer player = player(context);

        context.assertTrue(run(context, player.createCommandSourceStack(), GROUPS_IN_FIXTURE) == 6, "the cactus, berry, shard, gunpowder, smithing, and campfire groups should be listed for a normal player");
        context.succeed();
    }

    @GameTest
    public void conflictsForAnUnknownModIsEmpty(GameTestHelper context) throws CommandSyntaxException {
        context.assertTrue(run(context, player(context).createCommandSourceStack(), "polycraft conflicts nosuchmod") == 0, "no conflicts should be listed for an unknown mod");
        context.succeed();
    }

    @GameTest
    public void exportWritesCollapsedAndFilteredOptions(GameTestHelper context) throws CommandSyntaxException, IOException {
        Path file = exportFile();
        Files.deleteIfExists(file);
        try {
            int groups = run(context, op(player(context)), "polycraft export");
            context.assertTrue(groups >= 2, "the export should include at least the fixture groups");
            context.assertTrue(Files.exists(file), "the export file should exist");

            String text = Files.readString(file);
            context.assertTrue(text.contains("polycraft_test:two_cactus_a"), "recipe a should be an option");
            context.assertTrue(text.contains("polycraft_test:two_cactus_b"), "recipe b (same item, different count) should be an option");
            context.assertTrue(!text.contains("polycraft_test:two_cactus_copy"), "the copy of recipe a gives the same result, so it should collapse");
            context.assertTrue(!text.contains("polycraft_test:two_cactus_c"), "the excluded recipe should not be an option");
            context.assertTrue(text.contains("polycraft_test:gunpowder_slow") && text.contains("polycraft_test:gunpowder_fast"), "both gunpowder recipes should be options");
        } finally {
            Files.deleteIfExists(file);
        }

        context.succeed();
    }

    @GameTest
    public void exportAndReloadNeedOperatorPermission(GameTestHelper context) throws CommandSyntaxException {
        ServerPlayer player = player(context);
        CommandSourceStack normal = player.createCommandSourceStack();

        context.assertTrue(refused(context, normal, "polycraft export"), "export should be refused for a normal player");
        context.assertTrue(refused(context, normal, "polycraft reload"), "reload should be refused for a normal player");
        context.assertTrue(run(context, op(player), "polycraft reload") == 1, "reload should work for an operator");
        context.succeed();
    }

    @GameTest
    public void resetOnlyForgetsTheCallersChoices(GameTestHelper context) throws CommandSyntaxException {
        MinecraftServer server = context.getLevel().getServer();
        ServerPlayer alex = player(context);
        ServerPlayer sam = player(context);
        PolyCraftChoices choices = PolyCraftChoices.get(server);
        choices.reset(alex.getUUID());
        choices.reset(sam.getUUID());
        choices.choose(alex.getUUID(), "group-1", "a:one");
        choices.choose(alex.getUUID(), "group-2", "a:two");
        choices.choose(sam.getUUID(), "group-1", "a:three");

        int removed = run(context, alex.createCommandSourceStack(), "polycraft reset");

        context.assertTrue(removed == 2, "resetting should forget both of the caller's choices");
        context.assertTrue(choices.choiceFor(alex.getUUID(), "group-1").isEmpty(), "the caller's choice should be gone");
        context.assertTrue(choices.choiceFor(sam.getUUID(), "group-1").isPresent(), "another player's choice should remain");
        choices.reset(sam.getUUID());
        context.succeed();
    }

    @GameTest
    public void resettingAnotherPlayerNeedsAnOperator(GameTestHelper context) throws CommandSyntaxException {
        ServerPlayer alex = player(context);
        player(context);
        MinecraftServer server = context.getLevel().getServer();

        // every mock player shares one name, and a name selector picks the first match, so ask the server who that is
        String name = alex.getGameProfile().name();
        ServerPlayer target = server.getPlayerList().getPlayerByName(name);
        context.assertTrue(target != null, "the named player should exist");

        PolyCraftChoices choices = PolyCraftChoices.get(server);
        choices.reset(target.getUUID());
        choices.choose(target.getUUID(), "group-1", "a:one");
        String command = "polycraft reset " + name;

        context.assertTrue(refused(context, alex.createCommandSourceStack(), command), "a normal player can't reset someone else");
        context.assertTrue(choices.choiceFor(target.getUUID(), "group-1").isPresent(), "the other player's choice should be untouched");
        context.assertTrue(run(context, op(alex), command) == 1, "an operator can reset another player");
        context.assertTrue(choices.choiceFor(target.getUUID(), "group-1").isEmpty(), "the other player's choice should be gone");
        context.succeed();
    }

    @GameTest
    public void rememberChoicesOffStopsStoring(GameTestHelper context) {
        PolyCraftChoices choices = PolyCraftChoices.get(context.getLevel().getServer());
        UUID id = UUID.randomUUID();
        PolyCraftConfig config = PolyCraftConfig.get();
        boolean original = config.rememberChoices;
        try {
            config.rememberChoices = false;
            choices.choose(id, "group", "a:one");
            config.rememberChoices = true;
            context.assertTrue(choices.choiceFor(id, "group").isEmpty(), "nothing should have been stored while remembering was off");

            choices.choose(id, "group", "a:one");
            config.rememberChoices = false;
            context.assertTrue(choices.choiceFor(id, "group").isEmpty(), "reading is also off, even though a choice is stored");
        } finally {
            config.rememberChoices = original;
            choices.reset(id);
        }

        context.succeed();
    }

    @GameTest
    public void choicesSurviveSavingAndLoadingInOrder(GameTestHelper context) {
        UUID id = UUID.randomUUID();
        PolyCraftChoices original = new PolyCraftChoices();
        original.choose(id, "group-1", "a:one");
        original.choose(id, "group-2", "a:two");

        DataResult<Tag> encoded = PolyCraftChoices.CODEC.encodeStart(NbtOps.INSTANCE, original);
        PolyCraftChoices loaded = PolyCraftChoices.CODEC.parse(NbtOps.INSTANCE, encoded.getOrThrow()).getOrThrow();

        context.assertTrue(loaded.choiceFor(id, "group-1").equals(java.util.Optional.of("a:one")), "first choice should survive");
        context.assertTrue(loaded.choiceFor(id, "group-2").equals(java.util.Optional.of("a:two")), "second choice should survive");
        context.succeed();
    }
}

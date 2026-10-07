package com.chimericdream.opus.fabric.test;

import com.chimericdream.opus.component.BookIdComponent;
import com.chimericdream.opus.component.OpusComponentTypes;
import com.chimericdream.opus.item.OpusItems;
import com.chimericdream.opus.config.OpusGuidesConfig;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** {@code /opus give}: a non-op player gets the book; the default id and an explicit id both work. */
@SuppressWarnings("unused")
public class GiveCommandGameTest {
    private static ItemStack findBook(ServerPlayer player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(OpusItems.BOOK.get())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static void run(GameTestHelper context, ServerPlayer player, String command) {
        var server = context.getLevel().getServer();
        server.getCommands().performPrefixedCommand(player.createCommandSourceStack(), command);
    }

    @GameTest
    public void givesDefaultGuideToNonOp(GameTestHelper context) {
        ServerPlayer player = (ServerPlayer) context.makeMockServerPlayerInLevel();
        run(context, player, "opus give");

        ItemStack book = findBook(player);
        context.assertTrue(!book.isEmpty(), "a non-op player should receive a book");
        BookIdComponent id = book.get(OpusComponentTypes.BOOK_ID.get());
        context.assertTrue(id != null && id.bookId().equals("opus:guide"), "the default guide id should be opus:guide");
        context.succeed();
    }

    @GameTest
    public void givesNamedBook(GameTestHelper context) {
        ServerPlayer player = (ServerPlayer) context.makeMockServerPlayerInLevel();
        run(context, player, "opus give mymod:field_guide");

        BookIdComponent id = findBook(player).get(OpusComponentTypes.BOOK_ID.get());
        context.assertTrue(id != null && id.bookId().equals("mymod:field_guide"), "the named book id should be set");
        context.succeed();
    }

    @GameTest
    public void malformedIdGivesNothing(GameTestHelper context) {
        ServerPlayer player = (ServerPlayer) context.makeMockServerPlayerInLevel();
        run(context, player, "opus give Not A Valid Id");

        context.assertTrue(findBook(player).isEmpty(), "a malformed id should not give a book");
        context.succeed();
    }

    /** Runs {@code body} with {@code opsOnly} on, then restores the config file exactly as it was. */
    private static void withOpsOnly(GameTestHelper context, Runnable body) {
        Path file = OpusGuidesConfig.path();
        byte[] original = null;
        try {
            original = Files.exists(file) ? Files.readAllBytes(file) : null;
            Files.createDirectories(file.getParent());
            Files.writeString(file, "{\"opsOnly\": true}\n");
            OpusGuidesConfig.invalidate();
            body.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (original == null) {
                    Files.deleteIfExists(file);
                } else {
                    Files.write(file, original);
                }
            } catch (IOException ignored) {
            }
            OpusGuidesConfig.invalidate();
        }
    }

    @GameTest
    public void opsOnlyRefusesNonOp(GameTestHelper context) {
        ServerPlayer player = (ServerPlayer) context.makeMockServerPlayerInLevel();
        context.assertTrue(!player.createCommandSourceStack().permissions().hasPermission(
            new net.minecraft.server.permissions.Permission.HasCommandLevel(net.minecraft.server.permissions.PermissionLevel.GAMEMASTERS)),
            "precondition: the mock player must not have gamemaster permission");

        withOpsOnly(context, () -> run(context, player, "opus give"));

        context.assertTrue(findBook(player).isEmpty(), "a non-op should be refused when opsOnly is set");
        context.succeed();
    }

    @GameTest
    public void opsOnlyAllowsOp(GameTestHelper context) {
        ServerPlayer player = (ServerPlayer) context.makeMockServerPlayerInLevel();

        withOpsOnly(context, () -> context.getLevel().getServer().getCommands().performPrefixedCommand(
            player.createCommandSourceStack().withPermission(LevelBasedPermissionSet.GAMEMASTER), "opus give"));

        context.assertTrue(!findBook(player).isEmpty(), "an op should still be allowed when opsOnly is set");
        context.succeed();
    }
}

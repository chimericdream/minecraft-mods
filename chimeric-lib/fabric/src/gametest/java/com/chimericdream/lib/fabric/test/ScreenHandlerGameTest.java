package com.chimericdream.lib.fabric.test;

import com.chimericdream.lib.fabric.test.fixture.TestFixtures;
import com.chimericdream.lib.screen.DoubleWideInventoryScreenHandler;
import com.chimericdream.lib.screen.SimpleInventoryScreenHandler;
import com.chimericdream.lib.testkit.gametest.GameTestContainers;
import com.chimericdream.lib.testkit.gametest.GameTestMenus;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Opens the library's {@code SimpleInventoryScreenHandler} and {@code DoubleWideInventoryScreenHandler}
 * server-side over a fixture container with a mock player, and verifies slot layout and shift-click
 * ("quick move") routing in both directions.
 */
@SuppressWarnings("unused")
public class ScreenHandlerGameTest {
    @GameTest
    public void simpleHandlerLayoutAndQuickMoveBothWays(GameTestHelper context) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        Container container = new SimpleContainer(TestFixtures.SIMPLE_ROWS * 9);

        SimpleInventoryScreenHandler menu = new SimpleInventoryScreenHandler(
            TestFixtures.SIMPLE_MENU.get(), 1, player.getInventory(), container, TestFixtures.SIMPLE_ROWS
        );

        int expectedSlots = TestFixtures.SIMPLE_ROWS * 9 + 27 + 9;
        GameTestMenus.assertSlotCount(context, menu, expectedSlots);

        // container -> player: shift-clicking a container slot empties it into the player inventory.
        container.setItem(0, new ItemStack(Items.DIAMOND, 10));
        menu.quickMoveStack(player, 0);
        GameTestContainers.assertSlotEmpty(context, container, 0);

        // player -> container: shift-clicking a player slot routes back into the now-empty container.
        int firstPlayerSlot = container.getContainerSize();
        menu.getSlot(firstPlayerSlot).set(new ItemStack(Items.EMERALD, 5));
        menu.quickMoveStack(player, firstPlayerSlot);
        GameTestContainers.assertSlot(context, container, 0, Items.EMERALD, 5);

        context.succeed();
    }

    @GameTest
    public void doubleWideHandlerLayoutAndQuickMove(GameTestHelper context) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        Container container = new SimpleContainer(TestFixtures.DOUBLE_ROWS * 18);

        DoubleWideInventoryScreenHandler menu = new DoubleWideInventoryScreenHandler(
            TestFixtures.DOUBLE_MENU.get(), 1, player.getInventory(), container, TestFixtures.DOUBLE_ROWS
        );

        int expectedSlots = TestFixtures.DOUBLE_ROWS * 18 + 27 + 9;
        GameTestMenus.assertSlotCount(context, menu, expectedSlots);

        container.setItem(0, new ItemStack(Items.DIAMOND, 4));
        menu.quickMoveStack(player, 0);
        GameTestContainers.assertSlotEmpty(context, container, 0);

        context.succeed();
    }

    /**
     * Both handlers call {@code startOpen} in their constructor but used to never call
     * {@code stopOpen}, so a container backed by a {@code ContainerOpenersCounter} kept counting a
     * viewer that had already closed the screen.
     */
    @GameTest
    public void simpleHandlerBalancesOpenAndClose(GameTestHelper context) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        OpenCountingContainer container = new OpenCountingContainer(TestFixtures.SIMPLE_ROWS * 9);

        SimpleInventoryScreenHandler menu = new SimpleInventoryScreenHandler(
            TestFixtures.SIMPLE_MENU.get(), 1, player.getInventory(), container, TestFixtures.SIMPLE_ROWS
        );

        container.assertOpenCount(context, 1, "opening the menu");
        menu.removed(player);
        container.assertOpenCount(context, 0, "closing the menu");

        context.succeed();
    }

    @GameTest
    public void doubleWideHandlerBalancesOpenAndClose(GameTestHelper context) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        OpenCountingContainer container = new OpenCountingContainer(TestFixtures.DOUBLE_ROWS * 18);

        DoubleWideInventoryScreenHandler menu = new DoubleWideInventoryScreenHandler(
            TestFixtures.DOUBLE_MENU.get(), 1, player.getInventory(), container, TestFixtures.DOUBLE_ROWS
        );

        container.assertOpenCount(context, 1, "opening the menu");
        menu.removed(player);
        container.assertOpenCount(context, 0, "closing the menu");

        context.succeed();
    }

    /**
     * {@code SimpleContainer}'s own {@code startOpen}/{@code stopOpen} are no-ops, so this stands in
     * for the real block entities (which delegate to a {@code ContainerOpenersCounter}) and just
     * tallies the calls.
     */
    private static class OpenCountingContainer extends SimpleContainer {
        private int openCount = 0;

        OpenCountingContainer(int size) {
            super(size);
        }

        @Override
        public void startOpen(ContainerUser user) {
            openCount++;
        }

        @Override
        public void stopOpen(ContainerUser user) {
            openCount--;
        }

        void assertOpenCount(GameTestHelper context, int expected, String what) {
            if (openCount != expected) {
                context.fail("Expected an open count of " + expected + " after " + what + ", got " + openCount);
            }
        }
    }
}

package com.chimericdream.lib.testkit.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

/**
 * Reusable {@link AbstractContainerMenu} helpers for server-side screen-handler GameTests: assert a
 * handler's slot layout and simulate shift-click ("quick move") routing without a client. Chimeric
 * mods share a handful of container screen shapes (single- and double-wide), so this lives in the
 * published {@code testFixtures} variant alongside {@link GameTestContainers}.
 */
public final class GameTestMenus {
    private GameTestMenus() {
    }

    /** Fails the test unless {@code menu} exposes exactly {@code expected} slots. */
    public static void assertSlotCount(GameTestHelper context, AbstractContainerMenu menu, int expected) {
        int actual = menu.slots.size();

        if (actual != expected) {
            context.fail("Expected the menu to have " + expected + " slots, but it had " + actual);
        }
    }

    /**
     * Simulates a shift-click (quick move) on {@code slot} and returns the leftover stack the vanilla
     * contract reports ({@link ItemStack#EMPTY} when everything moved).
     */
    public static ItemStack shiftClick(AbstractContainerMenu menu, Player player, int slot) {
        return menu.quickMoveStack(player, slot);
    }
}

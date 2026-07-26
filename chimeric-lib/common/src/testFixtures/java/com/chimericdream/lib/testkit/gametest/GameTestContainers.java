package com.chimericdream.lib.testkit.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Reusable {@link Container} assertions for GameTests. Every mod in this repo that ships a container
 * block entity (Hopper X-Treme, Minekea crates/shelves, Shulker Stuff) exercises the same "fill a
 * slot, assert a slot" shape, so it lives here in the published {@code testFixtures} variant rather
 * than being copied per mod. Failures are reported through {@link GameTestHelper#fail(String)} so they
 * surface as normal GameTest failures.
 *
 * <p>Consume it from a downstream mod's {@code gametest} source set with
 * {@code gametestImplementation(testFixtures("com.chimericdream.lib:chimericlib-common-<mc>:<ver>"))}.
 */
public final class GameTestContainers {
    private GameTestContainers() {
    }

    /** Fails the test unless {@code container} holds exactly {@code count} of {@code expected} in {@code slot}. */
    public static void assertSlot(GameTestHelper context, Container container, int slot, Item expected, int count) {
        ItemStack stack = container.getItem(slot);

        if (!stack.is(expected) || stack.getCount() != count) {
            context.fail("Expected " + count + "x " + expected + " in slot " + slot + ", found " + describe(stack));
        }
    }

    /** Fails the test unless {@code slot} is empty. */
    public static void assertSlotEmpty(GameTestHelper context, Container container, int slot) {
        ItemStack stack = container.getItem(slot);

        if (!stack.isEmpty()) {
            context.fail("Expected slot " + slot + " to be empty, found " + describe(stack));
        }
    }

    /** Fails the test unless every slot is empty. */
    public static void assertEmpty(GameTestHelper context, Container container) {
        if (!container.isEmpty()) {
            context.fail("Expected an empty container, but " + countNonEmpty(container) + " slot(s) held items");
        }
    }

    /** Counts the non-empty slots in {@code container}. */
    public static int countNonEmpty(Container container) {
        int count = 0;

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (!container.getItem(slot).isEmpty()) {
                count++;
            }
        }

        return count;
    }

    private static String describe(ItemStack stack) {
        return stack.isEmpty() ? "<empty>" : stack.getCount() + "x " + stack.getItem();
    }
}

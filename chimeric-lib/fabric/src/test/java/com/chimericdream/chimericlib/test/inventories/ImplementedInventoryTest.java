package com.chimericdream.chimericlib.test.inventories;

import com.chimericdream.lib.inventories.ImplementedInventory;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Builds ItemStacks and reads item components, so it bootstraps Minecraft (see BootstrapMinecraft).
public class ImplementedInventoryTest extends BootstrapMinecraft {
    private static ImplementedInventory ofSize(int size) {
        return ImplementedInventory.of(NonNullList.withSize(size, ItemStack.EMPTY));
    }

    /**
     * The core of 1.6: two otherwise-identical partial stacks must be mergeable regardless of their
     * counts. The old ItemStack.matches comparison also compared counts, so a merge only happened when
     * the counts happened to coincide.
     */
    @Test
    void partialStacksWithDifferentCountsAreMergeable() {
        ImplementedInventory inv = ofSize(1);

        ItemStack existing = new ItemStack(Items.COBBLESTONE, 10);
        ItemStack incoming = new ItemStack(Items.COBBLESTONE, 5);

        assertTrue(inv.isMatchingPartialStack(incoming, existing),
            "cobblestone x5 should merge into cobblestone x10");
    }

    @Test
    void tryInsertMergesFittingStackAndReturnsEmpty() {
        ImplementedInventory inv = ofSize(1);
        inv.getItems().set(0, new ItemStack(Items.COBBLESTONE, 10));

        ItemStack remainder = inv.tryInsert(0, new ItemStack(Items.COBBLESTONE, 5));

        assertTrue(remainder.isEmpty(), "a fully-merged insert leaves no remainder");
        assertEquals(Items.COBBLESTONE, inv.getItem(0).getItem());
        assertEquals(15, inv.getItem(0).getCount(), "slot should hold 10 + 5 = 15");
    }

    @Test
    void tryInsertPartialMergeFillsToMaxAndReturnsRemainder() {
        ImplementedInventory inv = ofSize(1);
        inv.getItems().set(0, new ItemStack(Items.COBBLESTONE, 60));

        // 64 max - 60 = 4 fit; 10 - 4 = 6 remain.
        ItemStack remainder = inv.tryInsert(0, new ItemStack(Items.COBBLESTONE, 10));

        assertEquals(64, inv.getItem(0).getCount(), "slot should be filled to its max stack size");
        assertEquals(Items.COBBLESTONE, remainder.getItem());
        assertEquals(6, remainder.getCount(), "the overflow should be returned as the remainder");
    }

    @Test
    void stacksWithDifferentComponentsDoNotMerge() {
        ImplementedInventory inv = ofSize(1);

        ItemStack existing = new ItemStack(Items.COBBLESTONE, 10);
        inv.getItems().set(0, existing);

        ItemStack incoming = new ItemStack(Items.COBBLESTONE, 5);
        incoming.set(DataComponents.CUSTOM_NAME, Component.literal("Fancy Rock"));

        assertFalse(inv.isMatchingPartialStack(incoming, existing),
            "stacks differing only by components must not merge");

        ItemStack remainder = inv.tryInsert(0, incoming);
        assertEquals(5, remainder.getCount(), "a non-matching stack is returned unchanged");
        assertEquals(10, inv.getItem(0).getCount(), "the existing stack is left untouched");
    }

    @Test
    void tryInsertIntoEmptySlotStoresStack() {
        ImplementedInventory inv = ofSize(1);

        ItemStack remainder = inv.tryInsert(0, new ItemStack(Items.COBBLESTONE, 5));

        assertTrue(remainder.isEmpty());
        assertEquals(Items.COBBLESTONE, inv.getItem(0).getItem());
        assertEquals(5, inv.getItem(0).getCount());
    }

    @Test
    void tryInsertIntoFullMatchingSlotReturnsStackUnchanged() {
        ImplementedInventory inv = ofSize(1);
        inv.getItems().set(0, new ItemStack(Items.COBBLESTONE, 64));

        ItemStack remainder = inv.tryInsert(0, new ItemStack(Items.COBBLESTONE, 5));

        assertEquals(5, remainder.getCount(), "nothing fits into a full slot");
        assertEquals(64, inv.getItem(0).getCount());
    }
}

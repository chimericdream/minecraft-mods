package com.chimericdream.chimericlib.test.inventories;

import com.chimericdream.lib.testkit.BootstrapMinecraft;
import com.chimericdream.lib.inventories.InventoryUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Builds ItemStacks, so it bootstraps Minecraft (see BootstrapMinecraft).
public class InventoryUtilsTest extends BootstrapMinecraft {
    @Test
    void convertListToInventoryMatchesSizeAndContents() {
        NonNullList<ItemStack> list = NonNullList.withSize(4, ItemStack.EMPTY);
        list.set(0, new ItemStack(Items.DIAMOND, 3));
        list.set(2, new ItemStack(Items.STONE, 64));

        SimpleContainer inventory = InventoryUtils.convertListToInventory(list);

        assertEquals(4, inventory.getContainerSize());

        assertEquals(Items.DIAMOND, inventory.getItem(0).getItem());
        assertEquals(3, inventory.getItem(0).getCount());

        assertTrue(inventory.getItem(1).isEmpty());

        assertEquals(Items.STONE, inventory.getItem(2).getItem());
        assertEquals(64, inventory.getItem(2).getCount());

        assertTrue(inventory.getItem(3).isEmpty());
    }

    @Test
    void convertEmptyListYieldsEmptyInventory() {
        SimpleContainer inventory = InventoryUtils.convertListToInventory(NonNullList.create());

        assertEquals(0, inventory.getContainerSize());
        assertTrue(inventory.isEmpty());
    }
}

package com.chimericdream.lib.inventories;

import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class InventoryUtils {
    public static SimpleContainer convertListToInventory(NonNullList<ItemStack> list) {
        SimpleContainer inventory = new SimpleContainer(list.size());
        for (int i = 0; i < list.size(); i++) {
            inventory.setItem(i, list.get(i));
        }

        return inventory;
    }
}

package com.chimericdream.hopperxtreme.client.screen;

import com.chimericdream.hopperxtreme.item.ModItems;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class FilterSlot extends Slot {
    public FilterSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return ItemStack.areItemsEqual(stack, new ItemStack(ModItems.HOPPER_ITEM_FILTER_ITEM.get()));
    }
}

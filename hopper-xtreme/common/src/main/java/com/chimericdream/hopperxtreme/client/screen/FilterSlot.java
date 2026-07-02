package com.chimericdream.hopperxtreme.client.screen;

import com.chimericdream.hopperxtreme.item.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FilterSlot extends Slot {
    public FilterSlot(Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return ItemStack.isSameItem(stack, new ItemStack(ModItems.HOPPER_ITEM_FILTER_ITEM.get()));
    }
}

package com.chimericdream.hopperxtreme.client.screen;

import com.chimericdream.hopperxtreme.item.HopperItemFilterItem;
import com.chimericdream.hopperxtreme.item.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class NonFilterSlot extends Slot {
    private final int filterSlotIndex;

    public NonFilterSlot(Container inventory, int index, int x, int y, int filterSlotIndex) {
        super(inventory, index, x, y);
        this.filterSlotIndex = filterSlotIndex;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (HopperItemFilterItem.matchesFilter(this.container.getItem(this.filterSlotIndex), stack)) {
            return !ItemStack.isSameItem(stack, new ItemStack(ModItems.HOPPER_ITEM_FILTER_ITEM.get()));
        }

        return false;
    }
}

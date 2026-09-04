package com.chimericdream.hopperxtreme.client.screen;

import com.chimericdream.hopperxtreme.item.HopperItemFilterItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FilterSlot extends Slot {
    public FilterSlot(Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        // Accepts any tier of hopper item filter (they all install into a hopper's filter slot the
        // same way, differing only in how many filter entries they hold).
        return stack.getItem() instanceof HopperItemFilterItem;
    }
}

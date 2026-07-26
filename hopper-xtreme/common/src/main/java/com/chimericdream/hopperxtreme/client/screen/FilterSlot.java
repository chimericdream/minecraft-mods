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
        // stack.is(item) avoids allocating a throwaway ItemStack on every call, like the sibling
        // FilterSlot.mayPlace in HopperItemFilterScreenHandler. (A couple of older call sites --
        // NonFilterSlot and XtremeMultiHupperBlockEntity -- still test via ItemStack.isSameItem
        // against a freshly built stack; this is the cheaper equivalent.)
        return stack.is(ModItems.HOPPER_ITEM_FILTER_ITEM.get());
    }
}

package com.chimericdream.hopperxtreme.client.screen;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.item.HopperItemFilterItem;
import com.chimericdream.hopperxtreme.item.ModItems;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class HopperItemFilterScreenHandler extends AbstractHopperItemFilterScreenHandler {
    public static final Identifier SCREEN_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "screens/items/hopper_item_filter");

    public HopperItemFilterScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new ItemStack(ModItems.HOPPER_ITEM_FILTER_ITEM.get()));
    }

    public HopperItemFilterScreenHandler(int syncId, Inventory playerInventory, ItemStack stack) {
        super(ModItems.HOPPER_ITEM_FILTER_SCREEN_HANDLER.get(), syncId, playerInventory, stack);
    }

    @Override
    protected void addFilterSlots() {
        for (int i = 0; i < HopperItemFilterItem.STANDARD_FILTER_SLOTS; i++) {
            this.addSlot(new FilterSlot(this.filter, i, 44 + i * 18, 20));
        }
    }
}

package com.chimericdream.hopperxtreme.client.screen;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.item.HopperItemFilterItem;
import com.chimericdream.hopperxtreme.item.ModItems;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class DiamondHopperItemFilterScreenHandler extends AbstractHopperItemFilterScreenHandler {
    public static final Identifier SCREEN_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "screens/items/diamond_hopper_item_filter");

    private static final int COLUMNS = 5;

    public DiamondHopperItemFilterScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new ItemStack(ModItems.DIAMOND_HOPPER_ITEM_FILTER_ITEM.get()));
    }

    public DiamondHopperItemFilterScreenHandler(int syncId, Inventory playerInventory, ItemStack stack) {
        super(ModItems.DIAMOND_HOPPER_ITEM_FILTER_SCREEN_HANDLER.get(), syncId, playerInventory, stack);
    }

    @Override
    protected void addFilterSlots() {
        for (int i = 0; i < HopperItemFilterItem.DIAMOND_FILTER_SLOTS; i++) {
            int column = i % COLUMNS;
            int row = i / COLUMNS;

            this.addSlot(new FilterSlot(this.filter, i, 44 + column * 18, 20 + row * 18));
        }
    }

    @Override
    protected int playerInventoryYOffset() {
        // The diamond tier's second filter row pushes the player inventory block down by one more
        // row's worth of space than the standard tier's single-row layout.
        return 18;
    }
}

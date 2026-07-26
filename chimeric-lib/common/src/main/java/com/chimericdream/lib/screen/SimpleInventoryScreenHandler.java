package com.chimericdream.lib.screen;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

/**
 * A single-width (9-column) fixed-grid container menu. All behavior lives in
 * {@link InventoryScreenHandler}; this class only pins the column count.
 */
public class SimpleInventoryScreenHandler extends InventoryScreenHandler {
    private static final int COLUMNS = 9;

    public SimpleInventoryScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, int rowCount) {
        this(type, syncId, playerInventory, new SimpleContainer(ScreenHelpers.getInventorySize(rowCount)), rowCount);
    }

    public SimpleInventoryScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, Container inventory, int rowCount) {
        super(type, syncId, playerInventory, inventory, rowCount, COLUMNS);
    }
}

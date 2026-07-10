package com.chimericdream.minekea.client.screen.crate;

import com.chimericdream.lib.screen.DoubleWideInventoryScreenHandler;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.containers.crates.CrateBlock;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class DoubleCrateScreenHandler extends DoubleWideInventoryScreenHandler {
    public static final Identifier SCREEN_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "screens/container/double_crate");
    public static final Identifier TRAPPED_SCREEN_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "screens/container/double_crate/trapped");

    public DoubleCrateScreenHandler(int syncId, Inventory playerInventory) {
        super(null, syncId, playerInventory, CrateBlock.ROW_COUNT);
    }

    public DoubleCrateScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory) {
        super(type, syncId, playerInventory, CrateBlock.ROW_COUNT);
    }

    public DoubleCrateScreenHandler(int syncId, Inventory playerInventory, Container inventory) {
        super(null, syncId, playerInventory, inventory, CrateBlock.ROW_COUNT);
    }

    public DoubleCrateScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, Container inventory) {
        super(type, syncId, playerInventory, inventory, CrateBlock.ROW_COUNT);
    }
}

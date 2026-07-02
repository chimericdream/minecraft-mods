package com.chimericdream.minekea.client.screen.crate;

import com.chimericdream.lib.screen.SimpleInventoryScreenHandler;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.containers.crates.CrateBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class CrateScreenHandler extends SimpleInventoryScreenHandler {
    public static final ResourceLocation SCREEN_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "screens/container/crate");
    public static final ResourceLocation TRAPPED_SCREEN_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "screens/container/crate/trapped");

    public CrateScreenHandler(int syncId, Inventory playerInventory) {
        super(null, syncId, playerInventory, CrateBlock.ROW_COUNT);
    }

    public CrateScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory) {
        super(type, syncId, playerInventory, CrateBlock.ROW_COUNT);
    }

    public CrateScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, Container inventory) {
        super(type, syncId, playerInventory, inventory, CrateBlock.ROW_COUNT);
    }
}

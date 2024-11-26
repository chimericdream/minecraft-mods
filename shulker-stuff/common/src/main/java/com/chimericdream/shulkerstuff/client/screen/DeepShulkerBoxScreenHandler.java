package com.chimericdream.shulkerstuff.client.screen;

import com.chimericdream.lib.screen.SimpleInventoryScreenHandler;
import com.chimericdream.shulkerstuff.ModInfo;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class DeepShulkerBoxScreenHandler extends SimpleInventoryScreenHandler {
    public static final Identifier SCREEN_ID = Identifier.of(ModInfo.MOD_ID, "gui/block/deep_shulker_box");

    public final int extraRows;

    public DeepShulkerBoxScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(null, syncId, playerInventory, 0);
    }

    public DeepShulkerBoxScreenHandler(int syncId, PlayerInventory playerInventory, int extraRows) {
        super(null, syncId, playerInventory, 3 + extraRows);
        this.extraRows = extraRows;
    }

    public DeepShulkerBoxScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory) {
        this(type, syncId, playerInventory, 0);
    }

    public DeepShulkerBoxScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, int extraRows) {
        super(type, syncId, playerInventory, 3 + extraRows);
        this.extraRows = extraRows;
    }

    public DeepShulkerBoxScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory) {
        this(type, syncId, playerInventory, inventory, 0);
    }

    public DeepShulkerBoxScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, int extraRows) {
        super(type, syncId, playerInventory, inventory, 3 + extraRows);
        this.extraRows = extraRows;
    }
}

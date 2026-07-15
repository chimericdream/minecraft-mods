package com.chimericdream.lib.screen;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class SimpleInventoryScreenHandler extends AbstractContainerMenu {
    private final Container inventory;

    public SimpleInventoryScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, int rowCount) {
        this(type, syncId, playerInventory, new SimpleContainer(ScreenHelpers.getInventorySize(rowCount)), rowCount);
    }

    public SimpleInventoryScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, Container inventory, int rowCount) {
        super(type, syncId);

        checkContainerSize(inventory, ScreenHelpers.getInventorySize(rowCount));

        this.inventory = inventory;

        inventory.startOpen(playerInventory.player);

        int i = (rowCount - 4) * 18, j, k;
        for (j = 0; j < rowCount; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(
                    inventory,
                    k + j * 9,
                    8 + k * ScreenHelpers.ROW_HEIGHT,
                    ScreenHelpers.ROW_HEIGHT + j * ScreenHelpers.ROW_HEIGHT
                ));
            }
        }

        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(
                    playerInventory,
                    k + j * 9 + 9,
                    8 + k * ScreenHelpers.ROW_HEIGHT,
                    104 + j * ScreenHelpers.ROW_HEIGHT + i
                ));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(
                playerInventory,
                j,
                8 + j * ScreenHelpers.ROW_HEIGHT,
                162 + i
            ));
        }
    }

    public Container getInventory() {
        return inventory;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return this.inventory.stillValid(player);
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;

        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, this.inventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return newStack;
    }
}

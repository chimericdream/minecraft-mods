package com.chimericdream.lib.screen;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

/**
 * Shared base for the fixed-grid container menus. The layout is identical for every width — a
 * {@code columns}-wide storage grid on top and the standard 3x9 player inventory + hotbar below —
 * so the only per-menu variation is the column count (and the player-inventory x-offset that
 * derives from it). {@link SimpleInventoryScreenHandler} (9 columns) and
 * {@link DoubleWideInventoryScreenHandler} (18 columns) are thin subclasses that pass their width.
 */
public abstract class InventoryScreenHandler extends AbstractContainerMenu {
    private final Container inventory;

    protected InventoryScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, Container inventory, int rowCount, int columns) {
        super(type, syncId);

        checkContainerSize(inventory, rowCount * columns);

        this.inventory = inventory;

        inventory.startOpen(playerInventory.player);

        // The player inventory is always 9 wide; center it under a grid wider than 9.
        int playerX = 8 + (columns - 9) * ScreenHelpers.ROW_HEIGHT / 2;
        int i = (rowCount - 4) * 18, j, k;

        for (j = 0; j < rowCount; ++j) {
            for (k = 0; k < columns; ++k) {
                this.addSlot(new Slot(
                    inventory,
                    k + j * columns,
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
                    playerX + k * ScreenHelpers.ROW_HEIGHT,
                    104 + j * ScreenHelpers.ROW_HEIGHT + i
                ));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(
                playerInventory,
                j,
                playerX + j * ScreenHelpers.ROW_HEIGHT,
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

    /**
     * Balances the {@code startOpen} the constructor issues. Vanilla's {@code ChestMenu} does the
     * same; without it a container backed by a {@code ContainerOpenersCounter} never sees its viewer
     * leave (so lids stay open and open/close side effects never fire).
     */
    @Override
    public void removed(@NonNull Player player) {
        super.removed(player);
        this.inventory.stopOpen(player);
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

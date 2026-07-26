package com.chimericdream.hopperxtreme.client.screen;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.lib.screen.ScreenHelpers;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FilteredGlazedHopperScreenHandler extends AbstractContainerMenu {
    public static final Identifier SCREEN_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "gui/filtered_glazed_hopper");

    // 1 storage slot + 1 filter slot. The filter is the last container slot; deriving these from
    // getContainerSize() is unsafe because the server BE hides the filter slot but the client's dummy
    // SimpleContainer does not, so the two sides disagree (see CODE-REVIEW-PLAN 1.2/1.3).
    private static final int STORAGE_SLOT_COUNT = 1;
    private static final int FILTER_SLOT_INDEX = STORAGE_SLOT_COUNT;
    private static final int HOPPER_SLOT_COUNT = STORAGE_SLOT_COUNT + 1;

    private final Container hopper;

    public FilteredGlazedHopperScreenHandler(int syncId, Inventory playerInventory) {
        this(null, syncId, playerInventory, new SimpleContainer(2));
    }

    public FilteredGlazedHopperScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, Container hopper) {
        super(type, syncId);

        this.hopper = hopper;

        hopper.startOpen(playerInventory.player);

        this.addSlot(new NonFilterSlot(this.hopper, 0, 80, 20, FILTER_SLOT_INDEX));
        this.addSlot(new FilterSlot(this.hopper, FILTER_SLOT_INDEX, 152, 20));

        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(
                    playerInventory,
                    k + j * 9 + 9,
                    8 + k * ScreenHelpers.ROW_HEIGHT,
                    51 + j * ScreenHelpers.ROW_HEIGHT
                ));
            }
        }

        for (int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(
                playerInventory,
                j,
                8 + j * ScreenHelpers.ROW_HEIGHT,
                109
            ));
        }
    }

    public Container getInventory() {
        return hopper;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.hopper.stillValid(player);
    }

    /**
     * Balances the {@code startOpen} the constructor issues, the way vanilla's {@code ChestMenu}
     * does — without it the block entity's opener counter never sees the viewer leave.
     */
    @Override
    public void removed(Player player) {
        super.removed(player);
        this.hopper.stopOpen(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;

        int hopperSize = HOPPER_SLOT_COUNT;

        Slot slot = this.slots.get(invSlot);
        if (slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();

            if (invSlot < hopperSize) {
                if (!this.moveItemStackTo(originalStack, hopperSize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, hopperSize, false)) {
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

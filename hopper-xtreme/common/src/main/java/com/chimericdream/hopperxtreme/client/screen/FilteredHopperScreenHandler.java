package com.chimericdream.hopperxtreme.client.screen;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.lib.screen.ScreenHelpers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class FilteredHopperScreenHandler extends ScreenHandler {
    public static final Identifier SCREEN_ID = Identifier.of(ModInfo.MOD_ID, "gui/filtered_hopper");

    private final Inventory hopper;

    public FilteredHopperScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(null, syncId, playerInventory, new SimpleInventory(6));
    }

    public FilteredHopperScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory hopper) {
        super(type, syncId);

        this.hopper = hopper;

        hopper.onOpen(playerInventory.player);

        this.addSlot(new NonFilterSlot(this.hopper, 0, 44, 20));
        this.addSlot(new NonFilterSlot(this.hopper, 1, 62, 20));
        this.addSlot(new NonFilterSlot(this.hopper, 2, 80, 20));
        this.addSlot(new NonFilterSlot(this.hopper, 3, 98, 20));
        this.addSlot(new NonFilterSlot(this.hopper, 4, 116, 20));
        this.addSlot(new FilterSlot(this.hopper, 5, 152, 20));

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

    public Inventory getInventory() {
        return hopper;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.hopper.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;

        int hopperSize = this.hopper.size() + 1;

        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (invSlot < hopperSize) {
                if (!this.insertItem(originalStack, hopperSize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, hopperSize, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }
}

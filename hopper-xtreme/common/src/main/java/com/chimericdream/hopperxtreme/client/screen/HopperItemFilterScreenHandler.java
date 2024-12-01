package com.chimericdream.hopperxtreme.client.screen;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.item.HopperItemFilterItem;
import com.chimericdream.hopperxtreme.item.ModItems;
import com.chimericdream.lib.screen.ScreenHelpers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class HopperItemFilterScreenHandler extends ScreenHandler {
    public static final Identifier SCREEN_ID = Identifier.of(ModInfo.MOD_ID, "screens/items/hopper_item_filter");

    private final Inventory filter;

    public HopperItemFilterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new ItemStack(ModItems.HOPPER_ITEM_FILTER_ITEM.get()));
    }

    public HopperItemFilterScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack stack) {
        super(ModItems.HOPPER_ITEM_FILTER_SCREEN_HANDLER.get(), syncId);

        filter = new HopperItemFilterItem.FilterInventory(stack);

        filter.onOpen(playerInventory.player);

        this.addSlot(new FilterSlot(this.filter, 0, 44, 20));
        this.addSlot(new FilterSlot(this.filter, 1, 62, 20));
        this.addSlot(new FilterSlot(this.filter, 2, 80, 20));
        this.addSlot(new FilterSlot(this.filter, 3, 98, 20));
        this.addSlot(new FilterSlot(this.filter, 4, 116, 20));

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
        return filter;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        return ItemStack.EMPTY;
    }

    private static class FilterSlot extends Slot {
        public FilterSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return !stack.isOf(ModItems.HOPPER_ITEM_FILTER_ITEM.get());
        }

        @Override
        public ItemStack insertStack(ItemStack stack, int count) {
            ItemStack copy = stack.copy();
            copy.setCount(1);

            super.insertStack(copy, 1);

            return stack;
        }

        @Override
        public ItemStack takeStack(int amount) {
            super.takeStack(amount);

            return ItemStack.EMPTY;
        }
    }
}

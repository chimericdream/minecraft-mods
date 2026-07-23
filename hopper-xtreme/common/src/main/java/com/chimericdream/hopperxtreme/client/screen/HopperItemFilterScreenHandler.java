package com.chimericdream.hopperxtreme.client.screen;

import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.item.HopperItemFilterItem;
import com.chimericdream.hopperxtreme.item.ModItems;
import com.chimericdream.lib.screen.ScreenHelpers;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HopperItemFilterScreenHandler extends AbstractContainerMenu {
    public static final Identifier SCREEN_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "screens/items/hopper_item_filter");

    private final Container filter;

    public HopperItemFilterScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new ItemStack(ModItems.HOPPER_ITEM_FILTER_ITEM.get()));
    }

    public HopperItemFilterScreenHandler(int syncId, Inventory playerInventory, ItemStack stack) {
        super(ModItems.HOPPER_ITEM_FILTER_SCREEN_HANDLER.get(), syncId);

        filter = new HopperItemFilterItem.FilterInventory(stack);

        filter.startOpen(playerInventory.player);

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

    public Container getInventory() {
        return filter;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /**
     * Balances the {@code startOpen} the constructor issues, the way vanilla's {@code ChestMenu}
     * does — without it the backing container never sees the viewer leave.
     */
    @Override
    public void removed(Player player) {
        super.removed(player);
        this.filter.stopOpen(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int invSlot) {
        return ItemStack.EMPTY;
    }

    private static class FilterSlot extends Slot {
        public FilterSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return !stack.is(ModItems.HOPPER_ITEM_FILTER_ITEM.get());
        }

        @Override
        public @NotNull ItemStack safeInsert(ItemStack stack, int count) {
            ItemStack copy = stack.copy();
            copy.setCount(1);

            super.safeInsert(copy, 1);

            return stack;
        }

        @Override
        public @NotNull ItemStack remove(int amount) {
            super.remove(amount);

            return ItemStack.EMPTY;
        }
    }
}

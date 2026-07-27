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
import org.jspecify.annotations.NonNull;

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
    public boolean stillValid(@NonNull Player player) {
        return true;
    }

    /**
     * Balances the {@code startOpen} the constructor issues, the way vanilla's {@code ChestMenu}
     * does — without it the backing container never sees the viewer leave.
     */
    @Override
    public void removed(@NonNull Player player) {
        super.removed(player);
        this.filter.stopOpen(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NonNull Player player, int invSlot) {
        return ItemStack.EMPTY;
    }

    private static class FilterSlot extends Slot {
        public FilterSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(@NonNull ItemStack stack) {
            // Only ever place into an empty slot. Vanilla's AbstractContainerMenu#doClick swap
            // branch (a *different* item clicked onto an already-filled slot) bypasses safeInsert
            // and remove entirely -- it reads the slot's current item and hands it straight to the
            // cursor via setCarried. Gating mayPlace on !hasItem() forces that swap branch to never
            // match here, so replacing an entry is always two zero-cost/zero-gain steps (clear via
            // empty-cursor click, then place), never a single click that could leak the old ghost
            // out as a real, spendable item.
            return !hasItem() && !stack.is(ModItems.HOPPER_ITEM_FILTER_ITEM.get());
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int getMaxStackSize(@NonNull ItemStack stack) {
            return 1;
        }

        // Ghost insert: capture a 1-count copy of whatever was clicked in as the filter entry.
        // The player's real stack is never touched -- setting a filter costs nothing.
        @Override
        public @NotNull ItemStack safeInsert(ItemStack stack, int count) {
            if (!stack.isEmpty() && mayPlace(stack)) {
                setByPlayer(stack.copyWithCount(1));
            }

            return stack;
        }

        // Ghost extraction: clearing a filter entry never hands a real item back -- nothing was
        // ever spent to create it.
        @Override
        public @NotNull ItemStack remove(int amount) {
            super.remove(amount);

            return ItemStack.EMPTY;
        }
    }
}

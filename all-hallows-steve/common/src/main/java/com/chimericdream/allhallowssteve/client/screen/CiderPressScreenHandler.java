package com.chimericdream.allhallowssteve.client.screen;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.entity.CiderPressBlockEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.allhallowssteve.block.entity.CiderPressBlockEntity.SLOT_BOTTLE;
import static com.chimericdream.allhallowssteve.block.entity.CiderPressBlockEntity.SLOT_CIDER;
import static com.chimericdream.allhallowssteve.block.entity.CiderPressBlockEntity.SLOT_FRUIT;
import static com.chimericdream.allhallowssteve.block.entity.CiderPressBlockEntity.SLOT_FUEL;
import static com.chimericdream.allhallowssteve.block.entity.CiderPressBlockEntity.SLOT_POMACE;

public class CiderPressScreenHandler extends AbstractContainerMenu {
    public static final Identifier SCREEN_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "gui/block/cider_press");

    /** The player's inventory starts after the press's own slots. */
    private static final int FIRST_PLAYER_SLOT = CiderPressBlockEntity.INVENTORY_SIZE;

    private final Container press;
    private final ContainerData data;

    public CiderPressScreenHandler(int syncId, Inventory playerInventory) {
        this(null, syncId, playerInventory, new SimpleContainer(CiderPressBlockEntity.INVENTORY_SIZE), new SimpleContainerData(CiderPressBlockEntity.DATA_COUNT));
    }

    public CiderPressScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, Container press, ContainerData data) {
        super(type, syncId);

        checkContainerSize(press, CiderPressBlockEntity.INVENTORY_SIZE);
        checkContainerDataCount(data, CiderPressBlockEntity.DATA_COUNT);

        this.press = press;
        this.data = data;

        this.addSlot(new InputSlot(press, SLOT_FRUIT, 56, 17));
        this.addSlot(new InputSlot(press, SLOT_BOTTLE, 56, 53));
        this.addSlot(new InputSlot(press, SLOT_FUEL, 17, 35));
        this.addSlot(new OutputSlot(press, SLOT_CIDER, 116, 35));
        this.addSlot(new OutputSlot(press, SLOT_POMACE, 142, 35));

        this.addDataSlots(data);
        this.addStandardInventorySlots(playerInventory, 8, 84);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.press.stillValid(player);
    }

    /** Charges left in the press. */
    public int getFuel() {
        return this.data.get(CiderPressBlockEntity.DATA_FUEL);
    }

    /** Ticks left in the current pressing, or 0 when idle. */
    public int getPressTime() {
        return this.data.get(CiderPressBlockEntity.DATA_PRESS_TIME);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int end = this.slots.size();

        if (slotIndex < FIRST_PLAYER_SLOT) {
            if (!this.moveItemStackTo(stack, FIRST_PLAYER_SLOT, end, true)) {
                return ItemStack.EMPTY;
            }

            slot.onQuickCraft(stack, original);
        } else if (CiderPressBlockEntity.fuelCharges(stack) > 0) {
            if (!this.moveItemStackTo(stack, SLOT_FUEL, SLOT_FUEL + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (this.slots.get(SLOT_FRUIT).mayPlace(stack)) {
            if (!this.moveItemStackTo(stack, SLOT_FRUIT, SLOT_FRUIT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (this.slots.get(SLOT_BOTTLE).mayPlace(stack)) {
            if (!this.moveItemStackTo(stack, SLOT_BOTTLE, SLOT_BOTTLE + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (slotIndex < FIRST_PLAYER_SLOT + 27) {
            if (!this.moveItemStackTo(stack, FIRST_PLAYER_SLOT + 27, end, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, FIRST_PLAYER_SLOT, FIRST_PLAYER_SLOT + 27, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);

        return original;
    }

    /** A slot that only takes what the press itself would accept in that slot. */
    private static class InputSlot extends Slot {
        InputSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return this.container.canPlaceItem(this.getContainerSlot(), stack);
        }
    }

    /** Take-only: the press fills these. */
    private static class OutputSlot extends Slot {
        OutputSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
        }
    }
}

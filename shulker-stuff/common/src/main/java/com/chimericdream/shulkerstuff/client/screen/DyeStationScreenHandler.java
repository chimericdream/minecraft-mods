package com.chimericdream.shulkerstuff.client.screen;

import com.chimericdream.lib.colors.ColorHelpers;
import com.chimericdream.lib.screen.ScreenHelpers;
import com.chimericdream.shulkerstuff.ModInfo;
import com.chimericdream.shulkerstuff.block.entity.DyeStationBlockEntity;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffDyedColorComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class DyeStationScreenHandler extends ScreenHandler {
    public static final Identifier SCREEN_ID = Identifier.of(ModInfo.MOD_ID, "gui/block/dye_station");

    private final Inventory inventory;
    private final Inventory output;

    public DyeStationScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(null, syncId, playerInventory, new SimpleInventory(DyeStationBlockEntity.INVENTORY_SIZE));
    }

    public DyeStationScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(type, syncId);

        this.inventory = inventory;
        this.output = new SimpleInventory(1);

        inventory.onOpen(playerInventory.player);

        // top row
        this.addSlot(new DyeSlot(inventory, output, 0, 30, 17));
        this.addSlot(new DyeSlot(inventory, output, 1, 48, 17));
        this.addSlot(new DyeSlot(inventory, output, 2, 66, 17));

        // shulker box slot
        this.addSlot(new ShulkerSlot(inventory, output, 3, 48, 35));

        // bottom row
        this.addSlot(new DyeSlot(inventory, output, 4, 30, 53));
        this.addSlot(new DyeSlot(inventory, output, 5, 48, 53));
        this.addSlot(new DyeSlot(inventory, output, 6, 66, 53));

        // output slot
        this.addSlot(new OutputSlot(this.inventory, this.output, 124, 35));

        ((ShulkerDyeingSlot) this.slots.get(6)).updateOutput();

        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(
                    playerInventory,
                    k + j * 9 + 9,
                    8 + k * ScreenHelpers.ROW_HEIGHT,
                    84 + j * ScreenHelpers.ROW_HEIGHT
                ));
            }
        }

        for (int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(
                playerInventory,
                j,
                8 + j * ScreenHelpers.ROW_HEIGHT,
                142
            ));
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;

        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
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

    private static class ShulkerDyeingSlot extends Slot {
        private final Inventory output;

        public ShulkerDyeingSlot(Inventory inventory, Inventory output, int index, int x, int y) {
            super(inventory, index, x, y);
            this.output = output;
        }

        protected void updateOutput() {
            ItemStack shulker = this.inventory.getStack(3);
            if (shulker.isEmpty()) {
                this.output.setStack(0, ItemStack.EMPTY);
                return;
            }

            List<DyeItem> topHalfDyes = new ArrayList<>();
            List<DyeItem> bottomHalfDyes = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                ItemStack dye = this.inventory.getStack(i);
                if (dye.isEmpty()) {
                    continue;
                }

                topHalfDyes.add((DyeItem) dye.getItem());
            }

            for (int i = 4; i < 7; i++) {
                ItemStack dye = this.inventory.getStack(i);
                if (dye.isEmpty()) {
                    continue;
                }

                bottomHalfDyes.add((DyeItem) dye.getItem());
            }

            if (topHalfDyes.isEmpty() && bottomHalfDyes.isEmpty()) {
                ItemStack outputStack = shulker.copy();
                ShulkerStuffDyedColorComponent newDyedColorComponent = new ShulkerStuffDyedColorComponent(-1, -1);
                outputStack.set(ShulkerStuffComponentTypes.SHULKER_STUFF_DYED_COLOR_COMPONENT.get(), newDyedColorComponent);

                this.output.setStack(0, outputStack);

                return;
            }

            ColorHelpers.RGB lidColor = null, baseColor = null;
            ShulkerStuffDyedColorComponent ssDyedColorComponent = shulker.getComponents().get(ShulkerStuffComponentTypes.SHULKER_STUFF_DYED_COLOR_COMPONENT.get());
            if (ssDyedColorComponent != null) {
                lidColor = ColorHelpers.RGB.fromInt(ssDyedColorComponent.lidColor());
                baseColor = ColorHelpers.RGB.fromInt(ssDyedColorComponent.baseColor());
            }

            ColorHelpers.RGB lidRGB = ColorHelpers.mixColors(lidColor, topHalfDyes);
            ColorHelpers.RGB baseRGB = ColorHelpers.mixColors(baseColor, bottomHalfDyes);

            int lidColorInt = lidRGB == null ? -1 : lidRGB.toInt();
            int baseColorInt = baseRGB == null ? -1 : baseRGB.toInt();

            ItemStack outputStack = shulker.copy();
            ShulkerStuffDyedColorComponent newDyedColorComponent = new ShulkerStuffDyedColorComponent(lidColorInt, baseColorInt);
            outputStack.set(ShulkerStuffComponentTypes.SHULKER_STUFF_DYED_COLOR_COMPONENT.get(), newDyedColorComponent);

            this.output.setStack(0, outputStack);
        }

        @Override
        public void setStackNoCallbacks(ItemStack stack) {
            super.setStackNoCallbacks(stack);

            this.updateOutput();
        }

        @Override
        public ItemStack insertStack(ItemStack stack, int count) {
            ItemStack inserted = super.insertStack(stack, count);

            this.updateOutput();

            return inserted;
        }

        @Override
        public ItemStack takeStack(int amount) {
            ItemStack stack = super.takeStack(amount);

            this.updateOutput();

            return stack;
        }
    }

    private static class DyeSlot extends ShulkerDyeingSlot {
        public DyeSlot(Inventory inventory, Inventory output, int index, int x, int y) {
            super(inventory, output, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() instanceof DyeItem;
        }
    }

    private static class ShulkerSlot extends ShulkerDyeingSlot {
        public ShulkerSlot(Inventory inventory, Inventory output, int index, int x, int y) {
            super(inventory, output, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.isOf(Items.SHULKER_BOX);
        }
    }

    private static class OutputSlot extends Slot {
        private final Inventory input;

        public OutputSlot(Inventory input, Inventory inventory, int x, int y) {
            super(inventory, 0, x, y);
            this.input = input;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack takeStack(int amount) {
            for (int i = 0; i < 7; i++) {
                this.input.getStack(i).decrement(amount);
            }

            return super.takeStack(amount);
        }
    }
}

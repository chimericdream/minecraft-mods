package com.chimericdream.shulkerstuff.client.screen;

import com.chimericdream.lib.colors.ColorHelpers;
import com.chimericdream.lib.screen.ScreenHelpers;
import com.chimericdream.shulkerstuff.ModInfo;
import com.chimericdream.shulkerstuff.block.entity.DyeStationBlockEntity;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffComponentTypes;
import com.chimericdream.shulkerstuff.component.type.ShulkerStuffDyedColorComponent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DyeStationScreenHandler extends AbstractContainerMenu {
    public static final Identifier SCREEN_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "gui/block/dye_station");

    /** The station's own slots, 0-6. */
    public static final int STATION_SLOT_COUNT = DyeStationBlockEntity.INVENTORY_SIZE;
    /** The result slot, which is backed by {@link #output} rather than by the station. */
    public static final int OUTPUT_SLOT_INDEX = STATION_SLOT_COUNT;
    /** Everything from here on belongs to the player's inventory. */
    public static final int FIRST_PLAYER_SLOT = OUTPUT_SLOT_INDEX + 1;

    private final Container inventory;
    private final Container output;

    public DyeStationScreenHandler(int syncId, Inventory playerInventory) {
        this(null, syncId, playerInventory, new SimpleContainer(DyeStationBlockEntity.INVENTORY_SIZE));
    }

    public DyeStationScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, Container inventory) {
        super(type, syncId);

        this.inventory = inventory;
        this.output = new SimpleContainer(1);

        inventory.startOpen(playerInventory.player);

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
        this.addSlot(new OutputSlot(this.inventory, this.output, 124, 35, this::refreshOutput));

        refreshOutput();

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

    public Container getInventory() {
        return inventory;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    /** Recomputes the result from the current inputs. */
    private void refreshOutput() {
        ((ShulkerDyeingSlot) this.slots.get(OUTPUT_SLOT_INDEX - 1)).updateOutput();
    }

    /**
     * Balances the {@code startOpen} the constructor issues, the way vanilla's {@code ChestMenu}
     * does — without it the station's opener counter never sees the viewer leave.
     */
    @Override
    public void removed(Player player) {
        super.removed(player);
        this.inventory.stopOpen(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;

        Slot slot = this.slots.get(invSlot);
        if (slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();

            if (invSlot == OUTPUT_SLOT_INDEX) {
                // The result slot sits *after* the station's slots, so the old
                // `invSlot < inventory.getContainerSize()` test treated it as a player slot and
                // then found no legal target — shift-clicking the result did nothing at all.
                if (!this.moveItemStackTo(originalStack, FIRST_PLAYER_SLOT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (invSlot < STATION_SLOT_COUNT) {
                if (!this.moveItemStackTo(originalStack, FIRST_PLAYER_SLOT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, STATION_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();

                // A partial move leaves the slot non-empty, so ShulkerDyeingSlot#set never runs and
                // the result would go stale.
                if (invSlot < STATION_SLOT_COUNT) {
                    refreshOutput();
                }
            }

            int moved = newStack.getCount() - originalStack.getCount();
            if (moved <= 0) {
                return ItemStack.EMPTY;
            }

            // Consuming the inputs lives in onTake, so every path that hands the result to the
            // player — pick up, shift-click, swap, drag — pays for it exactly once.
            slot.onTake(player, newStack.copyWithCount(moved));
        }

        return newStack;
    }

    private static class ShulkerDyeingSlot extends Slot {
        private final Container output;

        public ShulkerDyeingSlot(Container inventory, Container output, int index, int x, int y) {
            super(inventory, index, x, y);
            this.output = output;
        }

        protected void updateOutput() {
            ItemStack shulker = this.container.getItem(3);
            if (shulker.isEmpty()) {
                this.output.setItem(0, ItemStack.EMPTY);
                return;
            }

            List<DyeColor> topHalfDyes = new ArrayList<>();
            List<DyeColor> bottomHalfDyes = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                ItemStack dye = this.container.getItem(i);
                if (dye.isEmpty()) {
                    continue;
                }

                DyeColor dyeColor = dye.get(DataComponents.DYE);
                if (dyeColor != null) {
                    topHalfDyes.add(dyeColor);
                }
            }

            for (int i = 4; i < 7; i++) {
                ItemStack dye = this.container.getItem(i);
                if (dye.isEmpty()) {
                    continue;
                }

                DyeColor dyeColor = dye.get(DataComponents.DYE);
                if (dyeColor != null) {
                    bottomHalfDyes.add(dyeColor);
                }
            }

            if (topHalfDyes.isEmpty() && bottomHalfDyes.isEmpty()) {
                ItemStack outputStack = shulker.copy();
                ShulkerStuffDyedColorComponent newDyedColorComponent = new ShulkerStuffDyedColorComponent(-1, -1);
                outputStack.set(ShulkerStuffComponentTypes.SHULKER_STUFF_DYED_COLOR_COMPONENT.get(), newDyedColorComponent);

                this.output.setItem(0, outputStack);

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

            this.output.setItem(0, outputStack);
        }

        @Override
        public void set(ItemStack stack) {
            super.set(stack);

            this.updateOutput();
        }

        @Override
        public @NotNull ItemStack safeInsert(ItemStack stack, int count) {
            ItemStack inserted = super.safeInsert(stack, count);

            this.updateOutput();

            return inserted;
        }

        @Override
        public @NotNull ItemStack remove(int amount) {
            ItemStack stack = super.remove(amount);

            this.updateOutput();

            return stack;
        }
    }

    private static class DyeSlot extends ShulkerDyeingSlot {
        public DyeSlot(Container inventory, Container output, int index, int x, int y) {
            super(inventory, output, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof DyeItem;
        }
    }

    private static class ShulkerSlot extends ShulkerDyeingSlot {
        public ShulkerSlot(Container inventory, Container output, int index, int x, int y) {
            super(inventory, output, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(Items.SHULKER_BOX);
        }
    }

    private static class OutputSlot extends Slot {
        private final Container input;
        private final Runnable refreshOutput;

        public OutputSlot(Container input, Container inventory, int x, int y, Runnable refreshOutput) {
            super(inventory, 0, x, y);
            this.input = input;
            this.refreshOutput = refreshOutput;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        /**
         * Charging for the result belongs here rather than in {@code remove(int)}: {@code remove} is
         * only one of several ways a slot's contents leave (drag, swap, number keys, shift-click),
         * and it ran before anything had committed. {@code onTake} runs once per handover, on every
         * path, with the amount that actually left.
         */
        @Override
        public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
            int taken = stack.getCount();

            if (taken > 0) {
                for (int i = 0; i < DyeStationBlockEntity.INVENTORY_SIZE; i++) {
                    // removeItem, not ItemStack#shrink: it empties the slot properly instead of
                    // leaving a zero-count stack behind.
                    this.input.removeItem(i, taken);
                }

                this.input.setChanged();

                // The old code never did this, so with stacked dyes the result stayed stale until
                // some other slot happened to be touched.
                this.refreshOutput.run();
            }

            super.onTake(player, stack);
        }
    }
}

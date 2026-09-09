package com.chimericdream.allhallowssteve.client.screen;

import com.chimericdream.lib.colors.ColorHelpers;
import com.chimericdream.lib.screen.ScreenHelpers;
import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import com.chimericdream.allhallowssteve.block.entity.CarvingStationBlockEntity;
import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.component.type.DyedColorComponent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CarvingStationScreenHandler extends AbstractContainerMenu {
    public static final Identifier SCREEN_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "gui/block/carving_station");

    /** The station's own slots, 0-3: dye slots 0-2, pumpkin slot 3. */
    public static final int STATION_SLOT_COUNT = CarvingStationBlockEntity.INVENTORY_SIZE;
    public static final int PUMPKIN_SLOT_INDEX = STATION_SLOT_COUNT - 1;
    /** The result slot, which is backed by {@link #output} rather than by the station. */
    public static final int OUTPUT_SLOT_INDEX = STATION_SLOT_COUNT;
    /** Everything from here on belongs to the player's inventory. */
    public static final int FIRST_PLAYER_SLOT = OUTPUT_SLOT_INDEX + 1;

    private final Container inventory;
    private final Container output;

    public CarvingStationScreenHandler(int syncId, Inventory playerInventory) {
        this(null, syncId, playerInventory, new SimpleContainer(CarvingStationBlockEntity.INVENTORY_SIZE));
    }

    public CarvingStationScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, Container inventory) {
        super(type, syncId);

        this.inventory = inventory;
        this.output = new SimpleContainer(1);

        inventory.startOpen(playerInventory.player);

        this.addSlot(new DyeSlot(inventory, output, 0, 38, 17));
        this.addSlot(new DyeSlot(inventory, output, 1, 38, 35));
        this.addSlot(new DyeSlot(inventory, output, 2, 38, 53));

        this.addSlot(new PumpkinSlot(inventory, output, PUMPKIN_SLOT_INDEX, 63, 35));

        this.addSlot(new OutputSlot(this.inventory, this.output, 121, 35, this::refreshOutput));

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
        ((CarvingSlot) this.slots.get(OUTPUT_SLOT_INDEX - 1)).updateOutput();
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

                if (invSlot < STATION_SLOT_COUNT) {
                    refreshOutput();
                }
            }

            int moved = newStack.getCount() - originalStack.getCount();
            if (moved <= 0) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, newStack.copyWithCount(moved));
        }

        return newStack;
    }

    private static class CarvingSlot extends Slot {
        private final Container output;

        public CarvingSlot(Container inventory, Container output, int index, int x, int y) {
            super(inventory, index, x, y);
            this.output = output;
        }

        protected void updateOutput() {
            ItemStack pumpkin = this.container.getItem(PUMPKIN_SLOT_INDEX);
            if (pumpkin.isEmpty()) {
                this.output.setItem(0, ItemStack.EMPTY);
                return;
            }

            List<DyeColor> dyes = new ArrayList<>();
            for (int i = 0; i < PUMPKIN_SLOT_INDEX; i++) {
                ItemStack dye = this.container.getItem(i);
                if (dye.isEmpty()) {
                    continue;
                }

                DyeColor dyeColor = dye.get(DataComponents.DYE);
                if (dyeColor != null) {
                    dyes.add(dyeColor);
                }
            }

            // Carving isn't implemented yet, so with no dye present there's nothing this station can
            // do to a plain pumpkin.
            if (dyes.isEmpty()) {
                this.output.setItem(0, ItemStack.EMPTY);
                return;
            }

            ColorHelpers.RGB currentColor = null;
            DyedColorComponent existing = pumpkin.get(AllHallowsSteveComponentTypes.DYED_COLOR_COMPONENT.get());
            if (existing != null) {
                currentColor = ColorHelpers.RGB.fromInt(existing.color());
            }

            ColorHelpers.RGB mixed = ColorHelpers.mixColors(currentColor, dyes);
            int colorInt = mixed == null ? DyedColorComponent.DEFAULT_COLOR : mixed.toInt();

            ItemStack outputStack = new ItemStack(ModBlocks.DYED_PUMPKIN.get());
            outputStack.set(AllHallowsSteveComponentTypes.DYED_COLOR_COMPONENT.get(), new DyedColorComponent(colorInt));

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

    private static class DyeSlot extends CarvingSlot {
        public DyeSlot(Container inventory, Container output, int index, int x, int y) {
            super(inventory, output, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof DyeItem;
        }
    }

    private static class PumpkinSlot extends CarvingSlot {
        public PumpkinSlot(Container inventory, Container output, int index, int x, int y) {
            super(inventory, output, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(Items.PUMPKIN) || stack.is(ModBlocks.DYED_PUMPKIN.get().asItem());
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

        @Override
        public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
            int taken = stack.getCount();

            if (taken > 0) {
                for (int i = 0; i < CarvingStationBlockEntity.INVENTORY_SIZE; i++) {
                    this.input.removeItem(i, taken);
                }

                this.input.setChanged();

                this.refreshOutput.run();
            }

            super.onTake(player, stack);
        }
    }
}

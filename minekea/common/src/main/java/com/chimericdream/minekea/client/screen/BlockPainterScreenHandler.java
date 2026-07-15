package com.chimericdream.minekea.client.screen;

import com.chimericdream.lib.screen.ScreenHelpers;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.item.Tools;
import com.chimericdream.minekea.item.tools.BlockPainterItem;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class BlockPainterScreenHandler extends AbstractContainerMenu {
    public static final Identifier SCREEN_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "screens/items/block_painter");

    private final BlockPainterItem.PainterInventory painter;
    private final int ROW_COUNT = 2;

    public BlockPainterScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new ItemStack(Tools.BLOCK_PAINTER_ITEM.get()));
    }

    public BlockPainterScreenHandler(int syncId, Inventory playerInventory, ItemStack stack) {
        super(Tools.BLOCK_PAINTER_SCREEN_HANDLER.get(), syncId);

        painter = new BlockPainterItem.PainterInventory(stack);

        int i = -18, j, k;
        for (j = 0; j < ROW_COUNT; ++j) {
            for (k = 0; k < 8; ++k) {
                this.addSlot(new PainterSlot(
                    painter,
                    k + j * 8,
                    17 + k * ScreenHelpers.ROW_HEIGHT,
                    ScreenHelpers.ROW_HEIGHT + j * ScreenHelpers.ROW_HEIGHT
                ));
            }
        }

        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(
                    playerInventory,
                    k + j * 9 + 9,
                    8 + k * ScreenHelpers.ROW_HEIGHT,
                    ScreenHelpers.getPlayerInventoryOffset(ROW_COUNT) + j * ScreenHelpers.ROW_HEIGHT + i
                ));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(
                playerInventory,
                j,
                8 + j * ScreenHelpers.ROW_HEIGHT,
                ScreenHelpers.getPlayerHotbarOffset(ROW_COUNT) + i
            ));
        }
    }

    public Container getInventory() {
        return painter;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.painter.stillValid(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;

        Slot slot = this.slots.get(invSlot);
        if (slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < this.painter.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.painter.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, this.painter.getContainerSize(), false)) {
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

    private static class PainterSlot extends Slot {
        public PainterSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return doesItemMatchSlot(stack);
        }

        private boolean doesItemMatchSlot(ItemStack item) {
            return switch (this.getContainerSlot()) {
                case 0 -> item.is(Items.DYE.white());
                case 1 -> item.is(Items.DYE.lightGray());
                case 2 -> item.is(Items.DYE.gray());
                case 3 -> item.is(Items.DYE.black());
                case 4 -> item.is(Items.DYE.brown());
                case 5 -> item.is(Items.DYE.red());
                case 6 -> item.is(Items.DYE.orange());
                case 7 -> item.is(Items.DYE.yellow());
                case 8 -> item.is(Items.DYE.lime());
                case 9 -> item.is(Items.DYE.green());
                case 10 -> item.is(Items.DYE.cyan());
                case 11 -> item.is(Items.DYE.lightBlue());
                case 12 -> item.is(Items.DYE.blue());
                case 13 -> item.is(Items.DYE.purple());
                case 14 -> item.is(Items.DYE.magenta());
                case 15 -> item.is(Items.DYE.pink());
                default -> false;
            };
        }
    }
}

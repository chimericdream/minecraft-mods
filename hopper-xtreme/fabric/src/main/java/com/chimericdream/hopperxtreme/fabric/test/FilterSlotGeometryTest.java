package com.chimericdream.hopperxtreme.fabric.test;

import com.chimericdream.hopperxtreme.block.ModBlocks;
import com.chimericdream.hopperxtreme.client.screen.FilteredHopperScreenHandler;
import com.chimericdream.hopperxtreme.entity.XtremeHopperBlockEntity;
import com.chimericdream.hopperxtreme.item.ModItems;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Menu-level regression tests for the filtered-hopper "filter geometry" bugs
 * (CODE-REVIEW-PLAN 1.2 and 1.3).
 *
 * <p>The server block entity hides the filter slot from the Container API
 * ({@code getContainerSize()} returns the storage-only count), but the client builds the menu over a
 * dummy {@code SimpleContainer} that does not. Deriving the filter-slot index or the hopper/player
 * boundary from {@code getContainerSize()} therefore disagrees between the two sides. These tests pin
 * the fixed behaviour on both sides.
 */
@SuppressWarnings("unused")
public class FilterSlotGeometryTest {
    /**
     * 1.2 — On the server (real BE), {@code NonFilterSlot} must read the true filter slot (index 5),
     * not {@code getContainerSize() - 1} (index 4, a real storage slot). With the bug, an ordinary item
     * in the last storage slot was treated as the filter and the GUI rejected every insert.
     */
    @GameTest
    public void nonFilterSlotReadsCorrectFilterSlotOnServer(GameTestHelper context) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();

        BlockPos pos = new BlockPos(1, 1, 1);
        context.setBlock(pos, ModBlocks.FILTERED_GOLDEN_HOPPER.get().defaultBlockState());
        XtremeHopperBlockEntity hopper = context.getBlockEntity(pos, XtremeHopperBlockEntity.class);

        if (hopper.getContainerSize() != 5) {
            context.fail(Component.literal("Expected a filtered hopper to report getContainerSize()==5, got " + hopper.getContainerSize()), pos);
            return;
        }

        // Fill the last STORAGE slot (index 4) with an ordinary item; leave the filter slot (index 5) empty.
        hopper.setItem(4, new ItemStack(Items.COBBLESTONE, 1));

        FilteredHopperScreenHandler menu = new FilteredHopperScreenHandler(null, 1, player.getInventory(), hopper);

        // An empty filter accepts everything, so storage slot 0 must allow an ordinary item.
        Slot storageSlot0 = menu.getSlot(0);
        if (!storageSlot0.mayPlace(new ItemStack(Items.DIAMOND))) {
            context.fail(Component.literal("Storage slot 0 rejected a diamond even though the filter slot is empty (1.2 regression)"), pos);
            return;
        }

        // Sanity: the filter slot itself only accepts the filter item.
        Slot filterSlot = menu.getSlot(5);
        if (filterSlot.mayPlace(new ItemStack(Items.DIAMOND))) {
            context.fail(Component.literal("Filter slot accepted a diamond, but should only accept the hopper item filter"), pos);
            return;
        }
        if (!filterSlot.mayPlace(new ItemStack(ModItems.HOPPER_ITEM_FILTER_ITEM.get()))) {
            context.fail(Component.literal("Filter slot rejected the hopper item filter"), pos);
            return;
        }

        context.succeed();
    }

    /**
     * 1.3 — Under the client's geometry (dummy {@code SimpleContainer(6)} where
     * {@code getContainerSize()} is 6, not 5), the shift-click boundary between hopper and player slots
     * must be exactly 6. The bug computed {@code getContainerSize() + 1 == 7}, so the first player slot
     * was treated as a hopper slot and shift-click routing desynced.
     */
    @GameTest
    public void quickMoveBoundaryUsesFixedHopperSlotCount(GameTestHelper context) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();

        // The no-BE constructor builds over a dummy SimpleContainer(6), reproducing the client's geometry.
        FilteredHopperScreenHandler menu = new FilteredHopperScreenHandler(1, player.getInventory());

        int expectedSlots = 6 + 27 + 9;
        if (menu.slots.size() != expectedSlots) {
            context.fail(Component.literal("Expected " + expectedSlots + " menu slots, got " + menu.slots.size()));
            return;
        }

        // Menu slot index 6 is the FIRST player slot. Shift-clicking it must move the item INTO the
        // hopper (container slots 0..5). With the bug it was treated as a hopper slot and never moved.
        int firstPlayerSlot = 6;
        menu.getSlot(firstPlayerSlot).set(new ItemStack(Items.DIAMOND, 3));
        menu.quickMoveStack(player, firstPlayerSlot);

        ItemStack hopperSlot0 = menu.getInventory().getItem(0);
        if (!hopperSlot0.is(Items.DIAMOND) || hopperSlot0.getCount() != 3) {
            context.fail(Component.literal("Expected the shift-clicked diamonds to move into hopper slot 0, found " + hopperSlot0));
            return;
        }

        context.succeed();
    }
}

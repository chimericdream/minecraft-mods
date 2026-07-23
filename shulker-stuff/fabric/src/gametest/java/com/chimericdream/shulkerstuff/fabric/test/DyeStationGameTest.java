package com.chimericdream.shulkerstuff.fabric.test;

import com.chimericdream.shulkerstuff.block.ModBlocks;
import com.chimericdream.shulkerstuff.block.entity.DyeStationBlockEntity;
import com.chimericdream.shulkerstuff.client.screen.DyeStationScreenHandler;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Coverage for the dye station's viewer counting and output-slot bookkeeping.
 *
 * <p>Three separate bugs met here: the opener counter tested for the wrong menu class (copy-pasted
 * from the barrel), the menu never called {@code stopOpen}, and taking the result consumed inputs
 * from {@code Slot#remove} — which no shift-click path reaches — without ever recomputing the
 * result afterwards.
 */
@SuppressWarnings("unused")
public class DyeStationGameTest {
    private static final BlockPos STATION_POS = new BlockPos(2, 2, 2);

    private record Station(DyeStationBlockEntity entity, ServerPlayer player) {
        DyeStationScreenHandler openMenu() {
            DyeStationScreenHandler menu = new DyeStationScreenHandler(
                ModBlocks.DYE_STATION_SCREEN_HANDLER.get(), 1, player.getInventory(), entity
            );
            player.containerMenu = menu;

            return menu;
        }
    }

    /** Places a dye station, loads a shulker box and one dye into it, and puts a player on top. */
    private static Station station(GameTestHelper context) {
        context.setBlock(STATION_POS, ModBlocks.DYE_STATION.get().defaultBlockState());

        DyeStationBlockEntity entity = context.getBlockEntity(STATION_POS, DyeStationBlockEntity.class);

        ServerPlayer player = context.makeMockServerPlayerInLevel();
        BlockPos absolute = context.absolutePos(STATION_POS);
        player.setPos(absolute.getX() + 0.5, absolute.getY(), absolute.getZ() + 0.5);

        return new Station(entity, player);
    }

    /**
     * {@code isOwnContainer} used to test {@code player.containerMenu instanceof ChestMenu}, so the
     * recheck never counted anybody actually looking in the station.
     */
    @GameTest
    public void openerRecheckCountsPlayersUsingTheStation(GameTestHelper context) {
        Station station = station(context);
        station.openMenu();

        DyeStationBlockEntity.tick(
            context.getLevel(),
            context.absolutePos(STATION_POS),
            context.getBlockState(STATION_POS),
            station.entity()
        );

        int viewers = DyeStationBlockEntity.getPlayersLookingInStationCount(
            context.getLevel(), context.absolutePos(STATION_POS)
        );

        if (viewers != 1) {
            context.fail("Expected the station to count 1 viewer, got " + viewers);
        }

        context.succeed();
    }

    /** The menu opens the container; closing it has to close the container too. */
    @GameTest
    public void closingTheMenuReleasesTheViewer(GameTestHelper context) {
        Station station = station(context);
        DyeStationScreenHandler menu = station.openMenu();

        int whileOpen = DyeStationBlockEntity.getPlayersLookingInStationCount(
            context.getLevel(), context.absolutePos(STATION_POS)
        );
        if (whileOpen != 1) {
            context.fail("Expected 1 viewer while the menu is open, got " + whileOpen);
        }

        menu.removed(station.player());

        int afterClose = DyeStationBlockEntity.getPlayersLookingInStationCount(
            context.getLevel(), context.absolutePos(STATION_POS)
        );
        if (afterClose != 0) {
            context.fail("Expected 0 viewers after closing the menu, got " + afterClose);
        }

        context.succeed();
    }

    /**
     * Shift-clicking the result used to do nothing: the old boundary check treated the result slot
     * as a player slot and then looked for a target among the station's own slots.
     */
    @GameTest
    public void shiftClickingTheOutputMovesItToThePlayer(GameTestHelper context) {
        Station station = station(context);
        station.entity().setItem(3, new ItemStack(Items.SHULKER_BOX));
        station.entity().setItem(0, new ItemStack(Items.DYE.red()));

        DyeStationScreenHandler menu = station.openMenu();

        ItemStack result = menu.getSlot(DyeStationScreenHandler.OUTPUT_SLOT_INDEX).getItem();
        if (result.isEmpty()) {
            context.fail("The station should have produced a dyed shulker box to shift-click");
        }

        menu.quickMoveStack(station.player(), DyeStationScreenHandler.OUTPUT_SLOT_INDEX);

        if (!station.player().getInventory().contains(stack -> stack.is(Items.SHULKER_BOX))) {
            context.fail("Shift-clicking the result should have moved a shulker box to the player");
        }

        assertInputsConsumed(context, station, 0);
        context.succeed();
    }

    /**
     * Picking the result up consumes the inputs exactly once and recomputes the result — the old
     * code shrank the inputs from {@code Slot#remove} and never refreshed, leaving a stale result.
     */
    @GameTest
    public void takingTheOutputConsumesInputsAndRefreshes(GameTestHelper context) {
        Station station = station(context);
        station.entity().setItem(3, new ItemStack(Items.SHULKER_BOX));
        station.entity().setItem(0, new ItemStack(Items.DYE.red(), 3));

        DyeStationScreenHandler menu = station.openMenu();
        menu.clicked(DyeStationScreenHandler.OUTPUT_SLOT_INDEX, 0, ContainerInput.PICKUP, station.player());

        if (!menu.getCarried().is(Items.SHULKER_BOX)) {
            context.fail("Expected to be carrying the dyed shulker box, got " + menu.getCarried());
        }

        assertInputsConsumed(context, station, 2);

        ItemStack refreshed = menu.getSlot(DyeStationScreenHandler.OUTPUT_SLOT_INDEX).getItem();
        if (!refreshed.isEmpty()) {
            context.fail("With the shulker box consumed the result should be empty, got " + refreshed);
        }

        context.succeed();
    }

    /** One shulker box and one dye leave the station per result taken. */
    private static void assertInputsConsumed(GameTestHelper context, Station station, int dyeRemaining) {
        ItemStack shulker = station.entity().getItem(3);
        if (!shulker.isEmpty()) {
            context.fail("The shulker box should have been consumed, but the slot holds " + shulker);
        }

        ItemStack dye = station.entity().getItem(0);
        if (dye.getCount() != dyeRemaining) {
            context.fail("Expected " + dyeRemaining + " dye left, but the slot holds " + dye);
        }
    }
}

package com.chimericdream.minekea.fabric.test;

import com.chimericdream.minekea.block.containers.crates.CrateBlock;
import com.chimericdream.minekea.entity.block.containers.CrateBlockEntity;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;

/**
 * Regression coverage for GitHub issue #138: a hopper on a double crate only filled the half it touched,
 * because hoppers talk to the block entity directly and only know how to combine vanilla chests.
 * {@link CrateBlock} now exposes both halves through {@code WorldlyContainerHolder}.
 */
@SuppressWarnings("unused")
public class DoubleCrateHopperGameTest {
    private static final BlockPos WEST_CRATE = new BlockPos(2, 2, 2);
    private static final BlockPos EAST_CRATE = WEST_CRATE.east();

    private static void placeDoubleCrate(GameTestHelper context) {
        Block crate = BuiltInRegistries.BLOCK.getValue(CrateBlock.makeId("oak"));

        context.setBlock(WEST_CRATE, crate.defaultBlockState()
            .setValue(CrateBlock.FACING, Direction.UP)
            .setValue(CrateBlock.CONNECTED_EAST, true)
            .setValue(CrateBlock.CRATE_TYPE, ChestType.LEFT));
        context.setBlock(EAST_CRATE, crate.defaultBlockState()
            .setValue(CrateBlock.FACING, Direction.UP)
            .setValue(CrateBlock.CONNECTED_WEST, true)
            .setValue(CrateBlock.CRATE_TYPE, ChestType.RIGHT));
    }

    private static void fill(CrateBlockEntity crate, ItemStack stack) {
        for (int i = 0; i < crate.getContainerSize(); i++) {
            crate.setItem(i, stack.copy());
        }
    }

    /** With the touched half completely full, the hopper must overflow into the other half. */
    @GameTest
    public void hopperOverflowsIntoOtherHalf(GameTestHelper context) {
        placeDoubleCrate(context);

        CrateBlockEntity west = context.getBlockEntity(WEST_CRATE, CrateBlockEntity.class);
        CrateBlockEntity east = context.getBlockEntity(EAST_CRATE, CrateBlockEntity.class);
        fill(west, new ItemStack(Items.COBBLESTONE, 64));

        BlockPos hopperPos = WEST_CRATE.above();
        context.setBlock(hopperPos, Blocks.HOPPER); // default facing DOWN, into the west crate
        context.getBlockEntity(hopperPos, HopperBlockEntity.class).setItem(0, new ItemStack(Items.REDSTONE, 3));

        context.succeedWhen(() -> {
            int inEast = 0;
            for (int i = 0; i < east.getContainerSize(); i++) {
                if (east.getItem(i).is(Items.REDSTONE)) {
                    inEast += east.getItem(i).getCount();
                }
            }

            context.assertTrue(inEast == 3, "the hopper should have pushed all 3 redstone into the far half, found " + inEast);
        });
    }

    /** The reported scenario: a hopper on the west end, pointing east into the crate, with that half full. */
    @GameTest
    public void sidewaysHopperOverflowsIntoOtherHalf(GameTestHelper context) {
        placeDoubleCrate(context);

        CrateBlockEntity west = context.getBlockEntity(WEST_CRATE, CrateBlockEntity.class);
        CrateBlockEntity east = context.getBlockEntity(EAST_CRATE, CrateBlockEntity.class);
        fill(west, new ItemStack(Items.REDSTONE_BLOCK, 64));

        BlockPos hopperPos = WEST_CRATE.west();
        context.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.EAST));
        context.getBlockEntity(hopperPos, HopperBlockEntity.class).setItem(0, new ItemStack(Items.REDSTONE_BLOCK, 64));

        context.succeedWhen(() -> context.assertTrue(
            !east.isEmpty(),
            "the sideways hopper should have pushed redstone blocks into the far half"));
    }

    /** A hopper below a double crate must also be able to pull from the half it is not touching. */
    @GameTest
    public void hopperDrainsOtherHalf(GameTestHelper context) {
        placeDoubleCrate(context);

        CrateBlockEntity west = context.getBlockEntity(WEST_CRATE, CrateBlockEntity.class);
        CrateBlockEntity east = context.getBlockEntity(EAST_CRATE, CrateBlockEntity.class);
        east.setItem(0, new ItemStack(Items.REDSTONE, 2));

        BlockPos hopperPos = WEST_CRATE.below();
        context.setBlock(hopperPos, Blocks.HOPPER); // default facing DOWN; pulls from the crate above it
        HopperBlockEntity hopper = context.getBlockEntity(hopperPos, HopperBlockEntity.class);

        context.succeedWhen(() -> context.assertTrue(
            east.isEmpty() && west.isEmpty(),
            "the hopper should have drained the far half too"));
    }
}

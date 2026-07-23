package com.chimericdream.sponj.fabric.test;

import com.chimericdream.sponj.BlockUtils;
import com.chimericdream.sponj.blocks.ModBlocks;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

/**
 * Coverage for sponj's absorption bounds.
 *
 * <p>The connected-sponj search is deliberately capped at {@link ModBlocks#MAX_CONNECTED_SPONJES}:
 * a sponj's clear radius and its block budget both scale with the connected count, so an unbounded
 * search over a large sponj wall would clear tens of thousands of blocks in one tick and lag the
 * server. These tests pin that the cap holds and that a structure under the cap counts in full.
 *
 * <p>Separately, {@code SponjBlock.absorbWater}'s washable-block branch (kelp, seagrass) used to
 * queue with a hardcoded {@code j < 6} while the fluid branches used {@code j < absorptionRadius},
 * so the multi-sponj radius bonus never reached plants; the last test covers that.
 */
@SuppressWarnings("unused")
public class SponjAbsorptionRangeGameTest {
    /**
     * A structure larger than the cap must contribute at most {@link ModBlocks#MAX_CONNECTED_SPONJES}
     * sponjes to an absorption — this is what bounds the per-tick clearing work.
     */
    @GameTest
    public void connectedCountIsCappedForLargeStructures(GameTestHelper context) {
        int cap = ModBlocks.MAX_CONNECTED_SPONJES;

        // A solid cube of sponjes with well more than `cap` blocks. Size it to the cap so the test
        // survives retuning the constant (side^3 > cap).
        int side = (int) Math.ceil(Math.cbrt(cap + 1)) + 1;
        BlockPos corner = new BlockPos(1, 1, 1);
        int placed = 0;
        for (int x = 0; x < side; x++) {
            for (int y = 0; y < side; y++) {
                for (int z = 0; z < side; z++) {
                    context.setBlock(corner.offset(x, y, z), ModBlocks.SPONJ_BLOCK.get().defaultBlockState());
                    placed++;
                }
            }
        }

        if (placed <= cap) {
            context.fail("Test setup error: placed " + placed + " sponjes but the cap is " + cap);
        }

        int found = BlockUtils.getConnectedBlocksByType(
            context.getLevel(),
            context.absolutePos(corner),
            ModBlocks.getSponjBlocks(),
            cap
        ).size();

        if (found != cap) {
            context.fail("A structure of " + placed + " sponjes should report exactly the cap ("
                + cap + ") as connected, but reported " + found);
        }

        context.succeed();
    }

    /**
     * A structure smaller than the cap must count every one of its sponjes — the cap only clips
     * oversized builds, it doesn't shrink ordinary ones.
     */
    @GameTest
    public void connectedCountReturnsAllWhenUnderTheCap(GameTestHelper context) {
        int count = Math.min(6, ModBlocks.MAX_CONNECTED_SPONJES);
        BlockPos start = new BlockPos(1, 2, 1);
        for (int x = 0; x < count; x++) {
            context.setBlock(start.offset(x, 0, 0), ModBlocks.SPONJ_BLOCK.get().defaultBlockState());
        }

        int found = BlockUtils.getConnectedBlocksByType(
            context.getLevel(),
            context.absolutePos(start),
            ModBlocks.getSponjBlocks(),
            ModBlocks.MAX_CONNECTED_SPONJES
        ).size();

        if (found != count) {
            context.fail("A line of " + count + " sponjes (under the cap) should all count, but "
                + found + " were reported");
        }

        context.succeed();
    }

    /**
     * Two stacked sponjes give an absorption radius of 6 + 3 = 9, so a seagrass corridor must be
     * cleared past the 6 steps the old hardcoded washable-block limit allowed.
     *
     * <p>The corridor is laid out so its graph distance from the sponj is forced: a run along z=0,
     * a single hop to z=1 at the far wall, then a run back along z=2. The empty z=1 row keeps the
     * two runs from touching, so the block at (7,2,2) really is 9 steps out.
     */
    @GameTest
    public void multiSponjRadiusAppliesToWashableBlocks(GameTestHelper context) {
        BlockPos nineStepsOut = new BlockPos(7, 2, 2);
        BlockPos eightStepsOut = new BlockPos(7, 2, 1);

        // Seagrass survives neighbour updates as long as it has a sturdy face below it.
        for (int x = 0; x <= 7; x++) {
            context.setBlock(new BlockPos(x, 1, 0), Blocks.STONE.defaultBlockState());
            context.setBlock(new BlockPos(x, 1, 2), Blocks.STONE.defaultBlockState());
        }
        context.setBlock(new BlockPos(7, 1, 1), Blocks.STONE.defaultBlockState());

        for (int x = 2; x <= 7; x++) {
            context.setBlock(new BlockPos(x, 2, 0), Blocks.SEAGRASS.defaultBlockState());
        }
        context.setBlock(eightStepsOut, Blocks.SEAGRASS.defaultBlockState());
        for (int x = 7; x >= 5; x--) {
            context.setBlock(new BlockPos(x, 2, 2), Blocks.SEAGRASS.defaultBlockState());
        }

        context.setBlock(new BlockPos(0, 2, 0), ModBlocks.SPONJ_BLOCK.get().defaultBlockState());
        context.setBlock(new BlockPos(0, 3, 0), ModBlocks.SPONJ_BLOCK.get().defaultBlockState());

        // Bridging the sponj to the corridor is what kicks off the absorption.
        context.setBlock(new BlockPos(1, 2, 0), Blocks.SEAGRASS.defaultBlockState());

        context.assertBlockPresent(Blocks.AIR, eightStepsOut);
        context.assertBlockPresent(Blocks.AIR, nineStepsOut);
        context.succeed();
    }
}

package com.chimericdream.sponj.fabric.test;

import com.chimericdream.sponj.blocks.ModBlocks;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

/**
 * Regression coverage for the two range bugs in sponj's absorption.
 *
 * <p>1. {@code BlockUtils.isWithinDistance} compared a <em>squared</em> distance against an
 * unsquared limit, so the connected-sponj flood fill stopped at &radic;32 &asymp; 5.7 blocks instead
 * of the intended 32. A sponj chain longer than that was silently cut in half: the far end never
 * counted toward the sponj count (smaller absorption radius / budget) and never turned wet.
 *
 * <p>2. {@code SponjBlock.absorbWater}'s washable-block branch (kelp, seagrass, &hellip;) queued with
 * a hardcoded {@code j < 6} while the fluid branches used {@code j < absorptionRadius}, so the
 * multi-sponj radius bonus didn't apply to plants.
 */
@SuppressWarnings("unused")
public class SponjAbsorptionRangeGameTest {
    /**
     * A straight chain of 8 sponjes, absorbing at one end. Every sponj in the chain must turn wet —
     * including the block 7 away, which the old &radic;32 cutoff never reached.
     */
    @GameTest
    public void wholeChainTurnsWetBeyondTheOldCutoff(GameTestHelper context) {
        BlockPos farEnd = new BlockPos(0, 2, 0);

        for (int x = 0; x <= 7; x++) {
            context.setBlock(new BlockPos(x, 2, 0), ModBlocks.SPONJ_BLOCK.get().defaultBlockState());
        }

        // Placing water on top of the near end fires neighborChanged there, so the flood fill starts
        // from x=7 and has to walk the full 7 blocks to reach the far end.
        context.setBlock(new BlockPos(7, 3, 0), Blocks.WATER.defaultBlockState());

        context.assertBlockPresent(ModBlocks.WET_SPONJ_BLOCK.get(), new BlockPos(7, 2, 0));
        context.assertBlockPresent(ModBlocks.WET_SPONJ_BLOCK.get(), farEnd);
        context.succeed();
    }

    /**
     * Two stacked sponjes give an absorption radius of 6 + 3 = 9, so a seagrass corridor must be
     * cleared well past the 6 steps the hardcoded washable-block limit allowed.
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

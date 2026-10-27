package com.chimericdream.forwardingaddress.fabric.test;

import com.chimericdream.forwardingaddress.fabric.test.fixture.TestFixtures;
import com.chimericdream.forwardingaddress.migration.MigrationEvents;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

/**
 * The chunk scan and the per-tick cap, driven by hand: a test reports the chunk itself and calls
 * {@link MigrationEvents#drain} with a budget, the way the server tick would.
 */
@SuppressWarnings("unused")
public class ChunkQueueGameTest {
    private static final int ROW = 10;

    private static int newBlocksInRow(GameTestHelper context) {
        int count = 0;
        for (int x = 0; x < ROW; x++) {
            if (context.getBlockState(new BlockPos(x % 8, 2 + x / 8, 3)).is(TestFixtures.NEW_BLOCK.get())) {
                count++;
            }
        }

        return count;
    }

    @GameTest
    public void aDrainNeverConvertsMoreThanItsBudget(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        for (int x = 0; x < ROW; x++) {
            context.setBlock(new BlockPos(x % 8, 2 + x / 8, 3), TestFixtures.OLD_BLOCK.get().defaultBlockState());
        }

        ChunkPos chunk = ChunkPos.containing(context.absolutePos(new BlockPos(0, 2, 3)));
        MigrationEvents.queue().enqueueChunk(level.dimension(), chunk);

        int handled = MigrationEvents.drain(level, 4);

        if (handled != 4) {
            context.fail("a budget of 4 should handle exactly 4 positions, but handled " + handled);
        }
        if (newBlocksInRow(context) > 4) {
            context.fail("no more than 4 of this test's blocks may convert in one drain, but " + newBlocksInRow(context) + " did");
        }

        // Let the rest through, in budget-sized steps, the way later ticks would.
        for (int i = 0; i < 1000 && MigrationEvents.queue().pendingPositions(level.dimension()) > 0; i++) {
            MigrationEvents.drain(level, 4);
        }

        if (newBlocksInRow(context) != ROW) {
            context.fail("every old block should convert eventually, but only " + newBlocksInRow(context) + " of " + ROW + " did");
        }

        context.succeed();
    }

    @GameTest
    public void aChunkWithNothingToConvertCostsNothing(GameTestHelper context) {
        ServerLevel level = context.getLevel();
        context.setBlock(new BlockPos(1, 2, 1), TestFixtures.NEW_BLOCK.get().defaultBlockState());

        ChunkPos chunk = ChunkPos.containing(context.absolutePos(new BlockPos(1, 2, 1)));
        MigrationEvents.queue().enqueueChunk(level.dimension(), chunk);

        // Other tests may have old blocks in this chunk, so only check that this block is untouched.
        MigrationEvents.drain(level, 1000);

        context.assertBlockPresent(TestFixtures.NEW_BLOCK.get(), new BlockPos(1, 2, 1));
        context.succeed();
    }
}

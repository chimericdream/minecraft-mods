package com.chimericdream.minekea.fabric.test;

import com.chimericdream.minekea.block.furniture.shutters.ShutterBlock;
import com.chimericdream.minekea.block.furniture.shutters.Shutters;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;

/**
 * Regression coverage for {@code OpenShutterHalfBlock}'s interaction guards.
 *
 * <p>An open-shutter half only ever exists flanking its parent {@code ShutterBlock}, which the mod
 * places and removes as a pair. If a half is left orphaned — a {@code /setblock}, a world edit, the
 * demo world, or a partially-broken shutter — its {@code useWithoutItem}/{@code playerWillDestroy}
 * used to cycle the {@code OPEN} property on whatever non-shutter block sat where the parent should
 * be, which throws ({@code IllegalArgumentException: Cannot set property OPEN...}) and crashes. The
 * guards now bail out when the centre block isn't a shutter; these tests pin that down, and also
 * check the guard didn't regress the normal open/close cycle.
 *
 * <p>{@link GameTestHelper#useBlock(BlockPos)} synthesises a hit at the block centre and dispatches
 * with an empty creative hand, so it lands squarely in {@code useWithoutItem} — exactly the click
 * path that used to crash.
 */
@SuppressWarnings("unused")
public class OpenShutterHalfBlockGameTest {
    private static final String MATERIAL = "oak";

    private static Block halfBlock() {
        return Shutters.OPEN_SHUTTER_HALF_BLOCKS.get(MATERIAL).get();
    }

    private static Block shutterBlock() {
        return Shutters.SHUTTER_BLOCKS.get(MATERIAL).get();
    }

    /**
     * Right-clicking an orphaned half (no parent shutter beside it) must no-op instead of crashing.
     * Without the guard this throws inside {@code useWithoutItem} and fails the test.
     */
    @GameTest
    public void usingOrphanedHalfDoesNotCrash(GameTestHelper context) {
        BlockPos pos = new BlockPos(2, 2, 2);
        context.setBlock(pos, halfBlock().defaultBlockState());

        // The would-be parent (pos.east(), per default WALL_SIDE=NORTH/HALF=LEFT) is air, so the old
        // code crashed cycling OPEN on it. The guard returns PASS and leaves the half untouched.
        context.useBlock(pos);

        context.assertBlockPresent(halfBlock(), pos);
        context.succeed();
    }

    /**
     * The guard must not regress real behaviour: opening a shutter spawns its two halves, and using
     * either half closes the shutter and clears both halves.
     */
    @GameTest
    public void openThenCloseViaHalfStillWorks(GameTestHelper context) {
        BlockPos shutterPos = new BlockPos(2, 2, 2);
        BlockPos westHalf = shutterPos.west();
        BlockPos eastHalf = shutterPos.east();

        // A closed shutter with clear air on both sides (default WALL_SIDE=NORTH opens east/west).
        context.setBlock(shutterPos, shutterBlock().defaultBlockState());

        context.startSequence()
            .thenExecute(() -> context.useBlock(shutterPos))                // open it
            .thenIdle(1)
            .thenExecute(() -> {
                context.assertBlockProperty(shutterPos, ShutterBlock.OPEN, true);
                context.assertBlockPresent(halfBlock(), westHalf);
                context.assertBlockPresent(halfBlock(), eastHalf);
            })
            .thenExecute(() -> context.useBlock(westHalf))                  // close via a half
            .thenIdle(1)
            .thenExecute(() -> {
                context.assertBlockProperty(shutterPos, ShutterBlock.OPEN, false);
                context.assertBlockNotPresent(halfBlock(), westHalf);
                context.assertBlockNotPresent(halfBlock(), eastHalf);
            })
            .thenSucceed();
    }
}

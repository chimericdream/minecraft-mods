package com.chimericdream.lib.fabric.test;

import com.chimericdream.lib.entities.SimpleSeatEntity;
import com.chimericdream.lib.fabric.test.fixture.TestFixtures;
import com.chimericdream.lib.testkit.gametest.GameTestEntities;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;

/**
 * Validates the {@code SimpleSeatEntity} lifecycle: it carries a rider, and once the rider dismounts
 * and the entity has lived past its 20-tick grace window, {@link SimpleSeatEntity#tick()} kills it —
 * so a sit/dismount cycle leaves no leaked seat entities.
 *
 * <p>Deliberately not asserted (known gaps, tracked in chimeric-lib POTENTIAL_FEATURES under
 * "SimpleSeatEntity polish"): freeing the rider when the seat block is broken mid-sit, and keeping the
 * dismount position out of walls. Those behaviours don't exist yet, so asserting them would only encode
 * the gap rather than test the code.
 */
@SuppressWarnings("unused")
public class SimpleSeatEntityGameTest {
    @GameTest(maxTicks = 80)
    public void sitDismountAutoDespawnLeavesNoLeak(GameTestHelper context) {
        BlockPos seatPos = new BlockPos(3, 2, 3);
        SimpleSeatEntity seat = context.spawn(TestFixtures.SEAT_ENTITY.get(), seatPos);
        ServerPlayer rider = context.makeMockServerPlayerInLevel();

        rider.startRiding(seat);
        context.assertTrue(!seat.getPassengers().isEmpty(), "the rider should be sitting on the seat");
        GameTestEntities.assertCount(context, TestFixtures.SEAT_ENTITY.get(), 1);

        // Dismount after a few ticks; the seat should self-destruct once it is both rider-less and past
        // its 20-tick grace window.
        context.runAtTickTime(5L, rider::stopRiding);
        context.runAtTickTime(60L, () -> {
            GameTestEntities.assertNone(context, TestFixtures.SEAT_ENTITY.get());
            context.succeed();
        });
    }
}

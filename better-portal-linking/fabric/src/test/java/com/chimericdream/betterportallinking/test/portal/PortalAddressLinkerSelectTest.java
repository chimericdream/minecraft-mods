package com.chimericdream.betterportallinking.test.portal;

import com.chimericdream.betterportallinking.portal.PortalAddress;
import com.chimericdream.betterportallinking.portal.PortalAddressLinker;
import com.chimericdream.betterportallinking.portal.PortalAddressLinker.Candidate;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link PortalAddressLinker#select} is the pure, level-independent scoring/tiebreak core —
 * exercised directly here with hand-built {@link Candidate}s, no {@code ServerLevel} involved.
 */
public class PortalAddressLinkerSelectTest extends BootstrapMinecraft {
    private static final PortalAddress RED_ADDRESS = PortalAddress.fromBlocks(
        List.of(Blocks.CONCRETE.red(), Blocks.CONCRETE.red(), Blocks.CONCRETE.red(), Blocks.CONCRETE.red()));
    private static final PortalAddress SINGLE_RED = PortalAddress.fromBlocks(List.of(Blocks.CONCRETE.red()));
    private static final PortalAddress SINGLE_BLUE = PortalAddress.fromBlocks(List.of(Blocks.CONCRETE.blue()));

    @Test
    void highestScoreWinsEvenWhenMuchFartherFromTarget() {
        BlockPos target = new BlockPos(0, 64, 0);

        // Full 4-block address match, but very far away.
        Candidate far = new Candidate(new BlockPos(1000, 64, 1000), RED_ADDRESS);
        // Only a 1-block match, but right next to the target.
        Candidate near = new Candidate(new BlockPos(0, 64, 1), SINGLE_RED);

        Optional<BlockPos> result = PortalAddressLinker.select(List.of(far, near), RED_ADDRESS, target);

        assertEquals(Optional.of(far.representativePos()), result);
    }

    @Test
    void emptyEntryAddressReturnsEmpty() {
        BlockPos target = new BlockPos(0, 64, 0);
        Candidate candidate = new Candidate(new BlockPos(1, 64, 1), RED_ADDRESS);

        Optional<BlockPos> result = PortalAddressLinker.select(List.of(candidate), PortalAddress.empty(), target);

        assertEquals(Optional.empty(), result);
    }

    @Test
    void emptyCandidateListReturnsEmpty() {
        BlockPos target = new BlockPos(0, 64, 0);

        Optional<BlockPos> result = PortalAddressLinker.select(List.of(), RED_ADDRESS, target);

        assertEquals(Optional.empty(), result);
    }

    @Test
    void allZeroScoreCandidatesReturnEmptyNeverA0ScoreWinner() {
        BlockPos target = new BlockPos(0, 64, 0);
        Candidate candidate = new Candidate(new BlockPos(1, 64, 1), SINGLE_BLUE);

        Optional<BlockPos> result = PortalAddressLinker.select(List.of(candidate), SINGLE_RED, target);

        assertEquals(Optional.empty(), result);
    }

    @Test
    void tiedScoreNearerCandidateWins() {
        BlockPos target = new BlockPos(0, 64, 0);
        Candidate nearer = new Candidate(new BlockPos(3, 64, 0), SINGLE_RED);
        Candidate farther = new Candidate(new BlockPos(10, 64, 0), SINGLE_RED);

        Optional<BlockPos> result = PortalAddressLinker.select(List.of(farther, nearer), SINGLE_RED, target);

        assertEquals(Optional.of(nearer.representativePos()), result);
    }

    @Test
    void tiedScoreAndDistanceIsDeterministicByLowestPackedPosition() {
        BlockPos target = new BlockPos(0, 64, 0);
        // Equidistant from target (dx = +/-5, same y, dz = 0 -> distSqr = 25 for both).
        Candidate a = new Candidate(new BlockPos(5, 64, 0), SINGLE_RED);
        Candidate b = new Candidate(new BlockPos(-5, 64, 0), SINGLE_RED);
        List<Candidate> candidates = List.of(a, b);

        assertEquals(
            a.representativePos().distSqr(target), b.representativePos().distSqr(target),
            "test setup requires an exact distance tie"
        );

        // Pinned to a concrete position rather than re-derived with asLong(): re-deriving would
        // only prove select() is self-consistent, and would still pass if the tiebreak key itself
        // changed. BlockPos.asLong() packs x into the high, signed bits, so negative x sorts lowest.
        BlockPos expected = new BlockPos(-5, 64, 0);

        Optional<BlockPos> result = PortalAddressLinker.select(candidates, SINGLE_RED, target);
        assertEquals(Optional.of(expected), result);

        // No randomness: repeated calls, and calls against shuffled/reversed orderings, must
        // always return the same exact position.
        List<Candidate> reversed = new ArrayList<>(candidates);
        Collections.reverse(reversed);

        Random random = new Random(42);
        for (int i = 0; i < 100; i++) {
            assertEquals(Optional.of(expected), PortalAddressLinker.select(candidates, SINGLE_RED, target));
            assertEquals(Optional.of(expected), PortalAddressLinker.select(reversed, SINGLE_RED, target));

            List<Candidate> shuffled = new ArrayList<>(candidates);
            Collections.shuffle(shuffled, random);
            assertEquals(Optional.of(expected), PortalAddressLinker.select(shuffled, SINGLE_RED, target));
        }
    }

    @Test
    void tiedScoreAndDistanceAmongMoreThanTwoCandidatesIsAlwaysTheLowestPackedPosition() {
        BlockPos target = new BlockPos(0, 64, 0);
        // Four positions on the same sphere around target (distSqr = 25 for all), so score and
        // distance are tied four ways; only the deterministic asLong() tiebreak can settle it.
        List<Candidate> candidates = List.of(
            new Candidate(new BlockPos(5, 64, 0), SINGLE_RED),
            new Candidate(new BlockPos(-5, 64, 0), SINGLE_RED),
            new Candidate(new BlockPos(0, 64, 5), SINGLE_RED),
            new Candidate(new BlockPos(0, 64, -5), SINGLE_RED)
        );

        double distSqr = candidates.get(0).representativePos().distSqr(target);
        for (Candidate candidate : candidates) {
            assertEquals(distSqr, candidate.representativePos().distSqr(target), "test setup requires a 4-way distance tie");
        }

        // Pinned concrete winner, for the same reason as the two-way case above.
        BlockPos expected = new BlockPos(-5, 64, 0);

        Random random = new Random(7);
        for (int i = 0; i < 100; i++) {
            List<Candidate> shuffled = new ArrayList<>(candidates);
            Collections.shuffle(shuffled, random);
            assertEquals(Optional.of(expected), PortalAddressLinker.select(shuffled, SINGLE_RED, target));
        }
    }
}

package com.chimericdream.betterportallinking.test.portal;

import com.chimericdream.betterportallinking.portal.PortalAddress;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link PortalAddress} is an immutable multiset of at most four vanilla {@link Block}s. Uses real
 * {@code Blocks.*} constants (hence {@link BootstrapMinecraft}) since scoring/equality is defined
 * in terms of actual {@link Block} identity.
 */
public class PortalAddressTest extends BootstrapMinecraft {
    @Test
    void noOverlapScoresZero() {
        PortalAddress entry = PortalAddress.fromBlocks(List.of(Blocks.RED_CONCRETE, Blocks.RED_CONCRETE));
        PortalAddress candidate = PortalAddress.fromBlocks(List.of(Blocks.BLUE_CONCRETE));

        assertEquals(0, entry.score(candidate));
        assertEquals(0, candidate.score(entry));
    }

    @Test
    void partialOverlapScoresSharedCount() {
        PortalAddress entry = PortalAddress.fromBlocks(List.of(Blocks.RED_CONCRETE, Blocks.BLUE_CONCRETE));
        PortalAddress candidate = PortalAddress.fromBlocks(List.of(Blocks.RED_CONCRETE, Blocks.WHITE_TERRACOTTA));

        assertEquals(1, entry.score(candidate));
        assertEquals(1, candidate.score(entry));
    }

    @Test
    void orderIndependentEqualsAndScore() {
        List<Block> blocks = List.of(Blocks.RED_CONCRETE, Blocks.BLUE_CONCRETE, Blocks.WHITE_TERRACOTTA, Blocks.RED_CONCRETE);
        List<Block> shuffled = Arrays.asList(Blocks.WHITE_TERRACOTTA, Blocks.RED_CONCRETE, Blocks.RED_CONCRETE, Blocks.BLUE_CONCRETE);

        PortalAddress a = PortalAddress.fromBlocks(blocks);
        PortalAddress b = PortalAddress.fromBlocks(shuffled);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(4, a.score(b));
        assertEquals(4, b.score(a));

        PortalAddress other = PortalAddress.fromBlocks(List.of(Blocks.RED_CONCRETE, Blocks.RED_CONCRETE, Blocks.BLUE_CONCRETE, Blocks.WHITE_TERRACOTTA));
        assertEquals(a.score(other), b.score(other));
    }

    @Test
    void duplicatesUseMultisetMinNotRawCount() {
        PortalAddress entryTwoA = PortalAddress.fromBlocks(List.of(Blocks.RED_CONCRETE, Blocks.RED_CONCRETE));
        PortalAddress candidateOneA = PortalAddress.fromBlocks(List.of(Blocks.RED_CONCRETE));
        PortalAddress candidateThreeA = PortalAddress.fromBlocks(
            List.of(Blocks.RED_CONCRETE, Blocks.RED_CONCRETE, Blocks.RED_CONCRETE));

        // min(2, 1) = 1, NOT 2 — this is the whole point of the multiset-min rule.
        assertEquals(1, entryTwoA.score(candidateOneA));
        assertEquals(1, candidateOneA.score(entryTwoA));

        // min(2, 3) = 2.
        assertEquals(2, entryTwoA.score(candidateThreeA));
        assertEquals(2, candidateThreeA.score(entryTwoA));
    }

    @Test
    void identicalFourBlockAddressesScoreFour() {
        List<Block> blocks = List.of(Blocks.RED_CONCRETE, Blocks.BLUE_CONCRETE, Blocks.WHITE_TERRACOTTA, Blocks.RED_CONCRETE);
        PortalAddress a = PortalAddress.fromBlocks(blocks);
        PortalAddress b = PortalAddress.fromBlocks(blocks);

        assertEquals(4, a.score(b));
    }

    @Test
    void anythingScoredAgainstEmptyIsZero() {
        PortalAddress nonEmpty = PortalAddress.fromBlocks(List.of(Blocks.RED_CONCRETE, Blocks.BLUE_CONCRETE));

        assertEquals(0, nonEmpty.score(PortalAddress.empty()));
        assertEquals(0, PortalAddress.empty().score(nonEmpty));
        assertEquals(0, PortalAddress.empty().score(PortalAddress.empty()));
    }

    @Test
    void fromCornersOmitsBlocksFailingThePredicate() {
        // "Air is ignored" and "untagged blocks are ignored" are both just the predicate
        // rejecting the corner's block — fromCorners must never insert a placeholder for it.
        List<BlockPos> corners = List.of(
            new BlockPos(0, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 1, 0), new BlockPos(1, 1, 0));

        PortalAddress allAirAllRejected = PortalAddress.fromCorners(
            corners,
            pos -> Blocks.AIR,
            block -> false
        );

        assertTrue(allAirAllRejected.isEmpty());
        assertEquals(0, allAirAllRejected.score(PortalAddress.fromBlocks(List.of(Blocks.RED_CONCRETE))));
    }

    @Test
    void fromCornersKeepsOnlyBlocksThatPassThePredicate() {
        List<BlockPos> corners = List.of(
            new BlockPos(0, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 1, 0), new BlockPos(1, 1, 0));

        PortalAddress address = PortalAddress.fromCorners(
            corners,
            pos -> pos.getX() == 0 ? Blocks.RED_CONCRETE : Blocks.AIR,
            block -> block == Blocks.RED_CONCRETE
        );

        assertEquals(PortalAddress.fromBlocks(List.of(Blocks.RED_CONCRETE, Blocks.RED_CONCRETE)), address);
        assertEquals(2, address.score(PortalAddress.fromBlocks(List.of(Blocks.RED_CONCRETE, Blocks.RED_CONCRETE))));
    }

    @Test
    void emptyAddressIsEmptyAndHasNoBlocks() {
        assertTrue(PortalAddress.empty().isEmpty());
        assertTrue(PortalAddress.fromBlocks(List.of()).isEmpty());
    }
}

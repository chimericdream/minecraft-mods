package com.chimericdream.betterportallinking.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Immutable multiset of at most four {@link Block}s read from a portal frame's diagonal corners.
 *
 * <p>Order never matters and duplicate blocks are preserved and counted: four corners of the same
 * block score higher against a matching frame than a single matching corner would. Air and
 * untagged blocks are simply absent from the multiset — there is no explicit "empty slot" marker.
 */
public final class PortalAddress {
    private static final PortalAddress EMPTY = new PortalAddress(Map.of());

    private final Map<Block, Integer> counts;

    private PortalAddress(Map<Block, Integer> counts) {
        this.counts = counts;
    }

    public static PortalAddress empty() {
        return EMPTY;
    }

    /**
     * Builds an address from a list of blocks. {@code null} entries (representing "no address
     * block here") are dropped. An all-null or empty list produces {@link #empty()}.
     */
    public static PortalAddress fromBlocks(List<Block> blocks) {
        Map<Block, Integer> counts = new HashMap<>();
        for (Block block : blocks) {
            if (block != null) {
                counts.merge(block, 1, Integer::sum);
            }
        }

        return counts.isEmpty() ? EMPTY : new PortalAddress(Map.copyOf(counts));
    }

    /**
     * Builds an address from a list of positions (typically the four frame corners): looks up the
     * block at each position via {@code blockAt}, then keeps only the ones {@code isAddressBlock}
     * accepts. A position whose block fails the predicate — including air, which fails every
     * reasonable address-block predicate — is simply omitted. Deliberately level-independent: the
     * caller supplies the lookup function instead of this class importing {@code Level}.
     */
    public static PortalAddress fromCorners(
        List<BlockPos> corners,
        Function<BlockPos, Block> blockAt,
        Predicate<Block> isAddressBlock
    ) {
        List<Block> blocks = new ArrayList<>(corners.size());
        for (BlockPos corner : corners) {
            Block block = blockAt.apply(corner);
            if (block != null && isAddressBlock.test(block)) {
                blocks.add(block);
            }
        }
        return fromBlocks(blocks);
    }

    public boolean isEmpty() {
        return counts.isEmpty();
    }

    /**
     * Multiset intersection size: for each distinct block, {@code min(countInThis, countInOther)},
     * summed across all distinct blocks. Symmetric in {@code this} and {@code other}. Both sides
     * have at most four elements, so this simple double pass over the (tiny) count maps is correct
     * and fast enough — no need for the "remove one match per element" trick.
     */
    public int score(PortalAddress other) {
        if (this.counts.isEmpty() || other.counts.isEmpty()) {
            return 0;
        }

        int score = 0;
        for (Map.Entry<Block, Integer> entry : this.counts.entrySet()) {
            Integer otherCount = other.counts.get(entry.getKey());
            if (otherCount != null) {
                score += Math.min(entry.getValue(), otherCount);
            }
        }
        return score;
    }

    @Override
    public String toString() {
        if (counts.isEmpty()) {
            return "PortalAddress[]";
        }

        return counts.entrySet().stream()
            .map(entry -> {
                String name = BuiltInRegistries.BLOCK.getKey(entry.getKey()).toString();
                return entry.getValue() > 1 ? name + "x" + entry.getValue() : name;
            })
            .sorted()
            .collect(Collectors.joining(", ", "PortalAddress[", "]"));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PortalAddress other && this.counts.equals(other.counts);
    }

    @Override
    public int hashCode() {
        return counts.hashCode();
    }
}

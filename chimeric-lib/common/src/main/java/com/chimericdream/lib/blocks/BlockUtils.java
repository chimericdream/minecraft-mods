package com.chimericdream.lib.blocks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * Small position/adjacency helpers shared across mods. Nothing here is specific to any one mod.
 */
public class BlockUtils {
    public static List<BlockPos> getAdjacentBlockPositions(BlockPos pos, boolean down) {
        List<BlockPos> around = new ArrayList<>();

        around.add(pos.north());
        around.add(pos.east());
        around.add(pos.south());
        around.add(pos.west());
        around.add(pos.above());

        if (down) {
            around.add(pos.below());
        }

        return around;
    }

    /**
     * Collects up to {@code maxCount} connected blocks whose type is in {@code blockTypes}, searching
     * outward from {@code start} breadth-first so the ones nearest {@code start} (by step count) are
     * the ones kept.
     *
     * <p>{@code maxCount} is intended as a deliberate gameplay/performance ceiling, not an incidental
     * limit: the search stops as soon as the cap is reached, so callers never walk (or count) an
     * entire large structure. For example, sponj scales both a sponge's absorption reach and its
     * budget of blocks to clear with how many connected sponjes this returns, so an unbounded count
     * would let a large sponj wall schedule tens of thousands of block operations in a single tick;
     * capping the count caps that work.
     */
    public static List<BlockPos> getConnectedBlocksByType(Level world, BlockPos start, List<Block> blockTypes, int maxCount) {
        List<BlockPos> connected = new ArrayList<>();

        if (maxCount <= 0) {
            return connected;
        }

        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> frontier = new ArrayDeque<>();

        visited.add(start);
        frontier.add(start);

        if (blockTypes.contains(world.getBlockState(start).getBlock())) {
            connected.add(start);
        }

        while (!frontier.isEmpty() && connected.size() < maxCount) {
            BlockPos pos = frontier.poll();

            for (BlockPos neighbor : getAdjacentBlockPositions(pos, true)) {
                if (!visited.add(neighbor)) {
                    continue;
                }

                if (!blockTypes.contains(world.getBlockState(neighbor).getBlock())) {
                    continue;
                }

                connected.add(neighbor);

                if (connected.size() >= maxCount) {
                    break;
                }

                frontier.add(neighbor);
            }
        }

        return connected;
    }
}

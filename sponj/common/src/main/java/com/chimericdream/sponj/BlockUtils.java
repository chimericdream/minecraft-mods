package com.chimericdream.sponj;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

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
	 * <p>{@code maxCount} is a deliberate gameplay/performance ceiling, not an incidental limit. A
	 * sponj's absorption reach and its budget of blocks to clear both scale with how many connected
	 * sponjes this returns (see {@code SponjBlock#absorbWater}), so an unbounded count would let a
	 * large sponj wall schedule tens of thousands of liquid-clearing operations in a single tick and
	 * lag the server. Capping the count caps that work; stopping the search at the cap also means we
	 * never walk the whole structure just to count it. See {@code ModBlocks#MAX_CONNECTED_SPONJES}.
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

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
	 * Flood-fills outward from {@code start}, collecting every connected block whose type is in
	 * {@code blockTypes}. A matching block more than {@code maxDistance} away is still collected, but
	 * the search does not continue through it.
	 *
	 * <p>The fill is iterative on purpose: {@code maxDistance} is measured in blocks, so a large
	 * sponge structure can hold tens of thousands of connected blocks — deep enough to overflow the
	 * stack if this recursed per block.
	 */
	public static List<BlockPos> getConnectedBlocksByType(Level world, BlockPos start, List<Block> blockTypes, int maxDistance) {
		Set<BlockPos> checkedBlocks = new HashSet<>();
		List<BlockPos> blocks = new ArrayList<>();
		Deque<BlockPos> frontier = new ArrayDeque<>();

		checkedBlocks.add(start);
		frontier.add(start);

		if (blockTypes.contains(world.getBlockState(start).getBlock())) {
			blocks.add(start);
		}

		while (!frontier.isEmpty()) {
			BlockPos pos = frontier.poll();

			for (BlockPos blockPos : getAdjacentBlockPositions(pos, true)) {
				if (!checkedBlocks.add(blockPos)) {
					continue;
				}

				if (!blockTypes.contains(world.getBlockState(blockPos).getBlock())) {
					continue;
				}

				blocks.add(blockPos);

				if (isWithinDistance(start, blockPos, maxDistance)) {
					frontier.add(blockPos);
				}
			}
		}

		return blocks;
	}

	public static Boolean isWithinDistance(BlockPos start, BlockPos end, int maxDistance) {
		return isWithinDistance(start, end, (double) maxDistance);
	}

	public static Boolean isWithinDistance(BlockPos start, BlockPos end, double maxDistance) {
		return start.distSqr(end) <= maxDistance * maxDistance;
	}
}

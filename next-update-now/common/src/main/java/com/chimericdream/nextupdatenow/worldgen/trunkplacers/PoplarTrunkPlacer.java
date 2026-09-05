package com.chimericdream.nextupdatenow.worldgen.trunkplacers;

import com.chimericdream.nextupdatenow.worldgen.ModWorldgenTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

/**
 * Ported from the 26.3-snapshot-9 client jar (net.minecraft.world.level.levelgen.feature.trunkplacers.PoplarTrunkPlacer),
 * adapted to 26.2's TreeConfiguration-based TrunkPlacer API (26.3 renamed that parameter type to
 * "TreeFeature" and switched TreeGrower to a WeightedList-based tree-selection API; neither exists in
 * 26.2, so this file targets 26.2's TrunkPlacer/TreeConfiguration shape directly. The trunk-placement
 * algorithm itself is unchanged from vanilla.
 */
public class PoplarTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<PoplarTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
        instance -> trunkPlacerParts(instance)
            .and(
                instance.group(
                    IntProviders.codec(0, 8).fieldOf("trunk_height_above_branches").forGetter(t -> t.trunkHeightAboveBranches),
                    IntProviders.codec(1, 4).fieldOf("branch_amount").forGetter(t -> t.branchAmount)
                )
            )
            .apply(instance, PoplarTrunkPlacer::new)
    );

    private final IntProvider trunkHeightAboveBranches;
    private final IntProvider branchAmount;

    public PoplarTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider trunkHeightAboveBranches, IntProvider branchAmount) {
        super(baseHeight, heightRandA, heightRandB);
        this.trunkHeightAboveBranches = trunkHeightAboveBranches;
        this.branchAmount = branchAmount;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModWorldgenTypes.POPLAR_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(
        WorldGenLevel level,
        BiConsumer<BlockPos, BlockState> trunkSetter,
        RandomSource random,
        int treeHeight,
        BlockPos origin,
        TreeConfiguration tree
    ) {
        placeBelowTrunkBlock(level, trunkSetter, random, origin.below(), tree);
        int trunkHeightUpToFoliageBranches = treeHeight - this.trunkHeightAboveBranches.sample(random);

        for (int y = 0; y < treeHeight; y++) {
            this.placeLog(level, trunkSetter, random, origin.above(y), tree);
            List<Direction> directions = getShuffledBranchDirections(random);
            if (trunkHeightUpToFoliageBranches - 1 == y) {
                int branches = this.branchAmount.sample(random);

                for (int x = 0; x < branches; x++) {
                    Direction branchDirection = directions.get(x);
                    this.placeLog(level, trunkSetter, random, origin.above(y).relative(branchDirection, 1), tree, getSidewaysStateModifier(branchDirection));
                }
            }
        }

        return List.of(new FoliagePlacer.FoliageAttachment(origin.above(trunkHeightUpToFoliageBranches), 0, false));
    }

    private static Function<BlockState, BlockState> getSidewaysStateModifier(Direction branchDirection) {
        return state -> state.trySetValue(RotatedPillarBlock.AXIS, branchDirection.getAxis());
    }

    private static List<Direction> getShuffledBranchDirections(RandomSource random) {
        return Direction.allShuffled(random).stream().filter(direction -> !direction.getAxis().isVertical()).collect(Collectors.toList());
    }
}

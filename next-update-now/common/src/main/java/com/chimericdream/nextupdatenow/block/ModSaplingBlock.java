package com.chimericdream.nextupdatenow.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 26.2's TreeGrower is a final class with room for only one primary "tree" ConfiguredFeature pick
 * (the 26.3 snapshot's WeightedList-based tree selection doesn't exist here — see
 * PoplarTrunkPlacer's header comment). To still get a random variant on bonemeal, this block is
 * handed one TreeGrower per variant and, once a sapling matures, picks among them itself
 * (equal weight) before delegating to that TreeGrower's own growTree() to place the tree.
 */
public class ModSaplingBlock extends SaplingBlock {
    private final TreeGrower[] treeGrowers;

    public ModSaplingBlock(TreeGrower[] treeGrowers, Properties properties) {
        super(treeGrowers[0], properties);
        this.treeGrowers = treeGrowers;
    }

    @Override
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (treeGrowers.length == 1 || state.getValue(STAGE) == 0) {
            super.advanceTree(level, pos, state, random);
            return;
        }

        TreeGrower grower = treeGrowers[random.nextInt(treeGrowers.length)];
        grower.growTree(level, level.getChunkSource().getGenerator(), pos, state, random);
    }
}

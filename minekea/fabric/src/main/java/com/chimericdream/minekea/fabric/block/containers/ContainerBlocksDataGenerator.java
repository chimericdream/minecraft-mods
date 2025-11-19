package com.chimericdream.minekea.fabric.block.containers;

import com.chimericdream.minekea.block.containers.barrels.Barrels;
import com.chimericdream.minekea.block.containers.crates.Crates;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.util.BlockDataGeneratorGroup;

import java.util.ArrayList;
import java.util.List;

public class ContainerBlocksDataGenerator implements BlockDataGeneratorGroup {
    protected static final List<ChimericLibBlockDataGenerator> BLOCK_GENERATORS = new ArrayList<>();

    static {
        Barrels.BLOCKS.forEach(block -> BLOCK_GENERATORS.add(new BarrelBlockDataGenerator(block.get())));
        Crates.CRATES.values().forEach(block -> BLOCK_GENERATORS.add(new CrateBlockDataGenerator(block.get())));
        Crates.TRAPPED_CRATES.values().forEach(block -> BLOCK_GENERATORS.add(new TrappedCrateBlockDataGenerator(block.get())));

        BLOCK_GENERATORS.add(new BarrelBlockDataGenerator.OakBarrelDataGenerator());
        BLOCK_GENERATORS.add(new CrateBlockDataGenerator.SharedCrateDataGenerator());
//        BLOCK_GENERATORS.add(new GlassJarBlockDataGenerator());
    }

    @Override
    public List<ChimericLibBlockDataGenerator> getBlockGenerators() {
        return BLOCK_GENERATORS;
    }
}

package com.chimericdream.lib.fabric.blocks.family;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.blocks.family.BlockFamily;
import com.chimericdream.lib.blocks.family.BlockFamilyVariant;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;

/**
 * Wraps whichever variants a {@link BlockFamily} registered in the matching
 * {@link FabricBlockDataGenerator}, ready to fold into a mod's own datagen aggregator.
 */
public class BlockFamilyDataGenerators {
    public static List<FabricBlockDataGenerator> of(BlockFamily family) {
        List<FabricBlockDataGenerator> generators = new ArrayList<>();

        family.getBlock(BlockFamilyVariant.STAIRS).ifPresent(block -> {
            BlockConfig config = family.getConfig(BlockFamilyVariant.STAIRS).orElseThrow();
            generators.add(new StairsBlockDataGenerator((StairBlock) block.get(), config));
        });

        family.getBlock(BlockFamilyVariant.SLAB).ifPresent(block -> {
            BlockConfig config = family.getConfig(BlockFamilyVariant.SLAB).orElseThrow();
            generators.add(new SlabBlockDataGenerator((SlabBlock) block.get(), config));
        });

        family.getBlock(BlockFamilyVariant.WALL).ifPresent(block -> {
            BlockConfig config = family.getConfig(BlockFamilyVariant.WALL).orElseThrow();
            generators.add(new WallBlockDataGenerator((WallBlock) block.get(), config));
        });

        return generators;
    }
}

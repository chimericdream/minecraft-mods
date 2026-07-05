package com.chimericdream.minekea.fabric.block.building.general;

import com.chimericdream.minekea.block.building.BuildingBlocks;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Function;

public class CrackedBasaltBricksDataGenerator extends ChimericLibBlockDataGenerator {
    public static Block BLOCK = BuildingBlocks.CRACKED_BASALT_BRICKS.get();

    public Block getBlock() {
        return BLOCK;
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block, Block>> getBuilder) {
        getBuilder.apply(BlockTags.MINEABLE_WITH_PICKAXE)
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        generator.oreSmelting(List.of(BuildingBlocks.BASALT_BRICKS.get()), RecipeCategory.BUILDING_BLOCKS, BLOCK, 0.1f, 200, "minekea");
        generator.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, BLOCK, BuildingBlocks.BASALT_BRICKS.get(), 1);
        generator.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, BuildingBlocks.BASALT_BRICKS.get(), BLOCK, 1);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, "Cracked Basalt Bricks");
        translationBuilder.add(BLOCK.asItem(), "Cracked Basalt Bricks");
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.createTrivialCube(BLOCK);
    }
}

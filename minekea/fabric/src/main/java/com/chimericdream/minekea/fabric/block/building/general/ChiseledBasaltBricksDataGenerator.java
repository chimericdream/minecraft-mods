package com.chimericdream.minekea.fabric.block.building.general;

import com.chimericdream.minekea.block.building.BuildingBlocks;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Function;

public class ChiseledBasaltBricksDataGenerator extends ChimericLibBlockDataGenerator {
    public static Block BLOCK = BuildingBlocks.CHISELED_BASALT_BRICKS.get();

    public Block getBlock() {
        return BLOCK;
    }

    @Override
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        getBuilder.apply(BlockTags.PICKAXE_MINEABLE)
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 4)
            .pattern("##")
            .pattern("##")
            .input('#', BuildingBlocks.BASALT_BRICKS.get())
            .criterion(RecipeGenerator.hasItem(BuildingBlocks.BASALT_BRICKS.get()),
                generator.conditionsFromItem(BuildingBlocks.BASALT_BRICKS.get()))
            .offerTo(exporter);

        generator.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, BLOCK, BuildingBlocks.BASALT_BRICKS.get(), 1);
        generator.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, BuildingBlocks.BASALT_BRICKS.get(), BLOCK, 1);
        generator.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_BASALT, BLOCK, 1);
        generator.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, BLOCK, Blocks.SMOOTH_BASALT, 1);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, "Chiseled Basalt Bricks");
        translationBuilder.add(BLOCK.asItem(), "Chiseled Basalt Bricks");
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator, RegistryWrapper.WrapperLookup registryLookup) {
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(BLOCK);
    }
}

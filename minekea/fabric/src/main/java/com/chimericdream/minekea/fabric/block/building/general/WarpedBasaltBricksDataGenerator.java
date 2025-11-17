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

public class WarpedBasaltBricksDataGenerator extends ChimericLibBlockDataGenerator {
    public static Block BLOCK = BuildingBlocks.WARPED_BASALT_BRICKS.get();

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
        generator.createShapeless(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .input(BuildingBlocks.BASALT_BRICKS.get())
            .input(Blocks.TWISTING_VINES)
            .criterion(RecipeGenerator.hasItem(BuildingBlocks.BASALT_BRICKS.get()),
                generator.conditionsFromItem(BuildingBlocks.BASALT_BRICKS.get()))
            .criterion(RecipeGenerator.hasItem(Blocks.TWISTING_VINES),
                generator.conditionsFromItem(Blocks.TWISTING_VINES))
            .offerTo(exporter);

        generator.offerStonecuttingRecipe(RecipeCategory.BUILDING_BLOCKS, BuildingBlocks.BASALT_BRICKS.get(), BLOCK, 1);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, "Warped Basalt Bricks");
        translationBuilder.add(BLOCK.asItem(), "Warped Basalt Bricks");
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(BLOCK);
    }
}

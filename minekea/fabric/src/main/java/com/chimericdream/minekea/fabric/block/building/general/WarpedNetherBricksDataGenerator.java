package com.chimericdream.minekea.fabric.block.building.general;

import com.chimericdream.minekea.block.building.BuildingBlocks;
import com.chimericdream.minekea.crop.ModCrops;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Function;

public class WarpedNetherBricksDataGenerator implements ChimericLibBlockDataGenerator {
    public static Block BLOCK = BuildingBlocks.WARPED_NETHER_BRICKS.get();

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
    public void configureItemTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Item>, ProvidedTagBuilder<Item, Item>> getBuilder) {
    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern("AB")
            .pattern("BA")
            .input('A', ModCrops.WARPED_WART_ITEM.get())
            .input('B', Items.NETHER_BRICK)
            .criterion(RecipeGenerator.hasItem(ModCrops.WARPED_WART_ITEM.get()),
                generator.conditionsFromItem(ModCrops.WARPED_WART_ITEM.get()))
            .criterion(RecipeGenerator.hasItem(Items.NETHER_BRICK),
                generator.conditionsFromItem(Items.NETHER_BRICK))
            .offerTo(exporter);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, "Warped Nether Bricks");
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(BLOCK);
    }

    @Override
    public void configureItemModels(ItemModelGenerator itemModelGenerator) {
    }

    @Override
    public void generateTextures() {
    }
}

package com.chimericdream.minekea.fabric.util;

import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Function;

public interface BlockDataGeneratorGroup {
    List<ChimericLibBlockDataGenerator> getBlockGenerators();

    default void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider recipeGenerator) {
        getBlockGenerators().forEach(generator -> generator.configureRecipes(registryLookup, exporter, recipeGenerator));
    }

    default void configureBlockLootTables(BlockLootSubProvider lootTableGenerator, HolderLookup.Provider registryLookup) {
        getBlockGenerators().forEach(generator -> generator.configureBlockLootTables(lootTableGenerator, registryLookup));
    }

    default void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        getBlockGenerators().forEach(generator -> generator.configureBlockStateModels(blockStateModelGenerator));
    }

    default void configureItemModels(ItemModelGenerators itemModelGenerator) {
        getBlockGenerators().forEach(generator -> generator.configureItemModels(itemModelGenerator));
    }

    default void generateTextures() {
        getBlockGenerators().forEach(ChimericLibBlockDataGenerator::generateTextures);
    }

    default void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBlockGenerators().forEach(generator -> generator.configureBlockTags(registryLookup, getBuilder));
    }

    default void configureItemTags(HolderLookup.Provider registryLookup, Function<TagKey<Item>, TagAppender<Item>> getBuilder) {
        getBlockGenerators().forEach(generator -> generator.configureItemTags(registryLookup, getBuilder));
    }

    default void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        getBlockGenerators().forEach(generator -> generator.configureTranslations(registryLookup, translationBuilder));
    }
}

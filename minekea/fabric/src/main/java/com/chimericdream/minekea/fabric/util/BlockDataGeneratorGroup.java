package com.chimericdream.minekea.fabric.util;

import com.chimericdream.lib.blocks.BlockDataGenerator;
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
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.List;
import java.util.function.Function;

public interface BlockDataGeneratorGroup {
    List<ChimericLibBlockDataGenerator> getBlockGenerators();

    default void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator recipeGenerator) {
        getBlockGenerators().forEach(generator -> generator.configureRecipes(registryLookup, exporter, recipeGenerator));
    }

    default void configureBlockLootTables(BlockLootTableGenerator lootTableGenerator) {
        getBlockGenerators().forEach(generator -> generator.configureBlockLootTables(lootTableGenerator));
    }

    default void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        getBlockGenerators().forEach(generator -> generator.configureBlockStateModels(blockStateModelGenerator));
    }

    default void configureItemModels(ItemModelGenerator itemModelGenerator) {
        getBlockGenerators().forEach(generator -> generator.configureItemModels(itemModelGenerator));
    }

    default void generateTextures() {
        getBlockGenerators().forEach(BlockDataGenerator::generateTextures);
    }

    default void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        getBlockGenerators().forEach(generator -> generator.configureBlockTags(registryLookup, getBuilder));
    }

    default void configureItemTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Item>, ProvidedTagBuilder<Item, Item>> getBuilder) {
        getBlockGenerators().forEach(generator -> generator.configureItemTags(registryLookup, getBuilder));
    }

    default void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        getBlockGenerators().forEach(generator -> generator.configureTranslations(registryLookup, translationBuilder));
    }
}

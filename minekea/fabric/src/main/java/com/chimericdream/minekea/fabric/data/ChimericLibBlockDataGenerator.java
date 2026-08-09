package com.chimericdream.minekea.fabric.data;

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

import java.util.function.Function;

abstract public class ChimericLibBlockDataGenerator {
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
    }

    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
    }

    public void configureItemTags(HolderLookup.Provider registryLookup, Function<TagKey<Item>, TagAppender<Item>> getBuilder) {
    }

    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
    }

    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
    }

    public void configureItemModels(ItemModelGenerators itemModelGenerator) {
    }

    public void generateTextures() {
    }

    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
    }
}

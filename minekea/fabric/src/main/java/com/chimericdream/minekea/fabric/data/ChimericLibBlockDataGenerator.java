package com.chimericdream.minekea.fabric.data;

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

import java.util.function.Function;

abstract public class ChimericLibBlockDataGenerator {
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
    }

    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
    }

    public void configureItemTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Item>, ProvidedTagBuilder<Item, Item>> getBuilder) {
    }

    public void configureBlockLootTables(BlockLootTableGenerator generator) {
    }

    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    }

    public void configureItemModels(ItemModelGenerator itemModelGenerator) {
    }

    public void generateTextures() {
    }

    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
    }
}

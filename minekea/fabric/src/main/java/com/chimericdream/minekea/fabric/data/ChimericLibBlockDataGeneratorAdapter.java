package com.chimericdream.minekea.fabric.data;

import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
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

/**
 * Adapts a chimeric-lib {@link FabricBlockDataGenerator} onto minekea's own
 * {@link ChimericLibBlockDataGenerator} base class, so generators built for the shared
 * {@code BlockFamily} API can sit in the same {@code BLOCK_GENERATORS} lists as minekea's
 * hand-written ones.
 */
public class ChimericLibBlockDataGeneratorAdapter extends ChimericLibBlockDataGenerator {
    private final FabricBlockDataGenerator delegate;

    public ChimericLibBlockDataGeneratorAdapter(FabricBlockDataGenerator delegate) {
        this.delegate = delegate;
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        delegate.configureRecipes(registryLookup, exporter, generator);
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        delegate.configureBlockTags(registryLookup, getBuilder);
    }

    @Override
    public void configureItemTags(HolderLookup.Provider registryLookup, Function<TagKey<Item>, TagAppender<Item>> getBuilder) {
        delegate.configureItemTags(registryLookup, getBuilder);
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        delegate.configureBlockLootTables(generator, registryLookup);
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        delegate.configureBlockStateModels(blockStateModelGenerator);
    }

    @Override
    public void configureItemModels(ItemModelGenerators itemModelGenerator) {
        delegate.configureItemModels(itemModelGenerator);
    }

    @Override
    public void generateTextures() {
        delegate.generateTextures();
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        delegate.configureTranslations(registryLookup, translationBuilder);
    }
}

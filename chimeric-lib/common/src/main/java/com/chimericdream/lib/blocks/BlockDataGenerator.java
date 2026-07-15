package com.chimericdream.lib.blocks;

import java.util.function.Function;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface BlockDataGenerator {
    default void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
    }

    default void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
    }

    default void configureItemTags(HolderLookup.Provider registryLookup, Function<TagKey<Item>, TagAppender<Item>> getBuilder) {
    }

    default void configureBlockLootTables(BlockLootSubProvider generator) {
    }

    default void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
    }

    default void configureItemModels(ItemModelGenerators itemModelGenerator) {
    }

    default void generateTextures() {
    }
}

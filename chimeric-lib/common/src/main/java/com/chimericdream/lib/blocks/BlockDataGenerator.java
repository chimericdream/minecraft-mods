package com.chimericdream.lib.blocks;

import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.registry.RegistryWrapper;

public interface BlockDataGenerator {
    default void configureRecipes(RecipeExporter exporter) {
    }

    default void configureBlockLootTables(RegistryWrapper.WrapperLookup registryLookup, BlockLootTableGenerator generator) {
    }

    default void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    }

    default void configureItemModels(ItemModelGenerator itemModelGenerator) {
    }

    default void generateTextures() {
    }
}

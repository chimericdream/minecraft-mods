package com.chimericdream.lib.blocks;

import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.data.server.recipe.RecipeExporter;
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

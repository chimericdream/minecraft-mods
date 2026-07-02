package com.chimericdream.lib.items;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.data.recipes.RecipeOutput;

public interface ItemDataGenerator {
    default void configureRecipes(RecipeOutput exporter) {
    }

    default void configureItemModels(ItemModelGenerators itemModelGenerator) {
    }

    default void generateTextures() {
    }
}

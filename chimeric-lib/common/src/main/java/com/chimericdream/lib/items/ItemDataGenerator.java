package com.chimericdream.lib.items;

import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.data.recipe.RecipeExporter;

public interface ItemDataGenerator {
    default void configureRecipes(RecipeExporter exporter) {
    }

    default void configureItemModels(ItemModelGenerator itemModelGenerator) {
    }

    default void generateTextures() {
    }
}

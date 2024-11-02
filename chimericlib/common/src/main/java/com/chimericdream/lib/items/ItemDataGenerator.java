package com.chimericdream.lib.items;

import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.server.recipe.RecipeExporter;

public interface ItemDataGenerator {
    default void configureRecipes(RecipeExporter exporter) {
    }

    default void configureItemModels(ItemModelGenerator itemModelGenerator) {
    }

    default void generateTextures() {
    }
}

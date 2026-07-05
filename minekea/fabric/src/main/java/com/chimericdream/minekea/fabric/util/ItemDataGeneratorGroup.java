package com.chimericdream.minekea.fabric.util;

import com.chimericdream.minekea.fabric.data.ChimericLibItemDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.function.Function;

public interface ItemDataGeneratorGroup {
    List<ChimericLibItemDataGenerator> getItemGenerators();

    default void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider recipeGenerator) {
        getItemGenerators().forEach(generator -> generator.configureRecipes(registryLookup, exporter, recipeGenerator));
    }

    default void configureItemModels(ItemModelGenerators itemModelGenerator) {
        getItemGenerators().forEach(generator -> generator.configureItemModels(itemModelGenerator));
    }

    default void generateTextures() {
        getItemGenerators().forEach(ChimericLibItemDataGenerator::generateTextures);
    }

    default void configureItemTags(HolderLookup.Provider registryLookup, Function<TagKey<Item>, TagAppender<Item, Item>> getBuilder) {
        getItemGenerators().forEach(generator -> generator.configureItemTags(registryLookup, getBuilder));
    }

    default void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        getItemGenerators().forEach(generator -> generator.configureTranslations(registryLookup, translationBuilder));
    }
}

package com.chimericdream.minekea.fabric.util;

import com.chimericdream.minekea.fabric.data.ChimericLibItemDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.List;
import java.util.function.Function;

public interface ItemDataGeneratorGroup {
    List<ChimericLibItemDataGenerator> getItemGenerators();

    default void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator recipeGenerator) {
        getItemGenerators().forEach(generator -> generator.configureRecipes(registryLookup, exporter, recipeGenerator));
    }

    default void configureItemModels(ItemModelGenerator itemModelGenerator) {
        getItemGenerators().forEach(generator -> generator.configureItemModels(itemModelGenerator));
    }

    default void generateTextures() {
        getItemGenerators().forEach(ChimericLibItemDataGenerator::generateTextures);
    }

    default void configureItemTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Item>, ProvidedTagBuilder<Item, Item>> getBuilder) {
        getItemGenerators().forEach(generator -> generator.configureItemTags(registryLookup, getBuilder));
    }

    default void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        getItemGenerators().forEach(generator -> generator.configureTranslations(registryLookup, translationBuilder));
    }
}

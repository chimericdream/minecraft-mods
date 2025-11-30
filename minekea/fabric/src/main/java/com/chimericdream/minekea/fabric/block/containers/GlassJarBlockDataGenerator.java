package com.chimericdream.minekea.fabric.block.containers;

import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.tag.MinekeaItemTags;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Function;

import static com.chimericdream.minekea.block.containers.ContainerBlocks.GLASS_JAR;
import static com.chimericdream.minekea.block.containers.GlassJarBlock.ALLOWED_ITEMS;

public class GlassJarBlockDataGenerator extends ChimericLibBlockDataGenerator {
    @Override
    public void configureItemTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Item>, ProvidedTagBuilder<Item, Item>> getBuilder) {
        ProvidedTagBuilder<Item, Item> builder = getBuilder.apply(MinekeaItemTags.GLASS_JAR_STORABLE).setReplace(false);

        ALLOWED_ITEMS.forEach(builder::add);
    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        generator.createShaped(RecipeCategory.DECORATIONS, GLASS_JAR.get(), 3)
            .pattern(" L ")
            .pattern("G G")
            .pattern("GGG")
            .input('L', ItemTags.PLANKS)
            .input('G', Items.GLASS_PANE)
            .criterion(RecipeGenerator.hasItem(Items.GLASS_PANE),
                generator.conditionsFromItem(Items.GLASS_PANE))
            .offerTo(exporter);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(GLASS_JAR.get(), "Glass Jar");
        translationBuilder.add(GLASS_JAR.get().asItem(), "Glass Jar");
    }
}

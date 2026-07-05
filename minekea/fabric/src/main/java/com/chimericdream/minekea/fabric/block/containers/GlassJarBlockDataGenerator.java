package com.chimericdream.minekea.fabric.block.containers;

import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.tag.MinekeaItemTags;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Function;

import static com.chimericdream.minekea.block.containers.ContainerBlocks.GLASS_JAR;
import static com.chimericdream.minekea.block.containers.GlassJarBlock.ALLOWED_ITEMS;

public class GlassJarBlockDataGenerator extends ChimericLibBlockDataGenerator {
    @Override
    public void configureItemTags(HolderLookup.Provider registryLookup, Function<TagKey<Item>, TagAppender<Item, Item>> getBuilder) {
        TagAppender<Item, Item> builder = getBuilder.apply(MinekeaItemTags.GLASS_JAR_STORABLE).setReplace(false);

        ALLOWED_ITEMS.forEach(builder::add);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        generator.shaped(RecipeCategory.DECORATIONS, GLASS_JAR.get(), 3)
            .pattern(" L ")
            .pattern("G G")
            .pattern("GGG")
            .define('L', ItemTags.PLANKS)
            .define('G', Items.GLASS_PANE)
            .unlockedBy(RecipeProvider.getHasName(Items.GLASS_PANE),
                generator.has(Items.GLASS_PANE))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(GLASS_JAR.get(), "Glass Jar");
        translationBuilder.add(GLASS_JAR.get().asItem(), "Glass Jar");
    }
}

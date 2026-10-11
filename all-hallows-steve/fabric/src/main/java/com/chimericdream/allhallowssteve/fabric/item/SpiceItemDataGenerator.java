package com.chimericdream.allhallowssteve.fabric.item;

import com.chimericdream.allhallowssteve.item.ModItems;
import com.chimericdream.allhallowssteve.tags.AHSTags;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.function.Function;

/**
 * Cinnamon, cloves, and nutmeg. Cinnamon drops from stripping logs (see {@code Spices}), cloves and
 * nutmeg are smelted from existing items, and nutmeg is also a farmer trade ({@code ModVillagerTrades}).
 */
public class SpiceItemDataGenerator {
    public static final List<Item> CLOVE_FLOWERS = List.of(
        Items.DANDELION,
        Items.POPPY,
        Items.BLUE_ORCHID,
        Items.ALLIUM,
        Items.AZURE_BLUET,
        Items.RED_TULIP,
        Items.ORANGE_TULIP,
        Items.WHITE_TULIP,
        Items.PINK_TULIP,
        Items.OXEYE_DAISY,
        Items.CORNFLOWER,
        Items.LILY_OF_THE_VALLEY
    );

    public static final List<Block> CINNAMON_STRIPPABLES = List.of(
        Blocks.CHERRY_LOG,
        Blocks.CHERRY_WOOD,
        Blocks.MANGROVE_LOG,
        Blocks.MANGROVE_WOOD,
        Blocks.JUNGLE_LOG,
        Blocks.JUNGLE_WOOD
    );

    private static final int SMELT_TIME = 200;
    private static final int SMOKE_TIME = 100;
    private static final float SMELT_XP = 0.1f;

    public static void configureItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(ModItems.CINNAMON.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.CLOVES.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.NUTMEG.get(), ModelTemplates.FLAT_ITEM);
    }

    public static void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Function<String, String> smelting = name -> name + "_from_smelting";
        Function<String, String> smoking = name -> name + "_from_smoking";

        Ingredient flowers = Ingredient.of(registryLookup.lookupOrThrow(Registries.ITEM).getOrThrow(AHSTags.CLOVE_FLOWERS));
        Ingredient cocoa = Ingredient.of(Items.COCOA_BEANS);

        SimpleCookingRecipeBuilder.smelting(flowers, RecipeCategory.FOOD, CookingBookCategory.FOOD, ModItems.CLOVES.get(), SMELT_XP, SMELT_TIME)
            .unlockedBy("has_clove_flower", generator.has(AHSTags.CLOVE_FLOWERS))
            .save(exporter, smelting.apply("cloves"));
        SimpleCookingRecipeBuilder.smoking(flowers, RecipeCategory.FOOD, ModItems.CLOVES.get(), SMELT_XP, SMOKE_TIME)
            .unlockedBy("has_clove_flower", generator.has(AHSTags.CLOVE_FLOWERS))
            .save(exporter, smoking.apply("cloves"));

        SimpleCookingRecipeBuilder.smelting(cocoa, RecipeCategory.FOOD, CookingBookCategory.FOOD, ModItems.NUTMEG.get(), SMELT_XP, SMELT_TIME)
            .unlockedBy(RecipeProvider.getHasName(Items.COCOA_BEANS), generator.has(Items.COCOA_BEANS))
            .save(exporter, smelting.apply("nutmeg"));
        SimpleCookingRecipeBuilder.smoking(cocoa, RecipeCategory.FOOD, ModItems.NUTMEG.get(), SMELT_XP, SMOKE_TIME)
            .unlockedBy(RecipeProvider.getHasName(Items.COCOA_BEANS), generator.has(Items.COCOA_BEANS))
            .save(exporter, smoking.apply("nutmeg"));
    }

    public static void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.CINNAMON.get(), "Cinnamon");
        translationBuilder.add(ModItems.CLOVES.get(), "Cloves");
        translationBuilder.add(ModItems.NUTMEG.get(), "Nutmeg");
    }
}

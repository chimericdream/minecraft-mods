package com.chimericdream.allhallowssteve.fabric.item;

import com.chimericdream.allhallowssteve.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * Soul cakes (plain, one per spice, and one for the mulling spices sachet), pumpkin soup, roasted pumpkin
 * seeds, apple pie, and spiced pumpkin pie. See {@code HarvestFoods} for the food values and effects.
 */
public class HarvestFoodItemDataGenerator {
    private static final int SOUL_CAKES_PER_BATCH = 2;
    private static final int SMELT_TIME = 200;
    private static final int SMOKE_TIME = 100;
    private static final int CAMPFIRE_TIME = 600;
    private static final float COOK_XP = 0.1f;

    private static List<Item> items() {
        return List.of(
            ModItems.SOUL_CAKE.get(),
            ModItems.SOUL_CAKE_CLOVES.get(),
            ModItems.SOUL_CAKE_CINNAMON.get(),
            ModItems.SOUL_CAKE_NUTMEG.get(),
            ModItems.SOUL_CAKE_SPICED.get(),
            ModItems.PUMPKIN_SOUP.get(),
            ModItems.ROASTED_PUMPKIN_SEEDS.get(),
            ModItems.APPLE_PIE.get(),
            ModItems.SPICED_PUMPKIN_PIE.get()
        );
    }

    public static void configureItemModels(ItemModelGenerators itemModelGenerator) {
        for (Item item : items()) {
            itemModelGenerator.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        }
    }

    public static void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        HolderLookup.RegistryLookup<Item> itemLookup = registryLookup.lookupOrThrow(Registries.ITEM);

        soulCake(itemLookup, exporter, generator, ModItems.SOUL_CAKE.get(), null);
        soulCake(itemLookup, exporter, generator, ModItems.SOUL_CAKE_CLOVES.get(), ModItems.CLOVES.get());
        soulCake(itemLookup, exporter, generator, ModItems.SOUL_CAKE_CINNAMON.get(), ModItems.CINNAMON.get());
        soulCake(itemLookup, exporter, generator, ModItems.SOUL_CAKE_NUTMEG.get(), ModItems.NUTMEG.get());
        soulCake(itemLookup, exporter, generator, ModItems.SOUL_CAKE_SPICED.get(), ModItems.MULLING_SPICES.get());

        ShapelessRecipeBuilder.shapeless(itemLookup, RecipeCategory.FOOD, ModItems.PUMPKIN_SOUP.get())
            .requires(Items.PUMPKIN)
            .requires(Items.BOWL)
            .requires(Ingredient.of(Items.CARROT, Items.POTATO))
            .unlockedBy(RecipeProvider.getHasName(Items.PUMPKIN), generator.has(Items.PUMPKIN))
            .save(exporter);

        Ingredient seeds = Ingredient.of(Items.PUMPKIN_SEEDS);
        String seedsHas = RecipeProvider.getHasName(Items.PUMPKIN_SEEDS);
        SimpleCookingRecipeBuilder.smelting(seeds, RecipeCategory.FOOD, CookingBookCategory.FOOD, ModItems.ROASTED_PUMPKIN_SEEDS.get(), COOK_XP, SMELT_TIME)
            .unlockedBy(seedsHas, generator.has(Items.PUMPKIN_SEEDS))
            .save(exporter, "roasted_pumpkin_seeds_from_smelting");
        SimpleCookingRecipeBuilder.smoking(seeds, RecipeCategory.FOOD, ModItems.ROASTED_PUMPKIN_SEEDS.get(), COOK_XP, SMOKE_TIME)
            .unlockedBy(seedsHas, generator.has(Items.PUMPKIN_SEEDS))
            .save(exporter, "roasted_pumpkin_seeds_from_smoking");
        SimpleCookingRecipeBuilder.campfireCooking(seeds, RecipeCategory.FOOD, ModItems.ROASTED_PUMPKIN_SEEDS.get(), COOK_XP, CAMPFIRE_TIME)
            .unlockedBy(seedsHas, generator.has(Items.PUMPKIN_SEEDS))
            .save(exporter, "roasted_pumpkin_seeds_from_campfire_cooking");

        ShapedRecipeBuilder.shaped(itemLookup, RecipeCategory.FOOD, ModItems.APPLE_PIE.get())
            .pattern("ASA")
            .pattern("WWW")
            .define('A', Items.APPLE)
            .define('S', Items.SUGAR)
            .define('W', Items.WHEAT)
            .unlockedBy(RecipeProvider.getHasName(Items.APPLE), generator.has(Items.APPLE))
            .save(exporter);

        ShapelessRecipeBuilder.shapeless(itemLookup, RecipeCategory.FOOD, ModItems.SPICED_PUMPKIN_PIE.get())
            .requires(Items.PUMPKIN_PIE)
            .requires(ModItems.MULLING_SPICES.get())
            .unlockedBy(RecipeProvider.getHasName(Items.PUMPKIN_PIE), generator.has(Items.PUMPKIN_PIE))
            .unlockedBy("has_mulling_spices", generator.has(ModItems.MULLING_SPICES.get()))
            .save(exporter);
    }

    /** Wheat, sugar, and an egg make a batch; at most one spice (or the sachet) picks the variant. */
    private static void soulCake(HolderLookup.RegistryLookup<Item> itemLookup, RecipeOutput exporter, RecipeProvider generator, Item result, Item spice) {
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(itemLookup, RecipeCategory.FOOD, result, SOUL_CAKES_PER_BATCH)
            .requires(Items.WHEAT)
            .requires(Items.SUGAR)
            .requires(Items.EGG)
            .unlockedBy(RecipeProvider.getHasName(Items.EGG), generator.has(Items.EGG));

        if (spice != null) {
            builder.requires(spice).unlockedBy(RecipeProvider.getHasName(spice), generator.has(spice));
        }

        builder.save(exporter);
    }

    public static void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.SOUL_CAKE.get(), "Soul Cake");
        translationBuilder.add(ModItems.SOUL_CAKE_CLOVES.get(), "Clove Soul Cake");
        translationBuilder.add(ModItems.SOUL_CAKE_CINNAMON.get(), "Cinnamon Soul Cake");
        translationBuilder.add(ModItems.SOUL_CAKE_NUTMEG.get(), "Nutmeg Soul Cake");
        translationBuilder.add(ModItems.SOUL_CAKE_SPICED.get(), "Spiced Soul Cake");
        translationBuilder.add(ModItems.PUMPKIN_SOUP.get(), "Pumpkin Soup");
        translationBuilder.add(ModItems.ROASTED_PUMPKIN_SEEDS.get(), "Roasted Pumpkin Seeds");
        translationBuilder.add(ModItems.APPLE_PIE.get(), "Apple Pie");
        translationBuilder.add(ModItems.SPICED_PUMPKIN_PIE.get(), "Spiced Pumpkin Pie");
    }
}

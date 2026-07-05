package com.chimericdream.minekea.fabric.item.ingredients;

import com.chimericdream.lib.colors.ColorHelpers;
import com.chimericdream.minekea.fabric.data.ChimericLibItemDataGenerator;
import com.chimericdream.minekea.item.ingredients.WaxItem;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class WaxItemDataGenerator extends ChimericLibItemDataGenerator {
    public WaxItem ITEM;

    public WaxItemDataGenerator(Item item) {
        ITEM = (WaxItem) item;
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(ITEM.ingredient),
                RecipeCategory.MISC,
                ITEM,
                0.1f,
                200
            )
            .unlockedBy(RecipeProvider.getHasName(ITEM.ingredient),
                generator.has(ITEM.ingredient))
            .save(exporter);

        if (ITEM.color.equals("plain")) {
            SimpleCookingRecipeBuilder.smelting(
                    Ingredient.of(Items.HONEYCOMB),
                    RecipeCategory.MISC,
                    ITEM,
                    0.1f,
                    200
                )
                .unlockedBy(RecipeProvider.getHasName(Items.HONEYCOMB),
                    generator.has(Items.HONEYCOMB))
                .save(exporter, ITEM.ITEM_ID.withSuffix("_from_honeycomb").toString());
        }
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        if (ITEM.color.equals("plain")) {
            translationBuilder.add(ITEM, "Wax");

            return;
        }

        translationBuilder.add(ITEM, String.format("%s Wax", ColorHelpers.getName(ITEM.color)));
    }

    @Override
    public void configureItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.createFlatItemModel(ITEM, ModelTemplates.FLAT_ITEM);
    }
}

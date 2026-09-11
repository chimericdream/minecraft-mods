package com.chimericdream.allhallowssteve.fabric.item;

import com.chimericdream.allhallowssteve.item.ModItems;
import com.chimericdream.allhallowssteve.item.PumpkinStencilItem;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * Every carving stencil shares the same flat item model and the same placeholder "craft from paper"
 * recipe - these recipes are expected to be replaced once the carving station's stencil-consuming
 * behavior is designed.
 */
public class PumpkinStencilItemDataGenerator {
    public static void configureItemModels(ItemModelGenerators itemModelGenerator) {
        for (RegistrySupplier<Item> item : ModItems.PUMPKIN_STENCIL_ITEMS) {
            itemModelGenerator.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
        }
    }

    public static void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        HolderGetter<Item> itemLookup = registryLookup.lookupOrThrow(Registries.ITEM);

        for (RegistrySupplier<Item> item : ModItems.PUMPKIN_STENCIL_ITEMS) {
            ShapelessRecipeBuilder.shapeless(itemLookup, RecipeCategory.MISC, item.get())
                .requires(Items.PAPER)
                .unlockedBy(RecipeProvider.getHasName(Items.PAPER), generator.has(Items.PAPER))
                .save(exporter);
        }
    }

    public static void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        for (RegistrySupplier<Item> item : ModItems.PUMPKIN_STENCIL_ITEMS) {
            PumpkinStencilItem stencilItem = (PumpkinStencilItem) item.get();

            translationBuilder.add(stencilItem, stencilItem.stencilName + " Carving Stencil");
        }
    }
}

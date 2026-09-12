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

import java.util.Map;

/**
 * Every carving stencil shares the same flat item model. Recipe-wise, stencils fall into three
 * groups: the blank stencil (crafted from paper + a pumpkin seed), the craftable stencils (a blank
 * stencil plus a thematic ingredient, per {@link #CRAFTABLE_STENCIL_INGREDIENTS}), and the loot-only
 * stencils (no recipe at all - see {@code AHSLootTableModifier} for where those come from instead).
 */
public class PumpkinStencilItemDataGenerator {
    /** Craftable stencils: 1 blank stencil + this ingredient. Stencils not listed here are loot-only. */
    private static final Map<String, Item> CRAFTABLE_STENCIL_INGREDIENTS = Map.ofEntries(
        Map.entry("crafter", Items.CRAFTING_TABLE),
        Map.entry("creaking", Items.RESIN_CLUMP),
        Map.entry("creaking_heart", Items.CREAKING_HEART),
        Map.entry("creeper", Items.CREEPER_HEAD),
        Map.entry("dispenser", Items.BOW),
        Map.entry("dropper", Items.COBBLESTONE),
        Map.entry("jack_o_lantern", Items.JACK_O_LANTERN),
        Map.entry("lodestone", Items.IRON_INGOT),
        Map.entry("observer", Items.REDSTONE),
        Map.entry("wither_rose", Items.WITHER_ROSE)
    );

    public static void configureItemModels(ItemModelGenerators itemModelGenerator) {
        for (RegistrySupplier<Item> item : ModItems.PUMPKIN_STENCIL_ITEMS) {
            itemModelGenerator.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
        }
    }

    public static void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        HolderGetter<Item> itemLookup = registryLookup.lookupOrThrow(Registries.ITEM);
        Item blankStencil = ModItems.BLANK_STENCIL.get();

        ShapelessRecipeBuilder.shapeless(itemLookup, RecipeCategory.MISC, blankStencil)
            .requires(Items.PAPER)
            .requires(Items.PUMPKIN_SEEDS)
            .unlockedBy(RecipeProvider.getHasName(Items.PAPER), generator.has(Items.PAPER))
            .save(exporter);

        for (RegistrySupplier<Item> item : ModItems.PUMPKIN_STENCIL_ITEMS) {
            PumpkinStencilItem stencilItem = (PumpkinStencilItem) item.get();
            Item ingredient = CRAFTABLE_STENCIL_INGREDIENTS.get(stencilItem.stencil);

            if (ingredient == null) {
                continue;
            }

            ShapelessRecipeBuilder.shapeless(itemLookup, RecipeCategory.MISC, item.get())
                .requires(blankStencil)
                .requires(ingredient)
                .unlockedBy(RecipeProvider.getHasName(blankStencil), generator.has(blankStencil))
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

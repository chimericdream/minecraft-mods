package com.chimericdream.minekea.fabric.item.tools;

import com.chimericdream.minekea.fabric.data.ChimericLibItemDataGenerator;
import com.chimericdream.minekea.item.Tools;
import com.chimericdream.minekea.item.tools.HammerItem;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class HammerItemDataGenerator extends ChimericLibItemDataGenerator {
    private final HammerItem ITEM;

    public HammerItemDataGenerator(Item item) {
        ITEM = (HammerItem) item;
    }

//    @Override
//    public void register() {
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
//            .register(itemGroup -> itemGroup.add(ITEM));
//    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        if (ITEM.itemIngredient == null) {
            generator.shaped(RecipeCategory.TOOLS, ITEM, 1)
                    .pattern("ISI")
                    .pattern(" S ")
                    .pattern(" S ")
                    .define('I', ITEM.itemIngredientTag)
                    .define('S', Items.STICK)
                    .unlockedBy("has_item_from_tag",
                        generator.has(ITEM.itemIngredientTag))
                    .unlockedBy(RecipeProvider.getHasName(Items.STICK),
                        generator.has(Items.STICK))
                    .save(exporter);

            return;
        }

        generator.shaped(RecipeCategory.TOOLS, ITEM, 1)
                .pattern("ISI")
                .pattern(" S ")
                .pattern(" S ")
                .define('I', ITEM.itemIngredient)
                .define('S', Items.STICK)
                .unlockedBy(RecipeProvider.getHasName(ITEM.itemIngredient),
                        generator.has(ITEM.itemIngredient))
                .unlockedBy(RecipeProvider.getHasName(Items.STICK),
                        generator.has(Items.STICK))
                .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(ITEM, String.format("%s Hammer", ITEM.materialName));
    }

    public static class NetheriteUpgrade extends ChimericLibItemDataGenerator {
        @Override
        public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
            SmithingTransformRecipeBuilder.smithing(
                            Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                            Ingredient.of(Tools.DIAMOND_HAMMER_ITEM.get()),
                            Ingredient.of(Items.NETHERITE_INGOT),
                            RecipeCategory.TOOLS,
                            Tools.NETHERITE_HAMMER_ITEM.get()
                    )
//                    .unlockedBy(RecipeProvider.getHasName(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
//                            generator.has(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE))
//                    .unlockedBy(RecipeProvider.getHasName(Tools.DIAMOND_HAMMER_ITEM.get()),
//                            generator.has(Tools.DIAMOND_HAMMER_ITEM.get()))
//                    .unlockedBy(RecipeProvider.getHasName(Items.NETHERITE_INGOT),
//                            generator.has(Items.NETHERITE_INGOT))
                    .save(exporter, ((HammerItem) Tools.NETHERITE_HAMMER_ITEM.get()).ITEM_ID.withSuffix("_upgrade_from_diamond").toString());
        }
    }
}

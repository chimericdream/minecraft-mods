package com.chimericdream.minekea.fabric.block.building.storage;

import com.chimericdream.minekea.block.building.storage.BlueEggCrateBlock;
import com.chimericdream.minekea.block.building.storage.StorageBlocks;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

public class BlueEggCrateBlockDataGenerator extends EggCrateBlockDataGenerator {
    protected final BlueEggCrateBlock BLOCK;

    public BlueEggCrateBlockDataGenerator() {
        BLOCK = (BlueEggCrateBlock) StorageBlocks.BLUE_EGG_CRATE_BLOCK.get();
    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .input('#', Items.BLUE_EGG)
            .criterion(RecipeGenerator.hasItem(Items.BLUE_EGG),
                generator.conditionsFromItem(Items.BLUE_EGG))
            .offerTo(exporter);

        generator.createShapeless(RecipeCategory.BUILDING_BLOCKS, Items.BLUE_EGG, 9)
            .input(BLOCK)
            .criterion(RecipeGenerator.hasItem(BLOCK),
                generator.conditionsFromItem(BLOCK))
            .offerTo(exporter);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, "Blue Egg Crate");
        translationBuilder.add(BLOCK.asItem(), "Blue Egg Crate");
    }
}

package com.chimericdream.minekea.fabric.block.building.storage;

import com.chimericdream.minekea.block.building.storage.BrownEggCrateBlock;
import com.chimericdream.minekea.block.building.storage.StorageBlocks;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

public class BrownEggCrateBlockDataGenerator extends EggCrateBlockDataGenerator {
    protected final BrownEggCrateBlock BLOCK;

    public BrownEggCrateBlockDataGenerator() {
        BLOCK = (BrownEggCrateBlock) StorageBlocks.BROWN_EGG_CRATE_BLOCK.get();
    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .input('#', Items.BROWN_EGG)
            .criterion(RecipeGenerator.hasItem(Items.BROWN_EGG),
                generator.conditionsFromItem(Items.BROWN_EGG))
            .offerTo(exporter);

        generator.createShapeless(RecipeCategory.BUILDING_BLOCKS, Items.BROWN_EGG, 9)
            .input(BLOCK)
            .criterion(RecipeGenerator.hasItem(BLOCK),
                generator.conditionsFromItem(BLOCK))
            .offerTo(exporter);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, "Brown Egg Crate");
        translationBuilder.add(BLOCK.asItem(), "Brown Egg Crate");
    }
}

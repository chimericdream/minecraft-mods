package com.chimericdream.minekea.fabric.block.building.storage;

import com.chimericdream.minekea.block.building.storage.BlueEggCrateBlock;
import com.chimericdream.minekea.block.building.storage.StorageBlocks;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

public class BlueEggCrateBlockDataGenerator extends EggCrateBlockDataGenerator {
    protected final BlueEggCrateBlock BLOCK;

    public BlueEggCrateBlockDataGenerator() {
        BLOCK = (BlueEggCrateBlock) StorageBlocks.BLUE_EGG_CRATE_BLOCK.get();
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .define('#', Items.BLUE_EGG)
            .unlockedBy(RecipeProvider.getHasName(Items.BLUE_EGG),
                generator.has(Items.BLUE_EGG))
            .save(exporter);

        generator.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.BLUE_EGG, 9)
            .requires(BLOCK)
            .unlockedBy(RecipeProvider.getHasName(BLOCK),
                generator.has(BLOCK))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, "Blue Egg Crate");
        translationBuilder.add(BLOCK.asItem(), "Blue Egg Crate");
    }
}

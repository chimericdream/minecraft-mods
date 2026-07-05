package com.chimericdream.minekea.fabric.block.building.storage;

import com.chimericdream.minekea.block.building.storage.BrownEggCrateBlock;
import com.chimericdream.minekea.block.building.storage.StorageBlocks;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

public class BrownEggCrateBlockDataGenerator extends EggCrateBlockDataGenerator {
    protected final BrownEggCrateBlock BLOCK;

    public BrownEggCrateBlockDataGenerator() {
        BLOCK = (BrownEggCrateBlock) StorageBlocks.BROWN_EGG_CRATE_BLOCK.get();
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .define('#', Items.BROWN_EGG)
            .unlockedBy(RecipeProvider.getHasName(Items.BROWN_EGG),
                generator.has(Items.BROWN_EGG))
            .save(exporter);

        generator.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.BROWN_EGG, 9)
            .requires(BLOCK)
            .unlockedBy(RecipeProvider.getHasName(BLOCK),
                generator.has(BLOCK))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, "Brown Egg Crate");
        translationBuilder.add(BLOCK.asItem(), "Brown Egg Crate");
    }
}

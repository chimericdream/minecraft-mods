package com.chimericdream.minekea.fabric.crop;

import com.chimericdream.minekea.crop.ModCrops;
import com.chimericdream.minekea.fabric.data.ChimericLibItemDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;

public class WarpedWartItemDataGenerator extends ChimericLibItemDataGenerator {
    public static final Item ITEM = ModCrops.WARPED_WART_ITEM.get();

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        generator.shapeless(RecipeCategory.BUILDING_BLOCKS, ITEM, 9)
            .requires(Blocks.WARPED_WART_BLOCK)
            .unlockedBy(RecipeProvider.getHasName(Blocks.WARPED_WART_BLOCK),
                generator.has(Blocks.WARPED_WART_BLOCK))
            .save(exporter);

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.WARPED_WART_BLOCK, 1)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .define('#', ITEM)
            .unlockedBy(RecipeProvider.getHasName(ITEM),
                generator.has(ITEM))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(ITEM, "Warped Wart");
    }
}

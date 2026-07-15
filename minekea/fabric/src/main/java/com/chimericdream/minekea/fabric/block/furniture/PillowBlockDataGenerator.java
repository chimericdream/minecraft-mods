package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.colors.ColorHelpers;
import com.chimericdream.lib.tags.CommonBlockTags;
import com.chimericdream.minekea.block.furniture.pillows.PillowBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.tag.MinekeaBlockTags;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class PillowBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public PillowBlock BLOCK;

    public PillowBlockDataGenerator(Block block) {
        BLOCK = (PillowBlock) block;
    }

    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBuilder.apply(MinekeaBlockTags.PILLOWS)
            .setReplace(false)
            .add(BLOCK.builtInRegistryHolder().key());

        getBuilder.apply(CommonBlockTags.SHEARS_MINEABLE)
            .setReplace(false)
            .add(BLOCK.builtInRegistryHolder().key());
    }

    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block wool = ColorHelpers.getWool(BLOCK.color);

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern("##")
            .pattern("##")
            .define('#', wool)
            .unlockedBy(RecipeProvider.getHasName(wool),
                generator.has(wool))
            .save(exporter);

        generator.shapeless(RecipeCategory.BUILDING_BLOCKS, wool, 4)
            .requires(BLOCK)
            .unlockedBy(RecipeProvider.getHasName(BLOCK),
                generator.has(BLOCK))
            .save(exporter);
    }

    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Pillow", ColorHelpers.getName(BLOCK.color)));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Pillow", ColorHelpers.getName(BLOCK.color)));
    }

    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.createTrivialCube(BLOCK);
    }
}

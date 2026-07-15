package com.chimericdream.minekea.fabric.block.building;

import com.chimericdream.lib.colors.ColorHelpers;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.block.building.dyed.DyedBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Function;

public class DyedBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public DyedBlock BLOCK;

    public DyedBlockDataGenerator(Block block) {
        BLOCK = (DyedBlock) block;
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.PICKAXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK.builtInRegistryHolder().key());
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block parentBlock = BLOCK.config.getIngredient();
        Item dye = ColorHelpers.getDye(BLOCK.color);

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 8)
            .pattern("###")
            .pattern("#D#")
            .pattern("###")
            .define('#', parentBlock)
            .define('D', dye)
            .unlockedBy(RecipeProvider.getHasName(parentBlock),
                generator.has(parentBlock))
            .unlockedBy(RecipeProvider.getHasName(dye),
                generator.has(dye))
            .save(exporter);
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Dyed %s", ColorHelpers.getName(BLOCK.color), BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Dyed %s", ColorHelpers.getName(BLOCK.color), BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.createTrivialCube(BLOCK);
    }
}

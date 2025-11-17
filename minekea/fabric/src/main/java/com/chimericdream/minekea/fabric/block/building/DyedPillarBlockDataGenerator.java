package com.chimericdream.minekea.fabric.block.building;

import com.chimericdream.lib.colors.ColorHelpers;
import com.chimericdream.lib.resource.TextureUtils;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.block.building.dyed.DyedPillarBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.Models;
import net.minecraft.client.data.TextureKey;
import net.minecraft.client.data.TextureMap;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Function;

public class DyedPillarBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public DyedPillarBlock BLOCK;

    public DyedPillarBlockDataGenerator(Block block) {
        BLOCK = (DyedPillarBlock) block;
    }

    @Override
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.PICKAXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Block parentBlock = BLOCK.config.getIngredient();
        Item dye = ColorHelpers.getDye(BLOCK.color);

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 8)
            .pattern("###")
            .pattern("#D#")
            .pattern("###")
            .input('#', parentBlock)
            .input('D', dye)
            .criterion(RecipeGenerator.hasItem(parentBlock),
                generator.conditionsFromItem(parentBlock))
            .criterion(RecipeGenerator.hasItem(dye),
                generator.conditionsFromItem(dye))
            .offerTo(exporter);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Dyed %s", ColorHelpers.getName(BLOCK.color), BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Dyed %s", ColorHelpers.getName(BLOCK.color), BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        TextureMap textures = new TextureMap()
            .put(TextureKey.END, TextureUtils.block(BLOCK.BLOCK_ID, "_end"))
            .put(TextureKey.SIDE, TextureUtils.block(BLOCK.BLOCK_ID, "_side"));

        Identifier subModelId = blockStateModelGenerator.createSubModel(BLOCK, "", Models.CUBE_COLUMN, unused -> textures);

        ModelUtils.registerBlockWithAxis(blockStateModelGenerator, DyedPillarBlock.AXIS, BLOCK, subModelId);
    }
}

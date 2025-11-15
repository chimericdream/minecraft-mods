package com.chimericdream.minekea.fabric.block.building;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.block.building.walls.WallBlock;
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
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.Optional;
import java.util.function.Function;

public class WallBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public final WallBlock BLOCK;

    public WallBlockDataGenerator(Block block) {
        BLOCK = (WallBlock) block;
    }

    @Override
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        getBuilder.apply(BlockTags.WALLS)
            .setReplace(false)
            .add(BLOCK);

        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.PICKAXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Block ingredient = BLOCK.config.getIngredient();

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 6)
            .pattern("###")
            .pattern("###")
            .input('#', ingredient)
            .criterion(RecipeGenerator.hasItem(ingredient),
                generator.conditionsFromItem(ingredient))
            .offerTo(exporter);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Wall", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        TextureMap textures = new TextureMap()
            .put(TextureKey.WALL, BLOCK.config.getTexture());

        ModelUtils.registerWallBlock(
            blockStateModelGenerator,
            BLOCK,
            textures,
            Models.WALL_INVENTORY,
            Models.TEMPLATE_WALL_POST,
            Models.TEMPLATE_WALL_SIDE,
            Models.TEMPLATE_WALL_SIDE_TALL
        );
    }
}

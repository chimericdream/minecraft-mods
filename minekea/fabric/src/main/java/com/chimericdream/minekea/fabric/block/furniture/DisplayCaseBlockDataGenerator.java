package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.block.furniture.displaycases.DisplayCaseBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.blockstate.suppliers.CustomBlockStateModelSupplier;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.data.TexturedModel;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Function;

public class DisplayCaseBlockDataGenerator extends ChimericLibBlockDataGenerator {
    private static final Model DISPLAY_CASE_MODEL = new CustomBlockStateModelSupplier.CustomBlockModel(
        BlockConfig.RenderType.CUTOUT,
        Optional.of(Identifier.of("minekea:block/furniture/display_case")),
        Optional.empty(),
        MinekeaTextures.MATERIAL,
        MinekeaTextures.STRIPPED_MATERIAL
    );

    private final DisplayCaseBlock BLOCK;

    public DisplayCaseBlockDataGenerator(Block block) {
        BLOCK = (DisplayCaseBlock) block;
    }

    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Block plankIngredient = BLOCK.config.getIngredient("planks");
        Block logIngredient = BLOCK.config.getIngredient("log");

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern(" G ")
            .pattern("X X")
            .pattern("###")
            .input('G', Blocks.GLASS)
            .input('X', plankIngredient)
            .input('#', logIngredient)
            .criterion(RecipeGenerator.hasItem(Blocks.GLASS),
                generator.conditionsFromItem(Blocks.GLASS))
            .criterion(RecipeGenerator.hasItem(plankIngredient),
                generator.conditionsFromItem(plankIngredient))
            .criterion(RecipeGenerator.hasItem(logIngredient),
                generator.conditionsFromItem(logIngredient))
            .offerTo(exporter);
    }

    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Display Case", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Display Case", BLOCK.config.getMaterialName()));
    }

    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(BLOCK);
    }

    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Block logIngredient = BLOCK.config.getIngredient("log");
        Block strippedLogIngredient = Optional.ofNullable(BLOCK.config.getIngredient("stripped_log")).orElse(logIngredient);

        TextureMap textures = new TextureMap()
            .put(MinekeaTextures.MATERIAL, TextureMap.getId(logIngredient))
            .put(MinekeaTextures.STRIPPED_MATERIAL, TextureMap.getId(strippedLogIngredient));

        blockStateModelGenerator.registerSingleton(
            BLOCK,
            TexturedModel.makeFactory((unused) -> textures, DISPLAY_CASE_MODEL)
        );
    }
}

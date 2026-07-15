package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.displaycases.DisplayCaseBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.blockstate.suppliers.CustomBlockStateModelSupplier;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;
import java.util.function.Function;

public class DisplayCaseBlockDataGenerator extends ChimericLibBlockDataGenerator {
    private static final ModelTemplate DISPLAY_CASE_MODEL = new CustomBlockStateModelSupplier.CustomBlockModel(
        BlockConfig.RenderType.CUTOUT,
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/display_case")),
        Optional.empty(),
        MinekeaTextures.MATERIAL,
        MinekeaTextures.STRIPPED_MATERIAL
    );

    private final DisplayCaseBlock BLOCK;

    public DisplayCaseBlockDataGenerator(Block block) {
        BLOCK = (DisplayCaseBlock) block;
    }

    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK.builtInRegistryHolder().key());
    }

    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block plankIngredient = BLOCK.config.getIngredient("planks");
        Block logIngredient = BLOCK.config.getIngredient("log");

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern(" G ")
            .pattern("X X")
            .pattern("###")
            .define('G', Blocks.GLASS)
            .define('X', plankIngredient)
            .define('#', logIngredient)
            .unlockedBy(RecipeProvider.getHasName(Blocks.GLASS),
                generator.has(Blocks.GLASS))
            .unlockedBy(RecipeProvider.getHasName(plankIngredient),
                generator.has(plankIngredient))
            .unlockedBy(RecipeProvider.getHasName(logIngredient),
                generator.has(logIngredient))
            .save(exporter);
    }

    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Display Case", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Display Case", BLOCK.config.getMaterialName()));
    }

    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Block logIngredient = BLOCK.config.getIngredient("log");
        Block strippedLogIngredient = Optional.ofNullable(BLOCK.config.getIngredient("stripped_log")).orElse(logIngredient);

        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.MATERIAL, TextureMapping.getBlockTexture(logIngredient))
            .put(MinekeaTextures.STRIPPED_MATERIAL, TextureMapping.getBlockTexture(strippedLogIngredient));

        blockStateModelGenerator.createTrivialBlock(
            BLOCK,
            TexturedModel.createDefault((unused) -> textures, DISPLAY_CASE_MODEL)
        );
    }
}

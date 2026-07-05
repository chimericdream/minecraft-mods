package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.seats.StoolBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Function;

public class StoolBlockDataGenerator extends ChimericLibBlockDataGenerator {
    private final StoolBlock BLOCK;

    protected static final ModelTemplate STOOL_MODEL = new ModelTemplate(
        Optional.of(ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/seating/stool")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );

    public StoolBlockDataGenerator(Block block) {
        this.BLOCK = (StoolBlock) block;
    }

    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 2)
            .pattern("PP")
            .pattern("LL")
            .define('P', plankIngredient)
            .define('L', logIngredient)
            .unlockedBy(RecipeProvider.getHasName(plankIngredient),
                generator.has(plankIngredient))
            .unlockedBy(RecipeProvider.getHasName(logIngredient),
                generator.has(logIngredient))
            .save(exporter);
    }

    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Stool", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Stool", BLOCK.config.getMaterialName()));
    }

    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.LOG, BuiltInRegistries.BLOCK.getKey(logIngredient).withPrefix("block/"))
            .put(MinekeaTextures.PLANKS, BuiltInRegistries.BLOCK.getKey(plankIngredient).withPrefix("block/"));

        blockStateModelGenerator.createTrivialBlock(
            BLOCK,
            TexturedModel.createDefault((unused) -> textures, STOOL_MODEL)
        );
    }
}

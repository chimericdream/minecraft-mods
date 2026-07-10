package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.shelves.ShelfBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Function;

public class ShelfBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final ModelTemplate SHELF_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/shelves/supported")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final ModelTemplate FLOATING_SHELF_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/shelves/floating")),
        Optional.empty(),
        MinekeaTextures.PLANKS
    );

    protected final ShelfBlock BLOCK;

    public ShelfBlockDataGenerator(Block block) {
        BLOCK = (ShelfBlock) block;
    }

    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block slabIngredient = BLOCK.config.getIngredient("slab");

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 3)
            .pattern("XXX")
            .pattern("# #")
            .pattern("X X")
            .define('X', slabIngredient)
            .define('#', Items.IRON_NUGGET)
            .unlockedBy(RecipeProvider.getHasName(slabIngredient),
                generator.has(slabIngredient))
            .unlockedBy(RecipeProvider.getHasName(Items.IRON_NUGGET),
                generator.has(Items.IRON_NUGGET))
            .save(exporter);
    }

    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Shelf", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Shelf", BLOCK.config.getMaterialName()));
    }

    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient("planks");
        Block logIngredient = BLOCK.config.getIngredient("log");

        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.LOG, TextureMapping.getBlockTexture(logIngredient))
            .put(MinekeaTextures.PLANKS, TextureMapping.getBlockTexture(plankIngredient));

        Identifier subModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", SHELF_MODEL, unused -> textures);

        ModelUtils.registerBlockWithWallSide(blockStateModelGenerator, ShelfBlock.WALL_SIDE, BLOCK, subModelId);
    }
}

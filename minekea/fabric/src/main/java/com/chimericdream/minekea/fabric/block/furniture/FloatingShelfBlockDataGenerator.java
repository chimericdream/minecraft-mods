package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.minekea.block.furniture.shelves.ShelfBlock;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class FloatingShelfBlockDataGenerator extends ShelfBlockDataGenerator {
    public FloatingShelfBlockDataGenerator(Block block) {
        super(block);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block slabIngredient = BLOCK.config.getIngredient("slab");

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 3)
            .pattern("XXX")
            .pattern("# #")
            .pattern("XXX")
            .define('X', slabIngredient)
            .define('#', Items.IRON_INGOT)
            .unlockedBy(RecipeProvider.getHasName(slabIngredient),
                generator.has(slabIngredient))
            .unlockedBy(RecipeProvider.getHasName(Items.IRON_INGOT),
                generator.has(Items.IRON_INGOT))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Floating Shelf", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Floating Shelf", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient("planks");

        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.PLANKS, TextureMapping.getBlockTexture(plankIngredient));

        Identifier subModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", FLOATING_SHELF_MODEL, unused -> textures);

        ModelUtils.registerBlockWithWallSide(blockStateModelGenerator, ShelfBlock.WALL_SIDE, BLOCK, subModelId);
    }
}


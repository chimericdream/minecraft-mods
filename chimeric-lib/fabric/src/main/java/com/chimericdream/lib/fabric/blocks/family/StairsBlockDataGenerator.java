package com.chimericdream.lib.fabric.blocks.family;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import com.chimericdream.lib.fabric.blocks.TagUtils;
import com.chimericdream.lib.fabric.blocks.TranslationUtils;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;

import java.util.function.Function;

public class StairsBlockDataGenerator implements FabricBlockDataGenerator {
    private final StairBlock block;
    private final BlockConfig config;

    public StairsBlockDataGenerator(StairBlock block, BlockConfig config) {
        this.block = block;
        this.config = config;
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        TagUtils.applyMineableTag(getBuilder, config.getTool(), block);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block ingredient = config.getIngredient();

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, block, 8)
            .pattern("#  ")
            .pattern("## ")
            .pattern("###")
            .define('#', ingredient)
            .unlockedBy(RecipeProvider.getHasName(ingredient), generator.has(ingredient))
            .save(exporter);
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(block);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        TranslationUtils.addBlockAndItem(translationBuilder, block, String.format("%s Stairs", config.getMaterialName()));
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Identifier textureId = config.getTexture();

        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.BOTTOM, new Material(textureId))
            .put(TextureSlot.TOP, new Material(textureId))
            .put(TextureSlot.SIDE, new Material(textureId));

        FamilyBlockModels.registerStairsBlock(
            blockStateModelGenerator,
            block,
            textures,
            ModelTemplates.STAIRS_INNER,
            ModelTemplates.STAIRS_STRAIGHT,
            ModelTemplates.STAIRS_OUTER
        );
    }
}

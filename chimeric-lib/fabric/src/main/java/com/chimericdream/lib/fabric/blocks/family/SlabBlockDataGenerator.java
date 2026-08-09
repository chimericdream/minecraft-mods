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
import net.minecraft.world.level.block.SlabBlock;

import java.util.function.Function;

public class SlabBlockDataGenerator implements FabricBlockDataGenerator {
    private final SlabBlock block;
    private final BlockConfig config;

    public SlabBlockDataGenerator(SlabBlock block, BlockConfig config) {
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

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, block, 6)
            .pattern("###")
            .define('#', ingredient)
            .unlockedBy(RecipeProvider.getHasName(ingredient), generator.has(ingredient))
            .save(exporter);
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.add(block, generator.createSlabItemTable(block));
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        TranslationUtils.addBlockAndItem(translationBuilder, block, String.format("%s Slab", config.getMaterialName()));
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Identifier textureId = config.getTexture();

        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.BOTTOM, new Material(textureId))
            .put(TextureSlot.TOP, new Material(textureId))
            .put(TextureSlot.ALL, new Material(textureId));

        FamilyBlockModels.registerSlabBlock(
            blockStateModelGenerator,
            block,
            textures,
            ModelTemplates.SLAB_BOTTOM,
            ModelTemplates.SLAB_TOP,
            ModelTemplates.CUBE_ALL
        );
    }
}

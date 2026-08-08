package com.chimericdream.lib.fabric.blocks.family;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import com.chimericdream.lib.util.Tool;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;

import java.util.Optional;
import java.util.function.Function;

public class WallBlockDataGenerator implements FabricBlockDataGenerator {
    private final WallBlock block;
    private final BlockConfig config;

    public WallBlockDataGenerator(WallBlock block, BlockConfig config) {
        this.block = block;
        this.config = config;
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBuilder.apply(BlockTags.WALLS)
            .setReplace(false)
            .add(block.builtInRegistryHolder().key());

        Tool tool = Optional.ofNullable(config.getTool()).orElse(Tool.PICKAXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(block.builtInRegistryHolder().key());
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block ingredient = config.getIngredient();

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, block, 6)
            .pattern("###")
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
        translationBuilder.add(block, String.format("%s Wall", config.getMaterialName()));
        translationBuilder.add(block.asItem(), String.format("%s Wall", config.getMaterialName()));
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.WALL, new Material(config.getTexture()));

        FamilyBlockModels.registerWallBlock(
            blockStateModelGenerator,
            block,
            textures,
            ModelTemplates.WALL_INVENTORY,
            ModelTemplates.WALL_POST,
            ModelTemplates.WALL_LOW_SIDE,
            ModelTemplates.WALL_TALL_SIDE
        );
    }
}

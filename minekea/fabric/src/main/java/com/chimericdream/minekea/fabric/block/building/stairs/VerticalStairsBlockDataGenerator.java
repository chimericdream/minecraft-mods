package com.chimericdream.minekea.fabric.block.building.stairs;

import com.chimericdream.lib.fabric.blocks.TranslationUtils;
import com.chimericdream.lib.fabric.blocks.TagUtils;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.stairs.VerticalStairsBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
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

import java.util.Optional;
import java.util.function.Function;

public class VerticalStairsBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public static final ModelTemplate VERTICAL_STAIRS_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/building/stairs/vertical")),
        Optional.empty(),
        TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE
    );

    public final VerticalStairsBlock BLOCK;

    public VerticalStairsBlockDataGenerator(Block block) {
        BLOCK = (VerticalStairsBlock) block;
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        TagUtils.applyMineableTag(getBuilder, BLOCK.config.getTool(), BLOCK);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block ingredient = BLOCK.config.getIngredient();

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 8)
            .pattern("###")
            .pattern(" ##")
            .pattern("  #")
            .define('#', ingredient)
            .unlockedBy(RecipeProvider.getHasName(ingredient),
                generator.has(ingredient))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        TranslationUtils.addBlockAndItem(translationBuilder, BLOCK, String.format("Vertical %s Stairs", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Identifier defaultTextureId = BLOCK.config.getTexture();
        Identifier bottomTextureId = BLOCK.config.getTextureOrDefault("bottom", defaultTextureId);
        Identifier topTextureId = BLOCK.config.getTextureOrDefault("top", defaultTextureId);
        Identifier sideTextureId = BLOCK.config.getTextureOrDefault("side", defaultTextureId);

        assert bottomTextureId != null && topTextureId != null && sideTextureId != null;

        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.BOTTOM, new Material(bottomTextureId))
            .put(TextureSlot.TOP, new Material(topTextureId))
            .put(TextureSlot.SIDE, new Material(sideTextureId));

        ModelUtils.registerVerticalStairsBlock(
            blockStateModelGenerator,
            BLOCK,
            textures,
            VERTICAL_STAIRS_MODEL
        );
    }
}

package com.chimericdream.minekea.fabric.block.building;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.covers.CoverBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
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

import static com.chimericdream.minekea.block.building.covers.CoverBlock.FACING;

public class CoverBlockDataGenerator extends ChimericLibBlockDataGenerator {
    // yowza
    public static final ModelTemplate COVER_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/building/cover")),
        Optional.empty(),
        TextureSlot.END,
        TextureSlot.SIDE
    );

    public CoverBlock BLOCK;

    public CoverBlockDataGenerator(Block block) {
        BLOCK = (CoverBlock) block;
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.PICKAXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block ingredient = BLOCK.config.getIngredient();

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 16)
            .pattern("# #")
            .pattern("   ")
            .pattern("# #")
            .define('#', ingredient)
            .unlockedBy(RecipeProvider.getHasName(ingredient),
                generator.has(ingredient))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Cover", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Cover", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Identifier endTextureId = BLOCK.config.getTexture();
        Identifier sideTextureId = Optional.ofNullable(BLOCK.config.getTexture("side")).orElse(endTextureId);

        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.END, endTextureId)
            .put(TextureSlot.SIDE, sideTextureId);

        Identifier subModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", COVER_MODEL, unused -> textures);

        ModelUtils.registerBlockWithHorizontalFacing(blockStateModelGenerator, FACING, BLOCK, subModelId);
    }
}

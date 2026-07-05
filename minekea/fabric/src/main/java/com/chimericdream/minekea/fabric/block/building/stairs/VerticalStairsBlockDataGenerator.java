package com.chimericdream.minekea.fabric.block.building.stairs;

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
import net.minecraft.core.HolderLookup;
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

public class VerticalStairsBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public static final ModelTemplate VERTICAL_STAIRS_MODEL = new ModelTemplate(
        Optional.of(ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "block/building/stairs/vertical")),
        Optional.empty(),
        TextureSlot.ALL
    );

    public final VerticalStairsBlock BLOCK;

    public VerticalStairsBlockDataGenerator(Block block) {
        BLOCK = (VerticalStairsBlock) block;
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
        translationBuilder.add(BLOCK, String.format("Vertical %s Stairs", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("Vertical %s Stairs", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping().put(TextureSlot.ALL, BLOCK.config.getTexture());

        ModelUtils.registerVerticalStairsBlock(
            blockStateModelGenerator,
            BLOCK,
            textures,
            VERTICAL_STAIRS_MODEL
        );
    }
}

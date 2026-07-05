package com.chimericdream.minekea.fabric.block.building.compressed;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.block.building.compressed.CompressedBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.TextureGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.Function;

public class CompressedBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public CompressedBlock BLOCK;

    public CompressedBlockDataGenerator(Block block) {
        BLOCK = (CompressedBlock) block;
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
        Block parentBlock = BuiltInRegistries.BLOCK.getValue(BLOCK.PARENT_BLOCK_ID);

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .define('#', parentBlock)
            .unlockedBy(RecipeProvider.getHasName(parentBlock), generator.has(parentBlock))
            .save(exporter);

        generator.shapeless(RecipeCategory.BUILDING_BLOCKS, parentBlock, 9)
            .requires(BLOCK)
            .unlockedBy(RecipeProvider.getHasName(BLOCK), generator.has(parentBlock))
            .save(exporter, BLOCK.PARENT_BLOCK_ID.withSuffix("_from_compressed").toString());
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        if (BLOCK.compressionLevel == 1) {
            translationBuilder.add(BLOCK.getDescriptionId(), String.format("Compressed %s", BLOCK.config.getMaterialName()));
        }
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.createTrivialCube(BLOCK);
    }

    @Override
    public void generateTextures() {
        TextureGenerator.getInstance().generate(BuiltInRegistries.BLOCK.getDefaultKey(), instance -> {
            generateTexture(instance, BLOCK.config.getMaterial(), BLOCK.BLOCK_ID);
        });
    }

    protected void addTextureOverlay(TextureGenerator.Instance<Block> instance, Optional<BufferedImage> source, ResourceLocation blockId) {
        if (source.isPresent()) {
            BufferedImage sourceImage = source.get();
            BufferedImage overlayImage = instance.getMinekeaImage(String.format("block/building/compressed/level-%d", BLOCK.compressionLevel)).orElse(null);

            BufferedImage combined = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

            Graphics g = combined.getGraphics();
            g.drawImage(sourceImage, 0, 0, null);
            g.drawImage(overlayImage, 0, 0, 16, 16, null);

            g.dispose();

            instance.generate(blockId, combined);
        }
    }

    protected void generateTexture(TextureGenerator.Instance<Block> instance, String key, ResourceLocation blockId) {
        final Optional<BufferedImage> source = instance.getImage(key);
        addTextureOverlay(instance, source, blockId);
    }

    public static class CompressedBlockTooltipDataGenerator extends ChimericLibBlockDataGenerator {
        @Override
        public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
            translationBuilder.add(CompressedBlock.TOOLTIP_LEVEL, "%dx Compressed");
            translationBuilder.add(CompressedBlock.TOOLTIP_COUNT, "(%s blocks)");
        }
    }
}

package com.chimericdream.minekea.fabric.block.building.compressed;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.block.building.compressed.CompressedBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.TextureGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

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
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.PICKAXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Block parentBlock = Registries.BLOCK.get(BLOCK.PARENT_BLOCK_ID);

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .input('#', parentBlock)
            .criterion(RecipeGenerator.hasItem(parentBlock), generator.conditionsFromItem(parentBlock))
            .offerTo(exporter);

        generator.createShapeless(RecipeCategory.BUILDING_BLOCKS, parentBlock, 9)
            .input(BLOCK)
            .criterion(RecipeGenerator.hasItem(BLOCK), generator.conditionsFromItem(parentBlock))
            .offerTo(exporter, BLOCK.PARENT_BLOCK_ID.withSuffixedPath("_from_compressed").toString());
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("Compressed %s", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(BLOCK);
    }

    @Override
    public void generateTextures() {
        TextureGenerator.getInstance().generate(Registries.BLOCK.getKey(), instance -> {
            generateTexture(instance, BLOCK.config.getMaterial(), BLOCK.BLOCK_ID);
        });
    }

    protected void addTextureOverlay(TextureGenerator.Instance<Block> instance, Optional<BufferedImage> source, Identifier blockId) {
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

    protected void generateTexture(TextureGenerator.Instance<Block> instance, String key, Identifier blockId) {
        final Optional<BufferedImage> source = instance.getImage(key);
        addTextureOverlay(instance, source, blockId);
    }

    public static class CompressedBlockTooltipDataGenerator extends ChimericLibBlockDataGenerator {
        @Override
        public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
            translationBuilder.add(CompressedBlock.TOOLTIP_LEVEL, "%dx Compressed");
            translationBuilder.add(CompressedBlock.TOOLTIP_COUNT, "(%s blocks)");
        }
    }
}

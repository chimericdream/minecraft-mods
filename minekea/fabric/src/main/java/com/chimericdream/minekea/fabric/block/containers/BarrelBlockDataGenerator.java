package com.chimericdream.minekea.fabric.block.containers;

import com.chimericdream.lib.resource.TextureUtils;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.block.containers.barrels.BarrelBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.TextureGenerator;
import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.Function;

public class BarrelBlockDataGenerator extends ChimericLibBlockDataGenerator {
    private final BarrelBlock BLOCK;

    public BarrelBlockDataGenerator(Block block) {
        BLOCK = (BarrelBlock) block;
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block slabIngredient = BLOCK.config.getIngredient("slab");

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern("PSP")
            .pattern("P P")
            .pattern("PSP")
            .define('P', plankIngredient)
            .define('S', slabIngredient)
            .unlockedBy(RecipeProvider.getHasName(plankIngredient),
                generator.has(plankIngredient))
            .unlockedBy(RecipeProvider.getHasName(slabIngredient),
                generator.has(slabIngredient))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Barrel", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Barrel", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Identifier bottomTexture = TextureUtils.block(BLOCK.BLOCK_ID, "_bottom");
        Identifier sideTexture = TextureUtils.block(BLOCK.BLOCK_ID, "_side");
        Identifier topTexture = TextureUtils.block(BLOCK.BLOCK_ID, "_top");
        Identifier topOpenTexture = TextureUtils.block(BLOCK.BLOCK_ID, "_top_open");

        TextureMapping baseTextures = new TextureMapping()
            .put(TextureSlot.BOTTOM, bottomTexture)
            .put(TextureSlot.SIDE, sideTexture)
            .put(TextureSlot.TOP, topTexture);

        TextureMapping openTextures = new TextureMapping()
            .put(TextureSlot.BOTTOM, bottomTexture)
            .put(TextureSlot.SIDE, sideTexture)
            .put(TextureSlot.TOP, topOpenTexture);

        Identifier baseModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", ModelTemplates.CUBE_BOTTOM_TOP, unused -> baseTextures);
        Identifier openModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_open", ModelTemplates.CUBE_BOTTOM_TOP, unused -> openTextures);

        MultiVariant baseModel = BlockModelGenerators.plainVariant(baseModelId);
        MultiVariant openModel = BlockModelGenerators.plainVariant(openModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(BLOCK)
                    .with(PropertyDispatch.initial(BarrelBlock.FACING, BarrelBlock.OPEN)
                        .select(Direction.NORTH, false, baseModel.with(VariantMutator.X_ROT.withValue(Quadrant.R90)))
                        .select(Direction.EAST, false, baseModel.with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                        .select(Direction.SOUTH, false, baseModel.with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                        .select(Direction.WEST, false, baseModel.with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                        .select(Direction.UP, false, baseModel)
                        .select(Direction.DOWN, false, baseModel.with(VariantMutator.X_ROT.withValue(Quadrant.R180)))

                        .select(Direction.NORTH, true, openModel.with(VariantMutator.X_ROT.withValue(Quadrant.R90)))
                        .select(Direction.EAST, true, openModel.with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                        .select(Direction.SOUTH, true, openModel.with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                        .select(Direction.WEST, true, openModel.with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                        .select(Direction.UP, true, openModel)
                        .select(Direction.DOWN, true, openModel.with(VariantMutator.X_ROT.withValue(Quadrant.R180)))
                    )
            );
    }

    @Override
    public void generateTextures() {
        generateTextures(BLOCK.faceTextureKey, BLOCK.sideTextureKey, BLOCK.BLOCK_ID);
    }

    public static void generateTextures(String faceKey, String sideKey, Identifier blockId) {
        TextureGenerator.getInstance().generate(Identifier.withDefaultNamespace("block"), instance -> {
            final Optional<BufferedImage> faceTexture = instance.getImage(faceKey);
            final Optional<BufferedImage> sideTexture = instance.getImage(sideKey);

            if (faceTexture.isPresent() && sideTexture.isPresent()) {
                BufferedImage faceImage = faceTexture.get();
                BufferedImage sideImage = sideTexture.get();

                BufferedImage bandsImage = instance.getMinekeaImage("block/barrels/barrel_bands").orElse(null);
                BufferedImage bottomOverlayImage = instance.getMinekeaImage("block/barrels/barrel_bottom_overlay").orElse(null);
                BufferedImage sideOverlayImage = instance.getMinekeaImage("block/barrels/barrel_side_overlay").orElse(null);
                BufferedImage topOverlayImage = instance.getMinekeaImage("block/barrels/barrel_top_overlay").orElse(null);
                BufferedImage topOpenOverlayImage = instance.getMinekeaImage("block/barrels/barrel_top_open_overlay").orElse(null);

                int fw = faceImage.getWidth();
                int fh = faceImage.getHeight();

                int sw = sideImage.getWidth();
                int sh = sideImage.getHeight();

                BufferedImage bottomCombined = new BufferedImage(fw, fh, BufferedImage.TYPE_INT_ARGB);
                BufferedImage sideCombined = new BufferedImage(sw, sh, BufferedImage.TYPE_INT_ARGB);
                BufferedImage topCombined = new BufferedImage(fw, fh, BufferedImage.TYPE_INT_ARGB);
                BufferedImage topOpenCombined = new BufferedImage(fw, fh, BufferedImage.TYPE_INT_ARGB);

                Graphics bg = bottomCombined.getGraphics();
                bg.drawImage(faceImage, 0, 0, null);
                bg.drawImage(bottomOverlayImage, 0, 0, fw, fh, null);
                bg.dispose();

                Graphics sg = sideCombined.getGraphics();
                sg.drawImage(sideImage, 0, 0, null);
                sg.drawImage(sideOverlayImage, 0, 0, sw, sh, null);
                sg.drawImage(bandsImage, 0, 0, sw, sh, null);
                sg.dispose();

                Graphics tg = topCombined.getGraphics();
                tg.drawImage(faceImage, 0, 0, null);
                tg.drawImage(topOverlayImage, 0, 0, fw, fh, null);
                tg.dispose();

                Graphics tog = topOpenCombined.getGraphics();
                tog.drawImage(faceImage, 0, 0, null);
                tog.drawImage(topOpenOverlayImage, 0, 0, fw, fh, null);
                tog.dispose();

                instance.generate(blockId.withSuffix("_bottom"), bottomCombined);
                instance.generate(blockId.withSuffix("_side"), sideCombined);
                instance.generate(blockId.withSuffix("_top"), topCombined);
                instance.generate(blockId.withSuffix("_top_open"), topOpenCombined);
            }
        });
    }

    public static class OakBarrelDataGenerator extends ChimericLibBlockDataGenerator {
        @Override
        public void generateTextures() {
            BarrelBlockDataGenerator.generateTextures("stripped_oak_log", "oak_planks", BuiltInRegistries.BLOCK.getKey(Blocks.BARREL));
        }
    }
}

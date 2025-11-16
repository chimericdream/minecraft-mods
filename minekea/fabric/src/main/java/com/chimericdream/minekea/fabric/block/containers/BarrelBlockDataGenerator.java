//package com.chimericdream.minekea.fabric.block.containers;
//
//import com.chimericdream.lib.resource.TextureUtils;
//import com.chimericdream.lib.util.Tool;
//import com.chimericdream.minekea.block.containers.barrels.BarrelBlock;
//import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
//import com.chimericdream.minekea.fabric.data.TextureGenerator;
//import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
//import net.minecraft.block.Block;
//import net.minecraft.block.Blocks;
//import net.minecraft.client.data.BlockStateModelGenerator;
//import net.minecraft.client.data.BlockStateVariantMap;
//import net.minecraft.client.data.Models;
//import net.minecraft.client.data.TextureKey;
//import net.minecraft.client.data.TextureMap;
//import net.minecraft.client.data.VariantsBlockModelDefinitionCreator;
//import net.minecraft.client.render.model.json.ModelVariantOperator;
//import net.minecraft.client.render.model.json.WeightedVariant;
//import net.minecraft.data.loottable.BlockLootTableGenerator;
//import net.minecraft.data.recipe.RecipeExporter;
//import net.minecraft.data.recipe.RecipeGenerator;
//import net.minecraft.data.tag.ProvidedTagBuilder;
//import net.minecraft.recipe.book.RecipeCategory;
//import net.minecraft.registry.Registries;
//import net.minecraft.registry.RegistryWrapper;
//import net.minecraft.registry.tag.TagKey;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.math.AxisRotation;
//import net.minecraft.util.math.Direction;
//
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.util.Optional;
//import java.util.function.Function;
//
//public class BarrelBlockDataGenerator extends ChimericLibBlockDataGenerator {
//    private final BarrelBlock BLOCK;
//
//    public BarrelBlockDataGenerator(Block block) {
//        BLOCK = (BarrelBlock) block;
//    }
//
//    @Override
//    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
//        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
//        getBuilder.apply(tool.getMineableTag())
//            .setReplace(false)
//            .add(BLOCK);
//    }
//
//    @Override
//    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
//        Block plankIngredient = BLOCK.config.getIngredient();
//        Block slabIngredient = BLOCK.config.getIngredient("slab");
//
//        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
//            .pattern("PSP")
//            .pattern("P P")
//            .pattern("PSP")
//            .input('P', plankIngredient)
//            .input('S', slabIngredient)
//            .criterion(RecipeGenerator.hasItem(plankIngredient),
//                generator.conditionsFromItem(plankIngredient))
//            .criterion(RecipeGenerator.hasItem(slabIngredient),
//                generator.conditionsFromItem(slabIngredient))
//            .offerTo(exporter);
//    }
//
//    @Override
//    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
//        translationBuilder.add(BLOCK, String.format("%s Barrel", BLOCK.config.getMaterialName()));
//    }
//
//    @Override
//    public void configureBlockLootTables(BlockLootTableGenerator generator) {
//        generator.addDrop(BLOCK);
//    }
//
//    @Override
//    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
//        Identifier bottomTexture = TextureUtils.block(BLOCK.BLOCK_ID, "_bottom");
//        Identifier sideTexture = TextureUtils.block(BLOCK.BLOCK_ID, "_side");
//        Identifier topTexture = TextureUtils.block(BLOCK.BLOCK_ID, "_top");
//        Identifier topOpenTexture = TextureUtils.block(BLOCK.BLOCK_ID, "_top_open");
//
//        TextureMap baseTextures = new TextureMap()
//            .put(TextureKey.BOTTOM, bottomTexture)
//            .put(TextureKey.SIDE, sideTexture)
//            .put(TextureKey.TOP, topTexture);
//
//        TextureMap openTextures = new TextureMap()
//            .put(TextureKey.BOTTOM, bottomTexture)
//            .put(TextureKey.SIDE, sideTexture)
//            .put(TextureKey.TOP, topOpenTexture);
//
//        Identifier baseModelId = blockStateModelGenerator.createSubModel(BLOCK, "", Models.CUBE_BOTTOM_TOP, unused -> baseTextures);
//        Identifier openModelId = blockStateModelGenerator.createSubModel(BLOCK, "_open", Models.CUBE_BOTTOM_TOP, unused -> openTextures);
//
//        WeightedVariant baseModel = BlockStateModelGenerator.createWeightedVariant(baseModelId);
//        WeightedVariant openModel = BlockStateModelGenerator.createWeightedVariant(openModelId);
//
//        blockStateModelGenerator.blockStateCollector
//            .accept(
//                VariantsBlockModelDefinitionCreator.of(BLOCK)
//                    .with(BlockStateVariantMap.models(BarrelBlock.FACING, BarrelBlock.OPEN)
//                        .register(Direction.DOWN, false, baseModel.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R180)))
//                        .register(Direction.EAST, false, baseModel.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)).apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)))
//                        .register(Direction.NORTH, false, baseModel.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)))
//                        .register(Direction.SOUTH, false, baseModel.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)).apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180)))
//                        .register(Direction.UP, false, baseModel)
//                        .register(Direction.WEST, false, baseModel.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)).apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270)))
//                        .register(Direction.DOWN, true, openModel.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R180)))
//                        .register(Direction.EAST, true, openModel.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)).apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)))
//                        .register(Direction.NORTH, true, openModel.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)))
//                        .register(Direction.SOUTH, true, openModel.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)).apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180)))
//                        .register(Direction.UP, true, openModel)
//                        .register(Direction.WEST, true, openModel.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)).apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270)))
//                    )
//            );
//    }
//
//    @Override
//    public void generateTextures() {
//        generateTextures(BLOCK.faceTextureKey, BLOCK.sideTextureKey, BLOCK.BLOCK_ID);
//    }
//
//    public static void generateTextures(String faceKey, String sideKey, Identifier blockId) {
//        TextureGenerator.getInstance().generate(Registries.BLOCK.getKey(), instance -> {
//            final Optional<BufferedImage> faceTexture = instance.getImage(faceKey);
//            final Optional<BufferedImage> sideTexture = instance.getImage(sideKey);
//
//            if (faceTexture.isPresent() && sideTexture.isPresent()) {
//                BufferedImage faceImage = faceTexture.get();
//                BufferedImage sideImage = sideTexture.get();
//
//                BufferedImage bandsImage = instance.getMinekeaImage("block/barrels/barrel_bands").orElse(null);
//                BufferedImage bottomOverlayImage = instance.getMinekeaImage("block/barrels/barrel_bottom_overlay").orElse(null);
//                BufferedImage sideOverlayImage = instance.getMinekeaImage("block/barrels/barrel_side_overlay").orElse(null);
//                BufferedImage topOverlayImage = instance.getMinekeaImage("block/barrels/barrel_top_overlay").orElse(null);
//                BufferedImage topOpenOverlayImage = instance.getMinekeaImage("block/barrels/barrel_top_open_overlay").orElse(null);
//
//                int fw = faceImage.getWidth();
//                int fh = faceImage.getHeight();
//
//                int sw = sideImage.getWidth();
//                int sh = sideImage.getHeight();
//
//                BufferedImage bottomCombined = new BufferedImage(fw, fh, BufferedImage.TYPE_INT_ARGB);
//                BufferedImage sideCombined = new BufferedImage(sw, sh, BufferedImage.TYPE_INT_ARGB);
//                BufferedImage topCombined = new BufferedImage(fw, fh, BufferedImage.TYPE_INT_ARGB);
//                BufferedImage topOpenCombined = new BufferedImage(fw, fh, BufferedImage.TYPE_INT_ARGB);
//
//                Graphics bg = bottomCombined.getGraphics();
//                bg.drawImage(faceImage, 0, 0, null);
//                bg.drawImage(bottomOverlayImage, 0, 0, fw, fh, null);
//                bg.dispose();
//
//                Graphics sg = sideCombined.getGraphics();
//                sg.drawImage(sideImage, 0, 0, null);
//                sg.drawImage(sideOverlayImage, 0, 0, sw, sh, null);
//                sg.drawImage(bandsImage, 0, 0, sw, sh, null);
//                sg.dispose();
//
//                Graphics tg = topCombined.getGraphics();
//                tg.drawImage(faceImage, 0, 0, null);
//                tg.drawImage(topOverlayImage, 0, 0, fw, fh, null);
//                tg.dispose();
//
//                Graphics tog = topOpenCombined.getGraphics();
//                tog.drawImage(faceImage, 0, 0, null);
//                tog.drawImage(topOpenOverlayImage, 0, 0, fw, fh, null);
//                tog.dispose();
//
//                instance.generate(blockId.withSuffixedPath("_bottom"), bottomCombined);
//                instance.generate(blockId.withSuffixedPath("_side"), sideCombined);
//                instance.generate(blockId.withSuffixedPath("_top"), topCombined);
//                instance.generate(blockId.withSuffixedPath("_top_open"), topOpenCombined);
//            }
//        });
//    }
//
//    public static class OakBarrelDataGenerator extends ChimericLibBlockDataGenerator {
//        @Override
//        public void generateTextures() {
//            BarrelBlockDataGenerator.generateTextures("stripped_oak_log", "oak_planks", Registries.BLOCK.getId(Blocks.BARREL));
//        }
//    }
//}

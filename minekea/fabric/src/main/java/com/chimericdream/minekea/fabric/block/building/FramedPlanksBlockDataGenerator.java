package com.chimericdream.minekea.fabric.block.building;

import com.chimericdream.lib.resource.TextureUtils;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.framed.FramedPlanksBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import com.chimericdream.minekea.tag.MinekeaBlockTags;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;

import java.util.Optional;
import java.util.function.Function;

public class FramedPlanksBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final Model CORE_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/building/framed_planks/core")),
        Optional.empty(),
        MinekeaTextures.BRACE,
        MinekeaTextures.MATERIAL
    );
    protected static final Model A_CONNECTED_MODEL = makeModel("a");
    protected static final Model B_CONNECTED_MODEL = makeModel("b");
    protected static final Model AB_CONNECTED_MODEL = makeModel("ab");

    public FramedPlanksBlock BLOCK;

    public FramedPlanksBlockDataGenerator(Block block) {
        BLOCK = (FramedPlanksBlock) block;
    }

    protected static Model makeModel(String direction) {
        return new Model(
            Optional.of(Identifier.of(ModInfo.MOD_ID, String.format("block/building/framed_planks/%s_connected", direction))),
            Optional.empty(),
            MinekeaTextures.BRACE,
            MinekeaTextures.MATERIAL
        );
    }

    @Override
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        getBuilder.apply(MinekeaBlockTags.FRAMED_PLANKS)
            .setReplace(false)
            .add(BLOCK);

        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        generator.createShapeless(RecipeCategory.BUILDING_BLOCKS, BLOCK, 6)
            .input(plankIngredient)
            .input(plankIngredient)
            .input(plankIngredient)
            .input(plankIngredient)
            .input(logIngredient)
            .criterion(RecipeGenerator.hasItem(plankIngredient),
                generator.conditionsFromItem(plankIngredient))
            .criterion(RecipeGenerator.hasItem(logIngredient),
                generator.conditionsFromItem(logIngredient))
            .offerTo(exporter);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("Framed %s Planks", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("Framed %s Planks", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator, RegistryWrapper.WrapperLookup registryLookup) {
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        TextureMap textures = new TextureMap()
            .put(MinekeaTextures.MATERIAL, TextureUtils.block(plankIngredient))
            .put(MinekeaTextures.BRACE, TextureUtils.block(logIngredient));

        Identifier modelId = blockStateModelGenerator.createSubModel(BLOCK, "", CORE_MODEL, unused -> textures);
        Identifier aConnectedModelId = blockStateModelGenerator.createSubModel(BLOCK, "_a_connected", A_CONNECTED_MODEL, unused -> textures);
        Identifier bConnectedModelId = blockStateModelGenerator.createSubModel(BLOCK, "_b_connected", B_CONNECTED_MODEL, unused -> textures);
        Identifier abConnectedModelId = blockStateModelGenerator.createSubModel(BLOCK, "_ab_connected", AB_CONNECTED_MODEL, unused -> textures);

        WeightedVariant coreVariant = BlockStateModelGenerator.createWeightedVariant(modelId);
        WeightedVariant aConnectedVariant = BlockStateModelGenerator.createWeightedVariant(aConnectedModelId);
        WeightedVariant bConnectedVariant = BlockStateModelGenerator.createWeightedVariant(bConnectedModelId);
        WeightedVariant abConnectedVariant = BlockStateModelGenerator.createWeightedVariant(abConnectedModelId);

        WeightedVariant invalidVariant = makeInvalidVariant(blockStateModelGenerator, BLOCK);

        blockStateModelGenerator.registerParentedItemModel(BLOCK, modelId);

        blockStateModelGenerator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(BLOCK).with(
                    BlockStateVariantMap
                        .models(
                            FramedPlanksBlock.FACING,
                            FramedPlanksBlock.CONNECTED_EAST,
                            FramedPlanksBlock.CONNECTED_WEST,
                            FramedPlanksBlock.CONNECTED_NORTH,
                            FramedPlanksBlock.CONNECTED_SOUTH
                        )
                        .register(Direction.NORTH, false, false, false, false, coreVariant)
                        .register(Direction.NORTH, true, false, false, false, aConnectedVariant)
                        .register(Direction.NORTH, false, true, false, false, bConnectedVariant)
                        .register(Direction.NORTH, true, true, false, false, abConnectedVariant)
                        .register(Direction.EAST, false, false, false, false, coreVariant.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270)))
                        .register(Direction.EAST, false, false, true, false, aConnectedVariant.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270)))
                        .register(Direction.EAST, false, false, false, true, bConnectedVariant.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270)))
                        .register(Direction.EAST, false, false, true, true, abConnectedVariant.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270)))
                        .register(Direction.NORTH, true, true, true, true, invalidVariant)
                        .register(Direction.NORTH, true, true, true, false, invalidVariant)
                        .register(Direction.NORTH, true, true, false, true, invalidVariant)
                        .register(Direction.NORTH, true, false, true, true, invalidVariant)
                        .register(Direction.NORTH, true, false, true, false, invalidVariant)
                        .register(Direction.NORTH, true, false, false, true, invalidVariant)
                        .register(Direction.NORTH, false, true, true, true, invalidVariant)
                        .register(Direction.NORTH, false, true, true, false, invalidVariant)
                        .register(Direction.NORTH, false, true, false, true, invalidVariant)
                        .register(Direction.NORTH, false, false, true, true, invalidVariant)
                        .register(Direction.NORTH, false, false, true, false, invalidVariant)
                        .register(Direction.NORTH, false, false, false, true, invalidVariant)
                        .register(Direction.SOUTH, true, true, true, true, invalidVariant)
                        .register(Direction.SOUTH, true, true, true, false, invalidVariant)
                        .register(Direction.SOUTH, true, true, false, true, invalidVariant)
                        .register(Direction.SOUTH, true, true, false, false, invalidVariant)
                        .register(Direction.SOUTH, true, false, true, true, invalidVariant)
                        .register(Direction.SOUTH, true, false, true, false, invalidVariant)
                        .register(Direction.SOUTH, true, false, false, true, invalidVariant)
                        .register(Direction.SOUTH, true, false, false, false, invalidVariant)
                        .register(Direction.SOUTH, false, true, true, true, invalidVariant)
                        .register(Direction.SOUTH, false, true, true, false, invalidVariant)
                        .register(Direction.SOUTH, false, true, false, true, invalidVariant)
                        .register(Direction.SOUTH, false, true, false, false, invalidVariant)
                        .register(Direction.SOUTH, false, false, true, true, invalidVariant)
                        .register(Direction.SOUTH, false, false, true, false, invalidVariant)
                        .register(Direction.SOUTH, false, false, false, true, invalidVariant)
                        .register(Direction.SOUTH, false, false, false, false, invalidVariant)
                        .register(Direction.WEST, true, true, true, true, invalidVariant)
                        .register(Direction.WEST, true, true, true, false, invalidVariant)
                        .register(Direction.WEST, true, true, false, true, invalidVariant)
                        .register(Direction.WEST, true, true, false, false, invalidVariant)
                        .register(Direction.WEST, true, false, true, true, invalidVariant)
                        .register(Direction.WEST, true, false, true, false, invalidVariant)
                        .register(Direction.WEST, true, false, false, true, invalidVariant)
                        .register(Direction.WEST, true, false, false, false, invalidVariant)
                        .register(Direction.WEST, false, true, true, true, invalidVariant)
                        .register(Direction.WEST, false, true, true, false, invalidVariant)
                        .register(Direction.WEST, false, true, false, true, invalidVariant)
                        .register(Direction.WEST, false, true, false, false, invalidVariant)
                        .register(Direction.WEST, false, false, true, true, invalidVariant)
                        .register(Direction.WEST, false, false, true, false, invalidVariant)
                        .register(Direction.WEST, false, false, false, true, invalidVariant)
                        .register(Direction.WEST, false, false, false, false, invalidVariant)
                        .register(Direction.EAST, true, true, true, true, invalidVariant)
                        .register(Direction.EAST, true, true, true, false, invalidVariant)
                        .register(Direction.EAST, true, true, false, true, invalidVariant)
                        .register(Direction.EAST, true, true, false, false, invalidVariant)
                        .register(Direction.EAST, true, false, true, true, invalidVariant)
                        .register(Direction.EAST, true, false, true, false, invalidVariant)
                        .register(Direction.EAST, true, false, false, true, invalidVariant)
                        .register(Direction.EAST, true, false, false, false, invalidVariant)
                        .register(Direction.EAST, false, true, true, true, invalidVariant)
                        .register(Direction.EAST, false, true, true, false, invalidVariant)
                        .register(Direction.EAST, false, true, false, true, invalidVariant)
                        .register(Direction.EAST, false, true, false, false, invalidVariant)
                )
            );
    }
}

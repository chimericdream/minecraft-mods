package com.chimericdream.minekea.fabric.data.model;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.fabric.data.blockstate.suppliers.CustomBlockStateModelSupplier;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.Models;
import net.minecraft.client.data.TextureKey;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class ModelUtils {
    public static final Model CUSTOM_TEMPLATE_LANTERN = new CustomBlockStateModelSupplier.CustomBlockModel(
        BlockConfig.RenderType.CUTOUT,
        Optional.of(Identifier.ofVanilla("block/template_lantern")),
        Optional.empty(),
        TextureKey.LANTERN
    );

    public static final Model CUSTOM_TEMPLATE_HANGING_LANTERN = new CustomBlockStateModelSupplier.CustomBlockModel(
        BlockConfig.RenderType.CUTOUT,
        Optional.of(Identifier.ofVanilla("block/template_hanging_lantern")),
        Optional.empty(),
        TextureKey.LANTERN
    );

    public static void registerGeneratedItem(ItemModelGenerator itemModelGenerator, Block block) {
        itemModelGenerator.register(block.asItem(), Models.GENERATED);
    }

    public static void registerLanternBlock(
        BlockStateModelGenerator blockStateModelGenerator,
        Block block,
        Identifier blockId
    ) {
        TextureMap textures = new TextureMap()
            .put(TextureKey.LANTERN, blockId.withPrefixedPath("block/"));

        Identifier baseModelId = blockStateModelGenerator.createSubModel(block, "_base", CUSTOM_TEMPLATE_LANTERN, unused -> textures);
        Identifier hangingModelId = blockStateModelGenerator.createSubModel(block, "_hanging", CUSTOM_TEMPLATE_HANGING_LANTERN, unused -> textures);

        WeightedVariant baseModel = BlockStateModelGenerator.createWeightedVariant(baseModelId);
        WeightedVariant hangingModel = BlockStateModelGenerator.createWeightedVariant(hangingModelId);

        blockStateModelGenerator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(block)
                    .with(BlockStateVariantMap.models(Properties.HANGING)
                        .register(true, hangingModel)
                        .register(false, baseModel))
            );
    }

    public static void registerBlockWithAxis(
        BlockStateModelGenerator blockStateModelGenerator,
        EnumProperty<Direction.Axis> axis,
        Block block,
        Identifier subModelId
    ) {
        WeightedVariant model = BlockStateModelGenerator.createWeightedVariant(subModelId);

        blockStateModelGenerator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(block)
                    .with(BlockStateVariantMap.models(axis)
                        .register(Direction.Axis.X, model.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)).apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)))
                        .register(Direction.Axis.Y, model)
                        .register(Direction.Axis.Z, model.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)))
                    )
            );
    }

    public static void registerBlockWithWallSide(
        BlockStateModelGenerator blockStateModelGenerator,
        EnumProperty<Direction> wallSide,
        Block block,
        Identifier subModelId
    ) {
        registerBlockWithHorizontalFacing(blockStateModelGenerator, wallSide, block, subModelId);
    }

    public static void registerBlockWithHorizontalFacing(
        BlockStateModelGenerator blockStateModelGenerator,
        EnumProperty<Direction> facing,
        Block block,
        Identifier subModelId
    ) {
        WeightedVariant model = BlockStateModelGenerator.createWeightedVariant(subModelId);

        blockStateModelGenerator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(block)
                    .with(BlockStateVariantMap.models(facing)
                        .register(Direction.NORTH, model)
                        .register(Direction.EAST, model.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)))
                        .register(Direction.SOUTH, model.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180)))
                        .register(Direction.WEST, model.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270)))
                    )
            );
    }

    public static void registerBlockWithFacing(
        BlockStateModelGenerator blockStateModelGenerator,
        EnumProperty<Direction> facing,
        Block block,
        Identifier subModelId
    ) {
        WeightedVariant model = BlockStateModelGenerator.createWeightedVariant(subModelId);

        blockStateModelGenerator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(block)
                    .with(BlockStateVariantMap.models(facing)
                        .register(Direction.NORTH, model.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)))
                        .register(Direction.EAST, model.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)).apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)))
                        .register(Direction.SOUTH, model.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R270)))
                        .register(Direction.WEST, model.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R90)).apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270)))
                        .register(Direction.UP, model)
                        .register(Direction.DOWN, model.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R180)))
                    )
            );
    }
}

package com.chimericdream.minekea.fabric.data.model;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.block.building.slabs.VerticalSlabBlock;
import com.chimericdream.minekea.block.building.stairs.VerticalStairsBlock;
import com.chimericdream.minekea.fabric.data.blockstate.suppliers.CustomBlockStateModelSupplier;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.enums.SlabType;
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

    public static void register

    public static void registerSlabBlock(
        BlockStateModelGenerator blockStateModelGenerator,
        SlabBlock block,
        TextureMap textures,
        Model bottomModel,
        Model topModel,
        Model doubleModel
    ) {
        Identifier bottomModelId = blockStateModelGenerator.createSubModel(block, "", bottomModel, unused -> textures);
        Identifier topModelId = blockStateModelGenerator.createSubModel(block, "_top", topModel, unused -> textures);
        Identifier doubleModelId = blockStateModelGenerator.createSubModel(block, "_double", doubleModel, unused -> textures);

        WeightedVariant bottomVariant = BlockStateModelGenerator.createWeightedVariant(bottomModelId);
        WeightedVariant topVariant = BlockStateModelGenerator.createWeightedVariant(topModelId);
        WeightedVariant doubleVariant = BlockStateModelGenerator.createWeightedVariant(doubleModelId);

        blockStateModelGenerator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(block)
                    .with(BlockStateVariantMap.models(SlabBlock.TYPE)
                        .register(SlabType.BOTTOM, bottomVariant)
                        .register(SlabType.TOP, topVariant)
                        .register(SlabType.DOUBLE, doubleVariant))
            );
    }

    public static void registerVerticalSlabBlock(
        BlockStateModelGenerator blockStateModelGenerator,
        VerticalSlabBlock block,
        TextureMap textures,
        Model model
    ) {
        Identifier modelId = blockStateModelGenerator.createSubModel(block, "", model, unused -> textures);

        WeightedVariant variant = BlockStateModelGenerator.createWeightedVariant(modelId).apply(ModelVariantOperator.UV_LOCK.withValue(true));

        blockStateModelGenerator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(block)
                    .with(BlockStateVariantMap.models(VerticalSlabBlock.FACING)
                        .register(
                            Direction.NORTH,
                            variant.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
                        )
                        .register(
                            Direction.EAST,
                            variant.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180))
                        )
                        .register(
                            Direction.SOUTH,
                            variant.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270))
                        )
                        .register(
                            Direction.WEST,
                            variant
                        )
                    )
            );
    }

    public static void registerStairsBlock(
        BlockStateModelGenerator blockStateModelGenerator,
        StairsBlock block,
        TextureMap textures,
        Model innerModel,
        Model straightModel,
        Model outerModel
    ) {
        Identifier innerModelId = blockStateModelGenerator.createSubModel(block, "_inner", innerModel, unused -> textures);
        Identifier straightModelId = blockStateModelGenerator.createSubModel(block, "", straightModel, unused -> textures);
        Identifier outerModelId = blockStateModelGenerator.createSubModel(block, "_outer", outerModel, unused -> textures);

        WeightedVariant innerVariant = BlockStateModelGenerator.createWeightedVariant(innerModelId);
        WeightedVariant straightVariant = BlockStateModelGenerator.createWeightedVariant(straightModelId);
        WeightedVariant outerVariant = BlockStateModelGenerator.createWeightedVariant(outerModelId);

        blockStateModelGenerator.blockStateCollector
            .accept(BlockStateModelGenerator.createStairsBlockState(block, innerVariant, straightVariant, outerVariant));
    }

    public static void registerVerticalStairsBlock(
        BlockStateModelGenerator blockStateModelGenerator,
        VerticalStairsBlock block,
        TextureMap textures,
        Model model
    ) {
        Identifier modelId = blockStateModelGenerator.createSubModel(block, "", model, unused -> textures);
        WeightedVariant variant = BlockStateModelGenerator.createWeightedVariant(modelId).apply(ModelVariantOperator.UV_LOCK.withValue(true));

        blockStateModelGenerator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(block)
                    .with(BlockStateVariantMap.models(VerticalSlabBlock.FACING)
                        .register(
                            Direction.NORTH,
                            variant
                        )
                        .register(
                            Direction.EAST,
                            variant.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
                        )
                        .register(
                            Direction.SOUTH,
                            variant.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180))
                        )
                        .register(
                            Direction.WEST,
                            variant.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270))
                        )
                    )
            );
    }

    public static void registerWallBlock(
        BlockStateModelGenerator blockStateModelGenerator,
        WallBlock block,
        TextureMap textures,
        Model inventoryModel,
        Model postModel,
        Model sideModel,
        Model sideTallModel
    ) {
        Identifier inventoryModelId = blockStateModelGenerator.createSubModel(block, "", inventoryModel, unused -> textures);
        Identifier postModelId = blockStateModelGenerator.createSubModel(block, "", postModel, unused -> textures);
        Identifier sideModelId = blockStateModelGenerator.createSubModel(block, "", sideModel, unused -> textures);
        Identifier sideTallModelId = blockStateModelGenerator.createSubModel(block, "", sideTallModel, unused -> textures);

        WeightedVariant postVariant = BlockStateModelGenerator.createWeightedVariant(postModelId);
        WeightedVariant sideVariant = BlockStateModelGenerator.createWeightedVariant(sideModelId);
        WeightedVariant sideTallVariant = BlockStateModelGenerator.createWeightedVariant(sideTallModelId);

        blockStateModelGenerator.blockStateCollector
            .accept(BlockStateModelGenerator.createWallBlockState(block, postVariant, sideVariant, sideTallVariant));
        blockStateModelGenerator.registerParentedItemModel(block, inventoryModelId);
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

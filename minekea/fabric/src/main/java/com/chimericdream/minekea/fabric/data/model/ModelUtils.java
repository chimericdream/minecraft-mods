package com.chimericdream.minekea.fabric.data.model;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.block.building.slabs.VerticalSlabBlock;
import com.chimericdream.minekea.block.building.stairs.VerticalStairsBlock;
import com.chimericdream.minekea.fabric.data.blockstate.suppliers.CustomBlockStateModelSupplier;
import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.Optional;

public class ModelUtils {
    public static final ModelTemplate CUSTOM_TEMPLATE_LANTERN = new CustomBlockStateModelSupplier.CustomBlockModel(
        BlockConfig.RenderType.CUTOUT,
        Optional.of(ResourceLocation.withDefaultNamespace("block/template_lantern")),
        Optional.empty(),
        TextureSlot.LANTERN
    );

    public static final ModelTemplate CUSTOM_TEMPLATE_HANGING_LANTERN = new CustomBlockStateModelSupplier.CustomBlockModel(
        BlockConfig.RenderType.CUTOUT,
        Optional.of(ResourceLocation.withDefaultNamespace("block/template_hanging_lantern")),
        Optional.empty(),
        TextureSlot.LANTERN
    );

    public static void registerGeneratedItem(ItemModelGenerators itemModelGenerator, Block block) {
        itemModelGenerator.generateFlatItem(block.asItem(), ModelTemplates.FLAT_ITEM);
    }

    public static void registerSlabBlock(
        BlockModelGenerators blockStateModelGenerator,
        SlabBlock block,
        TextureMapping textures,
        ModelTemplate bottomModel,
        ModelTemplate topModel,
        ModelTemplate doubleModel
    ) {
        ResourceLocation bottomModelId = blockStateModelGenerator.createSuffixedVariant(block, "", bottomModel, unused -> textures);
        ResourceLocation topModelId = blockStateModelGenerator.createSuffixedVariant(block, "_top", topModel, unused -> textures);
        ResourceLocation doubleModelId = blockStateModelGenerator.createSuffixedVariant(block, "_double", doubleModel, unused -> textures);

        MultiVariant bottomVariant = BlockModelGenerators.plainVariant(bottomModelId);
        MultiVariant topVariant = BlockModelGenerators.plainVariant(topModelId);
        MultiVariant doubleVariant = BlockModelGenerators.plainVariant(doubleModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(block)
                    .with(PropertyDispatch.initial(SlabBlock.TYPE)
                        .select(SlabType.BOTTOM, bottomVariant)
                        .select(SlabType.TOP, topVariant)
                        .select(SlabType.DOUBLE, doubleVariant))
            );
    }

    public static void registerVerticalSlabBlock(
        BlockModelGenerators blockStateModelGenerator,
        VerticalSlabBlock block,
        TextureMapping textures,
        ModelTemplate model
    ) {
        ResourceLocation modelId = blockStateModelGenerator.createSuffixedVariant(block, "", model, unused -> textures);

        MultiVariant variant = BlockModelGenerators.plainVariant(modelId).with(VariantMutator.UV_LOCK.withValue(true));

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(block)
                    .with(PropertyDispatch.initial(VerticalSlabBlock.FACING)
                        .select(
                            Direction.NORTH,
                            variant.with(VariantMutator.Y_ROT.withValue(Quadrant.R90))
                        )
                        .select(
                            Direction.EAST,
                            variant.with(VariantMutator.Y_ROT.withValue(Quadrant.R180))
                        )
                        .select(
                            Direction.SOUTH,
                            variant.with(VariantMutator.Y_ROT.withValue(Quadrant.R270))
                        )
                        .select(
                            Direction.WEST,
                            variant
                        )
                    )
            );
    }

    public static void registerStairsBlock(
        BlockModelGenerators blockStateModelGenerator,
        StairBlock block,
        TextureMapping textures,
        ModelTemplate innerModel,
        ModelTemplate straightModel,
        ModelTemplate outerModel
    ) {
        ResourceLocation innerModelId = blockStateModelGenerator.createSuffixedVariant(block, "_inner", innerModel, unused -> textures);
        ResourceLocation straightModelId = blockStateModelGenerator.createSuffixedVariant(block, "", straightModel, unused -> textures);
        ResourceLocation outerModelId = blockStateModelGenerator.createSuffixedVariant(block, "_outer", outerModel, unused -> textures);

        MultiVariant innerVariant = BlockModelGenerators.plainVariant(innerModelId);
        MultiVariant straightVariant = BlockModelGenerators.plainVariant(straightModelId);
        MultiVariant outerVariant = BlockModelGenerators.plainVariant(outerModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(BlockModelGenerators.createStairs(block, innerVariant, straightVariant, outerVariant));
    }

    public static void registerVerticalStairsBlock(
        BlockModelGenerators blockStateModelGenerator,
        VerticalStairsBlock block,
        TextureMapping textures,
        ModelTemplate model
    ) {
        ResourceLocation modelId = blockStateModelGenerator.createSuffixedVariant(block, "", model, unused -> textures);
        MultiVariant variant = BlockModelGenerators.plainVariant(modelId).with(VariantMutator.UV_LOCK.withValue(true));

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(block)
                    .with(PropertyDispatch.initial(VerticalSlabBlock.FACING)
                        .select(
                            Direction.NORTH,
                            variant
                        )
                        .select(
                            Direction.EAST,
                            variant.with(VariantMutator.Y_ROT.withValue(Quadrant.R90))
                        )
                        .select(
                            Direction.SOUTH,
                            variant.with(VariantMutator.Y_ROT.withValue(Quadrant.R180))
                        )
                        .select(
                            Direction.WEST,
                            variant.with(VariantMutator.Y_ROT.withValue(Quadrant.R270))
                        )
                    )
            );
    }

    public static void registerWallBlock(
        BlockModelGenerators blockStateModelGenerator,
        WallBlock block,
        TextureMapping textures,
        ModelTemplate inventoryModel,
        ModelTemplate postModel,
        ModelTemplate sideModel,
        ModelTemplate sideTallModel
    ) {
        ResourceLocation inventoryModelId = blockStateModelGenerator.createSuffixedVariant(block, "", inventoryModel, unused -> textures);
        ResourceLocation postModelId = blockStateModelGenerator.createSuffixedVariant(block, "", postModel, unused -> textures);
        ResourceLocation sideModelId = blockStateModelGenerator.createSuffixedVariant(block, "", sideModel, unused -> textures);
        ResourceLocation sideTallModelId = blockStateModelGenerator.createSuffixedVariant(block, "", sideTallModel, unused -> textures);

        MultiVariant postVariant = BlockModelGenerators.plainVariant(postModelId);
        MultiVariant sideVariant = BlockModelGenerators.plainVariant(sideModelId);
        MultiVariant sideTallVariant = BlockModelGenerators.plainVariant(sideTallModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(BlockModelGenerators.createWall(block, postVariant, sideVariant, sideTallVariant));
        blockStateModelGenerator.registerSimpleItemModel(block, inventoryModelId);
    }

    public static void registerLanternBlock(
        BlockModelGenerators blockStateModelGenerator,
        Block block,
        ResourceLocation blockId
    ) {
        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.LANTERN, blockId.withPrefix("block/"));

        ResourceLocation baseModelId = blockStateModelGenerator.createSuffixedVariant(block, "_base", CUSTOM_TEMPLATE_LANTERN, unused -> textures);
        ResourceLocation hangingModelId = blockStateModelGenerator.createSuffixedVariant(block, "_hanging", CUSTOM_TEMPLATE_HANGING_LANTERN, unused -> textures);

        MultiVariant baseModel = BlockModelGenerators.plainVariant(baseModelId);
        MultiVariant hangingModel = BlockModelGenerators.plainVariant(hangingModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(block)
                    .with(PropertyDispatch.initial(BlockStateProperties.HANGING)
                        .select(true, hangingModel)
                        .select(false, baseModel))
            );
    }

    public static void registerBlockWithAxis(
        BlockModelGenerators blockStateModelGenerator,
        EnumProperty<Direction.Axis> axis,
        Block block,
        ResourceLocation subModelId
    ) {
        MultiVariant model = BlockModelGenerators.plainVariant(subModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(block)
                    .with(PropertyDispatch.initial(axis)
                        .select(Direction.Axis.X, model.with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                        .select(Direction.Axis.Y, model)
                        .select(Direction.Axis.Z, model.with(VariantMutator.X_ROT.withValue(Quadrant.R90)))
                    )
            );
    }

    public static void registerBlockWithWallSide(
        BlockModelGenerators blockStateModelGenerator,
        EnumProperty<Direction> wallSide,
        Block block,
        ResourceLocation subModelId
    ) {
        registerBlockWithHorizontalFacing(blockStateModelGenerator, wallSide, block, subModelId);
    }

    public static void registerBlockWithHorizontalFacing(
        BlockModelGenerators blockStateModelGenerator,
        EnumProperty<Direction> facing,
        Block block,
        ResourceLocation subModelId
    ) {
        MultiVariant model = BlockModelGenerators.plainVariant(subModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(block)
                    .with(PropertyDispatch.initial(facing)
                        .select(Direction.NORTH, model)
                        .select(Direction.EAST, model.with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                        .select(Direction.SOUTH, model.with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                        .select(Direction.WEST, model.with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                    )
            );
    }

    public static void registerBlockWithFacing(
        BlockModelGenerators blockStateModelGenerator,
        EnumProperty<Direction> facing,
        Block block,
        ResourceLocation subModelId
    ) {
        MultiVariant model = BlockModelGenerators.plainVariant(subModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(block)
                    .with(PropertyDispatch.initial(facing)
                        .select(Direction.NORTH, model.with(VariantMutator.X_ROT.withValue(Quadrant.R90)))
                        .select(Direction.EAST, model.with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                        .select(Direction.SOUTH, model.with(VariantMutator.X_ROT.withValue(Quadrant.R270)))
                        .select(Direction.WEST, model.with(VariantMutator.X_ROT.withValue(Quadrant.R90)).with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                        .select(Direction.UP, model)
                        .select(Direction.DOWN, model.with(VariantMutator.X_ROT.withValue(Quadrant.R180)))
                    )
            );
    }
}

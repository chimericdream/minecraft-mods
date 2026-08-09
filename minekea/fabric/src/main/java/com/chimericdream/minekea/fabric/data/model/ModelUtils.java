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
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.Optional;

public class ModelUtils {
    public static final ModelTemplate CUSTOM_TEMPLATE_LANTERN = new CustomBlockStateModelSupplier.CustomBlockModel(
        BlockConfig.RenderType.CUTOUT,
        Optional.of(Identifier.withDefaultNamespace("block/template_lantern")),
        Optional.empty(),
        TextureSlot.LANTERN
    );

    public static final ModelTemplate CUSTOM_TEMPLATE_HANGING_LANTERN = new CustomBlockStateModelSupplier.CustomBlockModel(
        BlockConfig.RenderType.CUTOUT,
        Optional.of(Identifier.withDefaultNamespace("block/template_hanging_lantern")),
        Optional.empty(),
        TextureSlot.LANTERN
    );

    public static void registerGeneratedItem(ItemModelGenerators itemModelGenerator, Block block) {
        itemModelGenerator.generateFlatItem(block.asItem(), ModelTemplates.FLAT_ITEM);
    }

    public static void registerVerticalSlabBlock(
        BlockModelGenerators blockStateModelGenerator,
        VerticalSlabBlock block,
        TextureMapping textures,
        ModelTemplate model
    ) {
        Identifier modelId = blockStateModelGenerator.createSuffixedVariant(block, "", model, unused -> textures);

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

    public static void registerVerticalStairsBlock(
        BlockModelGenerators blockStateModelGenerator,
        VerticalStairsBlock block,
        TextureMapping textures,
        ModelTemplate model
    ) {
        Identifier modelId = blockStateModelGenerator.createSuffixedVariant(block, "", model, unused -> textures);
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

    public static void registerLanternBlock(
        BlockModelGenerators blockStateModelGenerator,
        Block block,
        Identifier blockId
    ) {
        TextureMapping textures = new TextureMapping()
            .put(TextureSlot.LANTERN, new Material(blockId.withPrefix("block/")));

        Identifier baseModelId = blockStateModelGenerator.createSuffixedVariant(block, "_base", CUSTOM_TEMPLATE_LANTERN, unused -> textures);
        Identifier hangingModelId = blockStateModelGenerator.createSuffixedVariant(block, "_hanging", CUSTOM_TEMPLATE_HANGING_LANTERN, unused -> textures);

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
        Identifier subModelId
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
        Identifier subModelId
    ) {
        registerBlockWithHorizontalFacing(blockStateModelGenerator, wallSide, block, subModelId);
    }

    public static void registerBlockWithHorizontalFacing(
        BlockModelGenerators blockStateModelGenerator,
        EnumProperty<Direction> facing,
        Block block,
        Identifier subModelId
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
        Identifier subModelId
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

package com.chimericdream.lib.fabric.blocks.model;

import com.chimericdream.lib.blocks.BlockConfig;
import com.mojang.math.Quadrant;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Optional;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Vanilla-block-shaped datagen helpers: rotation/facing dispatch and other model patterns that
 * apply to any block of that shape, regardless of mod content.
 */
public class ModelUtils {
    public static final CustomBlockModel CUSTOM_TEMPLATE_LANTERN = new CustomBlockModel(
        BlockConfig.RenderType.CUTOUT,
        Optional.of(Identifier.withDefaultNamespace("block/template_lantern")),
        Optional.empty(),
        TextureSlot.LANTERN
    );

    public static final CustomBlockModel CUSTOM_TEMPLATE_HANGING_LANTERN = new CustomBlockModel(
        BlockConfig.RenderType.CUTOUT,
        Optional.of(Identifier.withDefaultNamespace("block/template_hanging_lantern")),
        Optional.empty(),
        TextureSlot.LANTERN
    );

    private static final CustomCropModel CUSTOM_CROP = new CustomCropModel();

    public static void registerGeneratedItem(ItemModelGenerators itemModelGenerator, Block block) {
        itemModelGenerator.generateFlatItem(block.asItem(), ModelTemplates.FLAT_ITEM);
    }

    /** The fallback placeholder cube (bedrock-textured) datagen uses when a variant has no real model yet. */
    public static MultiVariant makeInvalidVariant(BlockModelGenerators blockStateModelGenerator, Block block) {
        Identifier invalidModelId = blockStateModelGenerator.createSuffixedVariant(block, "_invalid", ModelTemplates.CUBE_ALL,
            unused -> TextureMapping.singleSlot(TextureSlot.ALL, TextureMapping.getBlockTexture(Blocks.BEDROCK)));

        return BlockModelGenerators.plainVariant(invalidModelId);
    }

    public static void registerCrop(BlockModelGenerators generator, Block crop, Property<Integer> ageProperty, int... ageTextureIndices) {
        if (ageProperty.getAllValues().count() != ageTextureIndices.length) {
            throw new IllegalArgumentException();
        }

        Int2ObjectMap<Identifier> int2ObjectMap = new Int2ObjectOpenHashMap<>();
        PropertyDispatch<MultiVariant> blockStateVariantMap = PropertyDispatch.initial(ageProperty).generate((integer) -> {
            int i = ageTextureIndices[integer];

            Identifier identifier = int2ObjectMap.computeIfAbsent(i, (j) -> generator.createSuffixedVariant(crop, "_stage" + i, CUSTOM_CROP, TextureMapping::crop));

            return new MultiVariant(WeightedList.of(new Variant(identifier)));
        });

        generator.registerSimpleFlatItemModel(crop.asItem());
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(crop).with(blockStateVariantMap));
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

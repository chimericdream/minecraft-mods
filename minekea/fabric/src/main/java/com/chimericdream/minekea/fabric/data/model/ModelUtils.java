package com.chimericdream.minekea.fabric.data.model;

import com.chimericdream.minekea.block.building.slabs.VerticalSlabBlock;
import com.chimericdream.minekea.block.building.stairs.VerticalStairsBlock;
import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

public class ModelUtils {
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
}

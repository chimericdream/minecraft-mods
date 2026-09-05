package com.chimericdream.lib.fabric.blocks.family;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.SlabType;

/**
 * Vanilla-datagen-API glue shared by the family variant generators. Ported from Minekea's
 * {@code ModelUtils}, which was already generic — nothing here is Minekea-specific.
 */
public class FamilyBlockModels {
    public static void registerStairsBlock(
        BlockModelGenerators blockStateModelGenerator,
        StairBlock block,
        TextureMapping textures
    ) {
        registerStairsBlock(
            blockStateModelGenerator,
            block,
            textures,
            ModelTemplates.STAIRS_INNER,
            ModelTemplates.STAIRS_STRAIGHT,
            ModelTemplates.STAIRS_OUTER
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
        Identifier innerModelId = blockStateModelGenerator.createSuffixedVariant(block, "_inner", innerModel, unused -> textures);
        Identifier straightModelId = blockStateModelGenerator.createSuffixedVariant(block, "", straightModel, unused -> textures);
        Identifier outerModelId = blockStateModelGenerator.createSuffixedVariant(block, "_outer", outerModel, unused -> textures);

        MultiVariant innerVariant = BlockModelGenerators.plainVariant(innerModelId);
        MultiVariant straightVariant = BlockModelGenerators.plainVariant(straightModelId);
        MultiVariant outerVariant = BlockModelGenerators.plainVariant(outerModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(BlockModelGenerators.createStairs(block, innerVariant, straightVariant, outerVariant));
    }

    public static void registerSlabBlock(
        BlockModelGenerators blockStateModelGenerator,
        SlabBlock block,
        TextureMapping textures,
        ModelTemplate bottomModel,
        ModelTemplate topModel,
        ModelTemplate doubleModel
    ) {
        Identifier bottomModelId = blockStateModelGenerator.createSuffixedVariant(block, "", bottomModel, unused -> textures);
        Identifier topModelId = blockStateModelGenerator.createSuffixedVariant(block, "_top", topModel, unused -> textures);
        Identifier doubleModelId = blockStateModelGenerator.createSuffixedVariant(block, "_double", doubleModel, unused -> textures);

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

    public static void registerWallBlock(
        BlockModelGenerators blockStateModelGenerator,
        WallBlock block,
        TextureMapping textures,
        ModelTemplate inventoryModel,
        ModelTemplate postModel,
        ModelTemplate sideModel,
        ModelTemplate sideTallModel
    ) {
        Identifier inventoryModelId = blockStateModelGenerator.createSuffixedVariant(block, "", inventoryModel, unused -> textures);
        Identifier postModelId = blockStateModelGenerator.createSuffixedVariant(block, "", postModel, unused -> textures);
        Identifier sideModelId = blockStateModelGenerator.createSuffixedVariant(block, "", sideModel, unused -> textures);
        Identifier sideTallModelId = blockStateModelGenerator.createSuffixedVariant(block, "", sideTallModel, unused -> textures);

        MultiVariant postVariant = BlockModelGenerators.plainVariant(postModelId);
        MultiVariant sideVariant = BlockModelGenerators.plainVariant(sideModelId);
        MultiVariant sideTallVariant = BlockModelGenerators.plainVariant(sideTallModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(BlockModelGenerators.createWall(block, postVariant, sideVariant, sideTallVariant));
        blockStateModelGenerator.registerSimpleItemModel(block, inventoryModelId);
    }
}

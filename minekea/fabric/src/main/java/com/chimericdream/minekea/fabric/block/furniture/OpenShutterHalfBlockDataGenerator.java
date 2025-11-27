package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.shutters.OpenShutterHalfBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.minecraft.block.Block;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;

import java.util.Optional;
import java.util.function.Function;

public class OpenShutterHalfBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final Model LEFT_HALF_MODEL = makeModel("block/furniture/shutters/left_half");
    protected static final Model RIGHT_HALF_MODEL = makeModel("block/furniture/shutters/right_half");

    protected final OpenShutterHalfBlock BLOCK;

    public OpenShutterHalfBlockDataGenerator(Block block) {
        BLOCK = (OpenShutterHalfBlock) block;
    }

    protected static Model makeModel(String path) {
        return new Model(
            Optional.of(Identifier.of(ModInfo.MOD_ID, path)),
            Optional.empty(),
            MinekeaTextures.PANEL,
            MinekeaTextures.FRAME
        );
    }

    @Override
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        TextureMap textures = new TextureMap()
            .put(MinekeaTextures.FRAME, Registries.BLOCK.getId(logIngredient).withPrefixedPath("block/"))
            .put(MinekeaTextures.PANEL, Registries.BLOCK.getId(plankIngredient).withPrefixedPath("block/"));

        Identifier leftHalfModelId = blockStateModelGenerator.createSubModel(BLOCK, "_left_half", LEFT_HALF_MODEL, unused -> textures);
        Identifier rightHalfModelId = blockStateModelGenerator.createSubModel(BLOCK, "_right_half", RIGHT_HALF_MODEL, unused -> textures);

        WeightedVariant leftHalfModel = BlockStateModelGenerator.createWeightedVariant(leftHalfModelId);
        WeightedVariant rightHalfModel = BlockStateModelGenerator.createWeightedVariant(rightHalfModelId);

        blockStateModelGenerator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(BLOCK)
                    .with(
                        BlockStateVariantMap.models(OpenShutterHalfBlock.WALL_SIDE, OpenShutterHalfBlock.HALF)
                            .register(Direction.NORTH, OpenShutterHalfBlock.ShutterHalf.LEFT, rightHalfModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180)))
                            .register(Direction.SOUTH, OpenShutterHalfBlock.ShutterHalf.LEFT, rightHalfModel)
                            .register(Direction.EAST, OpenShutterHalfBlock.ShutterHalf.LEFT, rightHalfModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270)))
                            .register(Direction.WEST, OpenShutterHalfBlock.ShutterHalf.LEFT, rightHalfModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)))
                            .register(Direction.NORTH, OpenShutterHalfBlock.ShutterHalf.RIGHT, leftHalfModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180)))
                            .register(Direction.SOUTH, OpenShutterHalfBlock.ShutterHalf.RIGHT, leftHalfModel)
                            .register(Direction.EAST, OpenShutterHalfBlock.ShutterHalf.RIGHT, leftHalfModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270)))
                            .register(Direction.WEST, OpenShutterHalfBlock.ShutterHalf.RIGHT, leftHalfModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)))
                    )
            );
    }
}

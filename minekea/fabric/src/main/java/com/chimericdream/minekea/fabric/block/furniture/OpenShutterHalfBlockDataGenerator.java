package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.shutters.OpenShutterHalfBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Function;

public class OpenShutterHalfBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final ModelTemplate LEFT_HALF_MODEL = makeModel("block/furniture/shutters/left_half");
    protected static final ModelTemplate RIGHT_HALF_MODEL = makeModel("block/furniture/shutters/right_half");

    protected final OpenShutterHalfBlock BLOCK;

    public OpenShutterHalfBlockDataGenerator(Block block) {
        BLOCK = (OpenShutterHalfBlock) block;
    }

    protected static ModelTemplate makeModel(String path) {
        return new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, path)),
            Optional.empty(),
            MinekeaTextures.PANEL,
            MinekeaTextures.FRAME
        );
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK.builtInRegistryHolder().key());
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.FRAME, new Material(BuiltInRegistries.BLOCK.getKey(logIngredient).withPrefix("block/")))
            .put(MinekeaTextures.PANEL, new Material(BuiltInRegistries.BLOCK.getKey(plankIngredient).withPrefix("block/")));

        Identifier leftHalfModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_left_half", LEFT_HALF_MODEL, unused -> textures);
        Identifier rightHalfModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_right_half", RIGHT_HALF_MODEL, unused -> textures);

        MultiVariant leftHalfModel = BlockModelGenerators.plainVariant(leftHalfModelId);
        MultiVariant rightHalfModel = BlockModelGenerators.plainVariant(rightHalfModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(BLOCK)
                    .with(
                        PropertyDispatch.initial(OpenShutterHalfBlock.WALL_SIDE, OpenShutterHalfBlock.HALF)
                            .select(Direction.NORTH, OpenShutterHalfBlock.ShutterHalf.LEFT, rightHalfModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                            .select(Direction.SOUTH, OpenShutterHalfBlock.ShutterHalf.LEFT, rightHalfModel)
                            .select(Direction.EAST, OpenShutterHalfBlock.ShutterHalf.LEFT, rightHalfModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                            .select(Direction.WEST, OpenShutterHalfBlock.ShutterHalf.LEFT, rightHalfModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                            .select(Direction.NORTH, OpenShutterHalfBlock.ShutterHalf.RIGHT, leftHalfModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                            .select(Direction.SOUTH, OpenShutterHalfBlock.ShutterHalf.RIGHT, leftHalfModel)
                            .select(Direction.EAST, OpenShutterHalfBlock.ShutterHalf.RIGHT, leftHalfModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                            .select(Direction.WEST, OpenShutterHalfBlock.ShutterHalf.RIGHT, leftHalfModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                    )
            );
    }
}

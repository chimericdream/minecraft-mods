package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.shutters.ShutterBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;

import java.util.Optional;
import java.util.function.Function;

public class ShutterBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final Model CLOSED_MODEL = makeModel("block/furniture/shutters/closed");
    protected static final Model OPEN_MODEL = makeModel("block/furniture/shutters/open");

    protected final ShutterBlock BLOCK;

    public ShutterBlockDataGenerator(Block block) {
        BLOCK = (ShutterBlock) block;
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
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 6)
            .pattern("#X#")
            .pattern("#X#")
            .pattern("#X#")
            .input('X', plankIngredient)
            .input('#', logIngredient)
            .criterion(RecipeGenerator.hasItem(plankIngredient),
                generator.conditionsFromItem(plankIngredient))
            .criterion(RecipeGenerator.hasItem(logIngredient),
                generator.conditionsFromItem(logIngredient))
            .offerTo(exporter);
    }

    @Override
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Shutters", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Shutters", BLOCK.config.getMaterialName()));
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
            .put(MinekeaTextures.FRAME, Registries.BLOCK.getId(logIngredient).withPrefixedPath("block/"))
            .put(MinekeaTextures.PANEL, Registries.BLOCK.getId(plankIngredient).withPrefixedPath("block/"));

        Identifier closedModelId = blockStateModelGenerator.createSubModel(BLOCK, "", CLOSED_MODEL, unused -> textures);
        Identifier openModelId = blockStateModelGenerator.createSubModel(BLOCK, "_open", OPEN_MODEL, unused -> textures);

        WeightedVariant closedModel = BlockStateModelGenerator.createWeightedVariant(closedModelId);
        WeightedVariant openModel = BlockStateModelGenerator.createWeightedVariant(openModelId);

        blockStateModelGenerator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(BLOCK)
                    .with(
                        BlockStateVariantMap.models(ShutterBlock.OPEN, ShutterBlock.WALL_SIDE)
                            .register(false, Direction.NORTH, closedModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180)))
                            .register(false, Direction.SOUTH, closedModel)
                            .register(false, Direction.EAST, closedModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270)))
                            .register(false, Direction.WEST, closedModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)))
                            .register(true, Direction.NORTH, openModel)
                            .register(true, Direction.SOUTH, openModel)
                            .register(true, Direction.EAST, openModel)
                            .register(true, Direction.WEST, openModel)
                    )
            );
    }
}

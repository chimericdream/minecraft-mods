package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.shutters.ShutterBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
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
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Function;

public class ShutterBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final ModelTemplate CLOSED_MODEL = makeModel("block/furniture/shutters/closed");
    protected static final ModelTemplate OPEN_MODEL = makeModel("block/furniture/shutters/open");

    protected final ShutterBlock BLOCK;

    public ShutterBlockDataGenerator(Block block) {
        BLOCK = (ShutterBlock) block;
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
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 6)
            .pattern("#X#")
            .pattern("#X#")
            .pattern("#X#")
            .define('X', plankIngredient)
            .define('#', logIngredient)
            .unlockedBy(RecipeProvider.getHasName(plankIngredient),
                generator.has(plankIngredient))
            .unlockedBy(RecipeProvider.getHasName(logIngredient),
                generator.has(logIngredient))
            .save(exporter);
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK.builtInRegistryHolder().key());
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Shutters", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Shutters", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.FRAME, new Material(BuiltInRegistries.BLOCK.getKey(logIngredient).withPrefix("block/")))
            .put(MinekeaTextures.PANEL, new Material(BuiltInRegistries.BLOCK.getKey(plankIngredient).withPrefix("block/")));

        Identifier closedModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", CLOSED_MODEL, unused -> textures);
        Identifier openModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_open", OPEN_MODEL, unused -> textures);

        MultiVariant closedModel = BlockModelGenerators.plainVariant(closedModelId);
        MultiVariant openModel = BlockModelGenerators.plainVariant(openModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(BLOCK)
                    .with(
                        PropertyDispatch.initial(ShutterBlock.OPEN, ShutterBlock.WALL_SIDE)
                            .select(false, Direction.NORTH, closedModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                            .select(false, Direction.SOUTH, closedModel)
                            .select(false, Direction.EAST, closedModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                            .select(false, Direction.WEST, closedModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                            .select(true, Direction.NORTH, openModel)
                            .select(true, Direction.SOUTH, openModel)
                            .select(true, Direction.EAST, openModel)
                            .select(true, Direction.WEST, openModel)
                    )
            );
    }
}

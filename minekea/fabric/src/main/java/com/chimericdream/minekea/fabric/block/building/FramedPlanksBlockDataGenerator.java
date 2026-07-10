package com.chimericdream.minekea.fabric.block.building;

import com.chimericdream.lib.resource.TextureUtils;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.framed.FramedPlanksBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import com.chimericdream.minekea.tag.MinekeaBlockTags;
import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.HolderLookup;
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

public class FramedPlanksBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final ModelTemplate CORE_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/building/framed_planks/core")),
        Optional.empty(),
        MinekeaTextures.BRACE,
        MinekeaTextures.MATERIAL
    );
    protected static final ModelTemplate A_CONNECTED_MODEL = makeModel("a");
    protected static final ModelTemplate B_CONNECTED_MODEL = makeModel("b");
    protected static final ModelTemplate AB_CONNECTED_MODEL = makeModel("ab");

    public FramedPlanksBlock BLOCK;

    public FramedPlanksBlockDataGenerator(Block block) {
        BLOCK = (FramedPlanksBlock) block;
    }

    protected static ModelTemplate makeModel(String direction) {
        return new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("block/building/framed_planks/%s_connected", direction))),
            Optional.empty(),
            MinekeaTextures.BRACE,
            MinekeaTextures.MATERIAL
        );
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block, Block>> getBuilder) {
        getBuilder.apply(MinekeaBlockTags.FRAMED_PLANKS)
            .setReplace(false)
            .add(BLOCK);

        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        generator.shapeless(RecipeCategory.BUILDING_BLOCKS, BLOCK, 6)
            .requires(plankIngredient)
            .requires(plankIngredient)
            .requires(plankIngredient)
            .requires(plankIngredient)
            .requires(logIngredient)
            .unlockedBy(RecipeProvider.getHasName(plankIngredient),
                generator.has(plankIngredient))
            .unlockedBy(RecipeProvider.getHasName(logIngredient),
                generator.has(logIngredient))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("Framed %s Planks", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("Framed %s Planks", BLOCK.config.getMaterialName()));
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
            .put(MinekeaTextures.MATERIAL, TextureUtils.block(plankIngredient))
            .put(MinekeaTextures.BRACE, TextureUtils.block(logIngredient));

        Identifier modelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", CORE_MODEL, unused -> textures);
        Identifier aConnectedModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_a_connected", A_CONNECTED_MODEL, unused -> textures);
        Identifier bConnectedModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_b_connected", B_CONNECTED_MODEL, unused -> textures);
        Identifier abConnectedModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_ab_connected", AB_CONNECTED_MODEL, unused -> textures);

        MultiVariant coreVariant = BlockModelGenerators.plainVariant(modelId);
        MultiVariant aConnectedVariant = BlockModelGenerators.plainVariant(aConnectedModelId);
        MultiVariant bConnectedVariant = BlockModelGenerators.plainVariant(bConnectedModelId);
        MultiVariant abConnectedVariant = BlockModelGenerators.plainVariant(abConnectedModelId);

        MultiVariant invalidVariant = makeInvalidVariant(blockStateModelGenerator, BLOCK);

        blockStateModelGenerator.registerSimpleItemModel(BLOCK, modelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(BLOCK).with(
                    PropertyDispatch
                        .initial(
                            FramedPlanksBlock.CONNECTED_EAST,
                            FramedPlanksBlock.CONNECTED_WEST,
                            FramedPlanksBlock.CONNECTED_NORTH,
                            FramedPlanksBlock.CONNECTED_SOUTH
                        )
                        .select(false, false, false, false, coreVariant)
                        .select(true, false, false, false, aConnectedVariant)
                        .select(false, true, false, false, bConnectedVariant)
                        .select(false, false, true, false, aConnectedVariant.with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                        .select(false, false, false, true, bConnectedVariant.with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))

                        .select(true, true, false, false, abConnectedVariant)
                        .select(true, false, true, false, invalidVariant)
                        .select(true, false, false, true, invalidVariant)
                        .select(false, true, true, false, invalidVariant)
                        .select(false, true, false, true, invalidVariant)
                        .select(false, false, true, true, abConnectedVariant.with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))

                        .select(true, true, true, false, invalidVariant)
                        .select(true, true, false, true, invalidVariant)
                        .select(true, false, true, true, invalidVariant)
                        .select(false, true, true, true, invalidVariant)

                        .select(true, true, true, true, invalidVariant)
                )
            );
    }
}

package com.chimericdream.minekea.fabric.block.containers;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.containers.crates.CrateBlock;
import com.chimericdream.minekea.client.screen.crate.CrateScreenHandler;
import com.chimericdream.minekea.client.screen.crate.DoubleCrateScreenHandler;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.Models;
import net.minecraft.client.data.TextureKey;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AxisRotation;

import java.util.Optional;
import java.util.function.Function;

public class CrateBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final Model CRATE_MODEL = makeModel("block/containers/crate");
    protected static final Model HALF_DOUBLE_CRATE_MODEL = makeModel("block/containers/double_crate_half");

    protected final CrateBlock BLOCK;

    public CrateBlockDataGenerator(Block block) {
        BLOCK = (CrateBlock) block;
    }

    protected static Model makeModel(String path) {
        return new Model(
            Optional.of(Identifier.of(ModInfo.MOD_ID, path)),
            Optional.empty(),
            MinekeaTextures.BRACE,
            MinekeaTextures.MATERIAL
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
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Block ingredient1 = BLOCK.config.getIngredient();
        TagKey<Item> ingredient2 = BLOCK.config.getTagIngredient();

        generator.createShaped(RecipeCategory.MISC, BLOCK, 1)
            .pattern("#X#")
            .pattern("XXX")
            .pattern("#X#")
            .input('X', ingredient1)
            .input('#', ingredient2)
            .criterion(RecipeGenerator.hasItem(ingredient1),
                generator.conditionsFromItem(ingredient1))
            .criterion("has_log",
                generator.conditionsFromTag(ingredient2))
            .offerTo(exporter);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Crate", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Crate", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator, RegistryWrapper.WrapperLookup registryLookup) {
        generator.addDrop(BLOCK);
    }

    protected void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator, TextureMap textures) {
        Identifier baseModelId = blockStateModelGenerator.createSubModel(BLOCK, "", CRATE_MODEL, unused -> textures);
        Identifier halfModelId = blockStateModelGenerator.createSubModel(BLOCK, "_double_half", HALF_DOUBLE_CRATE_MODEL, unused -> textures);

        WeightedVariant baseModel = BlockStateModelGenerator.createWeightedVariant(baseModelId);
        WeightedVariant halfModel = BlockStateModelGenerator.createWeightedVariant(halfModelId);

        WeightedVariant brokenModel = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_ALL.upload(BLOCK, "_broken", TextureMap.of(TextureKey.ALL, Identifier.of("")), blockStateModelGenerator.modelCollector));

        blockStateModelGenerator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(BLOCK)
                    .with(
                        BlockStateVariantMap
                            .models(CrateBlock.CONNECTED_NORTH, CrateBlock.CONNECTED_EAST, CrateBlock.CONNECTED_SOUTH, CrateBlock.CONNECTED_WEST)

                            .register(false, false, false, false, baseModel)
                            .register(true, false, false, false, halfModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90)))
                            .register(false, true, false, false, halfModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180)))
                            .register(false, false, true, false, halfModel.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270)))
                            .register(false, false, false, true, halfModel)

                            .register(true, true, false, false, brokenModel)
                            .register(true, false, true, false, brokenModel)
                            .register(true, false, false, true, brokenModel)
                            .register(false, true, true, false, brokenModel)
                            .register(false, true, false, true, brokenModel)
                            .register(false, false, true, true, brokenModel)

                            .register(true, true, true, false, brokenModel)
                            .register(true, true, false, true, brokenModel)
                            .register(true, false, true, true, brokenModel)
                            .register(false, true, true, true, brokenModel)

                            .register(true, true, true, true, brokenModel)
                    )
            );
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        TextureMap textures = new TextureMap()
            .put(MinekeaTextures.BRACE, BLOCK.config.getTexture("brace"))
            .put(MinekeaTextures.MATERIAL, BLOCK.config.getTexture());

        this.configureBlockStateModels(blockStateModelGenerator, textures);
    }

    public static class SharedCrateDataGenerator extends ChimericLibBlockDataGenerator {
        @Override
        public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
            translationBuilder.add(CrateScreenHandler.SCREEN_ID, "Crate");
            translationBuilder.add(DoubleCrateScreenHandler.SCREEN_ID, "Large Crate");
            translationBuilder.add(CrateScreenHandler.TRAPPED_SCREEN_ID, "Trapped Crate");
            translationBuilder.add(DoubleCrateScreenHandler.TRAPPED_SCREEN_ID, "Trapped Large Crate");
        }
    }
}

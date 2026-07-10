package com.chimericdream.minekea.fabric.block.containers;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.containers.crates.CrateBlock;
import com.chimericdream.minekea.client.screen.crate.CrateScreenHandler;
import com.chimericdream.minekea.client.screen.crate.DoubleCrateScreenHandler;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Function;

public class CrateBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final ModelTemplate CRATE_MODEL = makeModel("block/containers/crate");
    protected static final ModelTemplate HALF_DOUBLE_CRATE_MODEL = makeModel("block/containers/double_crate_half");

    protected final CrateBlock BLOCK;

    public CrateBlockDataGenerator(Block block) {
        BLOCK = (CrateBlock) block;
    }

    protected static ModelTemplate makeModel(String path) {
        return new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, path)),
            Optional.empty(),
            MinekeaTextures.BRACE,
            MinekeaTextures.MATERIAL
        );
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block ingredient1 = BLOCK.config.getIngredient();
        TagKey<Item> ingredient2 = BLOCK.config.getTagIngredient();

        generator.shaped(RecipeCategory.MISC, BLOCK, 1)
            .pattern("#X#")
            .pattern("XXX")
            .pattern("#X#")
            .define('X', ingredient1)
            .define('#', ingredient2)
            .unlockedBy(RecipeProvider.getHasName(ingredient1), generator.has(ingredient1))
            .unlockedBy("has_log", generator.has(ingredient2))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Crate", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Crate", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    protected void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator, TextureMapping textures) {
        Identifier baseModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", CRATE_MODEL, unused -> textures);
        Identifier halfModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_double_half", HALF_DOUBLE_CRATE_MODEL, unused -> textures);

        MultiVariant baseModel = BlockModelGenerators.plainVariant(baseModelId);
        MultiVariant halfModel = BlockModelGenerators.plainVariant(halfModelId);

        MultiVariant brokenModel = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_ALL.createWithSuffix(BLOCK, "_broken", TextureMapping.singleSlot(TextureSlot.ALL, Identifier.withDefaultNamespace("")), blockStateModelGenerator.modelOutput));

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(BLOCK)
                    .with(
                        PropertyDispatch
                            .initial(CrateBlock.CONNECTED_NORTH, CrateBlock.CONNECTED_EAST, CrateBlock.CONNECTED_SOUTH, CrateBlock.CONNECTED_WEST)

                            .select(false, false, false, false, baseModel)
                            .select(true, false, false, false, halfModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                            .select(false, true, false, false, halfModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                            .select(false, false, true, false, halfModel.with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                            .select(false, false, false, true, halfModel)

                            .select(true, true, false, false, brokenModel)
                            .select(true, false, true, false, brokenModel)
                            .select(true, false, false, true, brokenModel)
                            .select(false, true, true, false, brokenModel)
                            .select(false, true, false, true, brokenModel)
                            .select(false, false, true, true, brokenModel)

                            .select(true, true, true, false, brokenModel)
                            .select(true, true, false, true, brokenModel)
                            .select(true, false, true, true, brokenModel)
                            .select(false, true, true, true, brokenModel)

                            .select(true, true, true, true, brokenModel)
                    )
            );
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.BRACE, BLOCK.config.getTexture("brace"))
            .put(MinekeaTextures.MATERIAL, BLOCK.config.getTexture());

        this.configureBlockStateModels(blockStateModelGenerator, textures);
    }

    public static class SharedCrateDataGenerator extends ChimericLibBlockDataGenerator {
        @Override
        public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
            translationBuilder.add(CrateScreenHandler.SCREEN_ID, "Crate");
            translationBuilder.add(DoubleCrateScreenHandler.SCREEN_ID, "Large Crate");
            translationBuilder.add(CrateScreenHandler.TRAPPED_SCREEN_ID, "Trapped Crate");
            translationBuilder.add(DoubleCrateScreenHandler.TRAPPED_SCREEN_ID, "Trapped Large Crate");
        }
    }
}

package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.seats.ChairBlock;
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

public class ChairBlockDataGenerator extends ChimericLibBlockDataGenerator {
    private final ChairBlock BLOCK;

    protected static final ModelTemplate CHAIR_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/seating/chair")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );

    public ChairBlockDataGenerator(Block block) {
        this.BLOCK = (ChairBlock) block;
    }

    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 2)
            .pattern("P ")
            .pattern("PP")
            .pattern("LL")
            .define('P', plankIngredient)
            .define('L', logIngredient)
            .unlockedBy(RecipeProvider.getHasName(plankIngredient),
                generator.has(plankIngredient))
            .unlockedBy(RecipeProvider.getHasName(logIngredient),
                generator.has(logIngredient))
            .save(exporter);
    }

    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK.builtInRegistryHolder().key());
    }

    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Chair", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Chair", BLOCK.config.getMaterialName()));
    }

    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.LOG, new Material(BuiltInRegistries.BLOCK.getKey(logIngredient).withPrefix("block/")))
            .put(MinekeaTextures.PLANKS, new Material(BuiltInRegistries.BLOCK.getKey(plankIngredient).withPrefix("block/")));

        Identifier modelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "", CHAIR_MODEL, unused -> textures);

        MultiVariant model = BlockModelGenerators.plainVariant(modelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(BLOCK)
                    .with(
                        PropertyDispatch.initial(ChairBlock.FACING)
                            .select(Direction.NORTH, model.with(VariantMutator.Y_ROT.withValue(Quadrant.R90)))
                            .select(Direction.EAST, model.with(VariantMutator.Y_ROT.withValue(Quadrant.R180)))
                            .select(Direction.SOUTH, model.with(VariantMutator.Y_ROT.withValue(Quadrant.R270)))
                            .select(Direction.WEST, model)
                    )
            );
    }
}

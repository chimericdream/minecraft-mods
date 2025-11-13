package com.chimericdream.minekea.fabric.block.building;

import com.chimericdream.lib.resource.TextureUtils;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.framed.FramedPlanksBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import com.chimericdream.minekea.tag.MinekeaBlockTags;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureMap;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Optional;
import java.util.function.Function;

public class FramedPlanksBlockDataGenerator implements ChimericLibBlockDataGenerator {
    protected static final Model CORE_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/building/framed_planks/core")),
        Optional.empty(),
        MinekeaTextures.BRACE,
        MinekeaTextures.MATERIAL
    );
    protected static final Model A_CONNECTED_MODEL = makeModel("a");
    protected static final Model B_CONNECTED_MODEL = makeModel("b");
    protected static final Model AB_CONNECTED_MODEL = makeModel("ab");

    public FramedPlanksBlock BLOCK;

    public FramedPlanksBlockDataGenerator(Block block) {
        BLOCK = (FramedPlanksBlock) block;
    }

    protected static Model makeModel(String direction) {
        return new Model(
            Optional.of(Identifier.of(ModInfo.MOD_ID, String.format("block/building/framed_planks/%s_connected", direction))),
            Optional.empty(),
            MinekeaTextures.BRACE,
            MinekeaTextures.MATERIAL
        );
    }

    @Override
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        getBuilder.apply(MinekeaBlockTags.FRAMED_PLANKS)
            .setReplace(false)
            .add(BLOCK);

        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureItemTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Item>, ProvidedTagBuilder<Item, Item>> getBuilder) {

    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        generator.createShapeless(RecipeCategory.BUILDING_BLOCKS, BLOCK, 6)
            .input(plankIngredient)
            .input(plankIngredient)
            .input(plankIngredient)
            .input(plankIngredient)
            .input(logIngredient)
            .criterion(RecipeGenerator.hasItem(plankIngredient),
                generator.conditionsFromItem(plankIngredient))
            .criterion(RecipeGenerator.hasItem(logIngredient),
                generator.conditionsFromItem(logIngredient))
            .offerTo(exporter);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("Framed %s Planks", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient();
        Block logIngredient = BLOCK.config.getIngredient("log");

        TextureMap textures = new TextureMap()
            .put(MinekeaTextures.MATERIAL, TextureUtils.block(plankIngredient))
            .put(MinekeaTextures.BRACE, TextureUtils.block(logIngredient));

        Identifier modelId = blockStateModelGenerator.createSubModel(BLOCK, "", CORE_MODEL, unused -> textures);
        Identifier aConnectedModelId = blockStateModelGenerator.createSubModel(BLOCK, "_a_connected", A_CONNECTED_MODEL, unused -> textures);
        Identifier bConnectedModelId = blockStateModelGenerator.createSubModel(BLOCK, "_b_connected", B_CONNECTED_MODEL, unused -> textures);
        Identifier abConnectedModelId = blockStateModelGenerator.createSubModel(BLOCK, "_ab_connected", AB_CONNECTED_MODEL, unused -> textures);

        blockStateModelGenerator.registerParentedItemModel(BLOCK, modelId);

        blockStateModelGenerator.blockStateCollector
            .accept(
                MultipartBlockStateSupplier.create(BLOCK)
                    .with(
                        When.create()
                            .set(FramedPlanksBlock.FACING, Direction.NORTH)
                            .set(FramedPlanksBlock.CONNECTED_EAST, false)
                            .set(FramedPlanksBlock.CONNECTED_WEST, false),
                        BlockStateVariant.create()
                            .put(VariantSettings.MODEL, modelId)
                    )
                    .with(
                        When.create()
                            .set(FramedPlanksBlock.FACING, Direction.NORTH)
                            .set(FramedPlanksBlock.CONNECTED_EAST, true)
                            .set(FramedPlanksBlock.CONNECTED_WEST, false),
                        BlockStateVariant.create()
                            .put(VariantSettings.MODEL, aConnectedModelId)
                    )
                    .with(
                        When.create()
                            .set(FramedPlanksBlock.FACING, Direction.NORTH)
                            .set(FramedPlanksBlock.CONNECTED_EAST, false)
                            .set(FramedPlanksBlock.CONNECTED_WEST, true),
                        BlockStateVariant.create()
                            .put(VariantSettings.MODEL, bConnectedModelId)
                    )
                    .with(
                        When.create()
                            .set(FramedPlanksBlock.FACING, Direction.NORTH)
                            .set(FramedPlanksBlock.CONNECTED_EAST, true)
                            .set(FramedPlanksBlock.CONNECTED_WEST, true),
                        BlockStateVariant.create()
                            .put(VariantSettings.MODEL, abConnectedModelId)
                    )
                    .with(
                        When.create()
                            .set(FramedPlanksBlock.FACING, Direction.EAST)
                            .set(FramedPlanksBlock.CONNECTED_NORTH, false)
                            .set(FramedPlanksBlock.CONNECTED_SOUTH, false),
                        BlockStateVariant.create()
                            .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                            .put(VariantSettings.MODEL, modelId)
                    )
                    .with(
                        When.create()
                            .set(FramedPlanksBlock.FACING, Direction.EAST)
                            .set(FramedPlanksBlock.CONNECTED_NORTH, true)
                            .set(FramedPlanksBlock.CONNECTED_SOUTH, false),
                        BlockStateVariant.create()
                            .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                            .put(VariantSettings.MODEL, aConnectedModelId)
                    )
                    .with(
                        When.create()
                            .set(FramedPlanksBlock.FACING, Direction.EAST)
                            .set(FramedPlanksBlock.CONNECTED_NORTH, false)
                            .set(FramedPlanksBlock.CONNECTED_SOUTH, true),
                        BlockStateVariant.create()
                            .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                            .put(VariantSettings.MODEL, bConnectedModelId)
                    )
                    .with(
                        When.create()
                            .set(FramedPlanksBlock.FACING, Direction.EAST)
                            .set(FramedPlanksBlock.CONNECTED_NORTH, true)
                            .set(FramedPlanksBlock.CONNECTED_SOUTH, true),
                        BlockStateVariant.create()
                            .put(VariantSettings.Y, VariantSettings.Rotation.R270)
                            .put(VariantSettings.MODEL, abConnectedModelId)
                    )
            );
    }

    @Override
    public void configureItemModels(ItemModelGenerator itemModelGenerator) {

    }

    @Override
    public void generateTextures() {

    }
}

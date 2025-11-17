package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.trapdoors.BookshelfTrapdoorBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;

import java.util.Optional;
import java.util.function.Function;

public class BookshelfTrapdoorBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final Model BOTTOM_MODEL = makeModel("block/furniture/trapdoors/bookshelves/bottom");
    protected static final Model TOP_MODEL = makeModel("block/furniture/trapdoors/bookshelves/top");
    protected static final Model OPEN_MODEL = makeModel("block/furniture/trapdoors/bookshelves/open");

    protected final BookshelfTrapdoorBlock BLOCK;

    public BookshelfTrapdoorBlockDataGenerator(Block block) {
        BLOCK = (BookshelfTrapdoorBlock) block;
    }

    protected static Model makeModel(String path) {
        return new Model(
            Optional.of(Identifier.of(ModInfo.MOD_ID, path)),
            Optional.empty(),
            MinekeaTextures.MATERIAL,
            MinekeaTextures.SHELF
        );
    }

    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Block bookshelf = BLOCK.config.getIngredient();

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 12)
            .pattern("###")
            .pattern("###")
            .input('#', bookshelf)
            .criterion(RecipeGenerator.hasItem(bookshelf),
                generator.conditionsFromItem(bookshelf))
            .offerTo(exporter);
    }

    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Bookshelf Trapdoor", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Bookshelf Trapdoor", BLOCK.config.getMaterialName()));
    }

    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(BLOCK);
    }

    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient("planks");

        TextureMap textures = new TextureMap()
            .put(MinekeaTextures.MATERIAL, TextureMap.getId(plankIngredient))
            .put(MinekeaTextures.SHELF, Identifier.of(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf0"));

        Identifier bottomModelId = blockStateModelGenerator.createSubModel(BLOCK, "_bottom", BOTTOM_MODEL, unused -> textures);
        Identifier topModelId = blockStateModelGenerator.createSubModel(BLOCK, "_top", TOP_MODEL, unused -> textures);
        Identifier openModelId = blockStateModelGenerator.createSubModel(BLOCK, "_open", OPEN_MODEL, unused -> textures);

        WeightedVariant bottom = BlockStateModelGenerator.createWeightedVariant(bottomModelId);
        WeightedVariant top = BlockStateModelGenerator.createWeightedVariant(topModelId);
        WeightedVariant open = BlockStateModelGenerator.createWeightedVariant(openModelId);

        blockStateModelGenerator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(BLOCK)
                    .with(
                        BlockStateVariantMap
                            .models(BookshelfTrapdoorBlock.FACING, BookshelfTrapdoorBlock.HALF, BookshelfTrapdoorBlock.OPEN)
                            .register(
                                Direction.EAST, BlockHalf.BOTTOM, false,
                                bottom.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
                            )
                            .register(
                                Direction.NORTH, BlockHalf.BOTTOM, false,
                                bottom
                            )
                            .register(
                                Direction.SOUTH, BlockHalf.BOTTOM, false,
                                bottom.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180))
                            )
                            .register(
                                Direction.WEST, BlockHalf.BOTTOM, false,
                                bottom.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270))
                            )
                            .register(
                                Direction.EAST, BlockHalf.TOP, false,
                                top.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
                            )
                            .register(
                                Direction.NORTH, BlockHalf.TOP, false,
                                top
                            )
                            .register(
                                Direction.SOUTH, BlockHalf.TOP, false,
                                top.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180))
                            )
                            .register(
                                Direction.WEST, BlockHalf.TOP, false,
                                top.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270))
                            )
                            .register(
                                Direction.EAST, BlockHalf.BOTTOM, true,
                                open.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
                            )
                            .register(
                                Direction.NORTH, BlockHalf.BOTTOM, true,
                                open
                            )
                            .register(
                                Direction.SOUTH, BlockHalf.BOTTOM, true,
                                open.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180))
                            )
                            .register(
                                Direction.WEST, BlockHalf.BOTTOM, true,
                                open.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270))
                            )
                            .register(
                                Direction.EAST, BlockHalf.TOP, true,
                                open.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R180)).apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270))
                            )
                            .register(
                                Direction.NORTH, BlockHalf.TOP, true,
                                open.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R180)).apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180))
                            )
                            .register(
                                Direction.SOUTH, BlockHalf.TOP, true,
                                open.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R180))
                            )
                            .register(
                                Direction.WEST, BlockHalf.TOP, true,
                                open.apply(ModelVariantOperator.ROTATION_X.withValue(AxisRotation.R180)).apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
                            )
                    )
            );

        blockStateModelGenerator.registerParentedItemModel(BLOCK, bottomModelId);
    }
}

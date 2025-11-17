package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.doors.BookshelfDoorBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.ItemModelGenerator;
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

public class BookshelfDoorBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final Model ITEM_MODEL = makeModel("item/furniture/doors/bookshelf");
    protected static final Model BOTTOM_MODEL = makeModel("block/furniture/doors/bookshelves/bottom");
    protected static final Model BOTTOM_HINGE_MODEL = makeModel("block/furniture/doors/bookshelves/bottom_rh");
    protected static final Model TOP_MODEL = makeModel("block/furniture/doors/bookshelves/top");
    protected static final Model TOP_HINGE_MODEL = makeModel("block/furniture/doors/bookshelves/top_rh");

    protected final BookshelfDoorBlock BLOCK;

    public BookshelfDoorBlockDataGenerator(Block block) {
        BLOCK = (BookshelfDoorBlock) block;
    }

    protected static Model makeModel(String path) {
        return new Model(
            Optional.of(Identifier.of(ModInfo.MOD_ID, path)),
            Optional.empty(),
            MinekeaTextures.MATERIAL,
            MinekeaTextures.SHELF
        );
    }

    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Block bookshelf = BLOCK.config.getIngredient();

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 3)
            .pattern("##")
            .pattern("##")
            .pattern("##")
            .input('#', bookshelf)
            .criterion(RecipeGenerator.hasItem(bookshelf),
                generator.conditionsFromItem(bookshelf))
            .offerTo(exporter);
    }

    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.doorDrops(BLOCK);
    }

    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Bookshelf Door", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Bookshelf Door", BLOCK.config.getMaterialName()));
    }

    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient("planks");

        TextureMap textures = new TextureMap()
            .put(MinekeaTextures.MATERIAL, TextureMap.getId(plankIngredient))
            .put(MinekeaTextures.SHELF, Identifier.of(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf0"));

        Identifier bottomModelId = blockStateModelGenerator.createSubModel(BLOCK, "_bottom", BOTTOM_MODEL, unused -> textures);
        Identifier bottomHingeModelId = blockStateModelGenerator.createSubModel(BLOCK, "_bottom_rh", BOTTOM_HINGE_MODEL, unused -> textures);
        Identifier topModelId = blockStateModelGenerator.createSubModel(BLOCK, "_top", TOP_MODEL, unused -> textures);
        Identifier topHingeModelId = blockStateModelGenerator.createSubModel(BLOCK, "_top_rh", TOP_HINGE_MODEL, unused -> textures);

        WeightedVariant bottom = BlockStateModelGenerator.createWeightedVariant(bottomModelId);
        WeightedVariant bottomHinge = BlockStateModelGenerator.createWeightedVariant(bottomHingeModelId);
        WeightedVariant top = BlockStateModelGenerator.createWeightedVariant(topModelId);
        WeightedVariant topHinge = BlockStateModelGenerator.createWeightedVariant(topHingeModelId);

        blockStateModelGenerator.blockStateCollector
            .accept(
                VariantsBlockModelDefinitionCreator.of(BLOCK)
                    .with(
                        BlockStateVariantMap
                            .models(BookshelfDoorBlock.FACING, BookshelfDoorBlock.HALF, BookshelfDoorBlock.HINGE, BookshelfDoorBlock.OPEN)
                            .register(
                                Direction.EAST, DoubleBlockHalf.LOWER, DoorHinge.LEFT, false,
                                bottom
                            )
                            .register(
                                Direction.EAST, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, true,
                                bottom.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270))
                            )
                            .register(
                                Direction.NORTH, DoubleBlockHalf.LOWER, DoorHinge.LEFT, false,
                                bottom.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270))
                            )
                            .register(
                                Direction.NORTH, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, true,
                                bottom.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180))
                            )
                            .register(
                                Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHinge.LEFT, false,
                                bottom.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
                            )
                            .register(
                                Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, true,
                                bottom
                            )
                            .register(
                                Direction.WEST, DoubleBlockHalf.LOWER, DoorHinge.LEFT, false,
                                bottom.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180))
                            )
                            .register(
                                Direction.WEST, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, true,
                                bottom.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
                            )
                            .register(
                                Direction.EAST, DoubleBlockHalf.LOWER, DoorHinge.LEFT, true,
                                bottomHinge.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
                            )
                            .register(
                                Direction.EAST, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, false,
                                bottomHinge
                            )
                            .register(
                                Direction.NORTH, DoubleBlockHalf.LOWER, DoorHinge.LEFT, true,
                                bottomHinge
                            )
                            .register(
                                Direction.NORTH, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, false,
                                bottomHinge.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270))
                            )
                            .register(
                                Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHinge.LEFT, true,
                                bottomHinge.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180))
                            )
                            .register(
                                Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, false,
                                bottomHinge.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
                            )
                            .register(
                                Direction.WEST, DoubleBlockHalf.LOWER, DoorHinge.LEFT, true,
                                bottomHinge.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270))
                            )
                            .register(
                                Direction.WEST, DoubleBlockHalf.LOWER, DoorHinge.RIGHT, false,
                                bottomHinge.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180))
                            )
                            .register(
                                Direction.EAST, DoubleBlockHalf.UPPER, DoorHinge.LEFT, false,
                                top
                            )
                            .register(
                                Direction.EAST, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, true,
                                top.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270))
                            )
                            .register(
                                Direction.NORTH, DoubleBlockHalf.UPPER, DoorHinge.LEFT, false,
                                top.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270))
                            )
                            .register(
                                Direction.NORTH, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, true,
                                top.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180))
                            )
                            .register(
                                Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHinge.LEFT, false,
                                top.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
                            )
                            .register(
                                Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, true,
                                top
                            )
                            .register(
                                Direction.WEST, DoubleBlockHalf.UPPER, DoorHinge.LEFT, false,
                                top.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180))
                            )
                            .register(
                                Direction.WEST, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, true,
                                top.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
                            )
                            .register(
                                Direction.EAST, DoubleBlockHalf.UPPER, DoorHinge.LEFT, true,
                                topHinge.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
                            )
                            .register(
                                Direction.EAST, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, false,
                                topHinge
                            )
                            .register(
                                Direction.NORTH, DoubleBlockHalf.UPPER, DoorHinge.LEFT, true,
                                topHinge
                            )
                            .register(
                                Direction.NORTH, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, false,
                                topHinge.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270))
                            )
                            .register(
                                Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHinge.LEFT, true,
                                topHinge.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180))
                            )
                            .register(
                                Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, false,
                                topHinge.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90))
                            )
                            .register(
                                Direction.WEST, DoubleBlockHalf.UPPER, DoorHinge.LEFT, true,
                                topHinge.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270))
                            )
                            .register(
                                Direction.WEST, DoubleBlockHalf.UPPER, DoorHinge.RIGHT, false,
                                topHinge.apply(ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180))
                            )
                    )
            );
    }

    public void configureItemModels(ItemModelGenerator itemModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient("planks");

        TextureMap textures = new TextureMap()
            .put(MinekeaTextures.MATERIAL, TextureMap.getId(plankIngredient))
            .put(MinekeaTextures.SHELF, Identifier.of(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf0"));

        ITEM_MODEL.upload(
            BLOCK.BLOCK_ID.withPrefixedPath("item/"),
            textures,
            itemModelGenerator.modelCollector
        );
    }
}

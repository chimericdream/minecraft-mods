package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.doors.BookshelfDoorBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Optional;
import java.util.function.Function;

public class BookshelfDoorBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final ModelTemplate ITEM_MODEL = makeModel("item/furniture/doors/bookshelf");
    protected static final ModelTemplate BOTTOM_MODEL = makeModel("block/furniture/doors/bookshelves/bottom");
    protected static final ModelTemplate BOTTOM_HINGE_MODEL = makeModel("block/furniture/doors/bookshelves/bottom_rh");
    protected static final ModelTemplate TOP_MODEL = makeModel("block/furniture/doors/bookshelves/top");
    protected static final ModelTemplate TOP_HINGE_MODEL = makeModel("block/furniture/doors/bookshelves/top_rh");

    protected final BookshelfDoorBlock BLOCK;

    public BookshelfDoorBlockDataGenerator(Block block) {
        BLOCK = (BookshelfDoorBlock) block;
    }

    protected static ModelTemplate makeModel(String path) {
        return new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, path)),
            Optional.empty(),
            MinekeaTextures.MATERIAL,
            MinekeaTextures.SHELF
        );
    }

    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block bookshelf = BLOCK.config.getIngredient();

        assert bookshelf != null;

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 3)
            .pattern("##")
            .pattern("##")
            .pattern("##")
            .define('#', bookshelf)
            .unlockedBy(RecipeProvider.getHasName(bookshelf),
                generator.has(bookshelf))
            .save(exporter);
    }

    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.createDoorTable(BLOCK);
    }

    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Bookshelf Door", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Bookshelf Door", BLOCK.config.getMaterialName()));
    }

    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient("planks");

        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.MATERIAL, TextureMapping.getBlockTexture(plankIngredient))
            .put(MinekeaTextures.SHELF, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf0"));

        Identifier bottomModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_bottom", BOTTOM_MODEL, unused -> textures);
        Identifier bottomHingeModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_bottom_rh", BOTTOM_HINGE_MODEL, unused -> textures);
        Identifier topModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_top", TOP_MODEL, unused -> textures);
        Identifier topHingeModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_top_rh", TOP_HINGE_MODEL, unused -> textures);

        MultiVariant bottom = BlockModelGenerators.plainVariant(bottomModelId);
        MultiVariant bottomHinge = BlockModelGenerators.plainVariant(bottomHingeModelId);
        MultiVariant top = BlockModelGenerators.plainVariant(topModelId);
        MultiVariant topHinge = BlockModelGenerators.plainVariant(topHingeModelId);

        blockStateModelGenerator.registerSimpleItemModel(BLOCK, BLOCK.BLOCK_ID.withPrefix("item/"));

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(BLOCK)
                    .with(
                        PropertyDispatch
                            .initial(BookshelfDoorBlock.FACING, BookshelfDoorBlock.HALF, BookshelfDoorBlock.HINGE, BookshelfDoorBlock.OPEN)
                            .select(
                                Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false,
                                bottom
                            )
                            .select(
                                Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true,
                                bottom.with(VariantMutator.Y_ROT.withValue(Quadrant.R270))
                            )
                            .select(
                                Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false,
                                bottom.with(VariantMutator.Y_ROT.withValue(Quadrant.R270))
                            )
                            .select(
                                Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true,
                                bottom.with(VariantMutator.Y_ROT.withValue(Quadrant.R180))
                            )
                            .select(
                                Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false,
                                bottom.with(VariantMutator.Y_ROT.withValue(Quadrant.R90))
                            )
                            .select(
                                Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true,
                                bottom
                            )
                            .select(
                                Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false,
                                bottom.with(VariantMutator.Y_ROT.withValue(Quadrant.R180))
                            )
                            .select(
                                Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true,
                                bottom.with(VariantMutator.Y_ROT.withValue(Quadrant.R90))
                            )
                            .select(
                                Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true,
                                bottomHinge.with(VariantMutator.Y_ROT.withValue(Quadrant.R90))
                            )
                            .select(
                                Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false,
                                bottomHinge
                            )
                            .select(
                                Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true,
                                bottomHinge
                            )
                            .select(
                                Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false,
                                bottomHinge.with(VariantMutator.Y_ROT.withValue(Quadrant.R270))
                            )
                            .select(
                                Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true,
                                bottomHinge.with(VariantMutator.Y_ROT.withValue(Quadrant.R180))
                            )
                            .select(
                                Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false,
                                bottomHinge.with(VariantMutator.Y_ROT.withValue(Quadrant.R90))
                            )
                            .select(
                                Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true,
                                bottomHinge.with(VariantMutator.Y_ROT.withValue(Quadrant.R270))
                            )
                            .select(
                                Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false,
                                bottomHinge.with(VariantMutator.Y_ROT.withValue(Quadrant.R180))
                            )
                            .select(
                                Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false,
                                top
                            )
                            .select(
                                Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true,
                                top.with(VariantMutator.Y_ROT.withValue(Quadrant.R270))
                            )
                            .select(
                                Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false,
                                top.with(VariantMutator.Y_ROT.withValue(Quadrant.R270))
                            )
                            .select(
                                Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true,
                                top.with(VariantMutator.Y_ROT.withValue(Quadrant.R180))
                            )
                            .select(
                                Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false,
                                top.with(VariantMutator.Y_ROT.withValue(Quadrant.R90))
                            )
                            .select(
                                Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true,
                                top
                            )
                            .select(
                                Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false,
                                top.with(VariantMutator.Y_ROT.withValue(Quadrant.R180))
                            )
                            .select(
                                Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true,
                                top.with(VariantMutator.Y_ROT.withValue(Quadrant.R90))
                            )
                            .select(
                                Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true,
                                topHinge.with(VariantMutator.Y_ROT.withValue(Quadrant.R90))
                            )
                            .select(
                                Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false,
                                topHinge
                            )
                            .select(
                                Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true,
                                topHinge
                            )
                            .select(
                                Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false,
                                topHinge.with(VariantMutator.Y_ROT.withValue(Quadrant.R270))
                            )
                            .select(
                                Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true,
                                topHinge.with(VariantMutator.Y_ROT.withValue(Quadrant.R180))
                            )
                            .select(
                                Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false,
                                topHinge.with(VariantMutator.Y_ROT.withValue(Quadrant.R90))
                            )
                            .select(
                                Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true,
                                topHinge.with(VariantMutator.Y_ROT.withValue(Quadrant.R270))
                            )
                            .select(
                                Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false,
                                topHinge.with(VariantMutator.Y_ROT.withValue(Quadrant.R180))
                            )
                    )
            );
    }

    public void configureItemModels(ItemModelGenerators itemModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient("planks");

        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.MATERIAL, TextureMapping.getBlockTexture(plankIngredient))
            .put(MinekeaTextures.SHELF, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf0"));

        ITEM_MODEL.create(
            BLOCK.BLOCK_ID.withPrefix("item/"),
            textures,
            itemModelGenerator.modelOutput
        );
    }
}

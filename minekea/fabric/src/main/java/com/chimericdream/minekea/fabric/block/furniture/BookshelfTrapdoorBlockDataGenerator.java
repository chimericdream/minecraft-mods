package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.trapdoors.BookshelfTrapdoorBlock;
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
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Half;

import java.util.Optional;
import java.util.function.Function;

public class BookshelfTrapdoorBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final ModelTemplate BOTTOM_MODEL = makeModel("block/furniture/trapdoors/bookshelves/bottom");
    protected static final ModelTemplate TOP_MODEL = makeModel("block/furniture/trapdoors/bookshelves/top");
    protected static final ModelTemplate OPEN_MODEL = makeModel("block/furniture/trapdoors/bookshelves/open");

    protected final BookshelfTrapdoorBlock BLOCK;

    public BookshelfTrapdoorBlockDataGenerator(Block block) {
        BLOCK = (BookshelfTrapdoorBlock) block;
    }

    protected static ModelTemplate makeModel(String path) {
        return new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, path)),
            Optional.empty(),
            MinekeaTextures.MATERIAL,
            MinekeaTextures.SHELF
        );
    }

    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block bookshelf = BLOCK.config.getIngredient();

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 12)
            .pattern("###")
            .pattern("###")
            .define('#', bookshelf)
            .unlockedBy(RecipeProvider.getHasName(bookshelf),
                generator.has(bookshelf))
            .save(exporter);
    }

    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK.builtInRegistryHolder().key());
    }

    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Bookshelf Trapdoor", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Bookshelf Trapdoor", BLOCK.config.getMaterialName()));
    }

    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient("planks");

        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.MATERIAL, TextureMapping.getBlockTexture(plankIngredient))
            .put(MinekeaTextures.SHELF, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf0")));

        Identifier bottomModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_bottom", BOTTOM_MODEL, unused -> textures);
        Identifier topModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_top", TOP_MODEL, unused -> textures);
        Identifier openModelId = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_open", OPEN_MODEL, unused -> textures);

        MultiVariant bottom = BlockModelGenerators.plainVariant(bottomModelId);
        MultiVariant top = BlockModelGenerators.plainVariant(topModelId);
        MultiVariant open = BlockModelGenerators.plainVariant(openModelId);

        blockStateModelGenerator.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(BLOCK)
                    .with(
                        PropertyDispatch
                            .initial(BookshelfTrapdoorBlock.FACING, BookshelfTrapdoorBlock.HALF, BookshelfTrapdoorBlock.OPEN)
                            .select(
                                Direction.EAST, Half.BOTTOM, false,
                                bottom.with(VariantMutator.Y_ROT.withValue(Quadrant.R90))
                            )
                            .select(
                                Direction.NORTH, Half.BOTTOM, false,
                                bottom
                            )
                            .select(
                                Direction.SOUTH, Half.BOTTOM, false,
                                bottom.with(VariantMutator.Y_ROT.withValue(Quadrant.R180))
                            )
                            .select(
                                Direction.WEST, Half.BOTTOM, false,
                                bottom.with(VariantMutator.Y_ROT.withValue(Quadrant.R270))
                            )
                            .select(
                                Direction.EAST, Half.TOP, false,
                                top.with(VariantMutator.Y_ROT.withValue(Quadrant.R90))
                            )
                            .select(
                                Direction.NORTH, Half.TOP, false,
                                top
                            )
                            .select(
                                Direction.SOUTH, Half.TOP, false,
                                top.with(VariantMutator.Y_ROT.withValue(Quadrant.R180))
                            )
                            .select(
                                Direction.WEST, Half.TOP, false,
                                top.with(VariantMutator.Y_ROT.withValue(Quadrant.R270))
                            )
                            .select(
                                Direction.EAST, Half.BOTTOM, true,
                                open.with(VariantMutator.Y_ROT.withValue(Quadrant.R90))
                            )
                            .select(
                                Direction.NORTH, Half.BOTTOM, true,
                                open
                            )
                            .select(
                                Direction.SOUTH, Half.BOTTOM, true,
                                open.with(VariantMutator.Y_ROT.withValue(Quadrant.R180))
                            )
                            .select(
                                Direction.WEST, Half.BOTTOM, true,
                                open.with(VariantMutator.Y_ROT.withValue(Quadrant.R270))
                            )
                            .select(
                                Direction.EAST, Half.TOP, true,
                                open.with(VariantMutator.X_ROT.withValue(Quadrant.R180)).with(VariantMutator.Y_ROT.withValue(Quadrant.R270))
                            )
                            .select(
                                Direction.NORTH, Half.TOP, true,
                                open.with(VariantMutator.X_ROT.withValue(Quadrant.R180)).with(VariantMutator.Y_ROT.withValue(Quadrant.R180))
                            )
                            .select(
                                Direction.SOUTH, Half.TOP, true,
                                open.with(VariantMutator.X_ROT.withValue(Quadrant.R180))
                            )
                            .select(
                                Direction.WEST, Half.TOP, true,
                                open.with(VariantMutator.X_ROT.withValue(Quadrant.R180)).with(VariantMutator.Y_ROT.withValue(Quadrant.R90))
                            )
                    )
            );

        blockStateModelGenerator.registerSimpleItemModel(BLOCK, bottomModelId);
    }
}

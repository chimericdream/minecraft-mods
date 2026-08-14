package com.chimericdream.minekea.fabric.block.building.stairs;

import com.chimericdream.lib.fabric.blocks.TranslationUtils;
import com.chimericdream.lib.fabric.blocks.TagUtils;
import com.chimericdream.lib.fabric.blocks.family.FamilyBlockModels;
import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.stairs.BookshelfStairsBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
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

public class BookshelfStairsBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final ModelTemplate INNER_BOOKSHELF_STAIRS_MODEL = makeModel("block/building/stairs/bookshelves/inner");
    protected static final ModelTemplate MAIN_BOOKSHELF_STAIRS_MODEL = makeModel("block/building/stairs/bookshelves/main");
    protected static final ModelTemplate OUTER_BOOKSHELF_STAIRS_MODEL = makeModel("block/building/stairs/bookshelves/outer");

    private final BookshelfStairsBlock BLOCK;

    public BookshelfStairsBlockDataGenerator(Block block) {
        BLOCK = (BookshelfStairsBlock) block;
    }

    protected static ModelTemplate makeModel(String path) {
        return new ModelTemplate(
            Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, path)),
            Optional.empty(),
            MinekeaTextures.SHELF,
            MinekeaTextures.MATERIAL
        );
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        TagUtils.applyMineableTag(getBuilder, BLOCK.config.getTool(), BLOCK);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block ingredient = BuiltInRegistries.BLOCK.getValue(BLOCK.BASE_BLOCK_ID);

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 8)
            .pattern("#  ")
            .pattern("## ")
            .pattern("###")
            .define('#', ingredient)
            .unlockedBy(RecipeProvider.getHasName(ingredient),
                generator.has(ingredient))
            .save(exporter);
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        TranslationUtils.addBlockAndItem(translationBuilder, BLOCK, String.format("%s Bookshelf Stairs", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Identifier textureId = BLOCK.config.getTexture();
        assert textureId != null;

        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.SHELF, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf0")))
            .put(MinekeaTextures.MATERIAL, new Material(textureId));

        FamilyBlockModels.registerStairsBlock(
            blockStateModelGenerator,
            BLOCK,
            textures,
            INNER_BOOKSHELF_STAIRS_MODEL,
            MAIN_BOOKSHELF_STAIRS_MODEL,
            OUTER_BOOKSHELF_STAIRS_MODEL
        );
    }
}

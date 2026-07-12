package com.chimericdream.minekea.fabric.block.building.slabs;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.slabs.BookshelfSlabBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
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

import static com.chimericdream.minekea.fabric.block.furniture.BookshelfBlockDataGenerator.BOOKSHELF_MODEL;

public class BookshelfSlabBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final ModelTemplate BOTTOM_BOOKSHELF_SLAB_MODEL = makeModel("block/building/slabs/bookshelves/bottom");
    protected static final ModelTemplate TOP_BOOKSHELF_SLAB_MODEL = makeModel("block/building/slabs/bookshelves/top");

    private final BookshelfSlabBlock BLOCK;

    public BookshelfSlabBlockDataGenerator(Block block) {
        BLOCK = (BookshelfSlabBlock) block;
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
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.PICKAXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 6)
            .pattern("###")
            .define('#', BuiltInRegistries.BLOCK.getValue(BLOCK.BASE_BLOCK_ID))
            .unlockedBy(RecipeProvider.getHasName(BuiltInRegistries.BLOCK.getValue(BLOCK.BASE_BLOCK_ID)),
                generator.has(BuiltInRegistries.BLOCK.getValue(BLOCK.BASE_BLOCK_ID)))
            .save(exporter);
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.add(BLOCK, generator.createSlabItemTable(BLOCK));
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Bookshelf Slab", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Bookshelf Slab", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Identifier textureId = BLOCK.config.getTexture();

        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.SHELF, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf0")))
            .put(MinekeaTextures.MATERIAL, new Material(textureId));

        ModelUtils.registerSlabBlock(
            blockStateModelGenerator,
            BLOCK,
            textures,
            BOTTOM_BOOKSHELF_SLAB_MODEL,
            TOP_BOOKSHELF_SLAB_MODEL,
            BOOKSHELF_MODEL
        );
    }
}

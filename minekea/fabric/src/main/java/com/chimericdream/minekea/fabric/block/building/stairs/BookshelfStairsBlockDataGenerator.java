package com.chimericdream.minekea.fabric.block.building.stairs;

import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.stairs.BookshelfStairsBlock;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class BookshelfStairsBlockDataGenerator extends StairsBlockDataGenerator {
    protected static final ModelTemplate INNER_BOOKSHELF_STAIRS_MODEL = makeModel("block/building/stairs/bookshelves/inner");
    protected static final ModelTemplate MAIN_BOOKSHELF_STAIRS_MODEL = makeModel("block/building/stairs/bookshelves/main");
    protected static final ModelTemplate OUTER_BOOKSHELF_STAIRS_MODEL = makeModel("block/building/stairs/bookshelves/outer");

    public BookshelfStairsBlockDataGenerator(Block block) {
        super(block);
    }

    protected static ModelTemplate makeModel(String path) {
        return new ModelTemplate(
            Optional.of(ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, path)),
            Optional.empty(),
            MinekeaTextures.SHELF,
            MinekeaTextures.MATERIAL
        );
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        ResourceLocation ingredientId = ((BookshelfStairsBlock) BLOCK).BASE_BLOCK_ID;
        Block ingredient = BuiltInRegistries.BLOCK.getValue(ingredientId);

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
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Bookshelf Stairs", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Bookshelf Stairs", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        ResourceLocation textureId = BLOCK.config.getTexture();

        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.SHELF, ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf0"))
            .put(MinekeaTextures.MATERIAL, textureId);

        ModelUtils.registerStairsBlock(
            blockStateModelGenerator,
            BLOCK,
            textures,
            INNER_BOOKSHELF_STAIRS_MODEL,
            MAIN_BOOKSHELF_STAIRS_MODEL,
            OUTER_BOOKSHELF_STAIRS_MODEL
        );
    }
}

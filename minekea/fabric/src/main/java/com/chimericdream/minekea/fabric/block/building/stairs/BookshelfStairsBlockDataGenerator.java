package com.chimericdream.minekea.fabric.block.building.stairs;

import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.stairs.BookshelfStairsBlock;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureMap;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class BookshelfStairsBlockDataGenerator extends StairsBlockDataGenerator {
    protected static final Model INNER_BOOKSHELF_STAIRS_MODEL = makeModel("block/building/stairs/bookshelves/inner");
    protected static final Model MAIN_BOOKSHELF_STAIRS_MODEL = makeModel("block/building/stairs/bookshelves/main");
    protected static final Model OUTER_BOOKSHELF_STAIRS_MODEL = makeModel("block/building/stairs/bookshelves/outer");

    public BookshelfStairsBlockDataGenerator(Block block) {
        super(block);
    }

    protected static Model makeModel(String path) {
        return new Model(
            Optional.of(Identifier.of(ModInfo.MOD_ID, path)),
            Optional.empty(),
            MinekeaTextures.SHELF,
            MinekeaTextures.MATERIAL
        );
    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Identifier ingredientId = ((BookshelfStairsBlock) BLOCK).BASE_BLOCK_ID;
        Block ingredient = Registries.BLOCK.get(ingredientId);

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 8)
            .pattern("#  ")
            .pattern("## ")
            .pattern("###")
            .input('#', ingredient)
            .criterion(RecipeGenerator.hasItem(ingredient),
                generator.conditionsFromItem(ingredient))
            .offerTo(exporter);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Bookshelf Stairs", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Identifier textureId = BLOCK.config.getTexture();

        TextureMap textures = new TextureMap()
            .put(MinekeaTextures.SHELF, Identifier.of(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf0"))
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

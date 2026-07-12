package com.chimericdream.minekea.fabric.block.building.stairs;

import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.stairs.VerticalBookshelfStairsBlock;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class VerticalBookshelfStairsBlockDataGenerator extends VerticalStairsBlockDataGenerator {
    protected static final ModelTemplate VERTICAL_BOOKSHELF_STAIRS_MODEL = makeModel("block/building/stairs/bookshelves/vertical");

    public VerticalBookshelfStairsBlockDataGenerator(Block block) {
        super(block);
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
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Identifier ingredientId = ((VerticalBookshelfStairsBlock) BLOCK).BASE_BLOCK_ID;
        Block ingredient = BuiltInRegistries.BLOCK.getValue(ingredientId);

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 8)
            .pattern("###")
            .pattern(" ##")
            .pattern("  #")
            .define('#', ingredient)
            .unlockedBy(RecipeProvider.getHasName(ingredient),
                generator.has(ingredient))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("Vertical %s Bookshelf Stairs", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("Vertical %s Bookshelf Stairs", BLOCK.config.getMaterialName()));
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping()
            .put(MinekeaTextures.SHELF, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf0")))
            .put(MinekeaTextures.MATERIAL, new Material(BLOCK.config.getTexture()));

        ModelUtils.registerVerticalStairsBlock(
            blockStateModelGenerator,
            BLOCK,
            textures,
            VERTICAL_BOOKSHELF_STAIRS_MODEL
        );
    }
}

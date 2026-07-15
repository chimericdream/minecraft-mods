package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.bookshelves.BookshelfBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Optional;
import java.util.function.Function;

public class BookshelfBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public static final ModelTemplate BOOKSHELF_MODEL = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/bookshelves/bookshelf")),
        Optional.empty(),
        MinekeaTextures.MATERIAL,
        MinekeaTextures.SHELF
    );

    protected final BookshelfBlock BLOCK;

    public BookshelfBlockDataGenerator(Block block) {
        BLOCK = (BookshelfBlock) block;
    }

    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBuilder.apply(BlockTags.ENCHANTMENT_POWER_PROVIDER).setReplace(false).add(BLOCK.builtInRegistryHolder().key());

        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.PICKAXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK.builtInRegistryHolder().key());
    }

    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block ingredient = BLOCK.config.getIngredient();

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 3)
            .pattern("###")
            .pattern("XXX")
            .pattern("###")
            .define('#', ingredient)
            .define('X', Items.BOOK)
            .unlockedBy(RecipeProvider.getHasName(ingredient),
                generator.has(ingredient))
            .unlockedBy(RecipeProvider.getHasName(Items.BOOK),
                generator.has(Items.BOOK))
            .save(exporter);
    }

    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.add(BLOCK, generator.createSingleItemTableWithSilkTouch(BLOCK, Items.BOOK, ConstantValue.exactly(3)));
    }

    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Bookshelf", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Bookshelf", BLOCK.config.getMaterialName()));
    }

    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        TextureMapping textures = new TextureMapping().put(MinekeaTextures.MATERIAL, new Material(BLOCK.config.getTexture()));

        Identifier variant0Id = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_v0", BOOKSHELF_MODEL, unused -> textures.put(MinekeaTextures.SHELF, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf0"))));
        Identifier variant1Id = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_v1", BOOKSHELF_MODEL, unused -> textures.put(MinekeaTextures.SHELF, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf1"))));
        Identifier variant2Id = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_v2", BOOKSHELF_MODEL, unused -> textures.put(MinekeaTextures.SHELF, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf2"))));
        Identifier variant3Id = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_v3", BOOKSHELF_MODEL, unused -> textures.put(MinekeaTextures.SHELF, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf3"))));
        Identifier variant4Id = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_v4", BOOKSHELF_MODEL, unused -> textures.put(MinekeaTextures.SHELF, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf4"))));
        Identifier variant5Id = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_v5", BOOKSHELF_MODEL, unused -> textures.put(MinekeaTextures.SHELF, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf5"))));
        Identifier variant6Id = blockStateModelGenerator.createSuffixedVariant(BLOCK, "_v6", BOOKSHELF_MODEL, unused -> textures.put(MinekeaTextures.SHELF, new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf6"))));

        Variant variant0 = BlockModelGenerators.plainModel(variant0Id);
        Variant variant1 = BlockModelGenerators.plainModel(variant1Id);
        Variant variant2 = BlockModelGenerators.plainModel(variant2Id);
        Variant variant3 = BlockModelGenerators.plainModel(variant3Id);
        Variant variant4 = BlockModelGenerators.plainModel(variant4Id);
        Variant variant5 = BlockModelGenerators.plainModel(variant5Id);
        Variant variant6 = BlockModelGenerators.plainModel(variant6Id);

        MultiVariant weightedVariant = BlockModelGenerators.variants(variant0, variant1, variant2, variant3, variant4, variant5, variant6);

        blockStateModelGenerator.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(BLOCK, weightedVariant)
        );

        blockStateModelGenerator.registerSimpleItemModel(BLOCK, variant0Id);
    }
}

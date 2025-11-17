package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.bookshelves.BookshelfBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Items;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Function;

public class BookshelfBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public static final Model BOOKSHELF_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/furniture/bookshelves/bookshelf")),
        Optional.empty(),
        MinekeaTextures.MATERIAL,
        MinekeaTextures.SHELF
    );

    protected final BookshelfBlock BLOCK;

    public BookshelfBlockDataGenerator(Block block) {
        BLOCK = (BookshelfBlock) block;
    }

    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        getBuilder.apply(BlockTags.ENCHANTMENT_POWER_PROVIDER).setReplace(false).add(BLOCK);

        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.PICKAXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Block ingredient = BLOCK.config.getIngredient();

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 3)
            .pattern("###")
            .pattern("XXX")
            .pattern("###")
            .input('#', ingredient)
            .input('X', Items.BOOK)
            .criterion(RecipeGenerator.hasItem(ingredient),
                generator.conditionsFromItem(ingredient))
            .criterion(RecipeGenerator.hasItem(Items.BOOK),
                generator.conditionsFromItem(Items.BOOK))
            .offerTo(exporter);
    }

    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(BLOCK, generator.drops(BLOCK, Items.BOOK, ConstantLootNumberProvider.create(3)));
    }

    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Bookshelf", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Bookshelf", BLOCK.config.getMaterialName()));
    }

    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        TextureMap textures = new TextureMap().put(MinekeaTextures.MATERIAL, BLOCK.config.getTexture());

        Identifier variant0Id = blockStateModelGenerator.createSubModel(BLOCK, "_v0", BOOKSHELF_MODEL, unused -> textures.put(MinekeaTextures.SHELF, Identifier.of(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf0")));
        Identifier variant1Id = blockStateModelGenerator.createSubModel(BLOCK, "_v1", BOOKSHELF_MODEL, unused -> textures.put(MinekeaTextures.SHELF, Identifier.of(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf1")));
        Identifier variant2Id = blockStateModelGenerator.createSubModel(BLOCK, "_v2", BOOKSHELF_MODEL, unused -> textures.put(MinekeaTextures.SHELF, Identifier.of(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf2")));
        Identifier variant3Id = blockStateModelGenerator.createSubModel(BLOCK, "_v3", BOOKSHELF_MODEL, unused -> textures.put(MinekeaTextures.SHELF, Identifier.of(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf3")));
        Identifier variant4Id = blockStateModelGenerator.createSubModel(BLOCK, "_v4", BOOKSHELF_MODEL, unused -> textures.put(MinekeaTextures.SHELF, Identifier.of(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf4")));
        Identifier variant5Id = blockStateModelGenerator.createSubModel(BLOCK, "_v5", BOOKSHELF_MODEL, unused -> textures.put(MinekeaTextures.SHELF, Identifier.of(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf5")));
        Identifier variant6Id = blockStateModelGenerator.createSubModel(BLOCK, "_v6", BOOKSHELF_MODEL, unused -> textures.put(MinekeaTextures.SHELF, Identifier.of(ModInfo.MOD_ID, "block/furniture/bookshelves/shelf6")));

        ModelVariant variant0 = BlockStateModelGenerator.createModelVariant(variant0Id);
        ModelVariant variant1 = BlockStateModelGenerator.createModelVariant(variant1Id);
        ModelVariant variant2 = BlockStateModelGenerator.createModelVariant(variant2Id);
        ModelVariant variant3 = BlockStateModelGenerator.createModelVariant(variant3Id);
        ModelVariant variant4 = BlockStateModelGenerator.createModelVariant(variant4Id);
        ModelVariant variant5 = BlockStateModelGenerator.createModelVariant(variant5Id);
        ModelVariant variant6 = BlockStateModelGenerator.createModelVariant(variant6Id);

        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(variant0, variant1, variant2, variant3, variant4, variant5, variant6);

        blockStateModelGenerator.blockStateCollector.accept(
            VariantsBlockModelDefinitionCreator.of(BLOCK, weightedVariant)
        );

        blockStateModelGenerator.registerParentedItemModel(BLOCK, variant0Id);
    }
}

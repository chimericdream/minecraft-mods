package com.chimericdream.minekea.fabric.block.furniture;

import com.chimericdream.lib.util.Tool;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.furniture.shelves.ShelfBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
import com.chimericdream.minekea.resource.MinekeaTextures;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureMap;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Function;

public class ShelfBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected static final Model SHELF_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/furniture/shelves/supported")),
        Optional.empty(),
        MinekeaTextures.LOG,
        MinekeaTextures.PLANKS
    );
    protected static final Model FLOATING_SHELF_MODEL = new Model(
        Optional.of(Identifier.of(ModInfo.MOD_ID, "block/furniture/shelves/floating")),
        Optional.empty(),
        MinekeaTextures.PLANKS
    );

    protected final ShelfBlock BLOCK;

    public ShelfBlockDataGenerator(Block block) {
        BLOCK = (ShelfBlock) block;
    }

    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        Tool tool = Optional.ofNullable(BLOCK.config.getTool()).orElse(Tool.AXE);
        getBuilder.apply(tool.getMineableTag())
            .setReplace(false)
            .add(BLOCK);
    }

    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Block slabIngredient = BLOCK.config.getIngredient("slab");

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 3)
            .pattern("XXX")
            .pattern("# #")
            .pattern("X X")
            .input('X', slabIngredient)
            .input('#', Items.IRON_NUGGET)
            .criterion(RecipeGenerator.hasItem(slabIngredient),
                generator.conditionsFromItem(slabIngredient))
            .criterion(RecipeGenerator.hasItem(Items.IRON_NUGGET),
                generator.conditionsFromItem(Items.IRON_NUGGET))
            .offerTo(exporter);
    }

    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("%s Shelf", BLOCK.config.getMaterialName()));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Shelf", BLOCK.config.getMaterialName()));
    }

    public void configureBlockLootTables(BlockLootTableGenerator generator, RegistryWrapper.WrapperLookup registryLookup) {
        generator.addDrop(BLOCK);
    }

    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Block plankIngredient = BLOCK.config.getIngredient("planks");
        Block logIngredient = BLOCK.config.getIngredient("log");

        TextureMap textures = new TextureMap()
            .put(MinekeaTextures.LOG, TextureMap.getId(logIngredient))
            .put(MinekeaTextures.PLANKS, TextureMap.getId(plankIngredient));

        Identifier subModelId = blockStateModelGenerator.createSubModel(BLOCK, "", SHELF_MODEL, unused -> textures);

        ModelUtils.registerBlockWithWallSide(blockStateModelGenerator, ShelfBlock.WALL_SIDE, BLOCK, subModelId);
    }
}

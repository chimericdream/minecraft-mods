package com.chimericdream.minekea.fabric.block.building.storage;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.colors.ColorHelpers;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.block.building.storage.DyeBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.blockstate.suppliers.CustomBlockStateModelSupplier;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureKey;
import net.minecraft.client.data.TextureMap;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Function;

public class DyeBlockDataGenerator implements ChimericLibBlockDataGenerator {
    public final DyeBlock BLOCK;

    private static final Model DYE_BLOCK_MODEL = new CustomBlockStateModelSupplier.CustomBlockModel(
        BlockConfig.RenderType.TRANSLUCENT,
        Optional.of(Identifier.of("minekea:block/storage/dye_block")),
        Optional.empty(),
        TextureKey.BOTTOM,
        TextureKey.SIDE,
        TextureKey.TOP
    );

    public DyeBlockDataGenerator(Block block) {
        this.BLOCK = (DyeBlock) block;
    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Item dye = ColorHelpers.getDye(BLOCK.color);

        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .input('#', dye)
            .criterion(RecipeGenerator.hasItem(dye),
                generator.conditionsFromItem(dye))
            .offerTo(exporter);

        generator.createShapeless(RecipeCategory.BUILDING_BLOCKS, dye, 9)
            .input(BLOCK)
            .criterion(RecipeGenerator.hasItem(BLOCK),
                generator.conditionsFromItem(BLOCK))
            .offerTo(exporter);
    }

    @Override
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {

    }

    @Override
    public void configureItemTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Item>, ProvidedTagBuilder<Item, Item>> getBuilder) {

    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, String.format("Compressed %s Dye", ColorHelpers.getName(BLOCK.color)));
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        TextureMap textures = new TextureMap()
            .put(TextureKey.BOTTOM, Identifier.of(ModInfo.MOD_ID, String.format("block/storage/dyes/%s/bottom", BLOCK.color)))
            .put(TextureKey.SIDE, Identifier.of(ModInfo.MOD_ID, String.format("block/storage/dyes/%s/side", BLOCK.color)))
            .put(TextureKey.TOP, Identifier.of(ModInfo.MOD_ID, String.format("block/storage/dyes/%s/top", BLOCK.color)));

        blockStateModelGenerator.registerSingleton(
            BLOCK,
            textures,
            DYE_BLOCK_MODEL
        );
    }

    @Override
    public void configureItemModels(ItemModelGenerator itemModelGenerator) {

    }

    @Override
    public void generateTextures() {

    }
}

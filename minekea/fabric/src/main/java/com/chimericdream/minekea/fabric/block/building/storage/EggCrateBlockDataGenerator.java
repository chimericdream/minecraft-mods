package com.chimericdream.minekea.fabric.block.building.storage;

import com.chimericdream.minekea.block.building.storage.EggCrateBlock;
import com.chimericdream.minekea.block.building.storage.StorageBlocks;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Function;

public class EggCrateBlockDataGenerator extends ChimericLibBlockDataGenerator {
    protected final EggCrateBlock BLOCK;

    public EggCrateBlockDataGenerator() {
        BLOCK = (EggCrateBlock) StorageBlocks.EGG_CRATE_BLOCK.get();
    }

    @Override
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        getBuilder.apply(BlockTags.HOE_MINEABLE)
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .input('#', Items.EGG)
            .criterion(RecipeGenerator.hasItem(Items.EGG),
                generator.conditionsFromItem(Items.EGG))
            .offerTo(exporter);

        generator.createShapeless(RecipeCategory.BUILDING_BLOCKS, Items.EGG, 9)
            .input(BLOCK)
            .criterion(RecipeGenerator.hasItem(BLOCK),
                generator.conditionsFromItem(BLOCK))
            .offerTo(exporter);
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator, RegistryWrapper.WrapperLookup registryLookup) {
        // @TODO: require silk touch?
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, "Egg Crate");
        translationBuilder.add(BLOCK.asItem(), "Egg Crate");
    }
}

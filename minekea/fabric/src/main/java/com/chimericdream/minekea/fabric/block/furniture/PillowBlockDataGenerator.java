//package com.chimericdream.minekea.fabric.block.furniture;
//
//import com.chimericdream.lib.colors.ColorHelpers;
//import com.chimericdream.lib.tags.CommonBlockTags;
//import com.chimericdream.minekea.block.furniture.pillows.PillowBlock;
//import com.chimericdream.minekea.tag.MinekeaBlockTags;
//import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
//import net.minecraft.block.Block;
//import net.minecraft.data.client.BlockStateModelGenerator;
//import net.minecraft.data.server.loottable.BlockLootTableGenerator;
//import net.minecraft.data.server.recipe.RecipeExporter;
//import net.minecraft.recipe.book.RecipeCategory;
//import net.minecraft.registry.RegistryWrapper;
//import net.minecraft.registry.tag.TagKey;
//
//import java.util.function.Function;
//
//public class PillowBlockDataGenerator extends ChimericLibBlockDataGenerator {
//    public PillowBlock BLOCK;
//
//    public PillowBlockDataGenerator(Block block) {
//        BLOCK = (PillowBlock) block;
//    }
//
//    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
//        getBuilder.apply(MinekeaBlockTags.PILLOWS)
//            .setReplace(false)
//            .add(BLOCK);
//
//        getBuilder.apply(CommonBlockTags.SHEARS_MINEABLE)
//            .setReplace(false)
//            .add(BLOCK);
//    }
//
//    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
//        Block wool = ColorHelpers.getWool(BLOCK.color);
//
//        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, BLOCK, 1)
//            .pattern("##")
//            .pattern("##")
//            .input('#', wool)
//            .criterion(RecipeGenerator.hasItem(wool),
//                generator.conditionsFromItem(wool))
//            .offerTo(exporter);
//
//        generator.createShapeless(RecipeCategory.BUILDING_BLOCKS, wool, 4)
//            .input(BLOCK)
//            .criterion(RecipeGenerator.hasItem(BLOCK),
//                generator.conditionsFromItem(BLOCK))
//            .offerTo(exporter);
//    }
//
//    public void configureBlockLootTables(BlockLootTableGenerator generator) {
//        generator.addDrop(BLOCK);
//    }
//
//    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
//        translationBuilder.add(BLOCK, String.format("%s Pillow", ColorHelpers.getName(BLOCK.color)));
//    }
//
//    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
//        blockStateModelGenerator.registerSimpleCubeAll(BLOCK);
//    }
//}

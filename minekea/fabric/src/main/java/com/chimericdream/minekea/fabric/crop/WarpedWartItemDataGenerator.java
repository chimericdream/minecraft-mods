//package com.chimericdream.minekea.fabric.crop;
//
//import com.chimericdream.lib.fabric.items.FabricItemDataGenerator;
//import com.chimericdream.minekea.crop.ModCrops;
//import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
//import net.minecraft.block.Blocks;
//import net.minecraft.data.server.recipe.RecipeExporter;
//import net.minecraft.item.Item;
//import net.minecraft.recipe.book.RecipeCategory;
//import net.minecraft.registry.RegistryWrapper;
//
//public class WarpedWartItemDataGenerator implements FabricItemDataGenerator {
//    public static final Item ITEM = ModCrops.WARPED_WART_ITEM.get();
//
//    @Override
//    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
//        generator.createShapeless(RecipeCategory.BUILDING_BLOCKS, ITEM, 9)
//            .input(Blocks.WARPED_WART_BLOCK)
//            .criterion(RecipeGenerator.hasItem(Blocks.WARPED_WART_BLOCK),
//                generator.conditionsFromItem(Blocks.WARPED_WART_BLOCK))
//            .offerTo(exporter);
//
//        generator.createShaped(RecipeCategory.BUILDING_BLOCKS, Blocks.WARPED_WART_BLOCK, 1)
//            .pattern("###")
//            .pattern("###")
//            .pattern("###")
//            .input('#', ITEM)
//            .criterion(RecipeGenerator.hasItem(ITEM),
//                generator.conditionsFromItem(ITEM))
//            .offerTo(exporter);
//    }
//
//    @Override
//    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
//        translationBuilder.add(ITEM, "Warped Wart");
//    }
//}

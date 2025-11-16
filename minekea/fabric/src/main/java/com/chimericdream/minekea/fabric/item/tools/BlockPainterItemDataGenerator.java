//package com.chimericdream.minekea.fabric.item.tools;
//
//import com.chimericdream.lib.fabric.items.FabricItemDataGenerator;
//import com.chimericdream.minekea.item.Tools;
//import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
//import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
//import net.minecraft.data.server.recipe.RecipeExporter;
//import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
//import net.minecraft.item.Item;
//import net.minecraft.item.Items;
//import net.minecraft.recipe.book.RecipeCategory;
//import net.minecraft.registry.RegistryWrapper;
//import net.minecraft.registry.tag.ItemTags;
//
//public class BlockPainterItemDataGenerator implements FabricItemDataGenerator {
//    public final Item ITEM;
//
//    public BlockPainterItemDataGenerator() {
//        ITEM = Tools.BLOCK_PAINTER_ITEM.get();
//    }
//
/// /    @Override
/// /    public void register() {
/// /        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
/// /            .register((itemGroup) -> {
/// /                itemGroup.add(this);
/// /            });
/// /    }
//
//    @Override
//    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
//        generator.createShaped(RecipeCategory.TOOLS, ITEM, 1)
//            .pattern(" NW")
//            .pattern(" SN")
//            .pattern("S  ")
//            .input('N', Items.IRON_NUGGET)
//            .input('S', Items.STICK)
//            .input('W', ItemTags.WOOL)
//            .criterion(RecipeGenerator.hasItem(Items.IRON_NUGGET),
//                generator.conditionsFromItem(Items.IRON_NUGGET))
//            .criterion(RecipeGenerator.hasItem(Items.STICK),
//                generator.conditionsFromItem(Items.STICK))
//            .criterion("has_wool", FabricRecipeProvider.conditionsFromTag(ItemTags.WOOL))
//            .offerTo(exporter);
//    }
//
//    @Override
//    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
//        translationBuilder.add(ITEM, "Block Painter");
//    }
//}

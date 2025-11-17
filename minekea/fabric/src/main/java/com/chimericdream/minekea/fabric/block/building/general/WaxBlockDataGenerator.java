package com.chimericdream.minekea.fabric.block.building.general;

import com.chimericdream.lib.colors.ColorHelpers;
import com.chimericdream.minekea.block.building.general.WaxBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.item.WaxItems;
import com.chimericdream.minekea.item.ingredients.WaxItem;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Function;

public class WaxBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public WaxBlock BLOCK;

    public WaxBlockDataGenerator(Block block) {
        BLOCK = (WaxBlock) block;
    }

    public Block getBlock() {
        return BLOCK;
    }

    @Override
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        getBuilder.apply(BlockTags.HOE_MINEABLE)
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        Item ingredient = WaxItems.WAX_ITEMS.getOrDefault(BLOCK.color, WaxItems.WAX_ITEMS.get("plain")).get();

        generator.createShaped(RecipeCategory.DECORATIONS, BLOCK, 1)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .input('#', ingredient)
            .criterion(RecipeGenerator.hasItem(ingredient), generator.conditionsFromItem(ingredient))
            .offerTo(exporter);

        generator.createShapeless(RecipeCategory.DECORATIONS, ingredient, 9)
            .input(BLOCK)
            .criterion(RecipeGenerator.hasItem(BLOCK),
                generator.conditionsFromItem(BLOCK))
            .offerTo(exporter, WaxItem.makeId(BLOCK.color).withSuffixedPath("_from_block").toString());
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        if (BLOCK.color.equals("plain")) {
            translationBuilder.add(BLOCK, "Wax Block");
            translationBuilder.add(BLOCK.asItem(), "Wax Block");

            return;
        }

        translationBuilder.add(BLOCK, String.format("%s Wax Block", ColorHelpers.getName(BLOCK.color)));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Wax Block", ColorHelpers.getName(BLOCK.color)));
    }

    @Override
    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(BLOCK);
    }
}

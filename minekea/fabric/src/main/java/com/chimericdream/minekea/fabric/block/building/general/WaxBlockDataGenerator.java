package com.chimericdream.minekea.fabric.block.building.general;

import com.chimericdream.lib.colors.ColorHelpers;
import com.chimericdream.minekea.block.building.general.WaxBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.item.WaxItems;
import com.chimericdream.minekea.item.ingredients.WaxItem;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

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
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block, Block>> getBuilder) {
        getBuilder.apply(BlockTags.MINEABLE_WITH_HOE)
            .setReplace(false)
            .add(BLOCK);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Item ingredient = WaxItems.WAX_ITEMS.getOrDefault(BLOCK.color, WaxItems.WAX_ITEMS.get("plain")).get();

        generator.shaped(RecipeCategory.DECORATIONS, BLOCK, 1)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .define('#', ingredient)
            .unlockedBy(RecipeProvider.getHasName(ingredient), generator.has(ingredient))
            .save(exporter);

        generator.shapeless(RecipeCategory.DECORATIONS, ingredient, 9)
            .requires(BLOCK)
            .unlockedBy(RecipeProvider.getHasName(BLOCK),
                generator.has(BLOCK))
            .save(exporter, WaxItem.makeId(BLOCK.color).withSuffix("_from_block").toString());
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        if (BLOCK.color.equals("plain")) {
            translationBuilder.add(BLOCK, "Wax Block");
            translationBuilder.add(BLOCK.asItem(), "Wax Block");

            return;
        }

        translationBuilder.add(BLOCK, String.format("%s Wax Block", ColorHelpers.getName(BLOCK.color)));
        translationBuilder.add(BLOCK.asItem(), String.format("%s Wax Block", ColorHelpers.getName(BLOCK.color)));
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.createTrivialCube(BLOCK);
    }
}

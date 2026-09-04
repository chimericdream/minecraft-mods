package com.chimericdream.jdcrafte.fabric.block;

import com.chimericdream.jdcrafte.JDCrafteMod;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import com.chimericdream.lib.fabric.blocks.TranslationUtils;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Function;

/**
 * Every {@code ROTATION} value shares the exact same static model - see {@code WeathervaneBlock}'s
 * class doc for why the rotation has to be applied at render time instead of baked into per-value
 * blockstate variants.
 */
public class WeathervaneBlockDataGenerator implements FabricBlockDataGenerator {
    protected final Block block;

    public WeathervaneBlockDataGenerator(Block block) {
        this.block = block;
    }

    private static Identifier modelId(String name) {
        return Identifier.fromNamespaceAndPath(JDCrafteMod.MOD_ID, "block/" + name);
    }

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBuilder.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(block.builtInRegistryHolder().key());
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(block);
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        generator.shaped(RecipeCategory.DECORATIONS, block)
            .pattern("   ")
            .pattern("F E")
            .pattern("III")
            .define('F', Items.FLINT)
            .define('E', ItemTags.EGGS)
            .define('I', Blocks.IRON_BARS)
            .unlockedBy(RecipeProvider.getHasName(Items.FLINT), generator.has(Items.FLINT))
            .unlockedBy("has_egg", generator.has(ItemTags.EGGS))
            .unlockedBy(RecipeProvider.getHasName(Blocks.IRON_BARS), generator.has(Blocks.IRON_BARS))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        TranslationUtils.addBlockAndItem(translationBuilder, block, "Weathervane");
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        MultiVariant baseModel = BlockModelGenerators.plainVariant(modelId("weathervane"));

        blockStateModelGenerator.registerSimpleItemModel(block, modelId("weathervane"));
        blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, baseModel));
    }
}

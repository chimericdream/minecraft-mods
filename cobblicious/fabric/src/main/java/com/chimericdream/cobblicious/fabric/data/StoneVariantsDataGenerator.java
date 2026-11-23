package com.chimericdream.cobblicious.fabric.data;

import com.chimericdream.cobblicious.block.StoneVariants;
import com.chimericdream.cobblicious.block.StoneVariants.StoneVariant;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import com.chimericdream.lib.fabric.blocks.TranslationUtils;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

/**
 * Data generation for the full blocks in {@link StoneVariants}. The stairs, slab, and wall of each
 * block come from chimeric-lib's block family generators.
 */
public class StoneVariantsDataGenerator implements FabricBlockDataGenerator {
    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        TagAppender<Block> pickaxe = getBuilder.apply(BlockTags.MINEABLE_WITH_PICKAXE).setReplace(false);

        for (StoneVariant variant : StoneVariants.ALL) {
            pickaxe.add(variant.block().get().builtInRegistryHolder().key());
        }
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        for (StoneVariant variant : StoneVariants.ALL) {
            Block result = variant.block().get();

            generator.shapeless(RecipeCategory.BUILDING_BLOCKS, result, 1)
                .requires(variant.base())
                .requires(variant.vine())
                .unlockedBy(RecipeProvider.getHasName(variant.base()), generator.has(variant.base()))
                .unlockedBy(RecipeProvider.getHasName(variant.vine()), generator.has(variant.vine()))
                .save(exporter);
            generator.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, variant.base(), result, 1);
        }
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        for (StoneVariant variant : StoneVariants.ALL) {
            TranslationUtils.addBlockAndItem(translationBuilder, variant.block().get(), variant.name());
        }
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        for (StoneVariant variant : StoneVariants.ALL) {
            generator.dropSelf(variant.block().get());
        }
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        for (StoneVariant variant : StoneVariants.ALL) {
            Block block = variant.block().get();

            if (variant.topTexture()) {
                // The same model that vanilla uses for blackstone: the side texture on the sides and
                // <block>_top on the top and bottom.
                blockStateModelGenerator.createTrivialBlock(block, TexturedModel.COLUMN_WITH_WALL);
            } else {
                blockStateModelGenerator.createTrivialCube(block);
            }
        }
    }
}

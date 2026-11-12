package com.chimericdream.cobblicious.fabric.data;

import com.chimericdream.cobblicious.block.ModBlocks;
import com.chimericdream.cobblicious.crop.ModCrops;
import com.chimericdream.lib.fabric.blocks.FabricBlockDataGenerator;
import com.chimericdream.lib.fabric.blocks.TranslationUtils;
import dev.architectury.registry.registries.RegistrySupplier;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.function.Function;

/**
 * Data generation for the full blocks in {@link ModBlocks}. The stairs, slab, and wall of each block
 * come from chimeric-lib's block family generators.
 */
public class BasaltBlocksDataGenerator implements FabricBlockDataGenerator {
    private static final String RECIPE_GROUP = "cobblicious";

    @Override
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        TagAppender<Block> pickaxe = getBuilder.apply(BlockTags.MINEABLE_WITH_PICKAXE).setReplace(false);

        for (RegistrySupplier<Block> block : ModBlocks.ALL) {
            pickaxe.add(block.get().builtInRegistryHolder().key());
        }
    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Block bricks = ModBlocks.BASALT_BRICKS.get();

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, bricks, 4)
            .pattern("##")
            .pattern("##")
            .define('#', Blocks.SMOOTH_BASALT)
            .unlockedBy(RecipeProvider.getHasName(Blocks.SMOOTH_BASALT), generator.has(Blocks.SMOOTH_BASALT))
            .save(exporter);
        generator.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, bricks, Blocks.SMOOTH_BASALT, 1);
        generator.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_BASALT, bricks, 1);

        Block chiseled = ModBlocks.CHISELED_BASALT_BRICKS.get();

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, chiseled, 4)
            .pattern("##")
            .pattern("##")
            .define('#', bricks)
            .unlockedBy(RecipeProvider.getHasName(bricks), generator.has(bricks))
            .save(exporter);
        generator.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, chiseled, bricks, 1);
        generator.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, bricks, chiseled, 1);
        generator.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, chiseled, Blocks.SMOOTH_BASALT, 1);
        generator.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, Blocks.SMOOTH_BASALT, chiseled, 1);

        Block cracked = ModBlocks.CRACKED_BASALT_BRICKS.get();

        generator.oreSmelting(List.of(bricks), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS, cracked, 0.1f, 200, RECIPE_GROUP);
        generator.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, cracked, bricks, 1);
        generator.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, bricks, cracked, 1);

        vinedBricks(generator, exporter, ModBlocks.CRIMSON_BASALT_BRICKS.get(), Blocks.WEEPING_VINES);
        vinedBricks(generator, exporter, ModBlocks.MOSSY_BASALT_BRICKS.get(), Blocks.VINE);
        vinedBricks(generator, exporter, ModBlocks.WARPED_BASALT_BRICKS.get(), Blocks.TWISTING_VINES);

        Item warpedWart = ModCrops.WARPED_WART_ITEM.get();

        generator.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WARPED_NETHER_BRICKS.get(), 1)
            .pattern("AB")
            .pattern("BA")
            .define('A', warpedWart)
            .define('B', Items.NETHER_BRICK)
            .unlockedBy(RecipeProvider.getHasName(warpedWart), generator.has(warpedWart))
            .unlockedBy(RecipeProvider.getHasName(Items.NETHER_BRICK), generator.has(Items.NETHER_BRICK))
            .save(exporter);
    }

    private static void vinedBricks(RecipeProvider generator, RecipeOutput exporter, Block result, Block vine) {
        Block bricks = ModBlocks.BASALT_BRICKS.get();

        generator.shapeless(RecipeCategory.BUILDING_BLOCKS, result, 1)
            .requires(bricks)
            .requires(vine)
            .unlockedBy(RecipeProvider.getHasName(bricks), generator.has(bricks))
            .unlockedBy(RecipeProvider.getHasName(vine), generator.has(vine))
            .save(exporter);
        generator.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, bricks, result, 1);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        TranslationUtils.addBlockAndItem(translationBuilder, ModBlocks.BASALT_BRICKS.get(), "Basalt Bricks");
        TranslationUtils.addBlockAndItem(translationBuilder, ModBlocks.CHISELED_BASALT_BRICKS.get(), "Chiseled Basalt Bricks");
        TranslationUtils.addBlockAndItem(translationBuilder, ModBlocks.CRACKED_BASALT_BRICKS.get(), "Cracked Basalt Bricks");
        TranslationUtils.addBlockAndItem(translationBuilder, ModBlocks.CRIMSON_BASALT_BRICKS.get(), "Crimson Basalt Bricks");
        TranslationUtils.addBlockAndItem(translationBuilder, ModBlocks.MOSSY_BASALT_BRICKS.get(), "Mossy Basalt Bricks");
        TranslationUtils.addBlockAndItem(translationBuilder, ModBlocks.WARPED_BASALT_BRICKS.get(), "Warped Basalt Bricks");
        TranslationUtils.addBlockAndItem(translationBuilder, ModBlocks.WARPED_NETHER_BRICKS.get(), "Warped Nether Bricks");
    }

    @Override
    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        for (RegistrySupplier<Block> block : ModBlocks.ALL) {
            generator.dropSelf(block.get());
        }
    }

    @Override
    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        for (RegistrySupplier<Block> block : ModBlocks.ALL) {
            blockStateModelGenerator.createTrivialCube(block.get());
        }
    }
}

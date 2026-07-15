package com.chimericdream.minekea.fabric.block.decorations;

import com.chimericdream.minekea.block.decorations.lighting.LanternBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
import com.chimericdream.minekea.tag.MinekeaBlockTags;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class LanternBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public LanternBlock BLOCK;

    public LanternBlockDataGenerator(Block block) {
        BLOCK = (LanternBlock) block;
    }

    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBuilder.apply(MinekeaBlockTags.LANTERNS).add(BLOCK.builtInRegistryHolder().key());
    }

    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        Item ingredient = BLOCK.config.getItem();

        generator.shaped(RecipeCategory.DECORATIONS, BLOCK, 1)
            .pattern("###")
            .pattern("#P#")
            .pattern("#T#")
            .define('#', Items.IRON_NUGGET)
            .define('P', Items.ENDER_PEARL)
            .define('T', Items.TORCH)
            .unlockedBy(RecipeProvider.getHasName(Items.IRON_NUGGET),
                generator.has(Items.IRON_NUGGET))
            .unlockedBy(RecipeProvider.getHasName(ingredient),
                generator.has(ingredient))
            .unlockedBy(RecipeProvider.getHasName(Items.TORCH),
                generator.has(Items.TORCH))
            .save(exporter);
    }

    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(BLOCK, BLOCK.config.getName());
        translationBuilder.add(BLOCK.asItem(), BLOCK.config.getName());
    }

    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(BLOCK);
    }

    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        ModelUtils.registerLanternBlock(blockStateModelGenerator, BLOCK, BLOCK.BLOCK_ID);
    }

    public void configureItemModels(ItemModelGenerators itemModelGenerator) {
        ModelUtils.registerGeneratedItem(itemModelGenerator, BLOCK);
    }
}

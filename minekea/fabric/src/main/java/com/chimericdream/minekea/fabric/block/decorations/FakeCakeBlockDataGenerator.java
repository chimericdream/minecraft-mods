package com.chimericdream.minekea.fabric.block.decorations;

import com.chimericdream.lib.tags.CommonBlockTags;
import com.chimericdream.minekea.block.decorations.DecorationBlocks;
import com.chimericdream.minekea.block.decorations.FakeCakeBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Function;

public class FakeCakeBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public void configureBlockTags(HolderLookup.Provider registryLookup, Function<TagKey<Block>, TagAppender<Block>> getBuilder) {
        getBuilder.apply(CommonBlockTags.SHEARS_MINEABLE)
            .setReplace(false)
            .add(DecorationBlocks.FAKE_CAKE.get().builtInRegistryHolder().key());
    }

    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        generator.shaped(RecipeCategory.DECORATIONS, DecorationBlocks.FAKE_CAKE.get(), 3)
            .pattern("AAA")
            .pattern("BEB")
            .pattern("CCC")
            .define('A', Items.CARPET.white())
            .define('B', Items.DYE.white())
            .define('C', Items.WOOL.brown())
            .define('E', Items.REDSTONE)
            .unlockedBy(RecipeProvider.getHasName(Items.CARPET.white()),
                generator.has(Items.CARPET.white()))
            .unlockedBy(RecipeProvider.getHasName(Items.DYE.white()),
                generator.has(Items.DYE.white()))
            .unlockedBy(RecipeProvider.getHasName(Items.WOOL.brown()),
                generator.has(Items.WOOL.brown()))
            .unlockedBy(RecipeProvider.getHasName(Items.REDSTONE),
                generator.has(Items.REDSTONE))
            .save(exporter);
    }

    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(DecorationBlocks.FAKE_CAKE.get(), "Cake");
        translationBuilder.add(DecorationBlocks.FAKE_CAKE.get().asItem(), "Cake");
        translationBuilder.add(FakeCakeBlock.TOOLTIP_KEY, "This cake is a lie!");
    }

    public void configureBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        Identifier identifier = ModelLocationUtils.getModelLocation(Blocks.CAKE);
        MultiVariant model = BlockModelGenerators.plainVariant(identifier);

        blockStateModelGenerator.registerSimpleItemModel(DecorationBlocks.FAKE_CAKE.get(), ModelLocationUtils.getModelLocation(Items.CAKE));

        blockStateModelGenerator.blockStateOutput
            .accept(MultiVariantGenerator.dispatch(DecorationBlocks.FAKE_CAKE.get(), model));
    }

    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(DecorationBlocks.FAKE_CAKE.get());
    }
}

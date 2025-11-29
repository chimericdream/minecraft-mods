package com.chimericdream.minekea.fabric.block.decorations;

import com.chimericdream.lib.tags.CommonBlockTags;
import com.chimericdream.minekea.block.decorations.DecorationBlocks;
import com.chimericdream.minekea.block.decorations.FakeCakeBlock;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import com.chimericdream.minekea.fabric.data.model.ModelUtils;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ModelIds;
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class FakeCakeBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public void configureBlockTags(RegistryWrapper.WrapperLookup registryLookup, Function<TagKey<Block>, ProvidedTagBuilder<Block, Block>> getBuilder) {
        getBuilder.apply(CommonBlockTags.SHEARS_MINEABLE)
            .setReplace(false)
            .add(DecorationBlocks.FAKE_CAKE.get());
    }

    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        generator.createShaped(RecipeCategory.DECORATIONS, DecorationBlocks.FAKE_CAKE.get(), 3)
            .pattern("AAA")
            .pattern("BEB")
            .pattern("CCC")
            .input('A', Items.WHITE_CARPET)
            .input('B', Items.WHITE_DYE)
            .input('C', Items.BROWN_WOOL)
            .input('E', Items.REDSTONE)
            .criterion(RecipeGenerator.hasItem(Items.WHITE_CARPET),
                generator.conditionsFromItem(Items.WHITE_CARPET))
            .criterion(RecipeGenerator.hasItem(Items.WHITE_DYE),
                generator.conditionsFromItem(Items.WHITE_DYE))
            .criterion(RecipeGenerator.hasItem(Items.BROWN_WOOL),
                generator.conditionsFromItem(Items.BROWN_WOOL))
            .criterion(RecipeGenerator.hasItem(Items.REDSTONE),
                generator.conditionsFromItem(Items.REDSTONE))
            .offerTo(exporter);
    }

    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(DecorationBlocks.FAKE_CAKE.get(), "Cake");
        translationBuilder.add(DecorationBlocks.FAKE_CAKE.get().asItem(), "Cake");
        translationBuilder.add(FakeCakeBlock.TOOLTIP_KEY, "This cake is a lie!");
    }

    public void configureBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Identifier identifier = ModelIds.getBlockModelId(Blocks.CAKE);
        WeightedVariant model = BlockStateModelGenerator.createWeightedVariant(identifier);

        blockStateModelGenerator.blockStateCollector
            .accept(VariantsBlockModelDefinitionCreator.of(DecorationBlocks.FAKE_CAKE.get(), model));
    }

    public void configureBlockLootTables(BlockLootTableGenerator generator, RegistryWrapper.WrapperLookup registryLookup) {
        generator.addDrop(DecorationBlocks.FAKE_CAKE.get());
    }

    public void configureItemModels(ItemModelGenerator itemModelGenerator) {
        ModelUtils.registerGeneratedItem(itemModelGenerator, DecorationBlocks.FAKE_CAKE.get());
    }
}

package com.chimericdream.minekea.fabric.block.decorations;

import com.chimericdream.minekea.block.decorations.DecorationBlocks;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.data.loottable.BlockLootTableGenerator;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

public class EndlessRodBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public void configureRecipes(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter, RecipeGenerator generator) {
        generator.createShapeless(RecipeCategory.DECORATIONS, DecorationBlocks.ENDLESS_ROD.get(), 1)
            .input(Items.END_ROD)
            .criterion(RecipeGenerator.hasItem(Items.END_ROD),
                generator.conditionsFromItem(Items.END_ROD))
            .offerTo(exporter);

        generator.createShapeless(RecipeCategory.DECORATIONS, Items.END_ROD, 1)
            .input(DecorationBlocks.ENDLESS_ROD.get())
            .criterion(RecipeGenerator.hasItem(DecorationBlocks.ENDLESS_ROD.get()),
                generator.conditionsFromItem(DecorationBlocks.ENDLESS_ROD.get()))
            .offerTo(exporter);
    }

    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(DecorationBlocks.ENDLESS_ROD.get(), "End(less) Rod");
        translationBuilder.add(DecorationBlocks.ENDLESS_ROD.get().asItem(), "End(less) Rod");
    }

    public void configureBlockLootTables(BlockLootTableGenerator generator) {
        generator.addDrop(DecorationBlocks.ENDLESS_ROD.get());
    }
}

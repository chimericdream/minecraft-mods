package com.chimericdream.minekea.fabric.block.decorations;

import com.chimericdream.minekea.block.decorations.DecorationBlocks;
import com.chimericdream.minekea.fabric.data.ChimericLibBlockDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

public class EndlessRodBlockDataGenerator extends ChimericLibBlockDataGenerator {
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        generator.shapeless(RecipeCategory.DECORATIONS, DecorationBlocks.ENDLESS_ROD.get(), 1)
            .requires(Items.END_ROD)
            .unlockedBy(RecipeProvider.getHasName(Items.END_ROD),
                generator.has(Items.END_ROD))
            .save(exporter);

        generator.shapeless(RecipeCategory.DECORATIONS, Items.END_ROD, 1)
            .requires(DecorationBlocks.ENDLESS_ROD.get())
            .unlockedBy(RecipeProvider.getHasName(DecorationBlocks.ENDLESS_ROD.get()),
                generator.has(DecorationBlocks.ENDLESS_ROD.get()))
            .save(exporter);
    }

    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(DecorationBlocks.ENDLESS_ROD.get(), "End(less) Rod");
        translationBuilder.add(DecorationBlocks.ENDLESS_ROD.get().asItem(), "End(less) Rod");
    }

    public void configureBlockLootTables(BlockLootSubProvider generator, HolderLookup.Provider registryLookup) {
        generator.dropSelf(DecorationBlocks.ENDLESS_ROD.get());
    }
}

package com.chimericdream.minekea.fabric.item.tools;

import com.chimericdream.minekea.fabric.data.ChimericLibItemDataGenerator;
import com.chimericdream.minekea.item.Tools;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class BlockPainterItemDataGenerator extends ChimericLibItemDataGenerator {
    public final Item ITEM;

    public BlockPainterItemDataGenerator() {
        ITEM = Tools.BLOCK_PAINTER_ITEM.get();
    }

//    @Override
//    public void register() {
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
//            .register((itemGroup) -> {
//                itemGroup.add(this);
//            });
//    }

    @Override
    public void configureRecipes(HolderLookup.Provider registryLookup, RecipeOutput exporter, RecipeProvider generator) {
        generator.shaped(RecipeCategory.TOOLS, ITEM, 1)
            .pattern(" NW")
            .pattern(" SN")
            .pattern("S  ")
            .define('N', Items.IRON_NUGGET)
            .define('S', Items.STICK)
            .define('W', ItemTags.WOOL)
            .unlockedBy(RecipeProvider.getHasName(Items.IRON_NUGGET),
                generator.has(Items.IRON_NUGGET))
            .unlockedBy(RecipeProvider.getHasName(Items.STICK),
                generator.has(Items.STICK))
            .unlockedBy("has_wool", generator.has(ItemTags.WOOL))
            .save(exporter);
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(ITEM, "Block Painter");
    }
}

package com.chimericdream.minekea.fabric.item.tools;

import com.chimericdream.lib.tags.CommonItemTags;
import com.chimericdream.minekea.fabric.data.ChimericLibItemDataGenerator;
import com.chimericdream.minekea.item.Tools;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Function;

public class WrenchItemDataGenerator extends ChimericLibItemDataGenerator {
    private final Item ITEM;

    public WrenchItemDataGenerator() {
        ITEM = Tools.WRENCH_ITEM.get();
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
            .pattern(" # ")
            .pattern(" ##")
            .pattern("#  ")
            .define('#', Items.IRON_INGOT)
            .unlockedBy(RecipeProvider.getHasName(Items.IRON_INGOT),
                generator.has(Items.IRON_INGOT))
            .save(exporter);
    }

    @Override
    public void configureItemTags(HolderLookup.Provider registryLookup, Function<TagKey<Item>, TagAppender<Item>> getBuilder) {
        getBuilder.apply(CommonItemTags.WRENCHES)
            .setReplace(false)
            .add(ITEM.builtInRegistryHolder().key());
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(ITEM, "Wrench");
    }
}

package com.chimericdream.lib.fabric.items;

import com.chimericdream.lib.items.ItemDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface FabricItemDataGenerator extends ItemDataGenerator {
    default void configureItemTags(HolderLookup.Provider registryLookup, Function<TagKey<Item>, FabricTagsProvider.ItemTagsProvider> getBuilder) {
    }

    default void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
    }
}

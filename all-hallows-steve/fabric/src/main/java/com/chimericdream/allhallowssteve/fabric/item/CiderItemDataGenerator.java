package com.chimericdream.allhallowssteve.fabric.item;

import com.chimericdream.allhallowssteve.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.HolderLookup;

/** The two items the cider press makes. Both use a plain flat item model. */
public class CiderItemDataGenerator {
    public static void configureItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(ModItems.CIDER.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.POMACE.get(), ModelTemplates.FLAT_ITEM);
    }

    public static void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.CIDER.get(), "Cider");
        translationBuilder.add(ModItems.POMACE.get(), "Pomace");
    }
}

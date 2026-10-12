package com.chimericdream.allhallowssteve.fabric.item;

import com.chimericdream.allhallowssteve.item.ModItems;
import com.chimericdream.allhallowssteve.item.MulledAppleCider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.HolderLookup;

/** Cider and pomace from the press, plus the mulled cider ladled out of a cauldron. Both use a plain flat item model. */
public class CiderItemDataGenerator {
    public static void configureItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(ModItems.APPLE_CIDER.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.MULLED_APPLE_CIDER.get(), ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.POMACE.get(), ModelTemplates.FLAT_ITEM);
    }

    public static void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.APPLE_CIDER.get(), "Apple Cider");
        translationBuilder.add(ModItems.MULLED_APPLE_CIDER.get(), "Mulled Apple Cider");
        translationBuilder.add(MulledAppleCider.SPICES_TOOLTIP_KEY, "Spices: %s");
        translationBuilder.add(ModItems.POMACE.get(), "Pomace");
    }
}

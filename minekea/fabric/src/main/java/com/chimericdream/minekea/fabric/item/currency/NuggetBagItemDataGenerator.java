package com.chimericdream.minekea.fabric.item.currency;

import com.chimericdream.minekea.fabric.data.ChimericLibItemDataGenerator;
import com.chimericdream.minekea.item.currency.NuggetBag;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;

public class NuggetBagItemDataGenerator extends ChimericLibItemDataGenerator {
    public NuggetBag ITEM;

    public NuggetBagItemDataGenerator(Item item) {
        ITEM = (NuggetBag) item;
    }

    @Override
    public void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(ITEM, String.format("%s Nugget Bag", ITEM.materialName));
    }
}

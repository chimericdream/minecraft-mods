package com.chimericdream.minekea.fabric.item.currency;

import com.chimericdream.minekea.fabric.data.ChimericLibItemDataGenerator;
import com.chimericdream.minekea.item.currency.NuggetBag;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;

public class NuggetBagItemDataGenerator extends ChimericLibItemDataGenerator {
    public NuggetBag ITEM;

    public NuggetBagItemDataGenerator(Item item) {
        ITEM = (NuggetBag) item;
    }

    @Override
    public void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(ITEM, String.format("%s Nugget Bag", ITEM.materialName));
    }
}

package com.chimericdream.lib.fabric.blocks;

import com.chimericdream.lib.blocks.BlockDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

public interface FabricBlockDataGenerator extends BlockDataGenerator {
    default void configureTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
    }
}

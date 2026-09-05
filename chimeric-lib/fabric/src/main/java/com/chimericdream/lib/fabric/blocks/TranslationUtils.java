package com.chimericdream.lib.fabric.blocks;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.world.level.block.Block;

public class TranslationUtils {
    /**
     * The {@code translationBuilder.add(block, name); translationBuilder.add(block.asItem(), name);}
     * idiom repeated across nearly every block datagen class's {@code configureTranslations}.
     */
    public static void addBlockAndItem(FabricLanguageProvider.TranslationBuilder translationBuilder, Block block, String name) {
        translationBuilder.add(block, name);
        translationBuilder.add(block.asItem(), name);
    }
}

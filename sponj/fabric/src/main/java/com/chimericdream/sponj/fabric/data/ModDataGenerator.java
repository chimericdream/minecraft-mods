package com.chimericdream.sponj.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        // pack.addProvider(ModRecipeProvider::new);
        // pack.addProvider(ModBlockLootTables::new);
        // pack.addProvider(ModEnglishLangProvider::new);
    }
}

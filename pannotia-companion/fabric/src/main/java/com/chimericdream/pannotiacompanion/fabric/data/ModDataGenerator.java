package com.chimericdream.pannotiacompanion.fabric.data;

import com.chimericdream.pannotiacompanion.fabric.villager.PannotiaCompanionVillagerTradeProvider;
import com.chimericdream.pannotiacompanion.villager.ModTradeSets;
import com.chimericdream.pannotiacompanion.villager.ModVillagerTrades;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(PannotiaCompanionVillagerTradeProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.VILLAGER_TRADE, ModVillagerTrades::bootstrap);
        registryBuilder.add(Registries.TRADE_SET, ModTradeSets::bootstrap);
    }
}

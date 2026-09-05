package com.chimericdream.archaeologytweaks.fabric.data;

import com.chimericdream.archaeologytweaks.fabric.worldgen.ArchaeologyTweaksConfiguredFeatures;
import com.chimericdream.archaeologytweaks.fabric.worldgen.ArchaeologyTweaksPlacedFeatures;
import com.chimericdream.archaeologytweaks.fabric.villager.ArchaeologyTweaksVillagerTradeProvider;
import com.chimericdream.archaeologytweaks.fabric.worldgen.ArchaeologyTweaksWorldgenProvider;
import com.chimericdream.archaeologytweaks.villager.ModTradeSets;
import com.chimericdream.archaeologytweaks.villager.ModVillagerTrades;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(ArchaeologyTweaksWorldgenProvider::new);
        pack.addProvider(ArchaeologyTweaksVillagerTradeProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, ArchaeologyTweaksConfiguredFeatures::configure);
        registryBuilder.add(Registries.PLACED_FEATURE, ArchaeologyTweaksPlacedFeatures::configure);
        registryBuilder.add(Registries.VILLAGER_TRADE, ModVillagerTrades::bootstrap);
        registryBuilder.add(Registries.TRADE_SET, ModTradeSets::bootstrap);
    }
}

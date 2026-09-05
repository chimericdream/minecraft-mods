package com.chimericdream.artificialheart.fabric.data;

import com.chimericdream.artificialheart.fabric.worldgen.ArtificialHeartConfiguredFeatures;
import com.chimericdream.artificialheart.fabric.worldgen.ArtificialHeartPlacedFeatures;
import com.chimericdream.artificialheart.fabric.worldgen.ArtificialHeartWorldgenProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(ArtificialHeartWorldgenProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.CONFIGURED_FEATURE, ArtificialHeartConfiguredFeatures::configure);
        registryBuilder.add(Registries.PLACED_FEATURE, ArtificialHeartPlacedFeatures::configure);
    }
}

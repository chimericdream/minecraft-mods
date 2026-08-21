package com.chimericdream.effectivegear.fabric.data;

import com.chimericdream.effectivegear.item.armor.Trims;
import com.chimericdream.effectivegear.fabric.worldgen.EffectiveGearTrimProvider;
import com.chimericdream.lib.trims.ArmorTrimAtlasProvider;
import com.chimericdream.lib.trims.TrimMaterialConfig;
import com.chimericdream.lib.trims.TrimMaterialRegistryHelper;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

import java.util.concurrent.CompletableFuture;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(EffectiveGearTrimProvider::new);
        pack.addProvider((FabricDataGenerator.Pack.Factory<ArmorTrimAtlasProvider>) output -> new ArmorTrimAtlasProvider(output, Trims.MATERIALS));
        pack.addProvider(EffectiveGearLangProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.TRIM_MATERIAL, context -> TrimMaterialRegistryHelper.bootstrap(context, Trims.MATERIALS));
    }

    private static class EffectiveGearLangProvider extends FabricLanguageProvider {
        protected EffectiveGearLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
            for (TrimMaterialConfig material : Trims.MATERIALS) {
                translationBuilder.add(material.translationKey(), material.displayName());
            }
        }
    }
}

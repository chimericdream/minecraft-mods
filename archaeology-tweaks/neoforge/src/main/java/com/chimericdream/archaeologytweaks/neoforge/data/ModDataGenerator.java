package com.chimericdream.archaeologytweaks.neoforge.data;

import com.chimericdream.archaeologytweaks.ModInfo;
import com.chimericdream.archaeologytweaks.neoforge.worldgen.ArchaeologyTweaksBiomeModifiers;
import com.chimericdream.archaeologytweaks.neoforge.worldgen.ArchaeologyTweaksConfiguredFeatures;
import com.chimericdream.archaeologytweaks.neoforge.worldgen.ArchaeologyTweaksPlacedFeatures;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class ModDataGenerator {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Server event) {
        RegistrySetBuilder registryBuilder = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ArchaeologyTweaksConfiguredFeatures::configure)
            .add(Registries.PLACED_FEATURE, ArchaeologyTweaksPlacedFeatures::configure)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ArchaeologyTweaksBiomeModifiers::configure);

        event.createDatapackRegistryObjects(registryBuilder);
    }
}

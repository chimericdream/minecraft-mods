package com.chimericdream.artificialheart.neoforge.data;

import com.chimericdream.artificialheart.ModInfo;
import com.chimericdream.artificialheart.neoforge.worldgen.ArtificialHeartBiomeModifiers;
import com.chimericdream.artificialheart.neoforge.worldgen.ArtificialHeartConfiguredFeatures;
import com.chimericdream.artificialheart.neoforge.worldgen.ArtificialHeartPlacedFeatures;
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
            .add(Registries.CONFIGURED_FEATURE, ArtificialHeartConfiguredFeatures::configure)
            .add(Registries.PLACED_FEATURE, ArtificialHeartPlacedFeatures::configure)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ArtificialHeartBiomeModifiers::configure);

        event.createDatapackRegistryObjects(registryBuilder);
    }
}

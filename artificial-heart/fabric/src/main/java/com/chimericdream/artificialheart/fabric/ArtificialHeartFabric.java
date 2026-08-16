package com.chimericdream.artificialheart.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;

import com.chimericdream.artificialheart.ArtificialHeartMod;
import com.chimericdream.artificialheart.fabric.worldgen.ArtificialHeartPlacedFeatures;

public final class ArtificialHeartFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ArtificialHeartMod.init();
        ArtificialHeartMod.postInit();

        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(Biomes.PALE_GARDEN),
            GenerationStep.Decoration.VEGETAL_DECORATION,
            ArtificialHeartPlacedFeatures.PALE_PUMPKIN_PATCH_PLACED_KEY
        );
    }
}

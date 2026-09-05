package com.chimericdream.archaeologytweaks.fabric;

import com.chimericdream.archaeologytweaks.ArchaeologyTweaksMod;
import com.chimericdream.archaeologytweaks.fabric.worldgen.ArchaeologyTweaksPlacedFeatures;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;

public final class ArchaeologyTweaksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        ArchaeologyTweaksMod.init();

        registerWorldgen();
    }

    private static void registerWorldgen() {
        BiomeModifications.addFeature(
            BiomeSelectors.tag(BiomeTags.IS_RIVER),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_CLAY_RIVER_PLACED_KEY
        );
        BiomeModifications.addFeature(
            BiomeSelectors.tag(BiomeTags.IS_BEACH),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_CLAY_BEACH_PLACED_KEY
        );
        BiomeModifications.addFeature(
            BiomeSelectors.tag(BiomeTags.IS_FOREST),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_DIRT_PLACED_KEY
        );
        BiomeModifications.addFeature(
            BiomeSelectors.tag(BiomeTags.IS_TAIGA),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_DIRT_TAIGA_PLACED_KEY
        );
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(Biomes.PLAINS),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_DIRT_PLAINS_PLACED_KEY
        );
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(Biomes.SWAMP),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_MUD_SWAMP_PLACED_KEY
        );
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(Biomes.MANGROVE_SWAMP),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_MUD_MANGROVE_SWAMP_PLACED_KEY
        );
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(Biomes.MANGROVE_SWAMP),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_PACKED_MUD_PLACED_KEY
        );
        BiomeModifications.addFeature(
            BiomeSelectors.tag(BiomeTags.IS_BADLANDS),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_RED_SAND_PLACED_KEY
        );
        BiomeModifications.addFeature(
            BiomeSelectors.foundInTheNether(),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_SOUL_SAND_PLACED_KEY
        );
        BiomeModifications.addFeature(
            BiomeSelectors.foundInTheNether(),
            GenerationStep.Decoration.UNDERGROUND_ORES,
            ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_SOUL_SOIL_PLACED_KEY
        );
    }
}

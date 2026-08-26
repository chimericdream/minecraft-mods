package com.chimericdream.archaeologytweaks.neoforge.worldgen;

import com.chimericdream.archaeologytweaks.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * A biome tag's contents can't be dereferenced while the datapack registries are still being built
 * (tags aren't bound until the full datapack loads), so unlike Fabric's {@code BiomeSelectors}, this
 * can't compose a tag-union {@link HolderSet} up front — each tag or explicit biome key is registered
 * as its own modifier instead, letting the tag stay an unresolved reference until it's actually needed.
 */
public class ArchaeologyTweaksBiomeModifiers {
    public static void configure(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        registerTag(context, biomes, placedFeatures, "suspicious_clay_river", BiomeTags.IS_RIVER, ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_CLAY_RIVER_PLACED_KEY);
        registerTag(context, biomes, placedFeatures, "suspicious_clay_beach", BiomeTags.IS_BEACH, ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_CLAY_BEACH_PLACED_KEY);

        registerTag(context, biomes, placedFeatures, "suspicious_dirt_forest", BiomeTags.IS_FOREST, ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_DIRT_PLACED_KEY);
        registerTag(context, biomes, placedFeatures, "suspicious_dirt_taiga", BiomeTags.IS_TAIGA, ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_DIRT_TAIGA_PLACED_KEY);
        registerKeys(context, biomes, placedFeatures, "suspicious_dirt_plains", ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_DIRT_PLAINS_PLACED_KEY, Biomes.PLAINS);

        registerKeys(context, biomes, placedFeatures, "suspicious_mud_swamp", ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_MUD_SWAMP_PLACED_KEY, Biomes.SWAMP);
        registerKeys(context, biomes, placedFeatures, "suspicious_mud_mangrove_swamp", ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_MUD_MANGROVE_SWAMP_PLACED_KEY, Biomes.MANGROVE_SWAMP);
        registerKeys(context, biomes, placedFeatures, "suspicious_packed_mud", ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_PACKED_MUD_PLACED_KEY, Biomes.MANGROVE_SWAMP);

        registerTag(context, biomes, placedFeatures, "suspicious_red_sand", BiomeTags.IS_BADLANDS, ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_RED_SAND_PLACED_KEY);
        registerTag(context, biomes, placedFeatures, "suspicious_rooted_dirt", BiomeTags.IS_FOREST, ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_ROOTED_DIRT_PLACED_KEY);

        registerTag(context, biomes, placedFeatures, "suspicious_soul_sand", BiomeTags.IS_NETHER, ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_SOUL_SAND_PLACED_KEY);
        registerTag(context, biomes, placedFeatures, "suspicious_soul_soil", BiomeTags.IS_NETHER, ArchaeologyTweaksPlacedFeatures.SUSPICIOUS_SOUL_SOIL_PLACED_KEY);
    }

    private static void registerTag(
        BootstrapContext<BiomeModifier> context,
        HolderGetter<Biome> biomes,
        HolderGetter<PlacedFeature> placedFeatures,
        String name,
        net.minecraft.tags.TagKey<Biome> tag,
        ResourceKey<PlacedFeature> featureKey
    ) {
        register(context, name, biomes.getOrThrow(tag), placedFeatures.getOrThrow(featureKey));
    }

    @SafeVarargs
    private static void registerKeys(
        BootstrapContext<BiomeModifier> context,
        HolderGetter<Biome> biomes,
        HolderGetter<PlacedFeature> placedFeatures,
        String name,
        ResourceKey<PlacedFeature> featureKey,
        ResourceKey<Biome>... biomeKeys
    ) {
        Holder<Biome>[] holders = new Holder[biomeKeys.length];
        for (int i = 0; i < biomeKeys.length; i++) {
            holders[i] = biomes.getOrThrow(biomeKeys[i]);
        }

        register(context, name, HolderSet.direct(holders), placedFeatures.getOrThrow(featureKey));
    }

    private static void register(
        BootstrapContext<BiomeModifier> context,
        String name,
        HolderSet<Biome> biomes,
        Holder<PlacedFeature> feature
    ) {
        context.register(
            ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, name)),
            new BiomeModifiers.AddFeaturesBiomeModifier(biomes, HolderSet.direct(feature), Decoration.UNDERGROUND_ORES)
        );
    }
}

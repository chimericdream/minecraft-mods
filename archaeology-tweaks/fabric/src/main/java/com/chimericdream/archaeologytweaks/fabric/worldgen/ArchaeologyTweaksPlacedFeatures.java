package com.chimericdream.archaeologytweaks.fabric.worldgen;

import com.chimericdream.archaeologytweaks.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class ArchaeologyTweaksPlacedFeatures {
    public static final ResourceKey<PlacedFeature> SUSPICIOUS_CLAY_RIVER_PLACED_KEY = key("suspicious_clay_river");
    public static final ResourceKey<PlacedFeature> SUSPICIOUS_CLAY_BEACH_PLACED_KEY = key("suspicious_clay_beach");
    public static final ResourceKey<PlacedFeature> SUSPICIOUS_DIRT_PLACED_KEY = key("suspicious_dirt");
    public static final ResourceKey<PlacedFeature> SUSPICIOUS_DIRT_TAIGA_PLACED_KEY = key("suspicious_dirt_taiga");
    public static final ResourceKey<PlacedFeature> SUSPICIOUS_DIRT_PLAINS_PLACED_KEY = key("suspicious_dirt_plains");
    public static final ResourceKey<PlacedFeature> SUSPICIOUS_MUD_SWAMP_PLACED_KEY = key("suspicious_mud_swamp");
    public static final ResourceKey<PlacedFeature> SUSPICIOUS_MUD_MANGROVE_SWAMP_PLACED_KEY = key("suspicious_mud_mangrove_swamp");
    public static final ResourceKey<PlacedFeature> SUSPICIOUS_PACKED_MUD_PLACED_KEY = key("suspicious_packed_mud");
    public static final ResourceKey<PlacedFeature> SUSPICIOUS_RED_SAND_PLACED_KEY = key("suspicious_red_sand");
    public static final ResourceKey<PlacedFeature> SUSPICIOUS_SOUL_SAND_PLACED_KEY = key("suspicious_soul_sand");
    public static final ResourceKey<PlacedFeature> SUSPICIOUS_SOUL_SOIL_PLACED_KEY = key("suspicious_soul_soil");

    private static ResourceKey<PlacedFeature> key(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, name));
    }

    // Rarity/count below are hand-tuned per block/biome from in-game playtesting, not vanilla
    // defaults. Count must come before the position-randomizing modifiers (see registerDisk /
    // registerOre) or it silently becomes a no-op — it duplicates whatever position already came
    // out of the earlier modifiers rather than generating N independently randomized attempts.
    public static void configure(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        registerDisk(context, configuredFeatures, SUSPICIOUS_CLAY_RIVER_PLACED_KEY, ArchaeologyTweaksConfiguredFeatures.SUSPICIOUS_CLAY_RIVER_CONFIGURED_KEY, 1);
        registerDisk(context, configuredFeatures, SUSPICIOUS_CLAY_BEACH_PLACED_KEY, ArchaeologyTweaksConfiguredFeatures.SUSPICIOUS_CLAY_BEACH_CONFIGURED_KEY, 20);
        registerDisk(context, configuredFeatures, SUSPICIOUS_DIRT_PLACED_KEY, ArchaeologyTweaksConfiguredFeatures.SUSPICIOUS_DIRT_CONFIGURED_KEY, 1);
        registerDisk(context, configuredFeatures, SUSPICIOUS_DIRT_TAIGA_PLACED_KEY, ArchaeologyTweaksConfiguredFeatures.SUSPICIOUS_DIRT_TAIGA_CONFIGURED_KEY, 8);
        registerDisk(context, configuredFeatures, SUSPICIOUS_DIRT_PLAINS_PLACED_KEY, ArchaeologyTweaksConfiguredFeatures.SUSPICIOUS_DIRT_PLAINS_CONFIGURED_KEY, 2);
        registerDisk(context, configuredFeatures, SUSPICIOUS_MUD_SWAMP_PLACED_KEY, ArchaeologyTweaksConfiguredFeatures.SUSPICIOUS_MUD_SWAMP_CONFIGURED_KEY, 16);
        registerDisk(context, configuredFeatures, SUSPICIOUS_MUD_MANGROVE_SWAMP_PLACED_KEY, ArchaeologyTweaksConfiguredFeatures.SUSPICIOUS_MUD_MANGROVE_SWAMP_CONFIGURED_KEY, 40);
        registerDisk(context, configuredFeatures, SUSPICIOUS_PACKED_MUD_PLACED_KEY, ArchaeologyTweaksConfiguredFeatures.SUSPICIOUS_PACKED_MUD_CONFIGURED_KEY, 40);
        registerDisk(context, configuredFeatures, SUSPICIOUS_RED_SAND_PLACED_KEY, ArchaeologyTweaksConfiguredFeatures.SUSPICIOUS_RED_SAND_CONFIGURED_KEY, 25);

        // Not biome-restricted to Soul Sand Valley (see biome injection) — soul sand/soil spawn
        // anywhere in the Nether. Height range matches vanilla's own nether_gold_ore (near the
        // full dimension height, not just the thin band vanilla's ore_soul_sand feature carves).
        registerOre(context, configuredFeatures, SUSPICIOUS_SOUL_SAND_PLACED_KEY, ArchaeologyTweaksConfiguredFeatures.SUSPICIOUS_SOUL_SAND_CONFIGURED_KEY, 2, VerticalAnchor.aboveBottom(10), VerticalAnchor.belowTop(10), 128);
        registerOre(context, configuredFeatures, SUSPICIOUS_SOUL_SOIL_PLACED_KEY, ArchaeologyTweaksConfiguredFeatures.SUSPICIOUS_SOUL_SOIL_CONFIGURED_KEY, 1, VerticalAnchor.aboveBottom(10), VerticalAnchor.belowTop(10), 128);
    }

    private static void registerDisk(
        BootstrapContext<PlacedFeature> context,
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures,
        ResourceKey<PlacedFeature> placedKey,
        ResourceKey<ConfiguredFeature<?, ?>> configuredKey,
        int rarity
    ) {
        Holder<ConfiguredFeature<?, ?>> configuredFeature = configuredFeatures.getOrThrow(configuredKey);

        // CountPlacement must come BEFORE InSquarePlacement/HeightmapPlacement: it doesn't generate
        // N randomized attempts on its own, it just duplicates whatever position already came out
        // of the earlier modifiers. Putting it last (as this used to) meant every "copy" was the
        // same single (x, y, z) — count was nearly a no-op. Vanilla's own disk/ore features always
        // put count first for exactly this reason.
        PlacementUtils.register(
            context,
            placedKey,
            configuredFeature,
            RarityFilter.onAverageOnceEvery(rarity),
            CountPlacement.of(8),
            InSquarePlacement.spread(),
            HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG),
            RandomOffsetPlacement.vertical(ConstantInt.of(-1)),
            BiomeFilter.biome()
        );
    }

    /**
     * Ore-style placement over a fixed Y range instead of a heightmap. The Nether has a solid roof
     * near the top of the world, so a heightmap search there lands at the ceiling instead of the
     * Soul Sand Valley floor and never finds its target block.
     */
    private static void registerOre(
        BootstrapContext<PlacedFeature> context,
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures,
        ResourceKey<PlacedFeature> placedKey,
        ResourceKey<ConfiguredFeature<?, ?>> configuredKey,
        int rarity,
        VerticalAnchor minY,
        VerticalAnchor maxY,
        int count
    ) {
        Holder<ConfiguredFeature<?, ?>> configuredFeature = configuredFeatures.getOrThrow(configuredKey);

        // See the comment in registerDisk: count must come before the modifiers that randomize
        // position, or every duplicate ends up at the same spot.
        PlacementUtils.register(
            context,
            placedKey,
            configuredFeature,
            RarityFilter.onAverageOnceEvery(rarity),
            CountPlacement.of(count),
            InSquarePlacement.spread(),
            HeightRangePlacement.uniform(minY, maxY),
            BiomeFilter.biome()
        );
    }
}

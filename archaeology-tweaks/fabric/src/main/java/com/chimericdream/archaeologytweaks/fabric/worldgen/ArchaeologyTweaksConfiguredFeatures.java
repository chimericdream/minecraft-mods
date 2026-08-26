package com.chimericdream.archaeologytweaks.fabric.worldgen;

import com.chimericdream.archaeologytweaks.ModInfo;
import com.chimericdream.archaeologytweaks.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;

/**
 * Most suspicious blocks are small {@code Feature.DISK} deposits (the same feature type vanilla uses
 * for clay/sand/gravel disks), anchored to the terrain heightmap so they're always exposed to air or
 * water. Rooted dirt and the two Soul Sand Valley blocks use {@code Feature.ORE} instead: rooted dirt
 * doesn't need the exposure guarantee, and heightmaps don't work in the Nether at all (the solid roof
 * near the top of the world means a heightmap search lands at the ceiling, not the valley floor) — see
 * {@link ArchaeologyTweaksPlacedFeatures}.
 */
public class ArchaeologyTweaksConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> SUSPICIOUS_CLAY_RIVER_CONFIGURED_KEY = key("suspicious_clay_river");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SUSPICIOUS_CLAY_BEACH_CONFIGURED_KEY = key("suspicious_clay_beach");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SUSPICIOUS_DIRT_CONFIGURED_KEY = key("suspicious_dirt");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SUSPICIOUS_DIRT_TAIGA_CONFIGURED_KEY = key("suspicious_dirt_taiga");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SUSPICIOUS_DIRT_PLAINS_CONFIGURED_KEY = key("suspicious_dirt_plains");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SUSPICIOUS_MUD_SWAMP_CONFIGURED_KEY = key("suspicious_mud_swamp");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SUSPICIOUS_MUD_MANGROVE_SWAMP_CONFIGURED_KEY = key("suspicious_mud_mangrove_swamp");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SUSPICIOUS_PACKED_MUD_CONFIGURED_KEY = key("suspicious_packed_mud");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SUSPICIOUS_RED_SAND_CONFIGURED_KEY = key("suspicious_red_sand");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SUSPICIOUS_ROOTED_DIRT_CONFIGURED_KEY = key("suspicious_rooted_dirt");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SUSPICIOUS_SOUL_SAND_CONFIGURED_KEY = key("suspicious_soul_sand");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SUSPICIOUS_SOUL_SOIL_CONFIGURED_KEY = key("suspicious_soul_soil");

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, name));
    }

    public static void configure(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        registerDisk(context, SUSPICIOUS_CLAY_RIVER_CONFIGURED_KEY, ModBlocks.SUSPICIOUS_CLAY.get(), 1, 2, Blocks.CLAY);
        registerDisk(context, SUSPICIOUS_CLAY_BEACH_CONFIGURED_KEY, ModBlocks.SUSPICIOUS_CLAY.get(), 1, 2, Blocks.CLAY, Blocks.SAND);
        registerDisk(context, SUSPICIOUS_DIRT_CONFIGURED_KEY, ModBlocks.SUSPICIOUS_DIRT.get(), 1, 2, Blocks.DIRT, Blocks.COARSE_DIRT);
        registerDisk(context, SUSPICIOUS_DIRT_TAIGA_CONFIGURED_KEY, ModBlocks.SUSPICIOUS_DIRT.get(), 1, 2, Blocks.DIRT, Blocks.COARSE_DIRT);
        registerDisk(context, SUSPICIOUS_DIRT_PLAINS_CONFIGURED_KEY, ModBlocks.SUSPICIOUS_DIRT.get(), 1, 2, Blocks.DIRT, Blocks.COARSE_DIRT);
        registerDisk(context, SUSPICIOUS_MUD_SWAMP_CONFIGURED_KEY, ModBlocks.SUSPICIOUS_MUD.get(), 1, 2, Blocks.MUD, Blocks.DIRT);
        registerDisk(context, SUSPICIOUS_MUD_MANGROVE_SWAMP_CONFIGURED_KEY, ModBlocks.SUSPICIOUS_MUD.get(), 1, 2, Blocks.MUD);
        registerDisk(context, SUSPICIOUS_PACKED_MUD_CONFIGURED_KEY, ModBlocks.SUSPICIOUS_PACKED_MUD.get(), 1, 1, Blocks.MUD);
        registerDisk(context, SUSPICIOUS_RED_SAND_CONFIGURED_KEY, ModBlocks.SUSPICIOUS_RED_SAND.get(), 1, 2, Blocks.RED_SAND);

        registerOre(context, SUSPICIOUS_ROOTED_DIRT_CONFIGURED_KEY, ModBlocks.SUSPICIOUS_ROOTED_DIRT.get(), Blocks.ROOTED_DIRT, 2);
        registerOre(context, SUSPICIOUS_SOUL_SAND_CONFIGURED_KEY, ModBlocks.SUSPICIOUS_SOUL_SAND.get(), Blocks.SOUL_SAND, 4);
        registerOre(context, SUSPICIOUS_SOUL_SOIL_CONFIGURED_KEY, ModBlocks.SUSPICIOUS_SOUL_SOIL.get(), Blocks.SOUL_SOIL, 3);
    }

    private static void registerDisk(
        BootstrapContext<ConfiguredFeature<?, ?>> context,
        ResourceKey<ConfiguredFeature<?, ?>> key,
        Block suspiciousBlock,
        int minRadius,
        int maxRadius,
        Block... targetBlocks
    ) {
        context.register(
            key,
            new ConfiguredFeature<>(
                Feature.DISK,
                new DiskConfiguration(
                    BlockStateProvider.simple(suspiciousBlock.defaultBlockState()),
                    BlockPredicate.matchesBlocks(targetBlocks),
                    UniformInt.of(minRadius, maxRadius),
                    1
                )
            )
        );
    }

    private static void registerOre(
        BootstrapContext<ConfiguredFeature<?, ?>> context,
        ResourceKey<ConfiguredFeature<?, ?>> key,
        Block suspiciousBlock,
        Block targetBlock,
        int size
    ) {
        context.register(
            key,
            new ConfiguredFeature<>(
                Feature.ORE,
                new OreConfiguration(new BlockMatchTest(targetBlock), suspiciousBlock.defaultBlockState(), size, 0.0F)
            )
        );
    }
}

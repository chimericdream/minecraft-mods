package com.chimericdream.artificialheart.neoforge.worldgen;

import com.chimericdream.artificialheart.ModInfo;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class ArtificialHeartPlacedFeatures {
    public static final ResourceKey<PlacedFeature> PALE_PUMPKIN_PATCH_PLACED_KEY =
        ResourceKey.create(
            Registries.PLACED_FEATURE,
            Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pale_pumpkin_patch")
        );

    public static void configure(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> palePumpkinPatch = configuredFeatures.getOrThrow(
            ArtificialHeartConfiguredFeatures.PALE_PUMPKIN_PATCH_CONFIGURED_KEY
        );

        PlacementUtils.register(
            context,
            PALE_PUMPKIN_PATCH_PLACED_KEY,
            palePumpkinPatch,
            RarityFilter.onAverageOnceEvery(8),
            InSquarePlacement.spread(),
            PlacementUtils.HEIGHTMAP,
            BiomeFilter.biome(),
            CountPlacement.of(96),
            RandomOffsetPlacement.ofTriangle(7, 3),
            BlockPredicateFilter.forPredicate(
                BlockPredicate.allOf(
                    BlockPredicate.ONLY_IN_AIR_PREDICATE,
                    BlockPredicate.matchesBlocks(Direction.DOWN.getUnitVec3i(), new Block[]{Blocks.GRASS_BLOCK, Blocks.PALE_MOSS_BLOCK})
                )
            )
        );
    }
}

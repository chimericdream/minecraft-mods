package com.chimericdream.artificialheart.fabric.worldgen;

import com.chimericdream.artificialheart.ModInfo;
import com.chimericdream.artificialheart.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class ArtificialHeartConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> PALE_PUMPKIN_PATCH_CONFIGURED_KEY =
        ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pale_pumpkin_patch")
        );

    public static void configure(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(
            PALE_PUMPKIN_PATCH_CONFIGURED_KEY,
            new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.PALE_PUMPKIN_BLOCK.get()))
            )
        );
    }
}

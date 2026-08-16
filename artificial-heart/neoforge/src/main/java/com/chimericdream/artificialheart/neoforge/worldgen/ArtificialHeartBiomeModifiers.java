package com.chimericdream.artificialheart.neoforge.worldgen;

import com.chimericdream.artificialheart.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ArtificialHeartBiomeModifiers {
    public static final ResourceKey<BiomeModifier> PALE_PUMPKIN_PATCH_BIOME_MODIFIER_KEY =
        ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pale_pumpkin_patch")
        );

    public static void configure(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        Holder<Biome> paleGarden = biomes.getOrThrow(Biomes.PALE_GARDEN);
        Holder<PlacedFeature> palePumpkinPatch = placedFeatures.getOrThrow(ArtificialHeartPlacedFeatures.PALE_PUMPKIN_PATCH_PLACED_KEY);

        context.register(
            PALE_PUMPKIN_PATCH_BIOME_MODIFIER_KEY,
            new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(paleGarden),
                HolderSet.direct(palePumpkinPatch),
                Decoration.VEGETAL_DECORATION
            )
        );
    }
}

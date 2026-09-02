package com.chimericdream.nextupdatenow.worldgen;

import com.chimericdream.nextupdatenow.worldgen.foliageplacers.PoplarFoliagePlacer;
import com.chimericdream.nextupdatenow.worldgen.trunkplacers.PoplarTrunkPlacer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

/**
 * FoliagePlacerType/TrunkPlacerType only expose a private constructor and a private register() vanilla
 * uses internally for OAK/SPRUCE/etc — there's no public factory for a new placer type. An access
 * widener entry (nextupdatenow.accesswidener) opens their constructors so we can build our own
 * instances.
 *
 * {@link #registerTrunkPlacerType()}/{@link #registerFoliagePlacerType()} are called from
 * {@code TrunkPlacerTypeMixin}/{@code FoliagePlacerTypeMixin}, injected into the TAIL of vanilla's own
 * {@code TrunkPlacerType}/{@code FoliagePlacerType} static initializer — not from
 * {@code NextUpdateNowMod.init()}. Two things ruled that out:
 * - A bare {@code Registry.register} call from the mod constructor/entrypoint throws on NeoForge:
 *   {@code BuiltInRegistries.TRUNK_PLACER_TYPE} is already frozen by {@code Bootstrap.bootStrap()}
 *   before any mod code runs there (Fabric's entrypoint runs before that freeze, so it never saw this).
 * - Routing it through Architectury's DeferredRegister (NeoForge's RegisterEvent, which normally
 *   reopens frozen registries for exactly this) stopped the crash but never actually registered the
 *   entry either — NeoForge's RegisterEvent doesn't cover these two registries, so world creation later
 *   failed with "Unknown registry key ... poplar_trunk_placer" trying to parse the poplar configured
 *   features.
 *
 * Injecting into the vanilla class's own {@code <clinit>} runs in the very same bootstrap pass that
 * populates OAK_TRUNK_PLACER etc., before the freeze, on both loaders — sidestepping mod-lifecycle
 * timing entirely.
 */
public class ModWorldgenTypes {
    public static TrunkPlacerType<PoplarTrunkPlacer> POPLAR_TRUNK_PLACER;
    public static FoliagePlacerType<PoplarFoliagePlacer> POPLAR_FOLIAGE_PLACER;

    public static void registerTrunkPlacerType() {
        POPLAR_TRUNK_PLACER = Registry.register(
            BuiltInRegistries.TRUNK_PLACER_TYPE,
            Identifier.withDefaultNamespace("poplar_trunk_placer"),
            new TrunkPlacerType<>(PoplarTrunkPlacer.CODEC)
        );
    }

    public static void registerFoliagePlacerType() {
        POPLAR_FOLIAGE_PLACER = Registry.register(
            BuiltInRegistries.FOLIAGE_PLACER_TYPE,
            Identifier.withDefaultNamespace("poplar_foliage_placer"),
            new FoliagePlacerType<>(PoplarFoliagePlacer.CODEC)
        );
    }
}

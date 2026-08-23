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
 * instances and register them into BuiltInRegistries directly, the same way vanilla's own constants do
 * internally.
 */
public class ModWorldgenTypes {
    public static final TrunkPlacerType<PoplarTrunkPlacer> POPLAR_TRUNK_PLACER = new TrunkPlacerType<>(PoplarTrunkPlacer.CODEC);
    public static final FoliagePlacerType<PoplarFoliagePlacer> POPLAR_FOLIAGE_PLACER = new FoliagePlacerType<>(PoplarFoliagePlacer.CODEC);

    public static void init() {
        Registry.register(BuiltInRegistries.TRUNK_PLACER_TYPE, Identifier.withDefaultNamespace("poplar_trunk_placer"), POPLAR_TRUNK_PLACER);
        Registry.register(BuiltInRegistries.FOLIAGE_PLACER_TYPE, Identifier.withDefaultNamespace("poplar_foliage_placer"), POPLAR_FOLIAGE_PLACER);
    }
}

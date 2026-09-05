package com.chimericdream.flatbedrock;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * Tracks which dimension is currently generating a chunk, so the bedrock-placement mixins can look
 * up the right per-dimension settings. "bedrock_floor" and "bedrock_roof" are the same shared
 * gradient names in every dimension's noise settings, so there's no other way to tell them apart at
 * that point in the surface-rule tree. Set once per chunk from a mixin on
 * {@code NoiseBasedChunkGenerator.buildSurface}, which is the only entry point with a level
 * reference; chunk generation runs on a worker-thread pool, so this has to be per-thread.
 */
public final class FlatBedrockContext {
    private static final ThreadLocal<ResourceKey<Level>> CURRENT_DIMENSION = new ThreadLocal<>();

    private FlatBedrockContext() {
    }

    public static void set(ResourceKey<Level> dimension) {
        CURRENT_DIMENSION.set(dimension);
    }

    public static void clear() {
        CURRENT_DIMENSION.remove();
    }

    public static ResourceKey<Level> get() {
        return CURRENT_DIMENSION.get();
    }
}

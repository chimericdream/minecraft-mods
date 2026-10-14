package com.chimericdream.bctweaks.config;

import org.jetbrains.annotations.Nullable;

/**
 * Resolves the flat part of a conduit's range: {@code base + (activeFrameBlocks / blocksPerStep) * rangePerStep}.
 * <p>
 * Vanilla is {@code 0 + (frameBlocks / 7) * 16}. A setting that is {@code null} (not in the config file) falls back to
 * the deprecated {@code conduitAddVanillaRange} flag: on means vanilla's numbers, off means no flat range.
 */
public final class ConduitRange {
    public static final double DEFAULT_BASE_RANGE = 0.0;
    public static final int DEFAULT_BLOCKS_PER_STEP = 7;
    public static final double DEFAULT_RANGE_PER_STEP = 16.0;

    private ConduitRange() {
    }

    public static double baseRange(@Nullable Double base) {
        return base != null ? base : DEFAULT_BASE_RANGE;
    }

    public static int blocksPerStep(@Nullable Integer blocksPerStep) {
        return blocksPerStep != null ? Math.max(1, blocksPerStep) : DEFAULT_BLOCKS_PER_STEP;
    }

    public static double rangePerStep(@Nullable Double rangePerStep, boolean legacyAddVanillaRange) {
        if (rangePerStep != null) {
            return rangePerStep;
        }

        return legacyAddVanillaRange ? DEFAULT_RANGE_PER_STEP : 0.0;
    }

    public static double flatRange(
        int activeFrameBlocks,
        @Nullable Double base,
        @Nullable Integer blocksPerStep,
        @Nullable Double rangePerStep,
        boolean legacyAddVanillaRange
    ) {
        return baseRange(base)
            + (double) (activeFrameBlocks / blocksPerStep(blocksPerStep)) * rangePerStep(rangePerStep, legacyAddVanillaRange);
    }
}

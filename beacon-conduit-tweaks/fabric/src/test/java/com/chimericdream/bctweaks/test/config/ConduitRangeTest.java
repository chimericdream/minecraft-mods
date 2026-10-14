package com.chimericdream.bctweaks.test.config;

import com.chimericdream.bctweaks.config.ConduitRange;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConduitRangeTest {
    @Test
    void nothingSetAndLegacyOnIsVanilla() {
        assertEquals(96.0, ConduitRange.flatRange(42, null, null, null, true));
        assertEquals(32.0, ConduitRange.flatRange(14, null, null, null, true));
        // Vanilla's integer division: 13 frame blocks is still only one step.
        assertEquals(16.0, ConduitRange.flatRange(13, null, null, null, true));
        assertEquals(0.0, ConduitRange.flatRange(6, null, null, null, true));
    }

    @Test
    void nothingSetAndLegacyOffHasNoFlatRange() {
        assertEquals(0.0, ConduitRange.flatRange(42, null, null, null, false));
    }

    @Test
    void newSettingsWinOverLegacy() {
        assertEquals(10.0 + 6 * 8.0, ConduitRange.flatRange(42, 10.0, 7, 8.0, false));
        assertEquals(10.0 + 6 * 8.0, ConduitRange.flatRange(42, 10.0, 7, 8.0, true));
        // An explicit 0 is a value, not "unset".
        assertEquals(0.0, ConduitRange.flatRange(42, null, null, 0.0, true));
    }

    @Test
    void eachSettingFallsBackOnItsOwn() {
        // Only the step size is set; the per-step range still comes from the legacy flag.
        assertEquals(42 / 3 * 16.0, ConduitRange.flatRange(42, null, 3, null, true));
        assertEquals(0.0, ConduitRange.flatRange(42, null, 3, null, false));
        // Only the base is set.
        assertEquals(5.0 + 96.0, ConduitRange.flatRange(42, 5.0, null, null, true));
    }

    @Test
    void blocksPerStepIsNeverBelowOne() {
        assertEquals(42 * 16.0, ConduitRange.flatRange(42, null, 0, null, true));
        assertEquals(42 * 16.0, ConduitRange.flatRange(42, null, -5, null, true));
    }

    @Test
    void defaultsMatchVanilla() {
        assertEquals(0.0, ConduitRange.DEFAULT_BASE_RANGE);
        assertEquals(7, ConduitRange.DEFAULT_BLOCKS_PER_STEP);
        assertEquals(16.0, ConduitRange.DEFAULT_RANGE_PER_STEP);
    }
}

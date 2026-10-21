package com.chimericdream.polycraft.test.core;

import com.chimericdream.polycraft.config.PolyCraftConfig;
import com.chimericdream.polycraft.core.ConfigLimits;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The defaults must match the config table in DESIGN.md. */
class ConfigTest {
    @Test
    void defaultsMatchTheDesign() {
        assertFalse(PolyCraftConfig.Defaults.DETECT_OVERLAPPING_INPUTS, "overlapping detection is off by default");
        assertEquals(6, PolyCraftConfig.Defaults.VISIBLE_OPTIONS);
        assertTrue(PolyCraftConfig.Defaults.REMEMBER_CHOICES);
        assertTrue(PolyCraftConfig.Defaults.SHOW_CONFLICT_BADGE);
        assertEquals(List.of(), PolyCraftConfig.Defaults.ENABLED_RECIPE_TYPES);
        assertEquals(List.of(), PolyCraftConfig.Defaults.DISABLED_RECIPE_TYPES);
        assertFalse(PolyCraftConfig.Defaults.DEBUG_LOGGING);
    }

    @Test
    void visibleOptionsAreClamped() {
        assertEquals(ConfigLimits.MIN_VISIBLE_OPTIONS, ConfigLimits.visibleOptions(-5));
        assertEquals(ConfigLimits.MIN_VISIBLE_OPTIONS, ConfigLimits.visibleOptions(0));
        assertEquals(6, ConfigLimits.visibleOptions(6));
        assertEquals(ConfigLimits.MAX_VISIBLE_OPTIONS, ConfigLimits.visibleOptions(500));
    }

    @Test
    void theDefaultVisibleCountIsInsideTheLimits() {
        assertEquals(ConfigLimits.DEFAULT_VISIBLE_OPTIONS, ConfigLimits.visibleOptions(ConfigLimits.DEFAULT_VISIBLE_OPTIONS));
    }
}

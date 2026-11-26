package com.chimericdream.enchantingfixes.fabric.test;

import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import com.chimericdream.enchantingfixes.enchantment.RandomLevels;
import com.chimericdream.enchantingfixes.enchantment.RandomLevels.Mode;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RandomLevelsTest extends BootstrapMinecraft {
    @Test
    void offUsesTheVanillaMaximum() {
        assertEquals(5, RandomLevels.cap(10, 5, Mode.OFF));
        assertEquals(1, RandomLevels.cap(255, 1, Mode.OFF));
    }

    @Test
    void balancedAllowsTwoLevelsAboveVanilla() {
        assertEquals(7, RandomLevels.cap(10, 5, Mode.BALANCED));
        assertEquals(5, RandomLevels.cap(255, 3, Mode.BALANCED));
    }

    @Test
    void insaneUsesTheConfiguredMaximum() {
        assertEquals(255, RandomLevels.cap(255, 3, Mode.INSANE));
    }

    @Test
    void aLowerConfiguredMaximumAlwaysApplies() {
        for (Mode mode : Mode.values()) {
            assertEquals(2, RandomLevels.cap(2, 5, mode), mode.name());
        }
    }

    @Test
    void theDefaultIsBalanced() {
        assertEquals(Mode.BALANCED, new EnchantingFixesConfig().randomLevels);
    }
}

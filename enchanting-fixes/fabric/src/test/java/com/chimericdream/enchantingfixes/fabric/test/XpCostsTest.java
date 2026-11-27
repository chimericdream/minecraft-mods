package com.chimericdream.enchantingfixes.fabric.test;

import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import com.chimericdream.enchantingfixes.enchantment.XpCosts;
import com.chimericdream.enchantingfixes.enchantment.XpCosts.Experience;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XpCostsTest extends BootstrapMinecraft {
    @Test
    void theTotalIsTheSumOfEachLevel() {
        long total = 0;
        for (int level = 0; level <= 500; level++) {
            assertEquals(total, XpCosts.pointsToReach(level), "level " + level);
            total += XpCosts.pointsForNextLevel(level);
        }
    }

    @Test
    void knownTotals() {
        assertEquals(27, XpCosts.pointsToReach(3));
        assertEquals(1395, XpCosts.pointsToReach(30));
        assertEquals(30970, XpCosts.pointsToReach(100));
    }

    @Test
    void theCostIsTheSameAtEachLevel() {
        // 3 levels is 27 points. Level 30 has 1,395 points, so 1,368 stay: level 29 (1,288) and 80 points.
        assertEquals(new Experience(29, 80), XpCosts.afterCost(30, 0, 3));
        // Level 100 has 30,970 points. 30,943 stay: level 99 (30,237) and 706 points.
        assertEquals(new Experience(99, 706), XpCosts.afterCost(100, 0, 3));
    }

    @Test
    void thePointsInTheLevelPayFirst() {
        assertEquals(new Experience(20, 3), XpCosts.afterCost(20, 30, 3));
        assertEquals(new Experience(20, 0), XpCosts.afterCost(20, 27, 3));
    }

    @Test
    void aCostOfAllTheXpGivesLevelZero() {
        assertEquals(new Experience(0, 0), XpCosts.afterCost(3, 0, 3));
        assertEquals(new Experience(0, 0), XpCosts.afterCost(2, 5, 3));
    }

    @Test
    void aCostOfZeroChangesNothing() {
        assertEquals(new Experience(42, 17), XpCosts.afterCost(42, 17, 0));
    }

    @Test
    void veryHighLevelsDoNotOverflow() {
        assertEquals(new Experience(0, 0), XpCosts.afterCost(5, 0, Integer.MAX_VALUE));
    }

    @Test
    void theSettingIsOnByDefault() {
        assertTrue(new EnchantingFixesConfig().fixedXpCosts);
    }
}

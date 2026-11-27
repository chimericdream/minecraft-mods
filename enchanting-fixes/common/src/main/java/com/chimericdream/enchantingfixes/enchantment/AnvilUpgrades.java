package com.chimericdream.enchantingfixes.enchantment;

/**
 * Linear anvil upgrades. In vanilla, two items give a higher level only when they have the same level, so each
 * level needs twice the items of the level before it. Each anvil use also doubles the repair cost of an item.
 * With this setting, the repair cost stops at {@value #REPAIR_COST_CAP}, and above the vanilla maximum level,
 * each item adds one level.
 */
public final class AnvilUpgrades {
    /** The repair cost after 5 anvil uses. Vanilla doubles it after each use. */
    public static final int REPAIR_COST_CAP = 31;

    private AnvilUpgrades() {
    }

    public static int repairCost(int cost) {
        return Math.min(cost, REPAIR_COST_CAP);
    }

    /**
     * The level of an enchantment when two items have different levels of it. Vanilla keeps the higher level.
     * If both levels are at or above the vanilla maximum, the result is one level above the higher level.
     *
     * @param higher          the higher of the two levels
     * @param lower           the lower of the two levels
     * @param vanillaMaxLevel the maximum level from the enchantment definition
     */
    public static int combinedLevel(int higher, int lower, int vanillaMaxLevel) {
        if (lower >= vanillaMaxLevel) {
            return higher + 1;
        }

        return higher;
    }
}

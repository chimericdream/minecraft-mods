package com.chimericdream.polycraft.core;

/** The arithmetic behind switching a furnace's recipe part-way through a cook. */
public final class CookMath {
    private CookMath() {
    }

    /**
     * Where a cook that has run {@code elapsed} ticks stands after the recipe changes to one that takes
     * {@code newTotal}. Elapsed time is kept, so the arrow may jump. A furnace finishes when elapsed equals the
     * total exactly, so a cook that is already past the new total is set one tick short of it and finishes on the
     * next tick instead of never finishing.
     */
    public static int switchedProgress(int elapsed, int newTotal) {
        if (newTotal <= 0) {
            return 0;
        }

        return elapsed >= newTotal ? newTotal - 1 : Math.max(0, elapsed);
    }

    /**
     * Whether a result can go into an output slot, mirroring vanilla's rule: an empty slot takes anything; a
     * filled one takes the same item and components as long as the stack stays within its limit.
     */
    public static boolean canMergeIntoOutput(boolean outputEmpty, boolean sameItemAndComponents, int outputCount, int resultCount, int maxStackSize) {
        if (outputEmpty) {
            return true;
        }

        return sameItemAndComponents && outputCount + resultCount <= maxStackSize;
    }
}

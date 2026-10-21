package com.chimericdream.polycraft.recipe;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A counter that goes up whenever anything that can change which recipe a station uses changes: a saved choice, a pin,
 * the rules. Stations that don't recompute every tick compare it to know when they must.
 */
public final class PolyCraftEpoch {
    private static final AtomicInteger EPOCH = new AtomicInteger();

    private PolyCraftEpoch() {
    }

    public static int get() {
        return EPOCH.get();
    }

    public static void bump() {
        EPOCH.incrementAndGet();
    }
}

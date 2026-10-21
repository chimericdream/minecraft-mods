package com.chimericdream.polycraft.core;

/** The allowed ranges for config values that would break the UI or the game if left unchecked. */
public final class ConfigLimits {
    public static final int MIN_VISIBLE_OPTIONS = 3;
    public static final int MAX_VISIBLE_OPTIONS = 12;
    public static final int DEFAULT_VISIBLE_OPTIONS = 6;

    private ConfigLimits() {
    }

    public static int visibleOptions(int configured) {
        return Math.max(MIN_VISIBLE_OPTIONS, Math.min(MAX_VISIBLE_OPTIONS, configured));
    }
}

package com.chimericdream.pannotiacompanion.home;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Per-player cooldown bookkeeping against a monotonically increasing tick counter. Not persisted. */
public class Cooldowns {
    private final Map<UUID, Long> readyAt = new HashMap<>();

    public void start(UUID player, long nowTick, int seconds) {
        if (seconds > 0) {
            readyAt.put(player, nowTick + seconds * 20L);
        }
    }

    /** Whole seconds left, rounded up; 0 when the player is free to go. */
    public int remainingSeconds(UUID player, long nowTick) {
        Long ready = readyAt.get(player);
        if (ready == null || ready <= nowTick) {
            return 0;
        }

        return (int) ((ready - nowTick + 19) / 20);
    }
}

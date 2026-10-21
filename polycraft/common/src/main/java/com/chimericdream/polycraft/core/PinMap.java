package com.chimericdream.polycraft.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The recipes a block has pinned, one per conflict group. A furnace sees different items over time, so one block
 * can hold several pins. The size is capped so a block that has seen thousands of groups can't grow without bound.
 */
public final class PinMap {
    public static final int MAX_PINS = 64;

    private final LinkedHashMap<String, String> pins = new LinkedHashMap<>();

    public Optional<String> get(String groupKey) {
        return Optional.ofNullable(pins.get(groupKey));
    }

    public boolean isPinned(String groupKey) {
        return pins.containsKey(groupKey);
    }

    /** Pins {@code recipeId} for the group, replacing an earlier pin. Returns whether anything changed. */
    public boolean pin(String groupKey, String recipeId) {
        String previous = pins.remove(groupKey);
        pins.put(groupKey, recipeId);

        while (pins.size() > MAX_PINS) {
            pins.remove(pins.keySet().iterator().next());
        }

        return !recipeId.equals(previous);
    }

    public boolean unpin(String groupKey) {
        return pins.remove(groupKey) != null;
    }

    public boolean isEmpty() {
        return pins.isEmpty();
    }

    public Map<String, String> entries() {
        return Map.copyOf(pins);
    }

    /** Replaces the pins with saved ones, oldest first. */
    public void restore(Map<String, String> saved) {
        pins.clear();
        saved.forEach(this::pin);
    }
}

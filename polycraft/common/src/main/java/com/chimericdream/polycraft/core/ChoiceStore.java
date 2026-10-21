package com.chimericdream.polycraft.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Each player's remembered choices: conflict group key to recipe id. Each player keeps at most {@code maxPerPlayer}
 * entries; setting a new one past that drops the one used least recently. Recipe ids that no longer exist are kept
 * until {@link #prune} or the cap removes them, so choices survive a datapack being switched off for a while.
 */
public final class ChoiceStore {
    private final int maxPerPlayer;
    private final Map<UUID, LinkedHashMap<String, String>> choices = new LinkedHashMap<>();

    public ChoiceStore(int maxPerPlayer) {
        if (maxPerPlayer < 1) {
            throw new IllegalArgumentException("maxPerPlayer must be at least 1");
        }

        this.maxPerPlayer = maxPerPlayer;
    }

    public Optional<String> get(UUID player, String groupKey) {
        Map<String, String> own = choices.get(player);
        return own == null ? Optional.empty() : Optional.ofNullable(own.get(groupKey));
    }

    /** Remembers a choice and marks it as the most recently used. Returns whether anything changed. */
    public boolean set(UUID player, String groupKey, String recipeId) {
        LinkedHashMap<String, String> own = choices.computeIfAbsent(player, id -> new LinkedHashMap<>());
        String previous = own.remove(groupKey);
        own.put(groupKey, recipeId);

        while (own.size() > maxPerPlayer) {
            own.remove(own.keySet().iterator().next());
        }

        return !recipeId.equals(previous);
    }

    public boolean remove(UUID player, String groupKey) {
        Map<String, String> own = choices.get(player);
        return own != null && own.remove(groupKey) != null;
    }

    public int clear(UUID player) {
        Map<String, String> removed = choices.remove(player);
        return removed == null ? 0 : removed.size();
    }

    public int clearAll() {
        int count = choices.values().stream().mapToInt(Map::size).sum();
        choices.clear();
        return count;
    }

    /** Removes every choice whose recipe id fails {@code exists}. Returns how many were removed. */
    public int prune(Predicate<String> exists) {
        int removed = 0;
        for (Map<String, String> own : choices.values()) {
            int before = own.size();
            own.values().removeIf(id -> !exists.test(id));
            removed += before - own.size();
        }

        choices.values().removeIf(Map::isEmpty);
        return removed;
    }

    /** A copy in least-recently-used first order, for saving. */
    public Map<UUID, Map<String, String>> snapshot() {
        Map<UUID, Map<String, String>> copy = new LinkedHashMap<>();
        choices.forEach((player, own) -> copy.put(player, new LinkedHashMap<>(own)));
        return copy;
    }

    /** Loads saved choices, oldest first, trimming anyone over the cap. */
    public void restore(Map<UUID, Map<String, String>> saved) {
        choices.clear();
        saved.forEach((player, own) -> own.forEach((groupKey, recipeId) -> set(player, groupKey, recipeId)));
    }
}

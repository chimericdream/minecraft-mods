package com.chimericdream.polycraft.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Decides when several recipes are really a choice for the player.
 * <p>
 * Two recipes that give the same item in the same amount are not a conflict, whatever their ids. The same item in a
 * different amount is, because packs commonly add a higher-yield version of a vanilla recipe.
 */
public final class ConflictDetector {
    public enum Mode {
        /** Only recipes that take exactly the same ingredients conflict. */
        IDENTICAL,
        /** Any recipes that the same grid satisfies conflict, even if their ingredients differ. */
        OVERLAPPING
    }

    /** One choice the player has: the distinct outcomes, in the order vanilla would have tried them. */
    public record Conflict(String groupKey, List<RecipeInfo> options) {
        public List<String> optionIds() {
            return options.stream().map(RecipeInfo::id).toList();
        }
    }

    private ConflictDetector() {
    }

    /**
     * Looks for a conflict among the recipes that match one actual input. {@code matches} must be in vanilla's order,
     * so the first entry is the recipe vanilla would have used. The group key is that recipe's signature, so adding
     * an unrelated overlapping recipe later doesn't change the key of an existing choice.
     */
    public static Optional<Conflict> detect(List<RecipeInfo> matches, Mode mode) {
        if (matches.size() < 2) {
            return Optional.empty();
        }

        RecipeInfo winner = matches.getFirst();
        List<RecipeInfo> distinct = new ArrayList<>();
        for (RecipeInfo candidate : matches) {
            if (mode == Mode.IDENTICAL && !candidate.signature().equals(winner.signature())) {
                continue;
            }

            if (distinct.stream().noneMatch(candidate::sameOutcome)) {
                distinct.add(candidate);
            }
        }

        if (distinct.size() < 2) {
            return Optional.empty();
        }

        return Optional.of(new Conflict(winner.signature().key(), List.copyOf(distinct)));
    }

    /**
     * Finds every identical-input conflict in a full recipe list, for the export command and reports. Input order is
     * kept inside each group; groups come back sorted by key so the output is the same on every run. Overlapping
     * conflicts depend on the actual grid, so they can't be listed this way.
     */
    public static List<Conflict> detectAll(Collection<RecipeInfo> recipes) {
        Map<String, List<RecipeInfo>> byKey = new LinkedHashMap<>();
        for (RecipeInfo recipe : recipes) {
            if (recipe.signature().isUnique()) {
                continue;
            }

            byKey.computeIfAbsent(recipe.signature().key(), key -> new ArrayList<>()).add(recipe);
        }

        List<Conflict> conflicts = new ArrayList<>();
        for (Map.Entry<String, List<RecipeInfo>> entry : byKey.entrySet()) {
            if (entry.getValue().size() < 2) {
                continue;
            }

            detect(entry.getValue(), Mode.IDENTICAL).ifPresent(conflicts::add);
        }

        conflicts.sort(Comparator.comparing(Conflict::groupKey));
        return conflicts;
    }
}

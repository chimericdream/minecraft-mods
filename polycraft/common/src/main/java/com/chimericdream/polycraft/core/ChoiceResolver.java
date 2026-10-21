package com.chimericdream.polycraft.core;

import java.util.List;
import java.util.Optional;

/**
 * Picks which recipe a station uses once a conflict is known. In order: the block's own pin, then the acting
 * player's saved choice, then the pack's defaults, then whatever vanilla would have used. Anything that isn't in
 * {@code options} any more (a recipe removed by a reload, a typo in the rules) is skipped, never an error.
 */
public final class ChoiceResolver {
    public enum Source {
        PIN,
        PLAYER,
        PACK_DEFAULT,
        VANILLA
    }

    public record Resolution(String recipeId, Source source) {
    }

    private ChoiceResolver() {
    }

    /**
     * @param options      the recipe ids to choose between, in vanilla's order; must not be empty
     * @param pin          the block's pinned recipe for this group, if it has one
     * @param playerChoice the acting player's saved recipe for this group; empty when there is no acting player
     * @param packOrder    the pack's preferred recipes, most preferred first
     */
    public static Resolution resolve(List<String> options, Optional<String> pin, Optional<String> playerChoice, List<String> packOrder) {
        if (options.isEmpty()) {
            throw new IllegalArgumentException("options must not be empty");
        }

        if (pin.isPresent() && options.contains(pin.get())) {
            return new Resolution(pin.get(), Source.PIN);
        }

        if (playerChoice.isPresent() && options.contains(playerChoice.get())) {
            return new Resolution(playerChoice.get(), Source.PLAYER);
        }

        for (String preferred : packOrder) {
            if (options.contains(preferred)) {
                return new Resolution(preferred, Source.PACK_DEFAULT);
            }
        }

        return new Resolution(options.getFirst(), Source.VANILLA);
    }
}

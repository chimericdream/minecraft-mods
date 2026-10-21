package com.chimericdream.polycraft.core;

/**
 * Everything the core needs to know about one recipe. {@code resultKey} identifies what comes out (the item plus
 * anything that changes it, such as components) without the count, which is kept separately because the same item
 * in a different amount is a different outcome.
 */
public record RecipeInfo(String id, Signature signature, String resultKey, int resultCount) {
    boolean sameOutcome(RecipeInfo other) {
        return resultCount == other.resultCount && resultKey.equals(other.resultKey);
    }
}

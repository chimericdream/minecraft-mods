package com.chimericdream.polycraft.core;

/**
 * What a recipe needs, in a form that is the same for any two recipes that take the same inputs: same recipe
 * type, same ingredients in the same places (mirrored and offset layouts count as the same). The canonical text
 * is stable across runs, so it can be stored and used as a key.
 * <p>
 * A recipe whose ingredients can't be put in canonical form gets a {@link #unique unique} signature that equals
 * nothing but itself, so it is never grouped with another recipe by mistake.
 */
public record Signature(String type, String canonical) {
    private static final String UNIQUE_PREFIX = "unique:";

    public static Signature unique(String type, String recipeId) {
        return new Signature(type, UNIQUE_PREFIX + recipeId);
    }

    public boolean isUnique() {
        return canonical.startsWith(UNIQUE_PREFIX);
    }

    /** The group key shared by every recipe with this signature. */
    public String key() {
        return type + "|" + canonical;
    }
}

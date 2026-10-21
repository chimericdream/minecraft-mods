package com.chimericdream.polycraft.core;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * Turns a recipe's ingredients into a {@link Signature}. Ingredients arrive as plain strings (see
 * {@link #ingredient}) so this class doesn't need Minecraft. A {@code null} ingredient means "the caller couldn't
 * express this ingredient canonically" (for example it matches on item data), and makes the whole recipe
 * {@link Signature#unique unique}. An empty string is an empty slot.
 */
public final class SignatureBuilder {
    public static final String EMPTY = "";

    private static final int HASH_BYTES = 8;

    private SignatureBuilder() {
    }

    /**
     * An ingredient that accepts any of {@code itemIds}. A tag and a list of exactly the tag's items give the same
     * key, so they count as the same ingredient. Returns {@code null} when nothing is accepted.
     */
    public static String ingredient(Collection<String> itemIds) {
        TreeSet<String> sorted = new TreeSet<>(itemIds);
        if (sorted.isEmpty()) {
            return null;
        }

        if (sorted.size() == 1) {
            return sorted.first();
        }

        return "set#" + hash(String.join(",", sorted));
    }

    /**
     * A shaped recipe: {@code cells} is row-major, {@code width * height} entries. Empty rows and columns around the
     * pattern are ignored, and a pattern equals its mirror image.
     */
    public static Signature shaped(String type, String recipeId, int width, int height, List<String> cells) {
        if (width <= 0 || height <= 0 || cells.size() != width * height || containsNull(cells)) {
            return Signature.unique(type, recipeId);
        }

        int top = height;
        int bottom = -1;
        int left = width;
        int right = -1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!cells.get(y * width + x).isEmpty()) {
                    top = Math.min(top, y);
                    bottom = Math.max(bottom, y);
                    left = Math.min(left, x);
                    right = Math.max(right, x);
                }
            }
        }

        if (bottom < 0) {
            return Signature.unique(type, recipeId);
        }

        int w = right - left + 1;
        int h = bottom - top + 1;
        List<String> plain = new ArrayList<>(w * h);
        List<String> mirrored = new ArrayList<>(w * h);
        for (int y = top; y <= bottom; y++) {
            for (int x = left; x <= right; x++) {
                plain.add(cells.get(y * width + x));
                mirrored.add(cells.get(y * width + (right - (x - left))));
            }
        }

        String a = String.join(";", plain);
        String b = String.join(";", mirrored);
        return new Signature(type, "shaped:" + w + "x" + h + ":" + (a.compareTo(b) <= 0 ? a : b));
    }

    /** A recipe that takes its ingredients in any order. */
    public static Signature shapeless(String type, String recipeId, List<String> ingredients) {
        if (ingredients.isEmpty() || containsNull(ingredients)) {
            return Signature.unique(type, recipeId);
        }

        List<String> sorted = new ArrayList<>(ingredients);
        sorted.sort(String::compareTo);
        return new Signature(type, "shapeless:" + String.join(";", sorted));
    }

    /** A recipe whose ingredients go in fixed slots: a furnace input, or a smithing template, base and addition. */
    public static Signature ordered(String type, String recipeId, List<String> slots) {
        if (slots.isEmpty() || containsNull(slots) || slots.stream().allMatch(String::isEmpty)) {
            return Signature.unique(type, recipeId);
        }

        return new Signature(type, "ordered:" + String.join(";", slots));
    }

    private static boolean containsNull(List<String> values) {
        for (String value : values) {
            if (value == null) {
                return true;
            }
        }

        return false;
    }

    private static String hash(String text) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder(HASH_BYTES * 2);
            for (int i = 0; i < HASH_BYTES; i++) {
                out.append(String.format("%02x", digest[i]));
            }

            return out.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is always available", e);
        }
    }
}

package com.chimericdream.polycraft.test.core;

import com.chimericdream.polycraft.core.Signature;
import com.chimericdream.polycraft.core.SignatureBuilder;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureBuilderTest {
    private static final String CRAFTING = "minecraft:crafting";
    private static final String E = SignatureBuilder.EMPTY;

    private static Signature shaped(String id, int w, int h, String... cells) {
        return SignatureBuilder.shaped(CRAFTING, id, w, h, Arrays.asList(cells));
    }

    @Test
    void mirroredPatternsAreTheSame() {
        Signature left = shaped("a", 2, 2, "log", "plank", E, E);
        Signature right = shaped("b", 2, 2, "plank", "log", E, E);

        assertEquals(left, right);
    }

    @Test
    void emptyRowsAndColumnsAroundThePatternAreIgnored() {
        Signature small = shaped("a", 2, 2, "stick", "stick", "stick", "stick");
        Signature inLargerGrid = shaped("b", 3, 3, E, E, E, E, "stick", "stick", E, "stick", "stick");

        assertEquals(small, inLargerGrid);
    }

    @Test
    void differentShapesAreDifferent() {
        Signature row = shaped("a", 3, 1, "stone", "stone", "stone");
        Signature column = shaped("b", 1, 3, "stone", "stone", "stone");

        assertNotEquals(row, column);
    }

    @Test
    void gapsInsidePatternsMatter() {
        Signature ring = shaped("a", 3, 3, "x", "x", "x", "x", E, "x", "x", "x", "x");
        Signature solid = shaped("b", 3, 3, "x", "x", "x", "x", "x", "x", "x", "x", "x");

        assertNotEquals(ring, solid);
    }

    @Test
    void differentRecipeTypesNeverMatch() {
        Signature crafting = SignatureBuilder.shapeless("minecraft:crafting", "a", List.of("x", "y"));
        Signature other = SignatureBuilder.shapeless("othermod:press", "b", List.of("x", "y"));

        assertNotEquals(crafting, other);
        assertNotEquals(crafting.key(), other.key());
    }

    @Test
    void shapelessIgnoresOrderButNotCounts() {
        Signature a = SignatureBuilder.shapeless(CRAFTING, "a", List.of("x", "y", "y"));
        Signature b = SignatureBuilder.shapeless(CRAFTING, "b", List.of("y", "x", "y"));
        Signature fewer = SignatureBuilder.shapeless(CRAFTING, "c", List.of("x", "y"));

        assertEquals(a, b);
        assertNotEquals(a, fewer);
    }

    @Test
    void orderedSlotsKeepTheirPositions() {
        Signature a = SignatureBuilder.ordered("minecraft:smithing", "a", List.of("template", "sword", "ingot"));
        Signature b = SignatureBuilder.ordered("minecraft:smithing", "b", List.of("template", "ingot", "sword"));
        Signature again = SignatureBuilder.ordered("minecraft:smithing", "c", List.of("template", "sword", "ingot"));

        assertNotEquals(a, b);
        assertEquals(a, again);
    }

    @Test
    void aTagAndAnEqualItemListAreTheSameIngredient() {
        String tag = SignatureBuilder.ingredient(Set.of("minecraft:oak_planks", "minecraft:birch_planks"));
        String list = SignatureBuilder.ingredient(List.of("minecraft:birch_planks", "minecraft:oak_planks"));

        assertEquals(tag, list);
    }

    @Test
    void aSingleItemIsItsOwnKey() {
        assertEquals("minecraft:stick", SignatureBuilder.ingredient(List.of("minecraft:stick")));
    }

    @Test
    void anEmptyIngredientCantBeExpressed() {
        assertNull(SignatureBuilder.ingredient(List.of()));
    }

    @Test
    void anUnknownIngredientMakesTheRecipeItsOwnGroup() {
        Signature a = SignatureBuilder.shapeless(CRAFTING, "a", Arrays.asList("x", null));
        Signature b = SignatureBuilder.shapeless(CRAFTING, "b", Arrays.asList("x", null));

        assertTrue(a.isUnique());
        assertNotEquals(a, b);
    }

    @Test
    void malformedShapesAreUnique() {
        assertTrue(SignatureBuilder.shaped(CRAFTING, "a", 2, 2, List.of("x")).isUnique());
        assertTrue(SignatureBuilder.shaped(CRAFTING, "b", 0, 0, List.of()).isUnique());
        assertTrue(shaped("c", 2, 1, E, E).isUnique());
    }

    @Test
    void keysAreStableAcrossCalls() {
        assertEquals(shaped("a", 2, 1, "x", "y").key(), shaped("z", 2, 1, "y", "x").key());
    }
}

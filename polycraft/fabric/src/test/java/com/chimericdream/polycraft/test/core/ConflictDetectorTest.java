package com.chimericdream.polycraft.test.core;

import com.chimericdream.polycraft.core.ConflictDetector;
import com.chimericdream.polycraft.core.ConflictDetector.Conflict;
import com.chimericdream.polycraft.core.ConflictDetector.Mode;
import com.chimericdream.polycraft.core.RecipeInfo;
import com.chimericdream.polycraft.core.Signature;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConflictDetectorTest {
    private static final Signature PLANKS = new Signature("minecraft:crafting", "shapeless:planks");
    private static final Signature LOGS = new Signature("minecraft:crafting", "shapeless:logs");

    private static RecipeInfo recipe(String id, Signature sig, String result, int count) {
        return new RecipeInfo(id, sig, result, count);
    }

    @Test
    void sameInputsDifferentOutputsConflict() {
        Optional<Conflict> conflict = ConflictDetector.detect(List.of(
            recipe("a:stairs", PLANKS, "minecraft:oak_stairs", 4),
            recipe("b:stairs", PLANKS, "b:fancy_stairs", 4)
        ), Mode.IDENTICAL);

        assertTrue(conflict.isPresent());
        assertEquals(List.of("a:stairs", "b:stairs"), conflict.get().optionIds());
        assertEquals(PLANKS.key(), conflict.get().groupKey());
    }

    @Test
    void sameItemInADifferentAmountIsAConflict() {
        Optional<Conflict> conflict = ConflictDetector.detect(List.of(
            recipe("minecraft:oak_stairs", PLANKS, "minecraft:oak_stairs", 4),
            recipe("pack:oak_stairs_x8", PLANKS, "minecraft:oak_stairs", 8)
        ), Mode.IDENTICAL);

        assertTrue(conflict.isPresent());
        assertEquals(2, conflict.get().options().size());
    }

    @Test
    void sameItemInTheSameAmountCollapses() {
        Optional<Conflict> conflict = ConflictDetector.detect(List.of(
            recipe("minecraft:oak_stairs", PLANKS, "minecraft:oak_stairs", 4),
            recipe("pack:oak_stairs_copy", PLANKS, "minecraft:oak_stairs", 4)
        ), Mode.IDENTICAL);

        assertTrue(conflict.isEmpty());
    }

    @Test
    void collapsingKeepsTheEarlierRecipe() {
        Conflict conflict = ConflictDetector.detect(List.of(
            recipe("first", PLANKS, "x", 1),
            recipe("copy", PLANKS, "x", 1),
            recipe("other", PLANKS, "y", 1)
        ), Mode.IDENTICAL).orElseThrow();

        assertEquals(List.of("first", "other"), conflict.optionIds());
    }

    @Test
    void aSingleRecipeIsNotAConflict() {
        assertTrue(ConflictDetector.detect(List.of(recipe("a", PLANKS, "x", 1)), Mode.IDENTICAL).isEmpty());
        assertTrue(ConflictDetector.detect(List.of(), Mode.IDENTICAL).isEmpty());
    }

    @Test
    void overlappingInputsOnlyConflictWhenEnabled() {
        List<RecipeInfo> matches = List.of(
            recipe("a:planks", PLANKS, "x", 1),
            recipe("b:logs", LOGS, "y", 1)
        );

        assertTrue(ConflictDetector.detect(matches, Mode.IDENTICAL).isEmpty());

        Conflict conflict = ConflictDetector.detect(matches, Mode.OVERLAPPING).orElseThrow();
        assertEquals(List.of("a:planks", "b:logs"), conflict.optionIds());
        assertEquals(PLANKS.key(), conflict.groupKey(), "the key follows the vanilla winner");
    }

    @Test
    void identicalModeIgnoresOverlappingRecipesInTheOptions() {
        Conflict conflict = ConflictDetector.detect(List.of(
            recipe("a", PLANKS, "x", 1),
            recipe("b", LOGS, "y", 1),
            recipe("c", PLANKS, "z", 1)
        ), Mode.IDENTICAL).orElseThrow();

        assertEquals(List.of("a", "c"), conflict.optionIds());
    }

    @Test
    void aWinnerWithAUniqueSignatureHasNoIdenticalConflict() {
        Signature unique = Signature.unique("minecraft:crafting", "a");

        assertTrue(ConflictDetector.detect(List.of(
            recipe("a", unique, "x", 1),
            recipe("b", PLANKS, "y", 1)
        ), Mode.IDENTICAL).isEmpty());
    }

    @Test
    void groupKeysAreTheSameOnEveryRun() {
        List<RecipeInfo> matches = List.of(recipe("a", PLANKS, "x", 1), recipe("b", PLANKS, "y", 1));

        assertEquals(
            ConflictDetector.detect(matches, Mode.IDENTICAL).orElseThrow().groupKey(),
            ConflictDetector.detect(matches, Mode.IDENTICAL).orElseThrow().groupKey()
        );
    }

    @Test
    void detectAllListsEveryGroupSortedByKey() {
        List<Conflict> all = ConflictDetector.detectAll(List.of(
            recipe("a", PLANKS, "x", 1),
            recipe("b", LOGS, "x", 1),
            recipe("c", PLANKS, "y", 1),
            recipe("d", LOGS, "x", 1),
            recipe("e", Signature.unique("minecraft:crafting", "e"), "z", 1),
            recipe("f", new Signature("minecraft:crafting", "shapeless:lonely"), "z", 1)
        ));

        assertEquals(1, all.size(), "logs collapse (same outcome) and lonely/unique recipes have no partner");
        assertEquals(List.of("a", "c"), all.getFirst().optionIds());
    }

    @Test
    void detectAllSortsByKey() {
        List<Conflict> all = ConflictDetector.detectAll(List.of(
            recipe("a", PLANKS, "x", 1),
            recipe("b", PLANKS, "y", 1),
            recipe("c", LOGS, "x", 1),
            recipe("d", LOGS, "y", 1)
        ));

        assertEquals(List.of(LOGS.key(), PLANKS.key()), all.stream().map(Conflict::groupKey).toList());
    }
}

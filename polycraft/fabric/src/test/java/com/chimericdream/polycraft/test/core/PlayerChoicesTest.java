package com.chimericdream.polycraft.test.core;

import com.chimericdream.polycraft.core.ChoiceStore;
import com.chimericdream.polycraft.core.CookMath;
import com.chimericdream.polycraft.core.PinMap;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerChoicesTest {
    private static final UUID ALEX = new UUID(0, 1);
    private static final UUID SAM = new UUID(0, 2);

    @Test
    void remembersPerPlayerAndPerGroup() {
        ChoiceStore store = new ChoiceStore(10);
        store.set(ALEX, "g1", "a");
        store.set(SAM, "g1", "b");
        store.set(ALEX, "g2", "c");

        assertEquals(Optional.of("a"), store.get(ALEX, "g1"));
        assertEquals(Optional.of("b"), store.get(SAM, "g1"));
        assertEquals(Optional.of("c"), store.get(ALEX, "g2"));
        assertEquals(Optional.empty(), store.get(SAM, "g2"));
        assertEquals(Optional.empty(), store.get(new UUID(9, 9), "g1"));
    }

    @Test
    void setReportsWhetherAnythingChanged() {
        ChoiceStore store = new ChoiceStore(10);

        assertTrue(store.set(ALEX, "g", "a"));
        assertFalse(store.set(ALEX, "g", "a"));
        assertTrue(store.set(ALEX, "g", "b"));
    }

    @Test
    void dropsTheLeastRecentlyUsedPastTheCap() {
        ChoiceStore store = new ChoiceStore(2);
        store.set(ALEX, "g1", "a");
        store.set(ALEX, "g2", "b");
        store.set(ALEX, "g1", "a2");
        store.set(ALEX, "g3", "c");

        assertEquals(Optional.of("a2"), store.get(ALEX, "g1"));
        assertEquals(Optional.empty(), store.get(ALEX, "g2"));
        assertEquals(Optional.of("c"), store.get(ALEX, "g3"));
    }

    @Test
    void removeAndClearOnlyAffectTheRightPlayer() {
        ChoiceStore store = new ChoiceStore(10);
        store.set(ALEX, "g1", "a");
        store.set(ALEX, "g2", "b");
        store.set(SAM, "g1", "c");

        assertTrue(store.remove(ALEX, "g1"));
        assertFalse(store.remove(ALEX, "g1"));
        assertEquals(1, store.clear(ALEX));
        assertEquals(Optional.of("c"), store.get(SAM, "g1"));
        assertEquals(1, store.clearAll());
    }

    @Test
    void pruneRemovesOnlyMissingRecipes() {
        ChoiceStore store = new ChoiceStore(10);
        store.set(ALEX, "g1", "keep");
        store.set(ALEX, "g2", "gone");
        store.set(SAM, "g1", "gone");

        assertEquals(2, store.prune("keep"::equals));
        assertEquals(Optional.of("keep"), store.get(ALEX, "g1"));
        assertEquals(Optional.empty(), store.get(SAM, "g1"));
        assertTrue(store.snapshot().containsKey(ALEX));
        assertFalse(store.snapshot().containsKey(SAM), "players left with nothing are forgotten");
    }

    @Test
    void unknownRecipeIdsSurviveASaveAndLoad() {
        ChoiceStore store = new ChoiceStore(10);
        store.set(ALEX, "g1", "from_a_datapack_that_is_off");

        ChoiceStore loaded = new ChoiceStore(10);
        loaded.restore(store.snapshot());

        assertEquals(Optional.of("from_a_datapack_that_is_off"), loaded.get(ALEX, "g1"));
    }

    @Test
    void restoreTrimsPlayersOverTheCap() {
        Map<String, String> many = new LinkedHashMap<>();
        for (int i = 0; i < 5; i++) {
            many.put("g" + i, "r" + i);
        }

        ChoiceStore store = new ChoiceStore(3);
        store.restore(Map.of(ALEX, many));

        assertEquals(List.of("g2", "g3", "g4"), List.copyOf(store.snapshot().get(ALEX).keySet()));
    }

    @Test
    void theCapMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> new ChoiceStore(0));
    }

    @Test
    void pinsAreReplacedPerGroup() {
        PinMap pins = new PinMap();

        assertTrue(pins.pin("g", "a"));
        assertFalse(pins.pin("g", "a"));
        assertTrue(pins.pin("g", "b"));
        assertEquals(Optional.of("b"), pins.get("g"));
        assertTrue(pins.isPinned("g"));
        assertTrue(pins.unpin("g"));
        assertFalse(pins.unpin("g"));
        assertTrue(pins.isEmpty());
    }

    @Test
    void pinsAreCapped() {
        PinMap pins = new PinMap();
        for (int i = 0; i < PinMap.MAX_PINS + 5; i++) {
            pins.pin("g" + i, "r");
        }

        assertEquals(PinMap.MAX_PINS, pins.entries().size());
        assertFalse(pins.isPinned("g0"));
        assertTrue(pins.isPinned("g" + (PinMap.MAX_PINS + 4)));
    }

    @Test
    void pinsRoundTrip() {
        PinMap pins = new PinMap();
        pins.pin("g1", "a");
        pins.pin("g2", "b");

        PinMap loaded = new PinMap();
        loaded.restore(pins.entries());

        assertEquals(pins.entries(), loaded.entries());
    }

    @Test
    void switchingKeepsElapsedTimeWhileItFits() {
        assertEquals(60, CookMath.switchedProgress(60, 200));
        assertEquals(60, CookMath.switchedProgress(60, 61));
    }

    @Test
    void switchingPastTheNewTotalFinishesOnTheNextTick() {
        assertEquals(99, CookMath.switchedProgress(150, 100));
        assertEquals(99, CookMath.switchedProgress(100, 100));
    }

    @Test
    void switchingWithNoUsableTotalResetsTheTimer() {
        assertEquals(0, CookMath.switchedProgress(40, 0));
        assertEquals(0, CookMath.switchedProgress(-5, 100));
    }

    @Test
    void outputMergeFollowsVanilla() {
        assertTrue(CookMath.canMergeIntoOutput(true, false, 0, 1, 64));
        assertTrue(CookMath.canMergeIntoOutput(false, true, 10, 1, 64));
        assertTrue(CookMath.canMergeIntoOutput(false, true, 63, 1, 64));
        assertFalse(CookMath.canMergeIntoOutput(false, true, 64, 1, 64));
        assertFalse(CookMath.canMergeIntoOutput(false, false, 1, 1, 64));
    }
}

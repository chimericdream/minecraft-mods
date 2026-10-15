package com.chimericdream.opus.test.core;

import com.chimericdream.opus.component.TomeContentsComponent;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TomeContentsTest {
    private static final List<String> BOOKS = List.of("a:one", "b:two", "a:one");

    @Test
    void emptyHasNoBooksAndNoSelection() {
        assertTrue(TomeContentsComponent.EMPTY.isEmpty());
        assertFalse(TomeContentsComponent.EMPTY.hasSelection());
    }

    @Test
    void duplicatesAreKept() {
        assertEquals(BOOKS, TomeContentsComponent.of(BOOKS).books());
    }

    @Test
    void selectionMustPointAtABook() {
        TomeContentsComponent contents = TomeContentsComponent.of(BOOKS);

        assertTrue(contents.withSelected(2).hasSelection());
        assertFalse(contents.withSelected(3).hasSelection());
        assertFalse(contents.withSelected(-1).hasSelection());
        assertFalse(contents.withSelected(1).unselected().hasSelection());
    }

    @Test
    void withoutRemovesOnlyTheChosenDuplicateAndClearsSelection() {
        TomeContentsComponent remaining = TomeContentsComponent.of(BOOKS).withSelected(2).without(2);

        assertEquals(List.of("a:one", "b:two"), remaining.books());
        assertFalse(remaining.hasSelection());
    }

    @Test
    void roundTripsThroughJson() {
        TomeContentsComponent original = TomeContentsComponent.of(BOOKS).withSelected(1);

        JsonElement json = TomeContentsComponent.CODEC.encodeStart(JsonOps.INSTANCE, original).getOrThrow();

        assertEquals(original, TomeContentsComponent.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow());
    }

    @Test
    void selectionIsOptionalWhenReading() {
        JsonElement json = com.google.gson.JsonParser.parseString("{\"books\":[\"a:one\"]}");

        TomeContentsComponent parsed = TomeContentsComponent.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();

        assertEquals(List.of("a:one"), parsed.books());
        assertEquals(TomeContentsComponent.NONE, parsed.selected());
    }
}

package com.chimericdream.opus.test.core;

import com.chimericdream.lib.testkit.BootstrapMinecraft;
import com.chimericdream.opus.component.TomeContentsComponent;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Stores ItemStacks, so it bootstraps Minecraft (see BootstrapMinecraft).
class TomeContentsTest extends BootstrapMinecraft {
    private static List<ItemStack> books() {
        return List.of(new ItemStack(Items.BOOK), new ItemStack(Items.PAPER), new ItemStack(Items.BOOK));
    }

    @Test
    void emptyHasNoBooksAndNoSelection() {
        assertTrue(TomeContentsComponent.EMPTY.isEmpty());
        assertFalse(TomeContentsComponent.EMPTY.hasSelection());
    }

    @Test
    void duplicatesAreKept() {
        assertEquals(3, TomeContentsComponent.of(books()).books().size());
    }

    @Test
    void storedStacksAreSingleItems() {
        TomeContentsComponent contents = TomeContentsComponent.of(List.of(new ItemStack(Items.PAPER, 16)));

        assertEquals(1, contents.books().getFirst().getCount());
    }

    @Test
    void equalityComparesStackContents() {
        assertEquals(TomeContentsComponent.of(books()), TomeContentsComponent.of(books()));
        assertEquals(TomeContentsComponent.of(books()).hashCode(), TomeContentsComponent.of(books()).hashCode());
        assertFalse(TomeContentsComponent.of(books()).equals(TomeContentsComponent.of(books()).withSelected(0)));
    }

    @Test
    void selectionMustPointAtABook() {
        TomeContentsComponent contents = TomeContentsComponent.of(books());

        assertTrue(contents.withSelected(2).hasSelection());
        assertFalse(contents.withSelected(3).hasSelection());
        assertFalse(contents.withSelected(-1).hasSelection());
        assertFalse(contents.withSelected(1).unselected().hasSelection());
    }

    @Test
    void withoutRemovesOnlyTheChosenDuplicateAndClearsSelection() {
        TomeContentsComponent remaining = TomeContentsComponent.of(books()).withSelected(2).without(2);

        assertEquals(TomeContentsComponent.of(List.of(new ItemStack(Items.BOOK), new ItemStack(Items.PAPER))), remaining);
        assertFalse(remaining.hasSelection());
    }

    @Test
    void roundTripsThroughJson() {
        TomeContentsComponent original = TomeContentsComponent.of(books()).withSelected(1);

        JsonElement json = TomeContentsComponent.CODEC.encodeStart(JsonOps.INSTANCE, original).getOrThrow();

        assertEquals(original, TomeContentsComponent.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow());
    }

    @Test
    void selectionIsOptionalWhenReading() {
        JsonElement json = com.google.gson.JsonParser.parseString("{\"books\":[{\"id\":\"minecraft:book\"}]}");

        TomeContentsComponent parsed = TomeContentsComponent.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();

        assertEquals(1, parsed.books().size());
        assertEquals(TomeContentsComponent.NONE, parsed.selected());
    }
}

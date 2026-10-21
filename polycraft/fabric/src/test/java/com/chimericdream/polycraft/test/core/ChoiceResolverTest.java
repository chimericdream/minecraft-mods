package com.chimericdream.polycraft.test.core;

import com.chimericdream.polycraft.core.ChoiceResolver;
import com.chimericdream.polycraft.core.ChoiceResolver.Resolution;
import com.chimericdream.polycraft.core.ChoiceResolver.Source;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChoiceResolverTest {
    private static final List<String> OPTIONS = List.of("vanilla", "b", "c");

    @Test
    void withNothingSetVanillaWins() {
        assertEquals(new Resolution("vanilla", Source.VANILLA), ChoiceResolver.resolve(OPTIONS, Optional.empty(), Optional.empty(), List.of()));
    }

    @Test
    void packDefaultBeatsVanilla() {
        assertEquals(new Resolution("c", Source.PACK_DEFAULT), ChoiceResolver.resolve(OPTIONS, Optional.empty(), Optional.empty(), List.of("c", "b")));
    }

    @Test
    void playerBeatsPackDefault() {
        assertEquals(new Resolution("b", Source.PLAYER), ChoiceResolver.resolve(OPTIONS, Optional.empty(), Optional.of("b"), List.of("c")));
    }

    @Test
    void pinBeatsAPlayerWhoWantsSomethingElse() {
        assertEquals(new Resolution("c", Source.PIN), ChoiceResolver.resolve(OPTIONS, Optional.of("c"), Optional.of("b"), List.of("vanilla")));
    }

    @Test
    void noActingPlayerSkipsThePlayerStep() {
        assertEquals(new Resolution("c", Source.PACK_DEFAULT), ChoiceResolver.resolve(OPTIONS, Optional.empty(), Optional.empty(), List.of("c")));
    }

    @Test
    void aStalePinFallsThroughToThePlayer() {
        assertEquals(new Resolution("b", Source.PLAYER), ChoiceResolver.resolve(OPTIONS, Optional.of("gone"), Optional.of("b"), List.of()));
    }

    @Test
    void aStalePlayerChoiceFallsThroughToThePack() {
        assertEquals(new Resolution("c", Source.PACK_DEFAULT), ChoiceResolver.resolve(OPTIONS, Optional.empty(), Optional.of("gone"), List.of("c")));
    }

    @Test
    void packEntriesThatAreNotOptionsAreSkipped() {
        assertEquals(new Resolution("b", Source.PACK_DEFAULT), ChoiceResolver.resolve(OPTIONS, Optional.empty(), Optional.empty(), List.of("typo", "b")));
    }

    @Test
    void everythingStaleFallsBackToVanilla() {
        assertEquals(new Resolution("vanilla", Source.VANILLA), ChoiceResolver.resolve(OPTIONS, Optional.of("x"), Optional.of("y"), List.of("z")));
    }

    @Test
    void emptyOptionsAreAProgrammingError() {
        assertThrows(IllegalArgumentException.class, () -> ChoiceResolver.resolve(List.of(), Optional.empty(), Optional.empty(), List.of()));
    }
}

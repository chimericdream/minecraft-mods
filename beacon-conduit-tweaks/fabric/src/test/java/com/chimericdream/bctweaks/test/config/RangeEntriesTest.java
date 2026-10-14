package com.chimericdream.bctweaks.test.config;

import com.chimericdream.bctweaks.config.RangeEntries;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Pure logic: no Minecraft bootstrap needed (Identifier.tryParse only validates a string).
public class RangeEntriesTest {
    @Test
    void toLinesIsSortedById() {
        Map<String, Double> map = new LinkedHashMap<>();
        map.put("minecraft:netherite_block", 2.0);
        map.put("minecraft:diamond_block", 0.5);

        assertEquals(
            List.of("minecraft:diamond_block=0.5", "minecraft:netherite_block=2.0"),
            RangeEntries.toLines(map)
        );
    }

    @Test
    void emptyMapGivesNoLines() {
        assertTrue(RangeEntries.toLines(Map.of()).isEmpty());
        assertTrue(RangeEntries.fromLines(List.of()).isEmpty());
    }

    @Test
    void linesRoundTrip() {
        Map<String, Double> map = Map.of(
            "minecraft:iron_block", 0.0,
            "minecraft:diamond_block", 0.5,
            "minecraft:netherite_block", 2.0
        );

        assertEquals(map, RangeEntries.fromLines(RangeEntries.toLines(map)));
    }

    @Test
    void parsesIntegersNegativesAndWhitespace() {
        Map<String, Double> map = RangeEntries.fromLines(List.of(
            "minecraft:gold_block = 4",
            "  minecraft:copper_block=-1.5  "
        ));

        assertEquals(4.0, map.get("minecraft:gold_block"));
        assertEquals(-1.5, map.get("minecraft:copper_block"));
    }

    @Test
    void modIdsWithUnderscoresAndDashesParse() {
        Map<String, Double> map = RangeEntries.fromLines(List.of("some_mod:fancy-block/variant=1"));

        assertEquals(Map.of("some_mod:fancy-block/variant", 1.0), map);
    }

    @Test
    void badLinesAreSkippedAndGoodOnesKept() {
        Map<String, Double> map = RangeEntries.fromLines(List.of(
            "nonsense",
            "=3",
            "minecraft:stone=",
            "minecraft:stone=abc",
            "Not A Valid Id=1",
            "minecraft:dirt=0.25"
        ));

        assertEquals(Map.of("minecraft:dirt", 0.25), map);
    }

    @Test
    void unknownIdsAreSkippedWhenAPredicateRejectsThem() {
        Map<String, Double> map = RangeEntries.fromLines(
            List.of("minecraft:stone=1", "minecraft:nonsense=2"),
            id -> id.equals("minecraft:stone")
        );

        assertEquals(Map.of("minecraft:stone", 1.0), map);
    }

    @Test
    void laterDuplicateWins() {
        Map<String, Double> map = RangeEntries.fromLines(List.of("minecraft:stone=1", "minecraft:stone=2"));

        assertEquals(Map.of("minecraft:stone", 2.0), map);
    }

    @Test
    void perBlockMapsAreSerializedFields() throws NoSuchFieldException, ClassNotFoundException {
        // `transient` fields are skipped by Gson; the maps must be saved to the config file.
        // Class.forName(..., false, ...) avoids running the config's static initializer.
        Class<?> config = Class.forName("com.chimericdream.bctweaks.config.BCTweaksConfig", false, getClass().getClassLoader());

        for (String name : List.of("beaconBaseRange", "beaconRangePerLevel", "conduitAddVanillaRange", "conduitBaseRange", "conduitFrameBlocksPerStep", "conduitRangePerStep", "beaconRangePerBlock", "conduitRangePerBlock")) {
            Field field = config.getDeclaredField(name);
            assertNotNull(field.getAnnotation(SerialEntry.class), name + " needs @SerialEntry");
            assertFalse(Modifier.isTransient(field.getModifiers()), name + " must not be transient");
        }
    }
}

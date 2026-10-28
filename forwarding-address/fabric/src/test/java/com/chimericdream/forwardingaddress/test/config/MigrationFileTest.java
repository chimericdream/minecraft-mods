package com.chimericdream.forwardingaddress.test.config;

import com.chimericdream.forwardingaddress.config.MigrationFile;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MigrationFileTest extends BootstrapMinecraft {
    private static Identifier id(String text) {
        return Identifier.parse(text);
    }

    @Test
    void readsBlocksItemsAndStubs() {
        MigrationFile.Parsed parsed = MigrationFile.parse("""
            {
              "blocks": [
                {"from": "old:a", "to": "new:a"},
                {"from": "old:b", "to": "new:b", "stub": {"template": "minecraft:oak_stairs"}},
                {"from": "old:c", "to": "new:c",
                 "stub": {"properties": {"facing": ["north", "south"], "lit": ["true", "false"]}},
                 "renameProperties": {"dir": "facing"},
                 "mapValues": {"half": {"upper": "top"}}}
              ],
              "items": [{"from": "old:wrench", "to": "new:wrench"}]
            }
            """);

        assertEquals(List.of(), parsed.problems());
        assertEquals(3, parsed.blocks().size());
        assertEquals(1, parsed.items().size());

        assertNull(parsed.blocks().get(0).stub());
        assertEquals(id("minecraft:oak_stairs"), parsed.blocks().get(1).stub().template());

        MigrationFile.BlockEntry third = parsed.blocks().get(2);
        assertEquals(List.of("north", "south"), third.stub().properties().get("facing"));
        assertEquals(Map.of("dir", "facing"), third.renameProperties());
        assertEquals(Map.of("half", Map.of("upper", "top")), third.mapValues());
        assertEquals(id("new:wrench"), parsed.items().getFirst().to());
    }

    @Test
    void anEntryWithABlankOrMissingTargetIsIgnoredWithAMessage() {
        MigrationFile.Parsed parsed = MigrationFile.parse("""
            {"blocks": [{"from": "old:a", "to": ""}, {"from": "old:b"}], "items": [{"from": "old:c", "to": " "}]}
            """);

        assertTrue(parsed.blocks().isEmpty());
        assertTrue(parsed.items().isEmpty());
        assertEquals(3, parsed.problems().size());
        assertTrue(parsed.problems().getFirst().contains("no target"));
    }

    @Test
    void oneBadEntryDoesNotStopTheRest() {
        MigrationFile.Parsed parsed = MigrationFile.parse("""
            {"blocks": [{"from": "NOT AN ID", "to": "new:a"}, {"from": "old:ok", "to": "new:ok"}, 7]}
            """);

        assertEquals(1, parsed.blocks().size());
        assertEquals(id("old:ok"), parsed.blocks().getFirst().from());
        assertEquals(2, parsed.problems().size());
    }

    @Test
    void aStubNeedsExactlyOneOfTemplateOrProperties() {
        MigrationFile.Parsed both = MigrationFile.parse("""
            {"blocks": [{"from": "a:b", "to": "c:d", "stub": {"template": "minecraft:stone", "properties": {}}}]}
            """);
        MigrationFile.Parsed neither = MigrationFile.parse("""
            {"blocks": [{"from": "a:b", "to": "c:d", "stub": {}}]}
            """);

        assertTrue(both.blocks().isEmpty());
        assertTrue(neither.blocks().isEmpty());
        assertEquals(1, both.problems().size());
        assertEquals(1, neither.problems().size());
    }

    @Test
    void rejectsPropertyAndValueNamesTheGameWouldChokeOn() {
        for (String properties : List.of(
            "{\"Facing\": [\"north\"]}",
            "{\"facing\": []}",
            "{\"facing\": [\"North\"]}",
            "{\"facing\": [\"north\", \"north\"]}",
            "{\"facing\": \"north\"}"
        )) {
            MigrationFile.Parsed parsed = MigrationFile.parse("{\"blocks\": [{\"from\": \"a:b\", \"to\": \"c:d\", \"stub\": {\"properties\": " + properties + "}}]}");

            assertTrue(parsed.blocks().isEmpty(), properties);
            assertEquals(1, parsed.problems().size(), properties);
        }
    }

    @Test
    void badJsonIsReportedNotThrown() {
        assertEquals(1, MigrationFile.parse("{not json").problems().size());
        assertEquals(1, MigrationFile.parse("[]").problems().size());
        assertNotNull(MigrationFile.parse("{}"));
        assertTrue(MigrationFile.parse("{}").problems().isEmpty());
    }

    @Test
    void aListWhereAnObjectBelongsIsReported() {
        MigrationFile.Parsed parsed = MigrationFile.parse("{\"blocks\": {}, \"items\": 3}");

        assertEquals(2, parsed.problems().size());
    }
}

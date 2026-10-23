package com.chimericdream.polycraft.test.core;

import com.chimericdream.polycraft.core.IdFilter;
import com.chimericdream.polycraft.core.Rules;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RulesParserTest {
    private final List<String> warnings = new ArrayList<>();

    private Rules parse(String json) {
        return Rules.parse(json, warnings::add);
    }

    @Test
    void readsAFullFile() {
        Rules rules = parse("""
            {
              "priority": ["minecraft:oak_planks_from_logs", "othermod:planks_x4"],
              "exclude": ["badmod:duplicate_recipe"],
              "groups": { "minecraft:crafting|shapeless:planks": { "default": "othermod:planks_x4" } }
            }
            """);

        assertEquals(List.of("minecraft:oak_planks_from_logs", "othermod:planks_x4"), rules.priority());
        assertEquals(Set.of("badmod:duplicate_recipe"), rules.exclude());
        assertEquals(Map.of("minecraft:crafting|shapeless:planks", "othermod:planks_x4"), rules.groupDefaults());
        assertTrue(warnings.isEmpty());
    }

    @Test
    void anEmptyObjectIsFine() {
        assertEquals(Rules.EMPTY.priority(), parse("{}").priority());
        assertTrue(warnings.isEmpty());
    }

    @Test
    void invalidJsonWarnsAndIsIgnored() {
        Rules rules = parse("{ nope");

        assertEquals(Rules.EMPTY, rules);
        assertEquals(1, warnings.size());
    }

    @Test
    void aNonObjectIsIgnored() {
        assertEquals(Rules.EMPTY, parse("[1, 2]"));
        assertEquals(1, warnings.size());
    }

    @Test
    void unknownKeysWarnButTheRestStillLoads() {
        Rules rules = parse("{\"colour\": \"red\", \"priority\": [\"a:b\"]}");

        assertEquals(List.of("a:b"), rules.priority());
        assertEquals(1, warnings.size());
    }

    @Test
    void badIdsAreSkippedWithAWarning() {
        Rules rules = parse("{\"priority\": [\"good:id\", \"Not An Id\", 5, \"ok:path/sub\"]}");

        assertEquals(List.of("good:id", "ok:path/sub"), rules.priority());
        assertEquals(2, warnings.size());
    }

    @Test
    void wrongTypesWarn() {
        parse("{\"priority\": \"a:b\", \"exclude\": 3, \"groups\": []}");

        assertEquals(3, warnings.size());
    }

    @Test
    void priorityNamespacesRejectWildcardsAndFullIds() {
        Rules rules = parse("{\"priorityNamespaces\": [\"create\", \"mod:*\", \"a:b\", 5, \"my_pack\"], \"exclude\": []}");

        assertEquals(List.of("create", "my_pack"), rules.priorityNamespaces());
        assertEquals(3, warnings.size());
    }

    @Test
    void priorityNamespacesRankAfterExactIdsAndKeepOptionOrder() {
        Rules rules = parse("{\"priority\": [\"b:exact\"], \"priorityNamespaces\": [\"a\", \"c\"]}");

        assertEquals(
            List.of("b:exact", "a:one", "a:two", "c:one"),
            rules.packOrder("g", List.of("c:one", "a:one", "b:exact", "a:two", "d:other"))
        );
    }

    @Test
    void mergeCombinesPriorityNamespaces() {
        Rules config = parse("{\"priorityNamespaces\": [\"a\"]}");
        Rules datapack = parse("{\"priorityNamespaces\": [\"a\", \"b\"]}");

        assertEquals(List.of("a", "b"), config.merge(datapack).priorityNamespaces());
    }

    @Test
    void aGroupWithoutADefaultIsSkipped() {
        Rules rules = parse("{\"groups\": {\"g\": {}, \"h\": {\"default\": \"a:b\"}}}");

        assertEquals(Map.of("h", "a:b"), rules.groupDefaults());
        assertEquals(1, warnings.size());
    }

    @Test
    void mergeLetsTheFirstWinAndCombinesTheRest() {
        Rules config = Rules.parse("""
            {"priority": ["a:one"], "exclude": ["x:y"], "groups": {"g": {"default": "a:config"}}}
            """, warnings::add);
        Rules datapack = Rules.parse("""
            {"priority": ["a:one", "a:two"], "exclude": ["z:w"], "groups": {"g": {"default": "a:pack"}, "h": {"default": "a:h"}}}
            """, warnings::add);

        Rules merged = config.merge(datapack);

        assertEquals(List.of("a:one", "a:two"), merged.priority());
        assertEquals(Set.of("x:y", "z:w"), merged.exclude());
        assertEquals("a:config", merged.groupDefaults().get("g"));
        assertEquals("a:h", merged.groupDefaults().get("h"));
    }

    @Test
    void packOrderPutsGroupThenRecipeThenGlobal() {
        Rules rules = Rules.parse("""
            {"priority": ["a:global"], "groups": {"the-group": {"default": "a:group"}, "a:member": {"default": "a:from_member"}}}
            """, warnings::add);

        assertEquals(
            List.of("a:group", "a:from_member", "a:global"),
            rules.packOrder("the-group", List.of("a:other", "a:member"))
        );
    }

    @Test
    void excludedRecipesAreReported() {
        Rules rules = parse("{\"exclude\": [\"bad:one\"]}");

        assertTrue(rules.isExcluded("bad:one"));
        assertFalse(rules.isExcluded("good:one"));
    }

    @Test
    void idFilterSupportsExactNamespaceAndEverything() {
        IdFilter filter = new IdFilter(List.of("minecraft:*", "polymod:special"), List.of("minecraft:banned"));

        assertTrue(filter.test("minecraft:crafting"));
        assertTrue(filter.test("polymod:special"));
        assertFalse(filter.test("polymod:other"));
        assertFalse(filter.test("minecraft:banned"), "deny wins over allow");
        assertTrue(IdFilter.ALLOW_ALL.test("anything:at_all"));
        assertFalse(new IdFilter(List.of(), List.of("*")).test("anything:at_all"));
    }
}

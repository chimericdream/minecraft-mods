package com.chimericdream.opus.test.core;

import com.chimericdream.opus.core.widget.RecipeSpec;
import com.chimericdream.opus.core.widget.SpecException;
import com.chimericdream.opus.core.frontmatter.YamlSupport;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipeSpecTest {
    private static RecipeSpec parse(String yaml) throws Exception {
        return RecipeSpec.parse(YamlSupport.loadMap(yaml));
    }

    @Test
    void shapedRecipeFillsTheGridTopLeftAligned() throws Exception {
        RecipeSpec r = parse("""
            pattern: ["I I", "ICI", " I "]
            key: { I: iron_ingot, C: chest }
            result: hopper
            """);

        assertEquals(RecipeSpec.Kind.CRAFTING_SHAPED, r.kind());
        assertEquals(3, r.patternWidth());
        assertEquals(3, r.patternHeight());
        assertEquals("minecraft:hopper", r.result());
        assertEquals("minecraft:iron_ingot", r.grid().get(0).alternatives().get(0));
        assertTrue(r.grid().get(1).isEmpty());
        assertEquals("minecraft:chest", r.grid().get(4).alternatives().get(0));
    }

    @Test
    void smallPatternsKeepTheirSize() throws Exception {
        RecipeSpec r = parse("pattern: [\"AA\", \"AA\"]\nkey: { A: oak_planks }\nresult: crafting_table\n");

        assertEquals(2, r.patternWidth());
        assertEquals(2, r.patternHeight());
    }

    @Test
    void alternativesAndTagsAreSupported() throws Exception {
        RecipeSpec r = parse("pattern: [\"P\"]\nkey: { P: '#minecraft:planks' }\nresult: stick\ncount: 4\n");

        assertEquals("#minecraft:planks", r.grid().get(0).alternatives().get(0));
        assertEquals(4, r.count());

        RecipeSpec alt = parse("ingredients: [[oak_log, birch_log], stick]\nresult: chest\n");
        assertEquals(2, alt.grid().get(0).alternatives().size());
        assertEquals(RecipeSpec.Kind.CRAFTING_SHAPELESS, alt.kind());
    }

    @Test
    void cookingAndStonecuttingAndSmithing() throws Exception {
        RecipeSpec smelt = parse("type: smelting\ningredient: iron_ore\nresult: iron_ingot\ncooking_time: 100\nexperience: 0.7\n");
        assertEquals(RecipeSpec.Kind.SMELTING, smelt.kind());
        assertEquals(100, smelt.cookingTime());
        assertEquals(0.7, smelt.experience());

        assertEquals(RecipeSpec.Kind.STONECUTTING, parse("type: stonecutting\ningredient: stone\nresult: stone_bricks\n").kind());

        RecipeSpec smith = parse("type: smithing\ntemplate: netherite_upgrade_smithing_template\nbase: diamond_sword\naddition: netherite_ingot\nresult: netherite_sword\n");
        assertEquals(3, smith.grid().size());
    }

    @Test
    void recipeByIdIsKeptForFutureServerSync() throws Exception {
        RecipeSpec r = parse("recipe: hopper\n");

        assertEquals(RecipeSpec.Kind.BY_ID, r.kind());
        assertEquals("minecraft:hopper", r.recipeId());
    }

    @Test
    void anInlineRecipeKeepsAnOptionalIdForTheFutureServerLookup() throws Exception {
        RecipeSpec r = parse("recipe: hopper\npattern: [\"I I\", \"ICI\", \" I \"]\nkey: { I: iron_ingot, C: chest }\nresult: hopper\n");

        assertEquals(RecipeSpec.Kind.CRAFTING_SHAPED, r.kind(), "an id never replaces the inline definition");
        assertEquals("minecraft:hopper", r.recipeId());
        assertEquals("minecraft:hopper", r.result());

        RecipeSpec smelt = parse("recipe: minecraft:iron_ingot_from_smelting_iron_ore\ntype: smelting\ningredient: iron_ore\nresult: iron_ingot\n");
        assertEquals(RecipeSpec.Kind.SMELTING, smelt.kind());
        assertEquals("minecraft:iron_ingot_from_smelting_iron_ore", smelt.recipeId());

        assertNull(parse("pattern: [\"A\"]\nkey: { A: stone }\nresult: stick\n").recipeId(), "no id when none is given");
    }

    @Test
    void invalidRecipesExplainWhy() {
        assertTrue(message("pattern: [\"AB\"]\nkey: { A: stone }\nresult: x\n").contains("'B'"));
        assertTrue(message("pattern: [\"A\"]\nkey: { A: stone, Z: dirt }\nresult: x\n").contains("never used"));
        assertTrue(message("pattern: [\"AAAA\"]\nkey: { A: stone }\nresult: x\n").contains("1-3"));
        assertTrue(message("pattern: [\"A\"]\nresult: x\n").contains("key"));
        assertTrue(message("type: bogus\nresult: x\n").contains("unknown recipe type"));
        assertTrue(message("pattern: [\"A\"]\nkey: { A: Not Valid }\nresult: x\n").contains("not a valid id"));
        assertTrue(message("pattern: [\"A\"]\nkey: { A: stone }\n").contains("result"));
    }

    private static String message(String yaml) {
        return assertThrows(SpecException.class, () -> RecipeSpec.parse(YamlSupport.loadMap(yaml))).getMessage();
    }

    @Test
    void yamlRejectsMalformedInputAsAnException() throws Exception {
        assertThrows(YamlSupport.YamlException.class, () -> YamlSupport.loadMap("a: [unclosed"));
        assertThrows(YamlSupport.YamlException.class, () -> YamlSupport.loadMap("- just\n- a list\n"));
        assertEquals(Map.of(), YamlSupport.loadMap(""));
    }
}

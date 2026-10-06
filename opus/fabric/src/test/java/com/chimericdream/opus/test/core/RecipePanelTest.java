package com.chimericdream.opus.test.core;

import com.chimericdream.opus.core.frontmatter.YamlSupport;
import com.chimericdream.opus.core.widget.RecipePanel;
import com.chimericdream.opus.core.widget.RecipeSpec;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipePanelTest {
    private static RecipePanel panel(String yaml) throws Exception {
        return RecipePanel.of(RecipeSpec.parse(YamlSupport.loadMap(yaml)));
    }

    private static long count(RecipePanel p, int source) {
        return p.slots().stream().filter(s -> s.source() == source).count();
    }

    private static final String CRAFTING = "pattern: [\"AA\", \"AA\"]\nkey: { A: oak_planks }\nresult: crafting_table\n";
    private static final String SMELTING = "type: smelting\ningredient: iron_ore\nresult: iron_ingot\n";

    @Test
    void craftingHasANineSlotGridAndABigResultSlot() throws Exception {
        RecipePanel p = panel(CRAFTING);

        assertEquals("container.crafting", p.titleKey());
        assertEquals(132, p.width());
        assertEquals(76, p.height());
        assertEquals(10, p.slots().size());
        for (int i = 0; i < 9; i++) {
            assertEquals(1, count(p, i), "grid slot " + i);
        }
        assertEquals(1, count(p, RecipePanel.RESULT));
        assertEquals(RecipePanel.BIG_SLOT, p.slots().stream().filter(s -> s.source() == RecipePanel.RESULT).findFirst().orElseThrow().size());
        assertNull(p.flame());
        assertFalse(p.shapeless());
    }

    @Test
    void shapelessIsFlaggedSoTheTitleCanSayIt() throws Exception {
        assertTrue(panel("ingredients: [stick, stick]\nresult: chest\n").shapeless());
    }

    @Test
    void anIdWithNoInlineDefinitionShowsTheIdInsteadOfATitle() throws Exception {
        RecipePanel p = panel("recipe: minecraft:hopper\n");

        assertEquals("minecraft:hopper", p.literalTitle());
        assertEquals(132, p.width());
    }

    @Test
    void furnaceRecipesHaveFuelSlotFlameAndResult() throws Exception {
        RecipePanel p = panel(SMELTING);

        assertEquals("container.furnace", p.titleKey());
        assertEquals(1, count(p, 0));
        assertEquals(1, count(p, RecipePanel.EMPTY), "the fuel slot is drawn empty");
        assertEquals(1, count(p, RecipePanel.RESULT));
        assertNotNull(p.flame());
        assertEquals(76, p.height());
        assertEquals(-1, p.footerY());

        assertEquals("container.blast_furnace", panel("type: blasting\ningredient: iron_ore\nresult: iron_ingot\n").titleKey());
        assertEquals("container.smoker", panel("type: smoking\ningredient: beef\nresult: cooked_beef\n").titleKey());
    }

    @Test
    void cookingInfoAddsAFooterLine() throws Exception {
        RecipePanel plain = panel(SMELTING);
        RecipePanel info = panel(SMELTING + "cooking_time: 200\nexperience: 0.7\n");

        assertTrue(info.height() > plain.height());
        assertTrue(info.footerY() >= 70, "below the slots");
        assertEquals(200, info.cookingTime());
        assertEquals(0.7, info.experience());
    }

    @Test
    void campfireHasNoFuelSlotOrFlame() throws Exception {
        RecipePanel p = panel("type: campfire_cooking\ningredient: beef\nresult: cooked_beef\n");

        assertEquals("block.minecraft.campfire", p.titleKey());
        assertEquals(0, count(p, RecipePanel.EMPTY));
        assertNull(p.flame());
        assertEquals(2, p.slots().size());
    }

    @Test
    void stonecuttingAndSmithingArePanelsWithASingleRowOfSlots() throws Exception {
        RecipePanel cut = panel("type: stonecutting\ningredient: stone\nresult: stone_bricks\n");
        assertEquals("container.stonecutter", cut.titleKey());
        assertEquals(2, cut.slots().size());

        RecipePanel smith = panel("type: smithing\ntemplate: netherite_upgrade_smithing_template\nbase: diamond_sword\naddition: netherite_ingot\nresult: netherite_sword\n");
        assertEquals("container.upgrade", smith.titleKey());
        assertEquals(4, smith.slots().size());
        assertEquals(1, count(smith, 0));
        assertEquals(1, count(smith, 1));
        assertEquals(1, count(smith, 2));
    }

    @Test
    void everythingStaysInsideThePanelAndSlotsDoNotOverlap() throws Exception {
        for (String yaml : new String[] {
            CRAFTING,
            "recipe: hopper\n",
            SMELTING,
            SMELTING + "cooking_time: 100\nexperience: 1\n",
            "type: campfire_cooking\ningredient: beef\nresult: cooked_beef\n",
            "type: stonecutting\ningredient: stone\nresult: stone_bricks\n",
            "type: smithing\ntemplate: netherite_upgrade_smithing_template\nbase: diamond_sword\naddition: netherite_ingot\nresult: netherite_sword\n",
        }) {
            RecipePanel p = panel(yaml);

            for (RecipePanel.Slot s : p.slots()) {
                assertTrue(s.x() >= 0 && s.y() >= 0 && s.x() + s.size() <= p.width() && s.y() + s.size() <= p.height(), "slot outside panel for: " + yaml + " " + s);
            }
            RecipePanel.Box a = p.arrow();
            assertTrue(a.x() >= 0 && a.x() + a.width() <= p.width() && a.y() >= 0 && a.y() + a.height() <= p.height(), "arrow outside panel for: " + yaml);

            for (int i = 0; i < p.slots().size(); i++) {
                for (int j = i + 1; j < p.slots().size(); j++) {
                    RecipePanel.Slot x = p.slots().get(i);
                    RecipePanel.Slot y = p.slots().get(j);
                    boolean overlap = x.x() < y.x() + y.size() && y.x() < x.x() + x.size() && x.y() < y.y() + y.size() && y.y() < x.y() + x.size();
                    assertFalse(overlap, "slots overlap for: " + yaml + " " + x + " " + y);
                }
                RecipePanel.Slot s = p.slots().get(i);
                boolean overArrow = s.x() < a.x() + a.width() && a.x() < s.x() + s.size() && s.y() < a.y() + a.height() && a.y() < s.y() + s.size();
                assertFalse(overArrow, "slot overlaps arrow for: " + yaml + " " + s);
            }
        }
    }

    @Test
    void theResultSlotIsVerticallyCentredOnTheInputs() throws Exception {
        RecipePanel p = panel(CRAFTING);

        RecipePanel.Slot result = p.slots().stream().filter(s -> s.source() == RecipePanel.RESULT).findFirst().orElseThrow();
        int gridCentre = (p.slots().get(0).y() + p.slots().get(8).y() + RecipePanel.SLOT) / 2;
        assertEquals(gridCentre, result.y() + result.size() / 2);
        assertEquals(gridCentre, p.arrow().y() + p.arrow().height() / 2, 1);
    }
}

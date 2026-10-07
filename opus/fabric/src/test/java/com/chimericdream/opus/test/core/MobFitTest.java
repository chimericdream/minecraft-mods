package com.chimericdream.opus.test.core;

import com.chimericdream.opus.core.widget.MobFit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MobFitTest {
    private static final MobFit.Bounds VILLAGER = new MobFit.Bounds(0.6f, 1.95f);
    private static final MobFit.Bounds CREEPER = new MobFit.Bounds(0.6f, 1.7f);
    private static final MobFit.Bounds BAT = new MobFit.Bounds(0.5f, 0.9f);
    private static final MobFit.Bounds GHAST = new MobFit.Bounds(4f, 4f);

    @Test
    void everyMobUsesTheSameScale() {
        float villager = MobFit.fit(VILLAGER, 1f, 800, 4).pixelsPerBlock();
        assertEquals(villager, MobFit.fit(CREEPER, 1f, 800, 4).pixelsPerBlock());
        assertEquals(villager, MobFit.fit(BAT, 1f, 800, 4).pixelsPerBlock());
        assertEquals(villager, MobFit.fit(GHAST, 1f, 800, 4).pixelsPerBlock());
    }

    @Test
    void villagerAtReferenceGuiScaleIsTheReferenceSize() {
        assertEquals(MobFit.REFERENCE_PIXELS_PER_BLOCK, MobFit.fit(VILLAGER, 1f, 800, 4).pixelsPerBlock());
    }

    @Test
    void guiScaleKeepsThePhysicalSize() {
        float at4 = MobFit.fit(VILLAGER, 1f, 800, 4).pixelsPerBlock();
        assertEquals(at4 * 2, MobFit.fit(VILLAGER, 1f, 800, 2).pixelsPerBlock(), 0.001f);
        assertEquals(at4 / 2, MobFit.fit(VILLAGER, 1f, 800, 8).pixelsPerBlock(), 0.001f);
    }

    @Test
    void smallMobsGetTheVillagersBoxAndBigOnesGrowIt() {
        MobFit.Result villager = MobFit.fit(VILLAGER, 1f, 800, 4);
        assertEquals(villager.width(), MobFit.fit(BAT, 1f, 800, 4).width());
        assertEquals(villager.height(), MobFit.fit(BAT, 1f, 800, 4).height());
        assertTrue(MobFit.fit(GHAST, 1f, 800, 4).height() > villager.height());
    }

    @Test
    void boxSidesAreMultiplesOfSixteen() {
        for (MobFit.Bounds b : new MobFit.Bounds[]{VILLAGER, CREEPER, BAT, GHAST}) {
            MobFit.Result r = MobFit.fit(b, 1f, 800, 3);
            assertEquals(0, r.width() % 16);
            assertEquals(0, r.height() % 16);
        }
    }

    @Test
    void mobShrinksToFitWhenThePageIsTooNarrow() {
        MobFit.Result r = MobFit.fit(GHAST, 1f, 64, 4);
        assertEquals(64, r.width());
        assertTrue(4f * 1.4f * r.pixelsPerBlock() <= r.width() - 2 * MobFit.PAD + 0.01f);
    }

    @Test
    void scaleIsAMultiplier() {
        assertEquals(MobFit.fit(VILLAGER, 1f, 800, 4).pixelsPerBlock() / 2, MobFit.fit(VILLAGER, 0.5f, 800, 4).pixelsPerBlock(), 0.001f);
    }
}

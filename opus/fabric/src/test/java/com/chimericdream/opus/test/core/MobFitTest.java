package com.chimericdream.opus.test.core;

import com.chimericdream.opus.core.widget.MobFit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MobFitTest {
    @Test
    void boxSidesAreMultiplesOfSixteen() {
        for (MobFit.Bounds b : new MobFit.Bounds[]{new MobFit.Bounds(0.6f, 1.95f), new MobFit.Bounds(4f, 4f), new MobFit.Bounds(0.5f, 0.9f)}) {
            MobFit.Result r = MobFit.fit(b, 1f, 400);
            assertEquals(0, r.width() % 16);
            assertEquals(0, r.height() % 16);
        }
    }

    @Test
    void tallMobGetsTallBoxAndCubicMobGetsSquareBox() {
        MobFit.Result zombie = MobFit.fit(new MobFit.Bounds(0.6f, 1.95f), 1f, 400);
        MobFit.Result ghast = MobFit.fit(new MobFit.Bounds(4f, 4f), 1f, 400);
        assertTrue(zombie.height() > zombie.width());
        assertEquals(ghast.width(), ghast.height());
    }

    @Test
    void mobAlwaysFitsInsideItsBoxWithPadding() {
        MobFit.Bounds b = new MobFit.Bounds(4f, 4f);
        MobFit.Result r = MobFit.fit(b, 8f, 40);
        assertTrue(b.width() * r.pixelsPerBlock() <= r.width() - 2 * MobFit.PAD + 0.01f);
        assertTrue(b.height() * r.pixelsPerBlock() <= r.height() - 2 * MobFit.PAD + 0.01f);
    }

    @Test
    void scaleIsAMultiplierOnTheFit() {
        MobFit.Bounds b = new MobFit.Bounds(0.6f, 1.95f);
        assertTrue(MobFit.fit(b, 0.5f, 400).pixelsPerBlock() < MobFit.fit(b, 1f, 400).pixelsPerBlock());
    }

    @Test
    void boxNeverExceedsAvailableWidth() {
        assertTrue(MobFit.fit(new MobFit.Bounds(4f, 4f), 1f, 50).width() <= 48);
    }
}

package com.chimericdream.chimericlib.test.colors;

import com.chimericdream.chimericlib.test.BootstrapMinecraft;
import com.chimericdream.lib.colors.ColorHelpers;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Touches Items/Blocks/DyeColor, so it bootstraps Minecraft (see BootstrapMinecraft).
public class ColorHelpersTest extends BootstrapMinecraft {
    @Test
    void rgbFromIntRoundTrips() {
        ColorHelpers.RGB rgb = new ColorHelpers.RGB(18, 240, 7);

        // The stored int drops alpha (alpha 0), so re-reading the channels must be lossless.
        assertEquals(rgb, ColorHelpers.RGB.fromInt(rgb.getColor()));
        assertEquals(rgb.getColor(), rgb.toInt());
    }

    @Test
    void rgbFromIntIgnoresAlpha() {
        // 0xAARRGGBB with a non-zero alpha byte still decodes to the RGB channels.
        ColorHelpers.RGB rgb = ColorHelpers.RGB.fromInt(0xFF12F007);

        assertEquals(0x12, rgb.r());
        assertEquals(0xF0, rgb.g());
        assertEquals(0x07, rgb.b());
    }

    @Test
    void rgbToHexZeroPadsChannels() {
        assertEquals("#000000", new ColorHelpers.RGB(0, 0, 0).toHex());
        assertEquals("#ffffff", new ColorHelpers.RGB(255, 255, 255).toHex());
        assertEquals("#ff0010", new ColorHelpers.RGB(255, 0, 16).toHex());
    }

    @Test
    void getTintReturnsVariantAtIndex() {
        int[] variants = {0xaaaaaa, 0xbbbbbb, 0xcccccc};

        assertEquals(0xaaaaaa, ColorHelpers.getTint(0, variants));
        assertEquals(0xcccccc, ColorHelpers.getTint(2, variants));
    }

    @Test
    void getTintFallsBackToFirstVariantWhenOutOfRange() {
        int[] variants = {0xaaaaaa, 0xbbbbbb};

        assertEquals(0xaaaaaa, ColorHelpers.getTint(2, variants));
        assertEquals(0xaaaaaa, ColorHelpers.getTint(99, variants));
    }

    @Test
    void getNameTitleCasesKnownColors() {
        assertEquals("White", ColorHelpers.getName("white"));
        assertEquals("Light Gray", ColorHelpers.getName("light_gray"));
        assertEquals("Pink", ColorHelpers.getName("pink"));
        // DyeColor overload delegates to the serialized name.
        assertEquals("Light Blue", ColorHelpers.getName(DyeColor.LIGHT_BLUE));
    }

    @Test
    void getNameRejectsUnknownColor() {
        assertThrows(RuntimeException.class, () -> ColorHelpers.getName("chartreuse"));
    }

    @Test
    void getColorsListsAllSixteenInOrder() {
        String[] colors = ColorHelpers.getColors();

        assertEquals(16, colors.length);
        assertEquals("white", colors[0]);
        assertEquals("pink", colors[15]);
        // Every entry must resolve through getName without throwing.
        for (String color : colors) {
            ColorHelpers.getName(color);
        }
    }

    @Test
    void getDyeAndWoolResolveForEveryColor() {
        for (String color : ColorHelpers.getColors()) {
            assertNotEquals(Items.AIR, ColorHelpers.getDye(color), color);
            assertNotEquals(Blocks.AIR, ColorHelpers.getWool(color), color);
        }
        assertSame(Items.DYE.red(), ColorHelpers.getDye(DyeColor.RED));
        assertSame(Blocks.WOOL.red(), ColorHelpers.getWool("red"));
    }

    @Test
    void getDyeAndWoolRejectUnknownColor() {
        assertThrows(RuntimeException.class, () -> ColorHelpers.getDye("chartreuse"));
        assertThrows(RuntimeException.class, () -> ColorHelpers.getWool("chartreuse"));
    }

    @Test
    void mixColorsWithNoInputsReturnsNull() {
        assertNull(ColorHelpers.mixColors(null, List.of()));
    }

    @Test
    void mixColorsWithSingleSeedIsIdentity() {
        ColorHelpers.RGB seed = new ColorHelpers.RGB(10, 20, 30);

        assertEquals(seed, ColorHelpers.mixColors(seed, List.of()));
    }

    @Test
    void mixColorsBlendsTowardAddedDye() {
        ColorHelpers.RGB mixed = ColorHelpers.mixColors(new ColorHelpers.RGB(0, 0, 0), List.of(DyeColor.RED));

        // Blending black with red must produce a real color whose channels stay in byte range.
        assertNotEquals(new ColorHelpers.RGB(0, 0, 0), mixed);
        assertTrue(mixed.r() >= 0 && mixed.r() <= 255, "r in range");
        assertTrue(mixed.g() >= 0 && mixed.g() <= 255, "g in range");
        assertTrue(mixed.b() >= 0 && mixed.b() <= 255, "b in range");
    }
}

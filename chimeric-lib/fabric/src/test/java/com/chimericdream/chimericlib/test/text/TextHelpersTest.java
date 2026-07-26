package com.chimericdream.chimericlib.test.text;

import com.chimericdream.lib.text.TextHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Pure component/string building — no registry access, so no Minecraft bootstrap is required.
public class TextHelpersTest {
    @Test
    void getColorTranslationKeyBuildsVanillaKey() {
        assertEquals("color.minecraft.red", TextHelpers.getColorTranslationKey("red"));
        assertEquals("color.minecraft.light_blue", TextHelpers.getColorTranslationKey("light_blue"));
    }

    @Test
    void formatTooltipAppliesAquaItalicStyle() {
        MutableComponent formatted = TextHelpers.formatTooltip(Component.literal("Hello"));

        assertEquals("Hello", formatted.getString());
        assertEquals(TextColor.fromLegacyFormat(ChatFormatting.AQUA), formatted.getStyle().getColor());
        assertTrue(formatted.getStyle().isItalic());
    }

    @Test
    void getTooltipWrapsTranslatableKeyWithStyle() {
        MutableComponent tooltip = TextHelpers.getTooltip("mymod.tooltip.example");

        // With no language loaded the translatable falls back to its key.
        assertEquals("mymod.tooltip.example", tooltip.getString());
        assertEquals(TextColor.fromLegacyFormat(ChatFormatting.AQUA), tooltip.getStyle().getColor());
        assertTrue(tooltip.getStyle().isItalic());
    }
}

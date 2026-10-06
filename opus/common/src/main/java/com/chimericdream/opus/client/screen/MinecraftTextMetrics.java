package com.chimericdream.opus.client.screen;

import com.chimericdream.opus.core.layout.TextMetrics;
import com.chimericdream.opus.core.model.Style;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Measures text with the game's font so the layout engine wraps exactly where the renderer will draw.
 *
 * <p>UNVERIFIED: never compiled. {@code Font#width(Component)} and {@code lineHeight} are used the same way in
 * better-target-dummies' {@code MobPickerScreen}.
 */
final class MinecraftTextMetrics implements TextMetrics {
    private final Font font;

    MinecraftTextMetrics(Font font) {
        this.font = font;
    }

    @Override
    public int width(String text, Style style) {
        return font.width(component(text, style, false));
    }

    @Override
    public int lineHeight() {
        return font.lineHeight;
    }

    /** The text as a styled component. Links get an underline so they read as links on any background. */
    static MutableComponent component(String text, Style style, boolean underline) {
        return Component.literal(text).withStyle(s -> s
            .withBold(style.bold())
            .withItalic(style.italic())
            .withStrikethrough(style.strikethrough())
            .withUnderlined(underline));
    }
}

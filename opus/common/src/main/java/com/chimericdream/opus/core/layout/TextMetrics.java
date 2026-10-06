package com.chimericdream.opus.core.layout;

import com.chimericdream.opus.core.model.Style;

/**
 * Text measurement supplied by the renderer, so layout needs no Minecraft classes. In game this wraps
 * {@code Font}; tests use a fixed-width fake.
 */
public interface TextMetrics {
    /** Width in pixels of {@code text} at scale 1. */
    int width(String text, Style style);

    /** Height in pixels of one line of text at scale 1. */
    int lineHeight();
}

package com.chimericdream.opus.core.layout;

import com.chimericdream.opus.core.model.Block;
import com.chimericdream.opus.core.model.Style;

/**
 * One positioned thing to draw. Coordinates are relative to the top-left of the page content area, so the
 * renderer only has to add its own origin and scroll offset.
 */
public sealed interface Element {
    int x();

    int y();

    int width();

    int height();

    default boolean contains(int px, int py) {
        return px >= x() && px < x() + width() && py >= y() && py < y() + height();
    }

    /** Semantic colour choice; the renderer maps roles to theme colours. */
    enum TextRole {
        BODY,
        HEADING,
        LINK,
        CODE,
        MUTED,
        CALLOUT_TITLE,
        ERROR
    }

    enum RectRole {
        CODE_BACKGROUND,
        QUOTE_BAR,
        RULE,
        TABLE_BORDER,
        TABLE_HEADER,
        CALLOUT_BACKGROUND,
        CALLOUT_BAR,
        HEADING_UNDERLINE
    }

    /** A run of text drawn at {@code scale}. {@code link} is the raw link destination, or {@code null}. */
    record Text(int x, int y, int width, int height, String text, Style style, float scale, TextRole role, String link) implements Element {
    }

    /** A filled rectangle. {@code variant} carries extra detail, e.g. the callout kind. */
    record Rect(int x, int y, int width, int height, RectRole role, String variant) implements Element {
    }

    /** An inline game-object icon (item, block or entity). */
    record Icon(int x, int y, int width, int height, String kind, String id, String link) implements Element {
    }

    /** Space reserved for a widget the renderer draws (recipe, item, entity). */
    record WidgetBox(int x, int y, int width, int height, Block.Widget widget) implements Element {
    }
}

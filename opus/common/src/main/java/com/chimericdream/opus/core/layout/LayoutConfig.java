package com.chimericdream.opus.core.layout;

import java.util.List;

/** Spacing and sizing constants for layout, in GUI pixels. */
public record LayoutConfig(
    int blockSpacing,
    int headingSpacing,
    int lineSpacing,
    int listGap,
    int listItemSpacing,
    int quoteIndent,
    int iconSize,
    int tablePadding,
    int boxPadding,
    List<Float> headingScales
) {
    public static LayoutConfig defaults() {
        return new LayoutConfig(5, 4, 1, 4, 2, 6, 16, 2, 4, List.of(1.8f, 1.5f, 1.25f, 1.1f, 1.0f, 1.0f));
    }

    public float headingScale(int level) {
        int i = Math.max(1, Math.min(level, headingScales.size())) - 1;
        return headingScales.get(i);
    }
}

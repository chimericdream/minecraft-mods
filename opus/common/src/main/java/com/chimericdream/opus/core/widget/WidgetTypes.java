package com.chimericdream.opus.core.widget;

import java.util.Set;

/** The fenced-block info strings that are treated as widgets rather than code. */
public final class WidgetTypes {
    public static final String RECIPE = "recipe";
    public static final String ITEM = "item";
    public static final String ENTITY = "entity";

    private static final Set<String> KNOWN = Set.of(RECIPE, ITEM, ENTITY);

    private WidgetTypes() {
    }

    public static boolean isWidget(String info) {
        return KNOWN.contains(info);
    }
}

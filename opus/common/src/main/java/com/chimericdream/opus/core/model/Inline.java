package com.chimericdream.opus.core.model;

import java.util.List;

/** Inline-level content of a paragraph, heading, list item or table cell. */
public sealed interface Inline {
    record Text(String text, Style style) implements Inline {
    }

    /**
     * A link. {@code destination} is the raw Markdown destination; resolve it with
     * {@code Book#resolve}. {@code line} is the 1-based source line, or 0 when unknown.
     */
    record Link(String destination, String title, List<Inline> children, int line) implements Inline {
    }

    /** {@code ![](item:minecraft:diamond)} - a game object drawn inline at icon size. */
    record Icon(String kind, String id, String alt) implements Inline {
    }

    /** A regular image reference that Opus doesn't know how to draw; shown as its alt text. */
    record Image(String source, String alt) implements Inline {
    }

    /**
     * {@code ![alt](texture:ns:path){width=64 align=right}} - a resource-pack texture. {@code id} is {@code ns:path}
     * relative to {@code textures/}, without {@code .png}. {@code width} is in book pixels, or 0 for the texture's own
     * size. {@code align} is {@code null} unless the author set it.
     */
    record Texture(String id, String alt, int width, Block.Align align) implements Inline {
    }

    /** A soft break renders as a space; a hard break always starts a new line. */
    record LineBreak(boolean hard) implements Inline {
    }

    static String plainText(List<Inline> inlines) {
        StringBuilder sb = new StringBuilder();
        appendPlainText(inlines, sb);
        return sb.toString();
    }

    private static void appendPlainText(List<Inline> inlines, StringBuilder sb) {
        for (Inline inline : inlines) {
            switch (inline) {
                case Text t -> sb.append(t.text());
                case Link l -> appendPlainText(l.children(), sb);
                case Icon i -> sb.append(i.alt() == null ? "" : i.alt());
                case Image i -> sb.append(i.alt() == null ? "" : i.alt());
                case Texture t -> sb.append(t.alt() == null ? "" : t.alt());
                case LineBreak b -> sb.append(' ');
            }
        }
    }
}

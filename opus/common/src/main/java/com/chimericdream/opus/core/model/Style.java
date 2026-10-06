package com.chimericdream.opus.core.model;

/** Inline text styling. Combined by nesting (e.g. bold inside italic). */
public record Style(boolean bold, boolean italic, boolean strikethrough, boolean code) {
    public static final Style PLAIN = new Style(false, false, false, false);

    public Style withBold() {
        return new Style(true, italic, strikethrough, code);
    }

    public Style withItalic() {
        return new Style(bold, true, strikethrough, code);
    }

    public Style withStrikethrough() {
        return new Style(bold, italic, true, code);
    }

    public Style withCode() {
        return new Style(bold, italic, strikethrough, true);
    }
}

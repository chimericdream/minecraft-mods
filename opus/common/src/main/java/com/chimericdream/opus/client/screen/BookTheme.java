package com.chimericdream.opus.client.screen;

import com.chimericdream.opus.core.layout.Element;

/**
 * Colours for the book screen (ARGB). A parchment look by default, close to the Markdown Manual screenshot.
 * Later this should come from {@code book.yml} / a resource-pack texture so books can be skinned.
 *
 * <p>UNVERIFIED: never compiled (plain data, so low risk).
 */
public record BookTheme(
    int border,
    int page,
    int sidebar,
    int sidebarSelected,
    int ink,
    int heading,
    int link,
    int linkHover,
    int muted,
    int error,
    int codeText,
    int codeBackground,
    int quoteBar,
    int rule,
    int tableBorder,
    int tableHeader
) {
    public static final BookTheme PARCHMENT = new BookTheme(
        0xFF5A3A22, 0xFFF4E8CF, 0xFFE6D5B3, 0xFFD2BB8F,
        0xFF2B2118, 0xFF1E160F, 0xFF2B3A9E, 0xFF5566E0, 0xFF7A6A55, 0xFFB02020,
        0xFF3B2F25, 0x33000000, 0xFF8A6D4B, 0xFF8A6D4B, 0xFF9C845F, 0x22000000
    );

    public int textColor(Element.TextRole role) {
        return switch (role) {
            case BODY -> ink;
            case HEADING -> heading;
            case LINK -> link;
            case CODE -> codeText;
            case MUTED -> muted;
            case CALLOUT_TITLE -> heading;
            case ERROR -> error;
        };
    }

    public int calloutColor(String kind) {
        return switch (kind == null ? "" : kind) {
            case "tip" -> 0xFF2E8B57;
            case "warning", "caution" -> 0xFFC77800;
            case "important" -> 0xFF8A3FB5;
            case "danger", "error" -> 0xFFB02020;
            default -> 0xFF2F6FB5;
        };
    }
}

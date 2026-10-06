package com.chimericdream.opus.core.book;

/**
 * A chapter/page/book icon from frontmatter. {@code item:minecraft:hopper} draws an item; {@code texture:ns:path}
 * draws a texture (path under {@code textures/}, no extension). A bare {@code minecraft:hopper} is an item.
 */
public record IconRef(Kind kind, String id) {
    public enum Kind {
        ITEM,
        TEXTURE
    }

    public static IconRef parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        String s = raw.trim();
        if (s.startsWith("texture:")) {
            return new IconRef(Kind.TEXTURE, s.substring("texture:".length()));
        }
        if (s.startsWith("item:")) {
            return new IconRef(Kind.ITEM, s.substring("item:".length()));
        }

        return new IconRef(Kind.ITEM, s.contains(":") ? s : "minecraft:" + s);
    }
}

package com.chimericdream.opus.core.widget;

import java.util.regex.Pattern;

/** Resource-location helpers that need no Minecraft classes. */
public final class Ids {
    private static final Pattern VALID = Pattern.compile("[a-z0-9_.\\-]+:[a-z0-9_.\\-/]+");

    private Ids() {
    }

    /** {@code hopper} becomes {@code minecraft:hopper}; tags keep their leading {@code #}. */
    public static String normalize(String raw) throws SpecException {
        if (raw == null || raw.isBlank()) {
            throw new SpecException("missing id");
        }

        String trimmed = raw.trim();
        boolean tag = trimmed.startsWith("#");
        String id = tag ? trimmed.substring(1) : trimmed;
        if (!id.contains(":")) {
            id = "minecraft:" + id;
        }
        if (!VALID.matcher(id).matches()) {
            throw new SpecException("'" + raw + "' is not a valid id (expected namespace:path)");
        }

        return tag ? "#" + id : id;
    }

    public static boolean isTag(String id) {
        return id.startsWith("#");
    }
}

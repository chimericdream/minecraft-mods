package com.chimericdream.polycraft.core;

import java.util.List;

/**
 * An allow list and a deny list of ids. Entries are an exact id ({@code mod:thing}), everything from one namespace
 * ({@code mod:*}), or everything ({@code *}). Deny wins; an empty allow list allows everything.
 */
public final class IdFilter {
    public static final IdFilter ALLOW_ALL = new IdFilter(List.of(), List.of());

    private final List<String> allow;
    private final List<String> deny;

    public IdFilter(List<String> allow, List<String> deny) {
        this.allow = List.copyOf(allow);
        this.deny = List.copyOf(deny);
    }

    public boolean test(String id) {
        if (matchesAny(deny, id)) {
            return false;
        }

        return allow.isEmpty() || matchesAny(allow, id);
    }

    private static boolean matchesAny(List<String> patterns, String id) {
        for (String pattern : patterns) {
            if (matches(pattern.trim(), id)) {
                return true;
            }
        }

        return false;
    }

    private static boolean matches(String pattern, String id) {
        if (pattern.equals("*")) {
            return true;
        }

        if (pattern.endsWith(":*")) {
            return id.startsWith(pattern.substring(0, pattern.length() - 1));
        }

        return pattern.equals(id);
    }
}

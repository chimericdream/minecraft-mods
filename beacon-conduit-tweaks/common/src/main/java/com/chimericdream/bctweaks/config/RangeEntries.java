package com.chimericdream.bctweaks.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import net.minecraft.resources.Identifier;

/**
 * Converts a per-block range map to and from the {@code block_id=range} lines the config screen edits.
 */
public final class RangeEntries {
    private RangeEntries() {
    }

    /** One line per entry, sorted by id (the config's own copy of a map does not keep its order). */
    public static List<String> toLines(Map<String, Double> map) {
        List<String> lines = new ArrayList<>();
        new TreeMap<>(map).forEach((id, range) -> lines.add(id + "=" + range));
        return lines;
    }

    /** Lines that are not {@code block_id=number} are skipped. */
    public static Map<String, Double> fromLines(List<String> lines) {
        return fromLines(lines, id -> true);
    }

    /** As {@link #fromLines(List)}, but also skips ids that {@code isKnown} rejects (for example, unregistered blocks). */
    public static Map<String, Double> fromLines(List<String> lines, Predicate<String> isKnown) {
        Map<String, Double> map = new LinkedHashMap<>();
        for (String line : lines) {
            int split = line.lastIndexOf('=');
            if (split < 1) {
                continue;
            }

            String id = line.substring(0, split).trim();
            if (Identifier.tryParse(id) == null || !isKnown.test(id)) {
                continue;
            }

            try {
                map.put(id, Double.parseDouble(line.substring(split + 1).trim()));
            } catch (NumberFormatException ignored) {
                // not a number: skip the line
            }
        }
        return map;
    }
}

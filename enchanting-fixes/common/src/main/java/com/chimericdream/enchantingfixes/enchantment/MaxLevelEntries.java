package com.chimericdream.enchantingfixes.enchantment;

import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Converts the per-enchantment level map to and from the {@code enchantment_id=level} lines that the
 * config screen edits. YACL has no map control.
 */
public final class MaxLevelEntries {
    private MaxLevelEntries() {
    }

    /** One line for each entry, sorted by ID. */
    public static List<String> toLines(Map<String, Integer> map) {
        List<String> lines = new ArrayList<>();
        new TreeMap<>(map).forEach((id, level) -> lines.add(id + "=" + level));
        return lines;
    }

    /**
     * Skips lines that are not {@code enchantment_id=level}. Clamps each level to the range that Minecraft
     * accepts. An ID without a namespace gets {@code minecraft:}.
     */
    public static Map<String, Integer> fromLines(List<String> lines) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (String line : lines) {
            int split = line.lastIndexOf('=');
            if (split < 1) {
                continue;
            }

            Identifier id = Identifier.tryParse(line.substring(0, split).trim());
            if (id == null) {
                continue;
            }

            try {
                int level = Integer.parseInt(line.substring(split + 1).trim());
                map.put(id.toString(), MaxLevels.clamp(level));
            } catch (NumberFormatException ignored) {
                // Not a number: skip the line.
            }
        }
        return map;
    }
}

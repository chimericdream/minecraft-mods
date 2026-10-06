package com.chimericdream.opus.core.frontmatter;

import com.chimericdream.opus.core.Diagnostics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The YAML frontmatter block at the top of a Markdown file, plus the remaining body.
 *
 * <pre>
 * ---
 * title: Hoppers
 * tags: [machines, logistics]
 * ---
 * Body starts here.
 * </pre>
 *
 * @param bodyStartLine 1-based line in the original file where {@code body} begins
 */
public record Frontmatter(Map<String, Object> data, String body, int bodyStartLine) {
    public static Frontmatter parse(String source, Diagnostics diagnostics, String path) {
        String text = source.replace("\r\n", "\n");
        if (text.startsWith("﻿")) {
            text = text.substring(1);
        }

        if (!isDelimiter(firstLine(text))) {
            return new Frontmatter(new LinkedHashMap<>(), text, 1);
        }

        String[] lines = text.split("\n", -1);
        int closing = -1;
        for (int i = 1; i < lines.length; i++) {
            String trimmed = lines[i].stripTrailing();
            if (trimmed.equals("---") || trimmed.equals("...")) {
                closing = i;
                break;
            }
        }

        if (closing < 0) {
            diagnostics.warn(path, 1, "frontmatter is never closed with '---'; treating the whole file as content");
            return new Frontmatter(new LinkedHashMap<>(), text, 1);
        }

        String yaml = String.join("\n", List.of(lines).subList(1, closing));
        String body = String.join("\n", List.of(lines).subList(closing + 1, lines.length));

        Map<String, Object> data;
        try {
            data = YamlSupport.loadMap(yaml);
        } catch (YamlSupport.YamlException e) {
            // +2: one for the opening '---' line, one because the YAML line is 1-based
            diagnostics.error(path, e.line() > 0 ? e.line() + 1 : 1, "invalid frontmatter: " + e.getMessage());
            data = new LinkedHashMap<>();
        }

        return new Frontmatter(data, body, closing + 2);
    }

    private static String firstLine(String text) {
        int nl = text.indexOf('\n');
        return nl < 0 ? text : text.substring(0, nl);
    }

    private static boolean isDelimiter(String line) {
        return line.stripTrailing().equals("---");
    }

    public Set<String> keys() {
        return Collections.unmodifiableSet(data.keySet());
    }

    /** The value as a string, or {@code null} when absent. Non-string scalars are stringified. */
    public String string(String key) {
        Object value = data.get(key);
        return value == null ? null : String.valueOf(value);
    }

    /** Accepts a YAML list or a comma-separated string. Never returns {@code null}. */
    public List<String> stringList(String key) {
        Object value = data.get(key);
        List<String> out = new ArrayList<>();
        if (value instanceof List<?> list) {
            for (Object item : list) {
                if (item != null && !String.valueOf(item).isBlank()) {
                    out.add(String.valueOf(item).trim());
                }
            }
        } else if (value != null) {
            for (String part : String.valueOf(value).split(",")) {
                if (!part.isBlank()) {
                    out.add(part.trim());
                }
            }
        }

        return out;
    }

    public Double number(String key) {
        Object value = data.get(key);
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        if (value instanceof String s) {
            try {
                return Double.parseDouble(s.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }

    public boolean bool(String key, boolean fallback) {
        Object value = data.get(key);
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof String s) {
            return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes");
        }

        return fallback;
    }
}

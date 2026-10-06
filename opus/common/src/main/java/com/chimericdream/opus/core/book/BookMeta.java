package com.chimericdream.opus.core.book;

import com.chimericdream.opus.core.Diagnostics;
import com.chimericdream.opus.core.frontmatter.YamlSupport;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Language-independent book settings from {@code book.yml}.
 *
 * @param icon an {@link IconRef} string such as {@code item:minecraft:book}; may be {@code null}
 */
public record BookMeta(String title, String icon, String description, String defaultLanguage, Map<String, Object> raw) {
    public static final String FILE_NAME = "book.yml";
    public static final String DEFAULT_LANGUAGE = "en_us";

    private static final Set<String> KNOWN_KEYS = Set.of("title", "icon", "description", "default_language");

    public static BookMeta empty(String fallbackTitle) {
        return new BookMeta(fallbackTitle, null, null, DEFAULT_LANGUAGE, new LinkedHashMap<>());
    }

    /** A missing or broken file never fails the load; it just yields defaults plus a diagnostic. */
    public static BookMeta parse(String yaml, String fallbackTitle, Diagnostics diagnostics) {
        Map<String, Object> data;
        try {
            data = YamlSupport.loadMap(yaml);
        } catch (YamlSupport.YamlException e) {
            diagnostics.error(FILE_NAME, e.line(), "invalid book.yml: " + e.getMessage());
            return empty(fallbackTitle);
        }

        for (String key : data.keySet()) {
            if (!KNOWN_KEYS.contains(key) && !key.startsWith("x-")) {
                diagnostics.warn(FILE_NAME, 0, "unknown key '" + key + "'");
            }
        }

        return new BookMeta(
            str(data.get("title"), fallbackTitle),
            str(data.get("icon"), null),
            str(data.get("description"), null),
            str(data.get("default_language"), DEFAULT_LANGUAGE),
            data
        );
    }

    private static String str(Object value, String fallback) {
        return value == null ? fallback : String.valueOf(value);
    }
}

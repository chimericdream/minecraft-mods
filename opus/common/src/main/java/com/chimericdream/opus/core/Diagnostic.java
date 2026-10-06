package com.chimericdream.opus.core;

/**
 * A problem found while loading a book: a bad frontmatter key, a broken link, a malformed widget, ...
 *
 * @param line 1-based line in {@code path}, or {@code 0} when unknown
 */
public record Diagnostic(Severity severity, String path, int line, String message) {
    public enum Severity {
        ERROR,
        WARNING
    }

    @Override
    public String toString() {
        String where = line > 0 ? path + ":" + line : path;
        return where + ": " + severity.name().toLowerCase() + ": " + message;
    }
}

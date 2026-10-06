package com.chimericdream.opus.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Collects {@link Diagnostic}s while a book loads. Not thread-safe; one instance per load. */
public final class Diagnostics {
    private final List<Diagnostic> entries = new ArrayList<>();

    public void error(String path, int line, String message) {
        entries.add(new Diagnostic(Diagnostic.Severity.ERROR, path, line, message));
    }

    public void warn(String path, int line, String message) {
        entries.add(new Diagnostic(Diagnostic.Severity.WARNING, path, line, message));
    }

    public List<Diagnostic> all() {
        return Collections.unmodifiableList(entries);
    }

    public boolean hasErrors() {
        return entries.stream().anyMatch(d -> d.severity() == Diagnostic.Severity.ERROR);
    }

    public long errorCount() {
        return entries.stream().filter(d -> d.severity() == Diagnostic.Severity.ERROR).count();
    }

    public long warningCount() {
        return entries.stream().filter(d -> d.severity() == Diagnostic.Severity.WARNING).count();
    }
}

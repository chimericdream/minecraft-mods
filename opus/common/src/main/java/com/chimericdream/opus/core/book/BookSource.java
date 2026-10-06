package com.chimericdream.opus.core.book;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * Where a book's files come from, for one language. Paths are relative to the language folder and always use
 * forward slashes (for example {@code machines/hoppers.md}). In-game this is backed by the resource manager; the
 * implementations here serve tests and the command-line validator.
 */
public interface BookSource {
    /** Every file available, sorted. */
    List<String> list();

    Optional<String> readText(String path);

    default boolean exists(String path) {
        return list().contains(path);
    }

    /** Files from {@code primary} win; files only in {@code fallback} fill the gaps (translation fallback). */
    static BookSource layered(BookSource primary, BookSource fallback) {
        return new BookSource() {
            @Override
            public List<String> list() {
                TreeSet<String> all = new TreeSet<>(primary.list());
                all.addAll(fallback.list());
                return List.copyOf(all);
            }

            @Override
            public Optional<String> readText(String path) {
                Optional<String> text = primary.readText(path);
                return text.isPresent() ? text : fallback.readText(path);
            }
        };
    }

    /** In-memory source for tests. */
    final class MapSource implements BookSource {
        private final Map<String, String> files = new LinkedHashMap<>();

        public MapSource with(String path, String text) {
            files.put(path, text);
            return this;
        }

        @Override
        public List<String> list() {
            List<String> paths = new ArrayList<>(files.keySet());
            Collections.sort(paths);
            return paths;
        }

        @Override
        public Optional<String> readText(String path) {
            return Optional.ofNullable(files.get(path));
        }
    }

    /** A folder on disk. */
    final class DirectorySource implements BookSource {
        private final Path root;

        public DirectorySource(Path root) {
            this.root = root;
        }

        @Override
        public List<String> list() {
            if (!Files.isDirectory(root)) {
                return List.of();
            }

            try (Stream<Path> walk = Files.walk(root)) {
                return walk.filter(Files::isRegularFile)
                    .map(p -> root.relativize(p).toString().replace('\\', '/'))
                    .sorted()
                    .toList();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public Optional<String> readText(String path) {
            try {
                return Optional.of(Files.readString(root.resolve(path), StandardCharsets.UTF_8));
            } catch (IOException e) {
                return Optional.empty();
            }
        }
    }
}

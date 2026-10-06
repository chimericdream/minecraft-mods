package com.chimericdream.opus.core.tools;

import com.chimericdream.opus.core.Diagnostics;
import com.chimericdream.opus.core.book.Book;
import com.chimericdream.opus.core.book.BookLoader;
import com.chimericdream.opus.core.book.BookMeta;
import com.chimericdream.opus.core.book.BookNode;
import com.chimericdream.opus.core.book.BookSource;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Command-line book checker: loads a book folder exactly as the game would and prints every problem.
 *
 * <pre>
 * java -cp ... com.chimericdream.opus.core.tools.ValidateBook &lt;book-dir&gt; [--lang=en_us]
 * </pre>
 *
 * {@code book-dir} is the folder holding {@code book.yml} and the per-language folders. Exits with status 1
 * when any error (not warning) is found, so it can gate a build.
 */
public final class ValidateBook {
    private ValidateBook() {
    }

    public static void main(String[] args) {
        System.exit(run(args, System.out, System.err));
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        Path dir = null;
        String lang = null;
        for (String arg : args) {
            if (arg.startsWith("--lang=")) {
                lang = arg.substring("--lang=".length());
            } else if (dir == null) {
                dir = Path.of(arg);
            }
        }

        if (dir == null || !Files.isDirectory(dir)) {
            err.println("usage: ValidateBook <book-dir> [--lang=en_us]");
            return 2;
        }

        Diagnostics diagnostics = new Diagnostics();
        String folder = dir.toAbsolutePath().normalize().getFileName().toString();
        BookMeta meta = BookMeta.empty(folder);
        Path metaFile = dir.resolve(BookMeta.FILE_NAME);
        if (Files.exists(metaFile)) {
            try {
                meta = BookMeta.parse(Files.readString(metaFile), folder, diagnostics);
            } catch (IOException e) {
                err.println("cannot read " + metaFile + ": " + e.getMessage());
                return 2;
            }
        } else {
            diagnostics.warn(BookMeta.FILE_NAME, 0, "no book.yml found; using defaults");
        }

        String language = lang != null ? lang : meta.defaultLanguage();
        BookSource primary = new BookSource.DirectorySource(dir.resolve(language));
        BookSource fallback = new BookSource.DirectorySource(dir.resolve(meta.defaultLanguage()));
        if (primary.list().isEmpty() && fallback.list().isEmpty()) {
            err.println("no files found under " + dir.resolve(language));
            return 2;
        }

        Book book = BookLoader.load(bookId(dir), BookSource.layered(primary, fallback), meta, diagnostics);

        diagnostics.all().forEach(d -> out.println(d));
        int pages = (int) book.nodes().stream().filter(n -> n.kind() == BookNode.Kind.PAGE).count();
        int chapters = (int) book.nodes().stream().filter(BookNode::isChapter).count();
        out.printf("%s: %d chapters, %d pages, %d tags, %d errors, %d warnings%n",
            book.id(), chapters, pages, book.tags().size(), diagnostics.errorCount(), diagnostics.warningCount());

        return diagnostics.hasErrors() ? 1 : 0;
    }

    /** {@code .../assets/<ns>/opus-books/<name>} becomes {@code ns:name}; anything else is {@code local:<name>}. */
    private static String bookId(Path dir) {
        Path abs = dir.toAbsolutePath().normalize();
        Path parent = abs.getParent();
        if (parent != null && parent.getFileName() != null && parent.getFileName().toString().equals("opus-books")
            && parent.getParent() != null && parent.getParent().getFileName() != null) {
            return parent.getParent().getFileName() + ":" + abs.getFileName();
        }

        return "local:" + abs.getFileName();
    }
}

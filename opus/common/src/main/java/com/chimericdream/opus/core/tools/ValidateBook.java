package com.chimericdream.opus.core.tools;

import com.chimericdream.opus.core.Diagnostics;
import com.chimericdream.opus.core.book.Book;
import com.chimericdream.opus.core.book.BookLoader;
import com.chimericdream.opus.core.book.BookMeta;
import com.chimericdream.opus.core.book.BookNode;
import com.chimericdream.opus.core.book.BookSource;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Command-line book checker: loads a book exactly as the game would and prints every problem.
 *
 * <pre>
 * java -jar opus.jar [--validate] &lt;path&gt; [--lang=en_us]
 * </pre>
 *
 * {@code path} can be a single book folder (the one holding {@code book.yml} and the per-language folders), or any
 * folder above one (a resource pack, a mod's resources, a whole project): every {@code assets/<ns>/opus-books/<name>}
 * book found beneath it is checked. Exits with status 1 when any error (not warning) is found, so it can gate a build.
 */
public final class ValidateBook {
    private static final int MAX_SEARCH_DEPTH = 8;
    private static final Set<String> SKIPPED_FOLDERS = Set.of(".git", ".gradle", "build", "node_modules", "run");

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
            } else if (arg.equals("--validate") || (arg.equals("validate") && dir == null)) {
                continue;
            } else if (dir == null) {
                dir = Path.of(arg);
            }
        }

        if (dir == null || !Files.isDirectory(dir)) {
            err.println("usage: java -jar opus.jar [--validate] <path> [--lang=en_us]");
            return 2;
        }

        List<Path> books = findBooks(dir);
        int status = 0;
        for (Path book : books) {
            status = Math.max(status, validate(book, lang, out, err));
        }

        return status;
    }

    /** A folder with {@code book.yml} is one book; otherwise look for {@code opus-books/*}; otherwise assume it is a book. */
    static List<Path> findBooks(Path dir) {
        if (Files.exists(dir.resolve(BookMeta.FILE_NAME))) {
            return List.of(dir);
        }

        List<Path> found = new ArrayList<>();
        try {
            Files.walkFileTree(dir, EnumSet.noneOf(FileVisitOption.class), MAX_SEARCH_DEPTH, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path d, BasicFileAttributes attrs) throws IOException {
                    Path name = d.getFileName();
                    if (!d.equals(dir) && name != null && SKIPPED_FOLDERS.contains(name.toString())) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }

                    if (name != null && name.toString().equals("opus-books")) {
                        try (Stream<Path> children = Files.list(d)) {
                            children.filter(Files::isDirectory).sorted().forEach(found::add);
                        }

                        return FileVisitResult.SKIP_SUBTREE;
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            // fall through to treating the folder as a book; validate() reports what is wrong with it
        }

        return found.isEmpty() ? List.of(dir) : found;
    }

    private static int validate(Path dir, String lang, PrintStream out, PrintStream err) {
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

        checkItemLook(dir, meta, diagnostics);

        Book book = BookLoader.load(bookId(dir), BookSource.layered(primary, fallback), meta, diagnostics);

        diagnostics.all().forEach(d -> out.println(d));
        int pages = (int) book.nodes().stream().filter(n -> n.kind() == BookNode.Kind.PAGE).count();
        int chapters = (int) book.nodes().stream().filter(BookNode::isChapter).count();
        out.printf("%s: %d chapters, %d pages, %d tags, %d errors, %d warnings%n",
            book.id(), chapters, pages, book.tags().size(), diagnostics.errorCount(), diagnostics.warningCount());

        return diagnostics.hasErrors() ? 1 : 0;
    }

    /**
     * Warns when {@code texture} or {@code model} in book.yml points at nothing. The files can only be located when the
     * book sits at {@code .../assets/<ns>/opus-books/<name>}; any other layout is skipped.
     */
    static void checkItemLook(Path dir, BookMeta meta, Diagnostics diagnostics) {
        Path abs = dir.toAbsolutePath().normalize();
        Path books = abs.getParent();
        Path assets = books == null || books.getParent() == null ? null : books.getParent().getParent();
        boolean inPack = books != null && books.getFileName() != null && books.getFileName().toString().equals("opus-books")
            && assets != null && assets.getFileName() != null && assets.getFileName().toString().equals("assets");

        String model = meta.model();
        if (model != null) {
            String[] id = splitId(model);
            if (id == null || (inPack && !Files.exists(assets.resolve(id[0]).resolve("models").resolve(id[1] + ".json")))) {
                diagnostics.warn(BookMeta.FILE_NAME, 0, "model '" + model + "' was not found (expected models/<path>.json); the default book look is used");
            }
        }

        String texture = meta.texture();
        if (texture != null) {
            String[] id = splitId(texture);
            if (id == null || !id[1].startsWith("item/")) {
                diagnostics.warn(BookMeta.FILE_NAME, 0, "texture '" + texture + "' must be an id under textures/item/, like ns:item/name; the default book look is used");
            } else if (inPack && !Files.exists(assets.resolve(id[0]).resolve("textures").resolve(id[1] + ".png"))) {
                diagnostics.warn(BookMeta.FILE_NAME, 0, "texture '" + texture + "' was not found (expected textures/<path>.png); the default book look is used");
            }
        }
    }

    /** {@code ns:path} (namespace optional, defaulting to minecraft) as {@code [ns, path]}; null when malformed. */
    private static String[] splitId(String raw) {
        String s = raw.trim();
        int colon = s.indexOf(':');
        String ns = colon < 0 ? "minecraft" : s.substring(0, colon);
        String path = colon < 0 ? s : s.substring(colon + 1);

        return ns.isEmpty() || path.isEmpty() || path.contains("..") ? null : new String[]{ns, path};
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

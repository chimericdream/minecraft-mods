package com.chimericdream.opus.test.core;

import com.chimericdream.opus.core.tools.ValidateBook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidateBookTest {
    private static final String GUIDE = "assets/opus/opus-books/guide";

    private static Path guideDir() {
        for (String prefix : List.of("common/src/main/resources/", "../common/src/main/resources/", "opus/common/src/main/resources/")) {
            Path candidate = Path.of(prefix + GUIDE);
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
        }

        throw new IllegalStateException("cannot find the bundled guide book from " + Path.of("").toAbsolutePath());
    }

    private record Result(int status, String out, String err) {
    }

    private static Result run(String... args) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        int status = ValidateBook.run(args, new PrintStream(out, true, StandardCharsets.UTF_8), new PrintStream(err, true, StandardCharsets.UTF_8));
        return new Result(status, out.toString(StandardCharsets.UTF_8), err.toString(StandardCharsets.UTF_8));
    }

    private static void write(Path root, String file, String text) throws IOException {
        Path p = root.resolve(file);
        Files.createDirectories(p.getParent());
        Files.writeString(p, text);
    }

    @Test
    void theBundledGuideBookIsClean() {
        Result r = run(guideDir().toString());

        assertEquals(0, r.status(), r.out());
        assertTrue(r.out().contains("opus:guide"), r.out());
        assertTrue(r.out().contains("0 errors, 0 warnings"), r.out());
    }

    @Test
    void brokenBooksFailWithFileAndLine(@TempDir Path dir) throws IOException {
        write(dir, "book.yml", "title: Broken\n");
        write(dir, "en_us/index.md", "# Home\n\nSee [nowhere](missing.md).\n");

        Result r = run(dir.toString());

        assertEquals(1, r.status());
        assertTrue(r.out().contains("index.md:3: error:"), r.out());
        assertTrue(r.out().contains("1 errors"), r.out());
    }

    @Test
    void warningsDoNotFailTheRun(@TempDir Path dir) throws IOException {
        write(dir, "en_us/index.md", "---\ntitel: typo\n---\nHello\n");

        Result r = run(dir.toString());

        assertEquals(0, r.status(), r.out());
        assertTrue(r.out().contains("warning"), r.out());
    }

    @Test
    void translationsFallBackToTheDefaultLanguage(@TempDir Path dir) throws IOException {
        write(dir, "book.yml", "title: T\n");
        write(dir, "en_us/index.md", "[sub](sub.md)\n");
        write(dir, "en_us/sub.md", "English\n");
        write(dir, "de_de/index.md", "[sub](sub.md)\n");

        Result r = run(dir.toString(), "--lang=de_de");

        assertEquals(0, r.status(), r.out());
    }

    @Test
    void badArgumentsReturnUsageStatus(@TempDir Path dir) {
        assertEquals(2, run().status());
        assertEquals(2, run(dir.resolve("does-not-exist").toString()).status());
        assertEquals(2, run(dir.toString()).status(), "a folder with no book files");
    }
}

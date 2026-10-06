package com.chimericdream.opus.client.book;

import com.chimericdream.opus.OpusMod;
import com.chimericdream.opus.core.Diagnostics;
import com.chimericdream.opus.core.book.Book;
import com.chimericdream.opus.core.book.BookLoader;
import com.chimericdream.opus.core.book.BookMeta;
import com.chimericdream.opus.core.book.BookSource;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Every book found in the loaded resource packs, rebuilt on each resource reload (so F3+T picks up edits and
 * a language change swaps translations).
 *
 * <p>Compiles against 26.2; not yet run in game. {@code LanguageManager#getSelected()} returning the language code string is
 * an assumption; check against the 26.2 sources.
 */
public final class BookRepository {
    private static final Map<Identifier, Book> BOOKS = new ConcurrentHashMap<>();

    private BookRepository() {
    }

    public static Book get(String bookId) {
        Identifier id = Identifier.tryParse(bookId);
        return id == null ? null : BOOKS.get(id);
    }

    public static Map<Identifier, Book> all() {
        return Map.copyOf(BOOKS);
    }

    public static void reload(ResourceManager manager) {
        BOOKS.clear();

        String language = Minecraft.getInstance().getLanguageManager().getSelected();

        // Every books/<name>/book.yml marks a book. Namespaces are separate books even with the same <name>.
        manager.listResources("books", id -> id.getPath().endsWith("/book.yml")).forEach((id, resource) -> {
            String[] parts = id.getPath().split("/");
            if (parts.length != 3) {
                return;
            }

            String name = parts[1];
            Identifier bookId = Identifier.fromNamespaceAndPath(id.getNamespace(), name);
            Diagnostics diagnostics = new Diagnostics();

            BookMeta meta = BookMeta.parse(readText(resource), name, diagnostics);
            String root = "books/" + name;
            BookSource primary = new ResourceBookSource(manager, id.getNamespace(), root + "/" + language);
            BookSource fallback = new ResourceBookSource(manager, id.getNamespace(), root + "/" + meta.defaultLanguage());

            Book book = BookLoader.load(bookId.toString(), BookSource.layered(primary, fallback), meta, diagnostics);
            BOOKS.put(bookId, book);

            diagnostics.all().forEach(d -> {
                String line = "[" + bookId + "] " + d;
                if (d.severity() == com.chimericdream.opus.core.Diagnostic.Severity.ERROR) {
                    OpusMod.LOGGER.error(line);
                } else {
                    OpusMod.LOGGER.warn(line);
                }
            });
        });

        OpusMod.LOGGER.info("Loaded {} Opus book(s)", BOOKS.size());
    }

    private static String readText(Resource resource) {
        try (InputStream stream = resource.open()) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            OpusMod.LOGGER.error("Could not read a book.yml", e);
            return "";
        }
    }
}

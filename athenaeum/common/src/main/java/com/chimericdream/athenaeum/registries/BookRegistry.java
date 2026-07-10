package com.chimericdream.athenaeum.registries;

import com.chimericdream.athenaeum.AthenaeumMod;
import com.chimericdream.athenaeum.config.AthenaeumConfig;
import com.chimericdream.athenaeum.data.AthenaeumBook;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class BookRegistry {
    protected final Map<Identifier, AthenaeumBook> books = new HashMap<>();
    protected final Map<String, Set<Identifier>> authors = new HashMap<>();

    public void clear() {
        this.books.clear();
        this.authors.clear();
    }

    public void addFromInputStream(Identifier bookId, InputStream stream) {
        JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

        String title = Optional.ofNullable(json.get("title"))
            .orElse(new JsonObject())
            .getAsString();

        String author = Optional.ofNullable(json.get("author"))
            .orElse(new JsonObject())
            .getAsString();

        List<String> pages = new ArrayList<>();
        Optional.ofNullable(json.get("pages"))
            .orElse(new JsonArray())
            .getAsJsonArray()
            .forEach(page -> pages.add(page.getAsString()));

        if (title == null || author == null || pages.isEmpty()) {
            AthenaeumMod.LOGGER.error("Invalid book: " + bookId);

            return;
        }

        AthenaeumBook book = new AthenaeumBook(title, author, pages);

        this.books.put(bookId, book);

        if (!this.authors.containsKey(author)) {
            this.authors.put(author, new HashSet<>());
        }

        this.authors.get(author).add(bookId);
    }

    public AthenaeumBook getById(Identifier bookId) {
        return this.books.get(bookId);
    }

    public List<AthenaeumBook> getBooksByAuthor(String author) {
        Set<Identifier> authorBooks = this.authors.get(author);

        if (authorBooks == null) {
            return null;
        }

        List<AthenaeumBook> bookList = new ArrayList<>();
        authorBooks.forEach(bookId -> bookList.add(this.books.get(bookId)));

        return bookList;
    }

    public AthenaeumBook getRandomBook(RandomSource random) {
        List<AthenaeumBook> bookList = new ArrayList<>(this.books.values());

        if (bookList.isEmpty()) {
            return null;
        }

        return bookList.get(random.nextInt(bookList.size()));
    }

    public AthenaeumBook getRandomBookByAuthor(String author, RandomSource random) {
        List<AthenaeumBook> bookList = this.getBooksByAuthor(author);

        if (bookList == null || bookList.isEmpty()) {
            return null;
        }

        return bookList.get(random.nextInt(bookList.size()));
    }

    public WrittenBookContent getRandomBookForLootTable(
        List<String> authors,
        NumberProvider editionProvider,
        LootContext context
    ) {
        List<AthenaeumBook> bookList;

        if (authors.isEmpty()) {
            bookList = new ArrayList<>(this.books.values());
        } else {
            bookList = new ArrayList<>();
            authors.forEach(author -> {
                List<AthenaeumBook> authorBooks = this.getBooksByAuthor(author);

                if (authorBooks != null) {
                    bookList.addAll(authorBooks);
                }
            });

            if (bookList.isEmpty()) {
                return null;
            }
        }

        AthenaeumBook book = bookList.get(context.getRandom().nextInt(bookList.size()));

        int edition = getRandomEdition(context);
        if (editionProvider.getInt(context) != -1) {
            edition = Math.clamp(editionProvider.getInt(context), 0, 3);
        }

        List<Filterable<Component>> pages = book.pages
            .stream()
            .map(Component::nullToEmpty)
            .map(Filterable::passThrough)
            .toList();

        return new WrittenBookContent(
            Filterable.passThrough(book.title),
            book.author,
            edition,
            pages,
            true
        );
    }

    private int getRandomEdition(LootContext context) {
        AthenaeumConfig config = AthenaeumConfig.HANDLER.instance();

        double roll = context.getRandom().nextDouble();
        if (roll <= config.firstEditionChance) {
            return 0;
        }

        roll -= config.firstEditionChance;
        if (roll <= config.secondEditionChance) {
            return 1;
        }

        roll -= config.secondEditionChance;
        if (roll <= config.thirdEditionChance) {
            return 2;
        }

        return 3;
    }
}
